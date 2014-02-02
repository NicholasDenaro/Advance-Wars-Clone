package denaro.nick.wars.multiplayer;

import java.io.IOException;
import java.util.ArrayList;

import denaro.nick.server.Message;
import denaro.nick.wars.Battle;
import denaro.nick.wars.BattleListener;
import denaro.nick.wars.Main;
import denaro.nick.wars.Team;

public class BattleSession implements BattleListener
{
	public BattleSession(String name, Battle battle)
	{
		this.name=name;
		this.battle=battle;
		battle.addBattleListener(this);
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
	
	public Team team(ServerClient client)
	{
		for(int i=0;i<clients.length;i++)
			if(clients[i]==client)
				return(battle.teams()[i]);
		System.out.println("ERROR: should return a team!");
		return(null);
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
	
	public ServerClient whosTurnClient()
	{
		return(clients[battle.whosTurn().id()]);
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
			Team[] teams=new Team[commanders.length];
			for(int i=0;i<commanders.length;i++)
			{
				teams[i]=Team.copy(Main.teamMap.get(i),Main.commanderMap.get(commanders[i]));
			}
			battle.teams(teams);
			Main.fixTeams(battle);
			battle.start();
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

	@Override
	public void teamLoses(Team team)
	{
		Message message=new Message(ServerClient.TEAMLOSES);
		message.addInt(team.id());
		sendMessage(message);
		
		message=new Message(ServerClient.UPDATEMAP);
		try
		{
			for(int a=0;a<battle().map().height();a++)
			{
				for(int i=0;i<battle().map().width();i++)
				{
					Main.writeTerrain(message,battle().map().terrain(i,a));
				}
			}
			for(int a=0;a<battle().map().height();a++)
			{
				for(int i=0;i<battle().map().width();i++)
				{
					Main.writeUnit(message,battle().map().unit(i,a));
				}
			}
			sendMessage(message);
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public void battleEnd()
	{
		Message message=new Message(ServerClient.ENDBATTLE);
		sendMessage(message);
	}
}
