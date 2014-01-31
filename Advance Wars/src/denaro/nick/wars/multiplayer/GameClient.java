package denaro.nick.wars.multiplayer;

import java.awt.Point;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import denaro.nick.core.EngineAction;
import denaro.nick.server.Client;
import denaro.nick.server.Message;
import denaro.nick.server.MyInputStream;
import denaro.nick.wars.Battle;
import denaro.nick.wars.BattleAction;
import denaro.nick.wars.GameMode;
import denaro.nick.wars.GameModeSelector;
import denaro.nick.wars.Main;
import denaro.nick.wars.Path;
import denaro.nick.wars.Team;
import denaro.nick.wars.Terrain;
import denaro.nick.wars.Unit;
import denaro.nick.wars.menu.ActionMenu;
import denaro.nick.wars.view.BattleLobbyView;
import denaro.nick.wars.view.BattleView;

public class GameClient extends Client
{
	public GameClient() throws IOException
	{
		super(new Socket(MainServer.hostname==null?InetAddress.getLocalHost().getHostAddress():MainServer.hostname,MainServer.port));
	}
	
	public GameClient(Socket socket) throws IOException
	{
		super(socket);
	}

	public Path readPath(MyInputStream in)
	{
		MultiplayerBattle mbattle=((MultiplayerBattle)Main.currentMode);
		int xpos=in.readInt();
		int ypos=in.readInt();
		Unit unit=mbattle.map().unit(xpos,ypos);
		int pathsize=in.readInt();
		Path path=new Path(mbattle.map(),unit.movementType(), unit.movement());
		path.start(in.readInt(),in.readInt());
		for(int i=0;i<pathsize-1;i++)
			path.addPoint(in.readInt(),in.readInt());
		
		return(path);
	}
	
