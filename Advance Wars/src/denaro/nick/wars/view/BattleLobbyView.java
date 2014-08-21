package denaro.nick.wars.view;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import denaro.nick.core.view.GameView2D;
import denaro.nick.core.Location;
import denaro.nick.wars.Main;
import denaro.nick.wars.menu.Menu;
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
		
		//draw commander
		action=">"+Main.commanderMap.get(lobby.commander()).name()+"<";
		g.drawString(action,width()/2-fm.stringWidth(action)/2,2*height()/3-2*fm.getHeight());
		
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
			
		}
		
		/*action="Press Enter to confirm";
		g.drawString(action,width()/2-fm.stringWidth(action)/2,height()-fm.getHeight()/2);*/
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
			
			BufferedImage img=lobby.battle().map().minimap();
			g.drawImage(img,width()/2-img.getWidth()/2,8,null);
			
			if(Main.menu==null)
				drawActions(lobby,g);
			
			drawMenus(g);
		}
	}
}
