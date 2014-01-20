package denaro.nick.wars.multiplayer;

import denaro.nick.server.Message;
import denaro.nick.wars.Battle;

public class BattleSession
{
	public BattleSession(String name, Battle battle)
	{
		this.name=name;
		this.battle=battle;
		this.size=battle.map().teams().size();
		System.out.println("session size: "+this.size);
		clients=new ServerClient[size];
		commanders=new int[size];
		for(int i=0;i<commanders.length;i++)
			commanders[i]=-1;
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
	
	public int firstOpenSlot()
	{
		for(int i=0;i<clients.length;i++)
			if(clients[i]==null)
				return(i);
		return(-1);
	}
	
	public int size()
	{
		return(size);
	}
	
	public int players()
	{
		int count=0;
		for(int i=0;i<clients.length;i++)
			if(clients[i]!=null)
				count++;
		return(count);
	}
	
	public String player(int index)
	{
		if(clients[index]!=null)
			return(clients[index].name());
		else
			return(null);
	}
	
	public int commanders(int index)
	{
		return(commanders[index]);
	}
	
	public boolean isEmpty()
	{
		return(players()==0);
	}
	
	public boolean isFull()
	{
		return(players()==size);
	}
	
	public boolean allReady()
	{
		for(int i=0;i<commanders.length;i++)
			if(commanders[i]==-1)
				return(false);
		return(true);
	}
	
	public int playerIndex(ServerClient client)
	{
		for(int i=0;i<clients.length;i++)
		{
			if(clients[i]==client)
			{
				return(i);
			}
		}
		return(-1);
	}
	
	public boolean isReady(ServerClient client)
	{
		for(int i=0;i<clients.length;i++)
		{
			if(clients[i]==client)
			{
				return(commanders[i]!=-1);
			}
		}
		return(false);
	}
	
	public boolean isReady(int index)
	{
		return(commanders[index]!=-1);
	}
	
	public void clientReady(ServerClient client, int commander)
	{
		for(int i=0;i<clients.length;i++)
		{
			if(clients[i]==client)
			{
				commanders[i]=commander;
				break;
			}
		}
	}
	
	public void checkIfReady()
	{
		if(allReady())
		{
			System.out.println("starting!");
			Message message=new Message(ServerClient.STARTSESSION);
			message.addInt(commanders.length);
			for(int i=0;i<commanders.length;i++)
			{
				message.addInt(i);
				message.addInt(commanders[i]);
			}
			sendMessage(message);
			playing=true;
		}
	}
	
	public boolean addClient(ServerClient client)
	{
		if(playing)
			return(false);
		if(players()>=size)
			return(false);
		
		for(int i=0;i<clients.length;i++)
		{
			if(clients[i]==null)
			{
				clients[i]=client;
				commanders[i]=-1;
				break;
			}
		}
		
		
		System.out.println("isFull? "+isFull());
		
		return(true);
	}
	
	public int removeClient(ServerClient client)
	{
		int index=-1;
		for(int i=0;i<clients.length;i++)
			if(clients[i]==client)
			{
				clients[i]=null;
				commanders[i]=-1;
				index=i;
				break;
			}
		if(isEmpty())
			MainServer.removeSession(name);
		return(index);
	}
	
	synchronized public void sendMessage(Message message)
	{
		System.out.println("message should be sent!");
		for(int i=0;i<clients.length;i++)
		{
			if(clients[i]!=null)
			{
				clients[i].addMessage(message);
				clients[i].sendMessages();
			}
		}
	}
	
	private String name;
	private int size;
	private ServerClient[] clients;
	private int[] commanders;
	private boolean playing;
	
	private Battle battle;
}
