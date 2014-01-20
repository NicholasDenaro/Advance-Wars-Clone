package denaro.nick.wars;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import denaro.nick.core.GameView2D;
import denaro.nick.server.Message;
import denaro.nick.wars.multiplayer.BattleLobby;
import denaro.nick.wars.multiplayer.MultiplayerBattle;
import denaro.nick.wars.multiplayer.ServerClient;

public class CommanderSelectionMenu extends Menu
{
	
	public CommanderSelectionMenu(Menu child, Point point, Map map, BattleSettings settings)
	{
		super(child,point);
		commanders=new ArrayList<Integer>();
		this.map=map;
		this.settings=settings;
		cursor(new Point(0,0));
	}
	
	@Override
	public void keyPressed(KeyEvent ke)
	{
		if(ke.getKeyCode()==KeyEvent.VK_ENTER)
		{
			Main.closeMenu();
			Main.closeMenu();
			ArrayList<Commander> coms=new ArrayList<Commander>();
			for(int i=0;i<commanders.size();i++)
			{
				coms.add(Main.commanderMap.get(commanders.get(i)));
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

	@Override
	public int columns()
	{
		return commanders.size();
	}
	
	@Override
	public int rows()
	{
		return 0;
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

		for(int i=0;i<commanders.size();i++)
		{
			action=Main.commanderMap.get(commanders.get(i)).name();
			if(cursor().y==i)
				action=">"+action+"<";
			g.drawString(action,view.width()/2-fm.stringWidth(action)/2,view.height()/4+fm.getHeight()*(i+1));
		}

		
		return(image);
	}
	
	
	private ArrayList<Integer> commanders;
	private Map map;
	private BattleSettings settings;
}
