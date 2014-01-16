package denaro.nick.wars.multiplayer;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

import denaro.nick.server.Client;
import denaro.nick.server.Message;
import denaro.nick.server.MyInputStream;
import denaro.nick.wars.Battle;
import denaro.nick.wars.Main;

public class ServerClient extends Client
{

	public ServerClient(Socket socket) throws IOException
	{
		super(socket);
	}

	@Override
	public void handleMessages(MyInputStream in, int messageid) throws IOException
	{
		switch(messageid)
		{
			case NEWSESSION:
				String name=in.readString();
				int size=in.readInt();
				ByteBuffer buffer=ByteBuffer.allocate(size);
				buffer.put(in.readBytes(size));
				Battle battle=Main.loadBattle(null,buffer);
				
				boolean added=MainServer.addSession(name,battle)!=null;
				System.out.println("added session: "+added);
				Message mes=new Message(messageid);
				mes.addBoolean(added);
				addMessage(mes);
				sendMessages();
			return;
		}
	}
	
	public static final int NEWSESSION=3;

	@Override
	public int maxMessageSize()
	{
		return(1024);
	}
}
