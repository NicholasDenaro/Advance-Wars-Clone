package denaro.nick.wars.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import denaro.nick.core.ControllerEvent;
import denaro.nick.core.Sprite;
import denaro.nick.wars.Battle;
import denaro.nick.wars.Main;

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
	public void actionPerformed(ControllerEvent event)
	{
		if(event.action()==ControllerEvent.PRESSED)
		{
			if(event.code()==Main.UP)
				moveCursorUp();
			if(event.code()==Main.DOWN)
				moveCursorDown();
			
			Battle battle=(Battle)Main.currentMode;
			
			if(event.code()==Main.ACTION)
			{
				if(actions[cursor().y].equals("Move"))
				{
					battle.moveUnit();
					Main.closeMenu();
				}
				else if(actions[cursor().y].equals("Hide"))
				{
					battle.hideUnit();
					Main.closeMenu();
				}
				else if(actions[cursor().y].equals("UnHide"))
				{
					battle.unHideUnit();
					Main.closeMenu();
				}
				else if(actions[cursor().y].equals("Capture"))
				{
					battle.unitCaptureBuilding(battle.selectedUnit(),battle.path().last());
					Main.closeMenu();
				}
				else if(actions[cursor().y].equals("Unite"))
				{
					battle.moveUnit();
					Main.closeMenu();
				}
				else if(actions[cursor().y].equals("Attack"))
				{
					AttackMenu menu=new AttackMenu(null,new Point(0,0),battle.attackableUnits());
					Main.openMenu(menu);
				}
				else if(actions[cursor().y].equals("Load"))
				{
					((Battle)Main.currentMode).loadUnit();
					Main.closeMenu();
				}
				else if(actions[cursor().y].equals("Unload"))
				{
					ArrayList<String> unloads=new ArrayList<String>();
					for(int i=0;i<battle.selectedUnit().maxCargo();i++)
						if(battle.selectedUnit().cargo(i)!=null)
							unloads.add("Unit "+i);
					Main.openMenu(new ActionMenu(null,point(),unloads));
				}
				else if(actions[cursor().y].contains("Unit "))
				{
					int slot=new Integer(actions[cursor().y].substring(5));
					Main.openMenu(new UnloadMenu(null,new Point(0,0),slot));
				}
				else if(actions[cursor().y].equals("Cancel"))
				{
					Main.closeMenu();
					if(battle.moveableArea()==null)
					{
						battle.selectedUnit(null);
						battle.path(null);
					}
				}
			}
			
			if(event.code()==Main.BACK)
			{
				Main.closeMenu();
				if(battle.moveableArea()==null)
				{
					battle.selectedUnit(null);
					battle.path(null);
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
		
		Battle battle=(Battle)Main.currentMode;
		
		if(ke.getKeyCode()==KeyEvent.VK_X)
		{
			if(actions[cursor().y].equals("Move"))
			{
				battle.moveUnit();
				Main.closeMenu();
			}
			else if(actions[cursor().y].equals("Hide"))
			{
				battle.hideUnit();
				Main.closeMenu();
			}
			else if(actions[cursor().y].equals("UnHide"))
			{
				battle.unHideUnit();
				Main.closeMenu();
			}
			else if(actions[cursor().y].equals("Capture"))
			{
				battle.unitCaptureBuilding(battle.selectedUnit(),battle.path().last());
				Main.closeMenu();
			}
			else if(actions[cursor().y].equals("Unite"))
			{
				battle.moveUnit();
				Main.closeMenu();
			}
			else if(actions[cursor().y].equals("Attack"))
			{
				AttackMenu menu=new AttackMenu(null,new Point(0,0),battle.attackableUnits());
				Main.openMenu(menu);
			}
			else if(actions[cursor().y].equals("Load"))
			{
				((Battle)Main.currentMode).loadUnit();
				Main.closeMenu();
			}
			else if(actions[cursor().y].equals("Unload"))
			{
				ArrayList<String> unloads=new ArrayList<String>();
				for(int i=0;i<battle.selectedUnit().maxCargo();i++)
					if(battle.selectedUnit().cargo(i)!=null)
						unloads.add("Unit "+i);
				Main.openMenu(new ActionMenu(null,point(),unloads));
			}
			else if(actions[cursor().y].contains("Unit "))
			{
				int slot=new Integer(actions[cursor().y].substring(5));
				Main.openMenu(new UnloadMenu(null,new Point(0,0),slot));
			}
			else if(actions[cursor().y].equals("Cancel"))
			{
				Main.closeMenu();
				if(battle.moveableArea()==null)
				{
					battle.selectedUnit(null);
					battle.path(null);
				}
			}
		}
		
		if(ke.getKeyCode()==KeyEvent.VK_Z)
		{
			Main.closeMenu();
			if(battle.moveableArea()==null)
			{
				battle.selectedUnit(null);
				battle.path(null);
			}
		}
	}*/

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
		if(i<actions.length)
		{
			g.drawImage(sprite.subimage(0,2), 0, i*sprite.height(), null);
			g.drawString(actions[i], 2, (i+1)*sprite.height()-2);
		}
		g.setColor(Color.pink);
		g.drawRect(0, cursor().y*sprite.height(), sprite.width(), sprite.height());
		
		return(image);
	}
	
	private String[] actions;
}
