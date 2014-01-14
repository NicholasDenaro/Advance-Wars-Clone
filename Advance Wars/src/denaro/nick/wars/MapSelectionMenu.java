package denaro.nick.wars;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import denaro.nick.core.GameView2D;

public class MapSelectionMenu extends Menu
{
	
	public MapSelectionMenu(Menu child, Point point, String mapName)
	{
		super(child,point);
		cursor(new Point(0,0));
		settings=new BattleSettings();
		this.mapName=mapName;
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
			settings.fogOfWar(true);
		}
		if(cursor().y==2)
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
			settings.fogOfWar(false);
		}
		if(cursor().y==2)
		{
			settings.weather(settings.weather()+1);
			if(settings.weather()>=Main.weatherMap.size())
				settings.weather(settings.weather()-1);
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
		
		if(ke.getKeyCode()==KeyEvent.VK_X)
		{
			if(cursor().y==3)
			{
				Main.closeMenu();
			}
			if(cursor().y==4)
			{
				Main.closeMenu();
				Map map=Main.loadMap(mapName);
				Main.createBattle(map,settings);
			}
		}
	}
	
	@Override
	public int columns()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int rows()
	{
		// TODO Auto-generated method stub
		return(5);
	}
	
	@Override
	public Image image()
	{
		GameView2D view=(GameView2D)Main.engine().view();
		BufferedImage image=new BufferedImage(view.width(),view.height(),BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=image.createGraphics();
		
		FontMetrics fm=g.getFontMetrics();
		
		g.setColor(Color.white);
		//g.fillRect(0,0,view.width(),view.height());
		
		g.setColor(Color.black);
		int index=cursor().y;
		
		String action="";
		action="Funds: "+settings.startingFunds();
		if(index==0)
			action=">"+action+"<";
		g.drawString(action,view.width()/2-fm.stringWidth(action)/2,view.height()/4+fm.getHeight());
		
		action="Fog: "+(settings.fogOfWar()?"(":"")+"On"+(settings.fogOfWar()?")":"")+"|"+(settings.fogOfWar()?"":"(")+"Off"+(settings.fogOfWar()?"":")");
		if(index==1)
			action=">"+action+"<";
		g.drawString(action,view.width()/2-fm.stringWidth(action)/2,view.height()/4+fm.getHeight()*2);
		
		action="Weather: "+Main.weatherMap.get(settings.weather()).name();
		if(index==2)
			action=">"+action+"<";
		g.drawString(action,view.width()/2-fm.stringWidth(action)/2,view.height()/4+fm.getHeight()*3);
		
		action="Back";
		if(index==3)
			action=">"+action+"<";
		g.drawString(action,view.width()/2-fm.stringWidth(action)/2,view.height()/4+fm.getHeight()*4);
		
		action="Start";
		if(index==4)
			action=">"+action+"<";
		g.drawString(action,view.width()/2-fm.stringWidth(action)/2,view.height()/4+fm.getHeight()*5);
		
		return(image);
	}
	
	private BattleSettings settings;
	private String mapName;
}
