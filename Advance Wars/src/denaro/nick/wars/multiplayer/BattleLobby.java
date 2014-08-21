package denaro.nick.wars.multiplayer;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import denaro.nick.core.controller.ControllerEvent;
import denaro.nick.server.Message;
import denaro.nick.wars.GameMode;
import denaro.nick.wars.Main;

public class BattleLobby extends GameMode
{
	public BattleLobby(MultiplayerBattle battle, int player)
	{
		this.battle=battle;
		this.player=player;
		players=new String[battle.map().teams().size()];
		locks=new boolean[players.length];
		cursor(new Point(0,0));
		locked=false;
		commander=0;
	}
	
	public MultiplayerBattle battle()
	{
		return(battle);
	}
	
	public int size()
	{
		return(players.length);
	}
	
	public void addPlayer(int index, String player)
	{
		System.out.println("adding player: "+index+" player: "+player);
		players[index]=player;
	}
	
	public void lockPlayer(int index)
	{
		locks[index]=true;
	}
	
	public void unlockPlayer(int index)
	{
		locks[index]=false;
	}
	
	public void removePlayer(int index)
	{
		players[index]=null;
	}
	
	public int player()
	{
		return(player);
	}
	
	public int commander()
	{
		return(commander);
	}
	
	public String players(int index)
	{
		return(players[index]);
	}
	
	public boolean isLocked(int index)
	{
		return(locks[index]);
	}
	
	@Override
	public void moveCursorLeft()
	{
		commander--;
		commander=(commander+Main.commanderMap.size())%Main.commanderMap.size();
	}
	
	@Override
	public void moveCursorRight()
	{
		commander++;
		commander%=Main.commanderMap.size();
	}
	
	@Override
	public void actionPerformed(ControllerEvent event)
	{
		if(event.action()==ControllerEvent.PRESSED)
		{
			if(event.code()==Main.LEFT)
				moveCursorLeft();
			if(event.code()==Main.RIGHT)
				moveCursorRight();
			
			if(event.code()==Main.ACTION||event.code()==Main.START)
			{
				locked=true;
				Main.client.addMessage(new Message(ServerClient.PLAYERREADY).addInt(commander));
				Main.client.sendMessages();
			}
			
			if(event.code()==Main.BACK)
			{
				if(!locked)
				{
					Main.gotoMainMenu();
					Message message=new Message(ServerClient.LEAVESESSION);
					Main.client.addMessage(message);
					Main.client.sendMessages();
				}
				else
				{
					locked=false;
					Main.client.addMessage(new Message(ServerClient.PLAYERREADY).addInt(-1));
					Main.client.sendMessages();
				}
			}
		}
		else if(event.action()==ControllerEvent.RELEASED)
		{
			
		}
	}
	
	/*@Override
	public void keyPressed(KeyEvent ke)
	{	
		if(ke.getKeyCode()==KeyEvent.VK_LEFT)
			moveCursorLeft();
		if(ke.getKeyCode()==KeyEvent.VK_RIGHT)
			moveCursorRight();
		
		if(ke.getKeyCode()==KeyEvent.VK_X||ke.getKeyCode()==KeyEvent.VK_ENTER)
		{
			locked=true;
			Main.client.addMessage(new Message(ServerClient.PLAYERREADY).addInt(commander));
			Main.client.sendMessages();
		}
		
		if(ke.getKeyCode()==KeyEvent.VK_Z)
		{
			if(!locked)
			{
				Main.gotoMainMenu();
				Message message=new Message(ServerClient.LEAVESESSION);
				Main.client.addMessage(message);
				Main.client.sendMessages();
			}
			else
			{
				locked=false;
				Main.client.addMessage(new Message(ServerClient.PLAYERREADY).addInt(-1));
				Main.client.sendMessages();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent ke)
	{
	}*/

	@Override
	public int columns()
	{
		return 0;
	}

	@Override
	public int rows()
	{
		return 0;
	}
	
	private MultiplayerBattle battle;
	private String[] players;
	private boolean[] locks;
	private int player;
	private boolean locked;
	private int commander;
}
