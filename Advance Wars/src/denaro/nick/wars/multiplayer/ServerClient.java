package denaro.nick.wars.multiplayer;

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
import denaro.nick.wars.Unit;

public class ServerClient extends Client
{

	public ServerClient(Socket socket) throws IOException
	{
		super(socket);
		session=null;
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
				String name=in.readString();
				int size=in.readInt();
				ByteBuffer buffer=ByteBuffer.allocate(size);
				buffer.put(in.readBytes(size));
				Battle battle=Main.loadBattle(null,buffer);
				
				if(!name.equalsIgnoreCase("back"))
					session=MainServer.addSession(name,battle);
				boolean added=session!=null;
				mes=new Message(messageid);
				mes.addBoolean(added);
				addMessage(mes);
				sendMessages();
			return;
			case JOINSESSION:
				String sesname=in.readString();
				mes=new Message(messageid);
				if((session=MainServer.session(sesname))!=null)
				{
					Battle bat=session.battle();
					Message batmes=Main.saveBattle(bat);
					mes.addMessage(batmes);
					Main.client.addMessage(mes);
					Main.client.sendMessages();
					
					session.addClient(this);
				}
			return;
			case LEAVESESSION:
				session.removeClient(this);
				mes=new Message(messageid);
				addMessage(mes);
				sendMessages();
			return;
			case UNITMOVE:
				int xpos=in.readInt();
				int ypos=in.readInt();
				Unit unit=session.battle().map().unit(xpos,ypos);
				int pathsize=in.readInt();
				Path path=new Path(unit.movementType(), unit.movement());
				path.start(in.readInt(),in.readInt());
				for(int i=0;i<pathsize-1;i++)
					path.addPoint(in.readInt(),in.readInt());
				if(path.isValid(session.battle().map()))
				{
					mes=new Message(messageid);
					mes.addInt(xpos);
					mes.addInt(ypos);
					mes.addInt(path.points().size());
					for(int i=0;i<pathsize;i++)
					{
						mes.addInt(path.points().get(i).x);
						mes.addInt(path.points().get(i).y);
					}
					session.sendMessage(mes);
				}
				else
				{
					mes=new Message(messageid);
					mes.addBoolean(false);
					addMessage(mes);
					sendMessages();
				}
			return;
			case UNITUNITE:
			return;
		}
	}
	
	private BattleSession session;
	
	
	public static final int SESSIONS=1;
	public static final int NEWSESSION=2;
	public static final int JOINSESSION=3;
	public static final int LEAVESESSION=4;
	public static final int UNITMOVE=5;
	public static final int UNITUNITE=6;
	public static final int UNITATTACK=7;
	public static final int UNITCAPTURE=8;
	public static final int UNITLOAD=9;
	public static final int UNITUNLOAD=10;

	@Override
	public int maxMessageSize()
	{
		return(1024*10);
	}
}
