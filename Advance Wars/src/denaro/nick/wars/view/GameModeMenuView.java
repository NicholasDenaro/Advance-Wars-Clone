package denaro.nick.wars.view;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import denaro.nick.core.view.GameView2D;
import denaro.nick.core.Location;
import denaro.nick.wars.GameModeSelector;
import denaro.nick.wars.Main;
import denaro.nick.wars.menu.Menu;
import denaro.nick.wars.menu.MinimapMenu;

public class GameModeMenuView extends GameView2D
{

	public GameModeMenuView(int width,int height,double hscale,double vscale)
	{
		super(width,height,hscale,vscale);
	}
	
	public void drawBackground(Location location,Graphics2D g)
	{
		g.drawImage(location.entityList().get(0).image(),0,0,null);
	}
	
	public void drawActions(GameModeSelector modemenu, Graphics2D g)
	{
		g.setColor(Color.black);
		
		FontMetrics fm=g.getFontMetrics();
		
		String action;
		
		int yOffset=0;
		
		if(Main.menu instanceof MinimapMenu)
			yOffset=32;
		
		for(int i=-1;i<=1;i++)
		{
			action=modemenu.action((modemenu.cursor().y+i));
			if(i==0)
				action=">"+action+"<";
			g.drawString(action,width()/2-fm.stringWidth(action)/2,yOffset+height()/2-(-i-1)*fm.getHeight());
		}
	}
	
	public void drawMenus(Graphics2D g)
	{
		if(Main.menu!=null)
		{
			Menu menu=Main.menu;
			g.drawImage(menu.image(),menu.point().x,menu.point().y,null);
			while((menu=menu.child())!=null)
				g.drawImage(menu.image(),menu.point().x,menu.point().y,null);
		}
	}
	
	@Override
	public void drawLocation(Location currentLocation, Graphics2D g)
	{
		if(Main.currentMode instanceof GameModeSelector)
		{
			GameModeSelector modemenu=(GameModeSelector)Main.currentMode;
			
			drawBackground(currentLocation,g);
			
			if(Main.menu==null||Main.menu instanceof MinimapMenu)
				drawActions(modemenu,g);
			
			drawMenus(g);
		}
	}
}
