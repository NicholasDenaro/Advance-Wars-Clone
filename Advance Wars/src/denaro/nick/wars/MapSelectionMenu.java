package denaro.nick.wars;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import denaro.nick.core.GameView2D;
import denaro.nick.server.Message;
import denaro.nick.wars.multiplayer.ServerClient;

public class MapSelectionMenu extends Menu
{
	
	public MapSelectionMenu(Menu child, Point point, String mapName)
	{
		super(child,point);
		cursor(new Point(0,0));
		settings=new BattleSettings();
		this.map=Main.loadMap(mapName);
		chooseCommanders=true;
		commanders=new ArrayList<Integer>();
		for(int i=0;i<map.teams().size();i++)
		{
			commanders.add(0);
		}
	}
	
	@Override
	public void moveCursorLeft()
	{
		if(!chooseCommanders)
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
		else
		{
			int id=commanders.get(cursor().y);
			id--;
			id=(id+Main.commanderMap.size())%Main.commanderMap.size();
			commanders.set(cursor().y,id);
		}
	}
	
	@Override
	public void moveCursorRight()
	{
		if(!chooseCommanders)
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
		else
		{
			int id=commanders.get(cursor().y);
			id++;
			id%=Main.commanderMap.size();
			commanders.set(cursor().y,id);
		}
	}
	
	@Override
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
		
		if(ke.getKeyCode()==KeyEvent.VK_ENTER)
		{
			if(chooseCommanders)
			{
				chooseCommanders=false;
			}
		}
		
		if(ke.getKeyCode()==KeyEvent.VK_X)
		{
			if(!chooseCommanders)
			{
				if(cursor().y==4)
				{
					chooseCommanders=true;
					cursor().y=0;
				}
				if(cursor().y==5)
				{
					Main.closeMenu();
					ArrayList<Commander> coms=new ArrayList<Commander>();
					for(int i=0;i<commanders.size();i++)
					{
						coms.add(Main.commanderMap.get(commanders.get(i)));
					}
					Battle battle=Main.createBattle(map,settings,coms);
					if(((GameModeMenu)Main.currentMode).previousState()==SelectionState.MULTIPLAYER)
					{
						Message mes=new Message(ServerClient.NEWSESSION);
						mes.addString("session1");
						Message bat=Main.saveBattle(battle);
						mes.addInt(bat.size());
						mes.addMessage(bat);
						Main.client.addMessage(mes);
						try
						{
							Main.client.sendMessages();
						}
						catch(IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else
						Main.startBattle(battle);
				}
			}
			else
			{
				chooseCommanders=false;
			}
		}
		
		if(ke.getKeyCode()==KeyEvent.VK_Z)
		{
			if(!chooseCommanders)
			{
				chooseCommanders=true;
				cursor().y=0;
			}
			else
			{
				Main.closeMenu();
			}
		}
	}
	
	@Override
	public int columns()
	{
		return 0;
	}
	
	@Override
	public int rows()
	{
		if(!chooseCommanders)
			return(6);
		else
			return(commanders.size());
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
		if(!chooseCommanders)
		{
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
		}
		else
		{
			String action;
			for(int i=0;i<commanders.size();i++)
			{
				action=Main.commanderMap.get(commanders.get(i)).name();
				if(index==i)
					action=">"+action+"<";
				g.drawString(action,view.width()/2-fm.stringWidth(action)/2,view.height()/4+fm.getHeight()*(i+1));
			}
		}
		return(image);
	}
	
	private boolean chooseCommanders;
	private ArrayList<Integer> commanders;
	private BattleSettings settings;
	private Map map;
}
