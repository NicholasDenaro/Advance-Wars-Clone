package denaro.nick.wars.multiplayer;

import java.util.ArrayList;

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
	
	public int players()
	{
		return(clients.size());
	}
	
	public boolean addClient(ServerClient client)
	{
		if(playing)
			return(false);
		if(clients.size()>=size)
			return(false);
		
		clients.add(client);
		return(true);
	}
	
	private String name;
	private int size;
	private ArrayList<ServerClient> clients;
	private boolean playing;
	
	private Battle battle;
}
