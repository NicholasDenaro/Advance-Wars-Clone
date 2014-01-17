package denaro.nick.wars.multiplayer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import denaro.nick.server.Client;
import denaro.nick.server.MyInputStream;
import denaro.nick.wars.Battle;
import denaro.nick.wars.GameModeMenu;
import denaro.nick.wars.Main;

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
				System.out.println("successfully created a new battle session.");
			return;
			case ServerClient.JOINSESSION:
				size=in.readInt();
				ByteBuffer buffer=ByteBuffer.allocate(size);
				buffer.put(in.readBytes(size));
				Battle battle=Main.loadBattle(null,buffer);
				Main.startBattle((MultiplayerBattle)battle);
			return;
			case ServerClient.LEAVESESSION:
				((GameModeMenu)Main.currentMode).changeState(GameModeMenu.SelectionState.MAIN);
			return;
		}
	}

	@Override
	public int maxMessageSize()
	{
		return 1024*10;//TODO check to see if this is too small?
	}
	
}
