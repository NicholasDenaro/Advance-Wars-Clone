package denaro.nick.wars.multiplayer;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import denaro.nick.server.Client;
import denaro.nick.server.Server;
import denaro.nick.wars.Battle;
import denaro.nick.wars.Main;

public class MainServer extends Server
{
	public MainServer(String hostname,int port) throws IOException
	{
		super(hostname,port);
		
		Main.loadAssets();
	}

	public static void main(String[] args)
	{
		try
		{
			sessions=new HashMap<String,BattleSession>();
			MainServer server=new MainServer(hostname,port);
			server.start();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public Client newClient(Socket socket) throws IOException
	{
		return(new ServerClient(socket,"guest_"+(guestid++)));
	}
	
	synchronized public static BattleSession addSession(String name, Battle battle)
	{
		if(sessions.containsKey(name))
			return(null);
		BattleSession session=new BattleSession(name,battle);
		sessions.put(name,session);
		return(session);
	}
	
	synchronized public static void removeSession(String name)
	{
		sessions.remove(name);
	}
	
	public static Set<String> sessions()
	{
		return(sessions.keySet());
	}
	
	public static BattleSession session(String name)
	{
		return(sessions.get(name));
	}
	
	public static final String hostname="localhost";//loopback!
	//public static final String hostname=null;
	public static final int port=7589;
	
	private static HashMap<String,BattleSession> sessions;
	
	private static int guestid=1;
}
