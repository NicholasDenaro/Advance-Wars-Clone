package denaro.nick.wars.menu;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import denaro.nick.core.controller.ControllerEvent;
import denaro.nick.core.view.GameView2D;
import denaro.nick.server.Message;
import denaro.nick.wars.BattleSettings;
import denaro.nick.wars.GameModeSelector;
import denaro.nick.wars.Main;
import denaro.nick.wars.Map;
import denaro.nick.wars.GameModeSelector.SelectionState;
import denaro.nick.wars.multiplayer.MultiplayerBattle;
import denaro.nick.wars.multiplayer.ServerClient;
import denaro.nick.wars.view.GameModeMenuView;

public class MapOptionsMenu extends Menu
{
	
	public MapOptionsMenu(Menu child, Point point, String mapName)
	{
		super(child,point);
		cursor(new Point(0,0));
		settings=new BattleSettings();
		this.map=Main.loadMap(mapName);
	}
	
	@Override
	public void moveCursorLeft()
	{
		if(cursor().y==0)
		{
			settings.startingFunds(settings.startingFunds()-1000);
			if(settings.startingFunds()<0)
				settings.startingFunds(0);
		}
		if(cursor().y==1)
		{
			settings.fundsPerTurn(settings.fundsPerTurn()-100);
			if(settings.fundsPerTurn()<0)
				settings.fundsPerTurn(0);
		}
		if(cursor().y==2)
		{
			settings.fogOfWar(true);
		}
		if(cursor().y==3)
		{
			settings.weather(settings.weather()-1);
			if(settings.weather()<0)
				settings.weather(0);
		}
	}
	
	@Override
	public void moveCursorRight()
	{
		if(cursor().y==0)
		{
			settings.startingFunds(settings.startingFunds()+1000);
		}
		if(cursor().y==1)
		{
			settings.fundsPerTurn(settings.fundsPerTurn()+100);
		}
		if(cursor().y==2)
		{
			settings.fogOfWar(false);
		}
		if(cursor().y==3)
		{
			settings.weather(settings.weather()+1);
			if(settings.weather()>=Main.weatherMap.size())
				settings.weather(settings.weather()-1);
		}
	}
	
	@Override
	public void actionPerformed(ControllerEvent event)
	{
		if(event.action()==ControllerEvent.PRESSED)
		{
			if(event.code()==Main.UP)
				moveCursorUp();
			if(event.code()==Main.DOWN)
				moveCursorDown();
			if(event.code()==Main.LEFT)
				moveCursorLeft();
			if(event.code()==Main.RIGHT)
				moveCursorRight();
			
			if(event.code()==Main.ACTION)
			{
				if(cursor().y==4)
				{
					Main.closeMenu();
					GameModeMenuView view=(GameModeMenuView)Main.engine().view();
					Main.openMenu(new MinimapMenu(null,new Point(view.width()/2,view.height()/3)));
					Main.engine().requestFocus(Main.currentMode);
				}
				if(cursor().y==5)
				{
					if(((GameModeSelector)Main.currentMode).previousState()==GameModeSelector.SelectionState.MULTIPLAYER)
					{
						MultiplayerBattle battle=Main.createMultiplayerBattle(map,settings);
						Message mes=new Message(ServerClient.NEWSESSION);
						System.out.print("Name the session: ");
						String ses=Main.getInput();
						mes.addString(ses);
						Message bat=Main.saveBattle(battle);
						mes.addInt(bat.size());
						mes.addMessage(bat);
						Main.client.addMessage(mes);
						Main.client.sendMessages();
					}
					else
					{
						CommanderSelectionMenu comMenu=new CommanderSelectionMenu(null,new Point(0,0),map,settings);
						Main.openMenu(comMenu);
					}
				}
			}
			
			if(event.code()==Main.BACK)
			{
				Main.closeMenu();
				GameModeMenuView view=(GameModeMenuView)Main.engine().view();
				Main.openMenu(new MinimapMenu(null,new Point(view.width()/2,view.height()/3)));
				Main.engine().requestFocus(Main.currentMode);
			}
		}
		else if(event.action()==ControllerEvent.RELEASED)
		{
			
		}
	}
	
