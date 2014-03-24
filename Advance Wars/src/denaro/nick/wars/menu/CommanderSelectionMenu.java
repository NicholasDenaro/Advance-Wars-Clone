package denaro.nick.wars.menu;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import denaro.nick.core.ControllerEvent;
import denaro.nick.core.GameView2D;
import denaro.nick.server.Message;
import denaro.nick.wars.Battle;
import denaro.nick.wars.BattleSettings;
import denaro.nick.wars.Commander;
import denaro.nick.wars.Main;
import denaro.nick.wars.Map;
import denaro.nick.wars.multiplayer.BattleLobby;
import denaro.nick.wars.multiplayer.MultiplayerBattle;
import denaro.nick.wars.multiplayer.ServerClient;

public class CommanderSelectionMenu extends Menu
{
	
	public CommanderSelectionMenu(Menu child, Point point, Map map, BattleSettings settings)
	{
		super(child,point);
		commanders=new int[map.teams().size()];
		this.map=map;
		this.settings=settings;
		cursor(new Point(0,0));
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
			{
				commanders[cursor().y]=(commanders[cursor().y]-1+Main.commanderMap.size())%Main.commanderMap.size();
			}
			if(event.code()==Main.RIGHT)
			{
				commanders[cursor().y]=(commanders[cursor().y]+1+Main.commanderMap.size())%Main.commanderMap.size();
			}
			
			if(event.code()==Main.BACK)
			{
				Main.closeMenu();
			}
			
			if(event.code()==Main.START)
			{
				Main.closeMenu();
				Main.closeMenu();
				ArrayList<Commander> coms=new ArrayList<Commander>();
				for(int i=0;i<commanders.length;i++)
				{
					coms.add(Main.commanderMap.get(commanders[i]));
				}
				
				if(Main.currentMode instanceof BattleLobby)
				{
					//TODO send message to lock in commander
				}
				else
				{
					Battle battle=Main.createBattle(map,settings,coms);
					Main.startBattle(battle);
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
		if(ke.getKeyCode()==KeyEvent.VK_UP)
			moveCursorUp();
		if(ke.getKeyCode()==KeyEvent.VK_DOWN)
			moveCursorDown();
		if(ke.getKeyCode()==KeyEvent.VK_LEFT)
		{
			commanders[cursor().y]=(commanders[cursor().y]-1+Main.commanderMap.size())%Main.commanderMap.size();
		}
		if(ke.getKeyCode()==KeyEvent.VK_RIGHT)
		{
			commanders[cursor().y]=(commanders[cursor().y]+1+Main.commanderMap.size())%Main.commanderMap.size();
		}
		
		if(ke.getKeyCode()==KeyEvent.VK_Z)
		{
			Main.closeMenu();
		}
		
		if(ke.getKeyCode()==KeyEvent.VK_ENTER)
		{
			Main.closeMenu();
			Main.closeMenu();
			ArrayList<Commander> coms=new ArrayList<Commander>();
			for(int i=0;i<commanders.length;i++)
			{
				coms.add(Main.commanderMap.get(commanders[i]));
			}
			
			if(Main.currentMode instanceof BattleLobby)
			{
				//TODO send message to lock in commander
			}
			else
			{
				Battle battle=Main.createBattle(map,settings,coms);
				Main.startBattle(battle);
			}
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
		return commanders.length;
	}
	
	@Override
	public Image image()
	{
		GameView2D view=(GameView2D)Main.engine().view();
		BufferedImage image=new BufferedImage(view.width(),view.height(),BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=image.createGraphics();
		
		FontMetrics fm=g.getFontMetrics();
		
		g.setColor(Color.white);
		
		g.setColor(Color.black);
		int index=cursor().y;

		String action="";

		for(int i=0;i<commanders.length;i++)
		{
			action=Main.commanderMap.get(commanders[i]).name();
			if(cursor().y==i)
				action=">"+action+"<";
			g.drawString(action,view.width()/2-fm.stringWidth(action)/2,view.height()/4+fm.getHeight()*(i+1));
		}

		
		return(image);
	}
	
	
	private int[] commanders;
	private Map map;
	private BattleSettings settings;
}