	@Override
	public void handleMessages(MyInputStream in, int messageid) throws IOException
	{
		final GameMode finalGameMode=Main.currentMode;
		switch(messageid)
		{
			case ServerClient.SESSIONS:
				int size=in.readInt();
				for(int i=0;i<size;i++)
					((GameModeSelector)Main.currentMode).addAction(in.readString());
				
			return;
			case ServerClient.NEWSESSION:
				boolean success=in.readBoolean();
				if(success)
				{
					System.out.println("successfully created a new battle session.");
					
					size=in.readInt();
					ByteBuffer buffer=ByteBuffer.allocate(size);
					buffer.put(in.readBytes(size));
					MultiplayerBattle battle=Main.loadMultiplayerBattle(null,buffer);
					
					String name=in.readString();
					int player=in.readInt();
					
					BattleLobby lobby=new BattleLobby(battle,player);
					Main.currentMode=lobby;
					Main.engine().view(new BattleLobbyView(240, 160, 2, 2));
					lobby.addPlayer(0,name);
					Main.closeAllMenus();
				}
			return;
			case ServerClient.JOINSESSION:
				System.out.println("joined session!");
				size=in.readInt();
				ByteBuffer buffer=ByteBuffer.allocate(size);
				buffer.put(in.readBytes(size));
				MultiplayerBattle battle=Main.loadMultiplayerBattle(null,buffer);
				
				int player=in.readInt();
				
				BattleLobby lobby=new BattleLobby(battle,player);
				Main.currentMode=lobby;
				Main.engine().view(new BattleLobbyView(240, 160, 2, 2));
				Main.closeAllMenus();
				size=in.readInt();
				for(int i=0;i<size;i++)
				{
					lobby.addPlayer(i,in.readString());
					if(in.readBoolean())
						lobby.lockPlayer(i);
				}
				lobby.addPlayer(player,in.readString());
				System.out.println("lobby player: "+lobby.player());
			return;
			case ServerClient.PLAYERJOINEDSESSION:
				int index=in.readInt();
				String pname=in.readString();
				if(Main.currentMode instanceof BattleLobby)
				{
					((BattleLobby)Main.currentMode).addPlayer(index,pname);
				}
			return;
			case ServerClient.PLAYERREADY:
				index=in.readInt();
				boolean ready=in.readBoolean();
				if(ready)
					((BattleLobby)Main.currentMode).lockPlayer(index);
				else
					((BattleLobby)Main.currentMode).unlockPlayer(index);
			return;
			case ServerClient.LEAVESESSION:
				index=in.readInt();
				((BattleLobby)Main.currentMode).removePlayer(index);
			return;
			case ServerClient.STARTSESSION:
				int teamsize=in.readInt();
				ArrayList<Team> teams=new ArrayList<Team>();
				for(int i=0;i<teamsize;i++)
				{
					int teamid=in.readInt();
					int comid=in.readInt();
					Team team=Team.copy(Main.teamMap.get(teamid),Main.commanderMap.get(comid));
					teams.add(team);
				}
				lobby=((BattleLobby)Main.currentMode);
				MultiplayerBattle mbattle=lobby.battle();
				Main.currentMode=mbattle;
				Main.engine().location(mbattle.map());
				Main.engine().view(new BattleView(240,160,2,2));
				Main.currentMode.addCursorListener((BattleView)Main.engine().view());
				Main.engine().requestFocus(Main.currentMode);
				mbattle.teams(teams);
				mbattle.myTeam(lobby.player());
				mbattle.start();
				System.out.println("Started: "+((MultiplayerBattle)Main.currentMode).started());
			return;
			case ServerClient.UPDATETEAM:
				int funds=in.readInt();
				mbattle=((MultiplayerBattle)Main.currentMode);
				mbattle.myTeam().funds(funds);
			return;
			case ServerClient.UPDATEMAP:
				mbattle=((MultiplayerBattle)Main.currentMode);
				for(int a=0;a<mbattle.map().height();a++)
				{
					for(int i=0;i<mbattle.map().width();i++)
					{
						Terrain terrain=Main.readTerrain(in);
						mbattle.map().setTerrain(terrain,i,a);
					}
				}
				for(int a=0;a<mbattle.map().height();a++)
				{
					for(int i=0;i<mbattle.map().width();i++)
					{
						Unit unit=Main.readUnit(in);
						mbattle.map().setUnit(unit,i,a);
						if(unit!=null)
							unit.move(i*Main.TILESIZE,a*Main.TILESIZE);
					}
				}
			return;
			case ServerClient.UPDATETERRAIN:
				mbattle=((MultiplayerBattle)Main.currentMode);
				final int finalx=in.readInt();
				final int finaly=in.readInt();
				final Terrain terrain=Main.readTerrain(in);
				BattleAction action=new BattleAction()
				{
					@Override
					public void callFunction()
					{
						((MultiplayerBattle)finalGameMode).map().setTerrain(terrain,finalx,finaly);
					}
				};
				mbattle.addAction(action);
			return;
			case ServerClient.ENDTURN:
				mbattle=((MultiplayerBattle)Main.currentMode);
				mbattle.nextTurn();
			return;
			case ServerClient.SPAWNUNIT:
				int x=in.readInt();
				int y=in.readInt();
				Unit unit=Main.readUnit(in);
				mbattle=((MultiplayerBattle)Main.currentMode);
				mbattle.map().addUnit(unit,x,y);
			return;
			case ServerClient.PURCHASEUNIT:
				boolean canbuy=in.readBoolean();
				if(canbuy)
				{
					int unitid=in.readInt();
					mbattle=((MultiplayerBattle)Main.currentMode);
					mbattle.spawnUnit(Unit.copy(Main.unitMap.get(unitid),mbattle.myTeam(),false),Main.currentMode.cursor());
					Main.closeMenu();
				}
				else
					Main.engine().requestFocus(Main.currentMenu());
			return;
			case ServerClient.UNITMOVE:case ServerClient.UNITUNITE:case ServerClient.UNITCAPTURE:
				mbattle=((MultiplayerBattle)Main.currentMode);
				Path path=readPath(in);
				unit=mbattle.map().unit(path.first().x,path.first().y);
				
				mbattle.moveUnitAlongPath(unit,path);
				
				mbattle.clearMovement();
				Main.closeMenu();
			return;
			case ServerClient.UNITATTACK:
				final Point attackerPos=new Point(in.readInt(),in.readInt());
				final Unit attacker=Main.readUnit(in);
				final Point defenderPos=new Point(in.readInt(),in.readInt());
				final Unit defender=Main.readUnit(in);
				action=new BattleAction()
				{
					@Override
					public void callFunction()
					{
						EngineAction engineAction=new EngineAction()
						{

							@Override
							public void init()
							{
								//empty
							}

							@Override
							public void callFunction()
							{
								
								((MultiplayerBattle)finalGameMode).map().setUnit(attacker,attackerPos.x,attackerPos.y);
								attacker.move(attackerPos.x*Main.TILESIZE,attackerPos.y*Main.TILESIZE);
								if(attacker.health()<=0)
									((MultiplayerBattle)finalGameMode).destroyUnit(attackerPos);
								System.out.println("defender: "+defender);
								((MultiplayerBattle)finalGameMode).map().setUnit(defender,defenderPos.x,defenderPos.y);
								defender.move(defenderPos.x*Main.TILESIZE,defenderPos.y*Main.TILESIZE);
								if(defender.health()<=0)
									((MultiplayerBattle)finalGameMode).destroyUnit(defenderPos);
							}

							@Override
							public boolean shouldEnd()
							{
								return true;
							}
						
						};
						Main.engine().addAction(engineAction);
					}
				};
				((MultiplayerBattle)finalGameMode).addAction(action);
			return;
			case ServerClient.UNITLOAD:
				mbattle=((MultiplayerBattle)Main.currentMode);
				final Unit finalUnitCargo=mbattle.map().unit(in.readInt(),in.readInt());
				final int cargoslot=in.readInt();
				final Unit finalUnit=mbattle.map().unit(in.readInt(),in.readInt());
				final boolean loaderenabled=finalUnit.enabled();
				action=new BattleAction()
				{
					@Override
					public void callFunction()
					{
						finalUnit.setCargo(finalUnitCargo,cargoslot);
						System.out.println("loader: "+loaderenabled);
						if(loaderenabled)
						{
							finalUnit.enabled(true);
						}
					}
				};
				mbattle.addAction(action);
			return;
			case ServerClient.UNITUNLOAD:
				mbattle=((MultiplayerBattle)Main.currentMode);
				final Point unloader=new Point(in.readInt(),in.readInt());
				final Unit unitholder=mbattle.map().unit(unloader.x,unloader.y);
				final int finalcargoslot=in.readInt();
				final Unit finalunit=unitholder.cargo(finalcargoslot);
				final Point finalpoint=new Point(in.readInt(),in.readInt());
				action=new BattleAction()
				{
					@Override
					public void callFunction()
					{
						MultiplayerBattle battle=((MultiplayerBattle)finalGameMode);
						battle.map().addUnit(finalunit,finalpoint.x,finalpoint.y);
						unitholder.setCargo(null,finalcargoslot);
						if(Team.sameTeam(battle.whosTurn(),battle.myTeam()))
						{
							System.out.println("checking for additional unload");
							ArrayList<String> actions=new ArrayList<String>();

							boolean canUnload=battle.canUnitUnload(unitholder,unloader);
							System.out.println("can unload: "+canUnload);
							System.out.println("units in unloader: "+unitholder.cargoCount());
							if(canUnload)
							{
								actions.add("Unload");
								actions.add("Cancel");
							}
							if(!actions.isEmpty())
							{
								battle.selectedUnit(unitholder);
								Path path=new Path(battle.map(),unitholder.movementType(),unitholder.movement());
								path.start(unloader.x,unloader.y);
								battle.path(path);
								System.out.println("additional unload!");
								Main.closeAllMenus();
								Main.openMenu(new ActionMenu(null,new Point(unloader.x*Main.TILESIZE,unloader.y*Main.TILESIZE),actions));
							}
						}
					}
				};
				mbattle.addAction(action);
			return;
			case ServerClient.UNITHIDE:
				mbattle=((MultiplayerBattle)Main.currentMode);
				path=readPath(in);
				unit=mbattle.map().unit(path.first().x,path.first().y);
				
				mbattle.moveUnitAlongPath(unit,path);
				
				unit.hidden(true);
				
				mbattle.clearMovement();
				Main.closeMenu();
			return;
			case ServerClient.UNITUNHIDE:
				mbattle=((MultiplayerBattle)Main.currentMode);
				path=readPath(in);
				unit=mbattle.map().unit(path.first().x,path.first().y);
				
				mbattle.moveUnitAlongPath(unit,path);
				
				unit.hidden(false);
				
				mbattle.clearMovement();
				Main.closeMenu();
			return;
		}
	}

	@Override
	public int maxMessageSize()
	{
		return 1024*10;//TODO check to see if this is too small?
	}
	
}
