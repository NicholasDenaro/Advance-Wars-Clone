package denaro.nick.wars;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import denaro.nick.core.GameView2D;
import denaro.nick.core.Location;
import denaro.nick.wars.multiplayer.BattleLobby;

public class BattleLobbyView extends GameView2D
{

	public BattleLobbyView(int width,int height,double hscale,double vscale)
	{
		super(width,height,hscale,vscale);
	}
	
	public void drawBackground(Location location,Graphics2D g)
	{
		g.drawImage(location.entityList().get(0).image(),0,0,null);
	}
	
	public void drawActions(BattleLobby lobby, Graphics2D g)
	{
		g.setColor(Color.black);
		
		FontMetrics fm=g.getFontMetrics();
		
		String action;
		
		for(int i=0;i<lobby.size();i++)
		{
			action=lobby.players(i);
			if(action==null)
				action="Emtpy";
			if(lobby.isLocked(i))
				action="+"+action+"+";
			else
				action="-"+action+"-";
			g.drawString(action,width()/2-fm.stringWidth(action)/2,2*height()/3+(i-1)*fm.getHeight());
			if(i==lobby.player())
			{
				action=">"+Main.commanderMap.get(lobby.commander()).name()+"<";
				g.drawString(action,width()/2-fm.stringWidth(action)/2,height()/2+(i-1)*fm.getHeight());
			}
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
		if(Main.currentMode instanceof BattleLobby)
		{
			BattleLobby lobby=(BattleLobby)Main.currentMode;
			
			drawBackground(currentLocation,g);
			
			if(Main.menu==null)
				drawActions(lobby,g);
			
			drawMenus(g);
		}
	}
}
