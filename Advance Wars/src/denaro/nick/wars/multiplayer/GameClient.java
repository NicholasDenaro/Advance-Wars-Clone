package denaro.nick.wars.multiplayer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

import denaro.nick.server.Client;
import denaro.nick.server.MyInputStream;
import denaro.nick.wars.Battle;
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
			case ServerClient.NEWSESSION:
				boolean success=in.readBoolean();
				System.out.println("successfully created a new battle session.");
			return;
		}
	}

	@Override
	public int maxMessageSize()
	{
		return 1024;//TODO check to see if this is too small?
	}
	
}
