package denaro.nick.wars;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.util.ArrayList;

import denaro.nick.core.Sprite;

public class ActionMenu extends Menu
{
	public ActionMenu(Menu child, Point point, String... actions)
	{
		super(child,point);
		this.actions=actions;
		cursor(new Point(0,0));
	}
	
	public int rows()
	{
		return(actions.length);
	}
	
	public int columns()
	{
		return(0);
	}
	
	public ActionMenu(Menu child, Point point, ArrayList<String> actions)
	{
		super(child,point);
		this.actions=new String[actions.size()];
		for(int i=0;i<actions.size();i++)
			this.actions[i]=actions.get(i);
		cursor(new Point(0,0));
	}

	@Override
	public void keyPressed(KeyEvent ke)
	{	
		if(ke.getKeyCode()==KeyEvent.VK_UP)
			moveCursorUp();
		if(ke.getKeyCode()==KeyEvent.VK_DOWN)
			moveCursorDown();
		
		if(ke.getKeyCode()==KeyEvent.VK_X)
		{
			if(actions[cursor().y].equals("Move"))
			{
				Main.battle.moveUnit();
				Main.closeMenu();
			}
			else if(actions[cursor().y].equals("Capture"))
			{
				Main.battle.unitCaptureBuilding(Main.battle.selectedUnit(),Main.battle.path().last());
				Main.closeMenu();
			}
			else if(actions[cursor().y].equals("Unite"))
			{
				Main.battle.moveUnit();
				Main.closeMenu();
			}
			if(actions[cursor().y].equals("Attack"))
			{
				AttackMenu menu=new AttackMenu(null,new Point(0,0),Main.battle.attackableUnits());
				Main.openMenu(menu);
			}
			else if(actions[cursor().y].equals("Cancel"))
			{
				Main.closeMenu();
			}
		}
		
		if(ke.getKeyCode()==KeyEvent.VK_Z)
		{
			Main.closeMenu();
		}
	}

	@Override
	public Image image()
	{
		
		Sprite sprite=Sprite.sprite("Action Menu");
		
		BufferedImage image=new BufferedImage(sprite.width()+1,sprite.height()*actions.length+1,BufferedImage.TYPE_INT_ARGB);
		if(child()!=null)
			return(image);
		Graphics2D g=image.createGraphics();
		g.setFont(new Font(g.getFont().getName(),Font.BOLD,sprite.height()-2));
		
		int i=0;
		
		g.setColor(Color.black);

		g.drawImage(sprite.subimage(0,0), 0, i*sprite.height(), null);
		g.drawString(actions[i], 2, (i+1)*sprite.height()-2);
		
		for(i=1;i<actions.length-1;i++)
		{
			g.drawImage(sprite.subimage(0,1), 0, i*sprite.height(), null);
			g.drawString(actions[i], 2, (i+1)*sprite.height()-2);
		}
		
		g.drawImage(sprite.subimage(0,2), 0, i*sprite.height(), null);
		g.drawString(actions[i], 2, (i+1)*sprite.height()-2);
		
		g.setColor(Color.pink);
		g.drawRect(0, cursor().y*sprite.height(), sprite.width(), sprite.height());
		
		return(image);
	}
	
	private String[] actions;
}
