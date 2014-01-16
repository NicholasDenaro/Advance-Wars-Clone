package denaro.nick.wars.multiplayer;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

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
		return(new ServerClient(socket));
	}
	
	synchronized public static BattleSession addSession(String name, Battle battle)
	{
		if(sessions.containsKey(name))
			return(null);
		BattleSession session=new BattleSession(name,battle);
		sessions.put(name,session);
		return(session);
	}
	
	public static final String hostname=null;//loopback!
	public static final int port=7589;
	
	private static HashMap<String,BattleSession> sessions;
}