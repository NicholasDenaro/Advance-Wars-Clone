package denaro.nick.wars.multiplayer;

import java.awt.Point;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Set;

import denaro.nick.server.Client;
import denaro.nick.server.Message;
import denaro.nick.server.MyInputStream;
import denaro.nick.wars.Battle;
import denaro.nick.wars.Main;
import denaro.nick.wars.Path;
import denaro.nick.wars.Team;
import denaro.nick.wars.Unit;

public class ServerClient extends Client
{

	public ServerClient(Socket socket, String name) throws IOException
	{
		super(socket);
		session=null;
		this.name=name;
	}

	public String name()
	{
		return(name);
	}
	
	public Path readPath(MyInputStream in)
	{
		int xpos=in.readInt();
		int ypos=in.readInt();
		Unit unit=session.battle().map().unit(xpos,ypos);
		int pathsize=in.readInt();
		Path path=new Path(session.battle().map(),unit.movementType(), unit.movement());
		path.start(in.readInt(),in.readInt());
		for(int i=0;i<pathsize-1;i++)
			path.addPoint(in.readInt(),in.readInt());
		
		return(path);
	}
	
	public void writePath(Message mes, Path path)
	{
		mes.addInt(path.first().x);
		mes.addInt(path.first().y);
		mes.addInt(path.points().size());
		for(int i=0;i<path.size();i++)
		{
			mes.addInt(path.points().get(i).x);
			mes.addInt(path.points().get(i).y);
		}
	}
	
