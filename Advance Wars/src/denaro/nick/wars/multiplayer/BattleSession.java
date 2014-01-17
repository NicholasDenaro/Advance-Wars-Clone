package denaro.nick.wars.multiplayer;

import java.util.ArrayList;

import denaro.nick.server.Message;
import denaro.nick.wars.Battle;

public class BattleSession
{
	public BattleSession(String name, Battle battle)
	{
		this.name=name;
		this.battle=battle;
		size=battle.teams().size();
		clients=new ArrayList<ServerClient>();
		playing=false;
	}
	
	public Battle battle()
	{
		return(battle);
	}
	
	public String name()
	{
		return(name);
	}
	
	public int players()
	{
		return(clients.size());
	}
	
	public boolean isEmpty()
	{
		return(players()==0);
	}
	
	public boolean isFull()
	{
		return(players()==size);
	}
	
	public boolean addClient(ServerClient client)
	{
		if(playing)
			return(false);
		if(clients.size()>=size)
			return(false);
		
		clients.add(client);
		
		if(isFull())
		{
			//TODO tell all clients to start map!
		}
		return(true);
	}
	
	public void removeClient(ServerClient client)
	{
		clients.remove(client);
		if(isEmpty())
			MainServer.removeSession(name);
	}
	
	synchronized public void sendMessage(Message message)
	{
		for(ServerClient client:clients)
		{
			client.addMessage(message);
			client.sendMessages();
		}
	}
	
	private String name;
	private int size;
	private ArrayList<ServerClient> clients;
	private boolean playing;
	
	private Battle battle;
}
