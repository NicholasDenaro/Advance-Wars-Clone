package denaro.nick.wars.multiplayer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import denaro.nick.server.Client;
import denaro.nick.server.Message;
import denaro.nick.server.MyInputStream;
import denaro.nick.wars.Battle;
import denaro.nick.wars.BattleLobbyView;
import denaro.nick.wars.BattleView;
import denaro.nick.wars.GameModeMenu;
import denaro.nick.wars.Main;
import denaro.nick.wars.Team;

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

	@Override
	public void handleMessages(MyInputStream in, int messageid) throws IOException
	{
		switch(messageid)
		{
			case ServerClient.SESSIONS:
				int size=in.readInt();
				for(int i=0;i<size;i++)
					((GameModeMenu)Main.currentMode).addAction(in.readString());
				
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
				Main.engine().requestFocus(Main.currentMode);
				mbattle.teams(teams);
				mbattle.myTeam(lobby.player());
				mbattle.start();
				System.out.println("Started: "+((MultiplayerBattle)Main.currentMode).started());
			return;
		}
	}

	@Override
	public int maxMessageSize()
	{
		return 1024*10;//TODO check to see if this is too small?
	}
	
}