	@Override
	public void handleMessages(MyInputStream in, int messageid) throws IOException
	{
		Message mes;
		switch(messageid)
		{
			case SESSIONS:
				Set<String> sessions=MainServer.sessions();
				mes=new Message(messageid);
				mes.addInt(sessions.size());
				for(String s:sessions)
					mes.addString(s);
				addMessage(mes);
				sendMessages();
			return;
			case NEWSESSION:
				String sesname=in.readString();
				
				int size=in.readInt();
				ByteBuffer buffer=ByteBuffer.allocate(size);
				buffer.put(in.readBytes(size));
				Battle battle=Main.loadBattle(null,buffer);
				
				if(!sesname.equalsIgnoreCase("back"))
					session=MainServer.addSession(sesname,battle);
				if(session!=null)
					session.addClient(this);
				boolean added=session!=null;
				System.out.println("added: "+added);
				System.out.println("session players: "+session.players());
				mes=new Message(messageid);
				mes.addBoolean(added);
				if(added)
				{
					System.out.println("added!");
					Message batmes=Main.saveBattle(battle);
					mes.addInt(batmes.size());
					mes.addMessage(batmes);
					
					mes.addString(name);
					mes.addInt(0);
				}
				addMessage(mes);
				sendMessages();
			return;
			case JOINSESSION:
				sesname=in.readString();
				mes=new Message(messageid);
				if((session=MainServer.session(sesname))!=null&&!session.isFull())
				{
					Battle bat=session.battle();
					Message batmes=Main.saveBattle(bat);
					mes.addInt(batmes.size());
					mes.addMessage(batmes);
					
					int index=session.firstOpenSlot();
					
					mes.addInt(index);
					
					size=session.size();
					mes.addInt(size);
					for(int i=0;i<size;i++)
					{
						mes.addString(session.player(i));
						mes.addBoolean(session.isReady(i));
					}
					mes.addString(name);
					
					addMessage(mes);
					sendMessages();
					
					session.sendMessage(new Message(PLAYERJOINEDSESSION).addInt(index).addString(name));
					
					session.addClient(this);
				}
			return;
			case PLAYERREADY:
				int commander=in.readInt();
				session.clientReady(this,commander);
				mes=new Message(messageid);
				int pindex=session.playerIndex(this);
				mes.addInt(pindex);
				mes.addBoolean(session.isReady(pindex));
				session.sendMessage(mes);
				session.checkIfReady();
			return;
			case LEAVESESSION:
				mes=new Message(messageid);
				mes.addInt(session.removeClient(this));
				session.sendMessage(mes);
			return;
			case ENDTURN:
				battle=session.battle();
				battle.nextTurn();
				System.out.println("turn: "+battle.turn());
				mes=new Message(messageid);
				session.sendMessage(mes);
				mes=new Message(UPDATEMAP);
				for(int a=0;a<session.battle().map().height();a++)
				{
					for(int i=0;i<session.battle().map().width();i++)
					{
						Main.writeTerrain(mes,session.battle().map().terrain(i,a));
					}
				}
				for(int a=0;a<session.battle().map().height();a++)
				{
					for(int i=0;i<session.battle().map().width();i++)
					{
						Main.writeUnit(mes,session.battle().map().unit(i,a));
					}
				}
				session.sendMessage(mes);
				
				mes=new Message(UPDATETEAM);
				mes.addInt(battle.teams().get(session.playerIndex(session.whosTurnClient())).funds());
				session.whosTurnClient().addMessage(mes);
				session.whosTurnClient().sendMessages();
			return;
			case PURCHASEUNIT:
				int unitid=in.readInt();
				Point spawnLocation=new Point(in.readInt(),in.readInt());
				int cost=Main.unitMap.get(unitid).cost();
				Team team=session.team(this);
				if(Team.sameTeam(session.battle().whosTurn(),team))
				{
					Unit unit=Unit.copy(Main.unitMap.get(unitid),team,false);
					mes=new Message(messageid);
					boolean bought=team.funds()>=cost;
					if(bought)
					{
						session.battle().spawnUnit(unit,spawnLocation);
						mes.addBoolean(true);
						mes.addInt(unitid);
					}
					else
					{
						mes.addBoolean(false);
					}
					addMessage(mes);
					sendMessages();
					if(bought)
					{
						Message spawnmes=new Message(SPAWNUNIT);
						spawnmes.addInt(spawnLocation.x);
						spawnmes.addInt(spawnLocation.y);
						Main.writeUnit(spawnmes,unit);
						session.sendMessage(spawnmes);
					}
				}
				else
				{
					System.out.println("?ERROR?: sent message during wrong turn.");
				}
			return;
			case UNITMOVE:case UNITUNITE:
				Path path=readPath(in);
				if(path.isValid())
				{
					Unit unit=session.battle().map().unit(path.first().x,path.first().y);
					session.battle().moveUnitAlongPath(unit,path);
					mes=new Message(messageid);
					writePath(mes,path);
					session.sendMessage(mes);
				}
			return;
			//case UNITUNITE:
			//return;
			case UNITATTACK:
				path=readPath(in);
				
				Point attackerPoint=new Point(path.first().x,path.first().y);
				Point defenderPoint=new Point(in.readInt(),in.readInt());
				if(path.isValid())
				{
					//movement message
					mes=new Message(UNITMOVE);
					writePath(mes,path);
					session.sendMessage(mes);
					
					//attack message
					mes=new Message(messageid);
					Unit attacker=session.battle().map().unit(attackerPoint.x,attackerPoint.y);
					Unit defender=session.battle().map().unit(defenderPoint.x,defenderPoint.y);
					session.battle().selectedUnit(attacker);
					session.battle().path(path);
					boolean attacked=session.battle().attackUnit(attackerPoint,defenderPoint);
					if(attacked)
					{
						mes.addInt(path.last().x);
						mes.addInt(path.last().y);
						Main.writeUnit(mes,attacker);
						
						mes.addInt(defenderPoint.x);
						mes.addInt(defenderPoint.y);
						Main.writeUnit(mes,defender);
					}
					else
					{
						//do nothing! wooo
					}
					session.sendMessage(mes);
				}
			return;
			case UNITCAPTURE:
				path=readPath(in);
				if(path.isValid())
				{
					Unit unit=session.battle().map().unit(path.first().x,path.first().y);
					session.battle().selectedUnit(unit);
					session.battle().path(path);
					if(session.battle().unitCaptureBuilding(unit,path.last()))
					{
						mes=new Message(messageid);
						writePath(mes,path);
						session.sendMessage(mes);
						mes=new Message(UPDATETERRAIN);
						mes.addInt(path.last().x);
						mes.addInt(path.last().y);
						Main.writeTerrain(mes,session.battle().map().terrain(path.last().x,path.last().y));
						session.sendMessage(mes);
					}
					else
					{
						mes=new Message(UNITMOVE);
						writePath(mes,path);
						session.sendMessage(mes);
					}
				}
			return;
			case UNITLOAD:
				path=readPath(in);
				session.battle().cursor(new Point(in.readInt(),in.readInt()));
				if(path.isValid())
				{
					Unit unit=session.battle().map().unit(path.first().x,path.first().y);
					Unit holder=session.battle().map().unit(path.last().x,path.last().y);
					boolean holderenabled=holder.enabled();
					session.battle().selectedUnit(unit);
					session.battle().path(path);
					int cargoslot=-1;
					if(session.battle().moveUnit())
					{
						cargoslot=holder.addCargo(unit);
						if(holderenabled)
							unit.enabled(true);
						mes=new Message(messageid);
						mes.addInt(path.first().x);
						mes.addInt(path.first().y);
						mes.addInt(cargoslot);
						mes.addInt(path.last().x);
						mes.addInt(path.last().y);
						session.sendMessage(mes);
					}
					mes=new Message(UNITMOVE);
					writePath(mes,path);
					session.sendMessage(mes);
				}
			return;
			case UNITUNLOAD:
				path=readPath(in);
				int cargoslot=in.readInt();
				Point empty=new Point(in.readInt(),in.readInt());
				if(path.isValid())
				{
					Unit unit=session.battle().map().unit(path.first().x,path.first().y);
					session.battle().selectedUnit(unit);
					session.battle().path(path);
					
					mes=new Message(UNITMOVE);
					writePath(mes,path);
					session.sendMessage(mes);
					
					if(session.battle().unloadUnit(cargoslot,empty))
					{
						mes=new Message(messageid);
						mes.addInt(path.last().x);
						mes.addInt(path.last().y);
						mes.addInt(cargoslot);
						mes.addInt(empty.x);
						mes.addInt(empty.y);
						session.sendMessage(mes);
					}
					else
					{
						mes=new Message(UNITMOVE);
						writePath(mes,path);
						session.sendMessage(mes);
					}
				}
			return;
			case UNITHIDE:
				path=readPath(in);
				if(path.isValid())
				{
					Unit unit=session.battle().map().unit(path.first().x,path.first().y);
					if(session.battle().moveUnitAlongPath(unit,path))
					{
						unit.hidden(true);
						mes=new Message(messageid);
						writePath(mes,path);
						session.sendMessage(mes);
					}
					else
					{
						mes=new Message(UNITMOVE);
						writePath(mes,path);
						session.sendMessage(mes);
					}
				}
			return;
			case UNITUNHIDE:
				path=readPath(in);
				if(path.isValid())
				{
					Unit unit=session.battle().map().unit(path.first().x,path.first().y);
					if(session.battle().moveUnitAlongPath(unit,path))
					{
						unit.hidden(false);
						mes=new Message(messageid);
						writePath(mes,path);
						session.sendMessage(mes);
					}
					else
					{
						mes=new Message(UNITMOVE);
						writePath(mes,path);
						session.sendMessage(mes);
					}
				}
			return;
		}
	}
	
	private BattleSession session;
	
	
	public static final int SESSIONS=5;
	public static final int NEWSESSION=6;
	public static final int JOINSESSION=7;
	public static final int PLAYERJOINEDSESSION=8;
	public static final int PLAYERREADY=9;
	public static final int LEAVESESSION=10;
	public static final int STARTSESSION=11;
	
	public static final int UPDATETEAM=14;
	public static final int UPDATEMAP=15;
	public static final int UPDATETERRAIN=16;
	public static final int ENDTURN=17;
	public static final int SPAWNUNIT=18;
	public static final int PURCHASEUNIT=19;
	public static final int UNITMOVE=20;
	public static final int UNITUNITE=21;
	public static final int UNITATTACK=22;
	public static final int UNITCAPTURE=23;
	public static final int UNITLOAD=24;
	public static final int UNITUNLOAD=25;
	public static final int UNITHIDE=26;
	public static final int UNITUNHIDE=27;

	@Override
	public int maxMessageSize()
	{
		return(1024*10);
	}
	
	private String name;
}