	/*@Override
	public void keyPressed(KeyEvent ke)
	{
		if(ke.getKeyCode()==KeyEvent.VK_UP)
			moveCursorUp();
		if(ke.getKeyCode()==KeyEvent.VK_DOWN)
			moveCursorDown();
		if(ke.getKeyCode()==KeyEvent.VK_LEFT)
			moveCursorLeft();
		if(ke.getKeyCode()==KeyEvent.VK_RIGHT)
			moveCursorRight();
		
		if(ke.getKeyCode()==KeyEvent.VK_X)
		{
			if(cursor().y==4)
			{
				Main.closeMenu();
				GameModeMenuView view=(GameModeMenuView)Main.engine().view();
				Main.openMenu(new MinimapMenu(null,new Point(view.width()/2,view.height()/3)));
				Main.engine().requestFocus(Main.currentMode);
			}
			if(cursor().y==5)
			{
				if(((GameModeSelector)Main.currentMode).previousState()==GameModeSelector.SelectionState.MULTIPLAYER)
				{
					MultiplayerBattle battle=Main.createMultiplayerBattle(map,settings);
					Message mes=new Message(ServerClient.NEWSESSION);
					System.out.print("Name the session: ");
					String ses=Main.getInput();
					mes.addString(ses);
					Message bat=Main.saveBattle(battle);
					mes.addInt(bat.size());
					mes.addMessage(bat);
					Main.client.addMessage(mes);
					Main.client.sendMessages();
				}
				else
				{
					CommanderSelectionMenu comMenu=new CommanderSelectionMenu(null,new Point(0,0),map,settings);
					Main.openMenu(comMenu);
				}
			}
		}
		
		if(ke.getKeyCode()==KeyEvent.VK_Z)
		{
			Main.closeMenu();
			GameModeMenuView view=(GameModeMenuView)Main.engine().view();
			Main.openMenu(new MinimapMenu(null,new Point(view.width()/2,view.height()/3)));
			Main.engine().requestFocus(Main.currentMode);
		}
	}*/
	
	@Override
	public int columns()
	{
		return 0;
	}
	
	@Override
	public int rows()
	{
		return(6);
	}
	
	@Override
	public Image image()
	{
		GameView2D view=(GameView2D)Main.engine().view();
		BufferedImage image=new BufferedImage(view.width(),view.height(),BufferedImage.TYPE_INT_ARGB);
		if(child()!=null)
			return(image);
		Graphics2D g=image.createGraphics();
		
		FontMetrics fm=g.getFontMetrics();
		
		g.setColor(Color.white);
		
		g.setColor(Color.black);
		int index=cursor().y;

		String action="";
		action="Starting Funds: "+settings.startingFunds();
		if(index==0)
			action=">"+action+"<";
		g.drawString(action,view.width()/2-fm.stringWidth(action)/2,view.height()/4+fm.getHeight()*1);
		
		action="Funds per Building: "+settings.fundsPerTurn();
		if(index==1)
			action=">"+action+"<";
		g.drawString(action,view.width()/2-fm.stringWidth(action)/2,view.height()/4+fm.getHeight()*2);
		
		action="Fog: "+(settings.fogOfWar()?"(":"")+"On"+(settings.fogOfWar()?")":"")+"|"+(settings.fogOfWar()?"":"(")+"Off"+(settings.fogOfWar()?"":")");
		if(index==2)
			action=">"+action+"<";
		g.drawString(action,view.width()/2-fm.stringWidth(action)/2,view.height()/4+fm.getHeight()*3);
		
		action="Weather: "+Main.weatherMap.get(settings.weather()).name();
		if(index==3)
			action=">"+action+"<";
		g.drawString(action,view.width()/2-fm.stringWidth(action)/2,view.height()/4+fm.getHeight()*4);
		
		action="Back";
		if(index==4)
			action=">"+action+"<";
		g.drawString(action,view.width()/2-fm.stringWidth(action)/2,view.height()/4+fm.getHeight()*5);
		
		action="Start";
		if(index==5)
			action=">"+action+"<";
		g.drawString(action,view.width()/2-fm.stringWidth(action)/2,view.height()/4+fm.getHeight()*6);
		
		return(image);
	}
	
	private BattleSettings settings;
	private Map map;
}
