package denaro.nick.wars.menu;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import denaro.nick.core.controller.ControllerEvent;
import denaro.nick.wars.Battle;
import denaro.nick.wars.Main;

public class AttackMenu extends Menu
{

	public AttackMenu(Menu child, Point point, ArrayList<Point> enemies)
	{
		super(child, point);
		this.enemies=enemies;
		cursor(new Point(0,0));
		Main.currentMode.cursor(enemies.get(cursor().y));
	}
	
	public ArrayList<Point> enemies()
	{
		return(enemies);
	}
	
	public int rows()
	{
		return(enemies.size());
	}
	
	public int columns()
	{
		return(0);
	}
	
	@Override
	public void actionPerformed(ControllerEvent event)
	{
		if(event.action()==ControllerEvent.PRESSED)
		{
			if(event.code()==Main.UP)
			{
				//cursor=(--cursor+enemies.size())%enemies.size();
				moveCursorUp();
				Main.currentMode.cursor(enemies.get(cursor().y));
			}
			if(event.code()==Main.DOWN)
			{
				//cursor=++cursor%enemies.size();
				moveCursorDown();
				Main.currentMode.cursor(enemies.get(cursor().y));
			}
			
			if(event.code()==Main.ACTION)
			{
				((Battle)Main.currentMode).attackUnit(((Battle)Main.currentMode).path().first(),enemies.get(cursor().y));
				Main.closeMenu();
				Main.closeMenu();
			}
			
			if(event.code()==Main.BACK)
			{
				Main.closeMenu();
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
		{
			//cursor=(--cursor+enemies.size())%enemies.size();
			moveCursorUp();
			Main.currentMode.cursor(enemies.get(cursor().y));
		}
		if(ke.getKeyCode()==KeyEvent.VK_DOWN)
		{
			//cursor=++cursor%enemies.size();
			moveCursorDown();
			Main.currentMode.cursor(enemies.get(cursor().y));
		}
		
		if(ke.getKeyCode()==KeyEvent.VK_X)
		{
			((Battle)Main.currentMode).attackUnit(((Battle)Main.currentMode).path().first(),enemies.get(cursor().y));
			Main.closeMenu();
			Main.closeMenu();
		}
		
		if(ke.getKeyCode()==KeyEvent.VK_Z)
		{
			Main.closeMenu();
		}
	}*/
	
	
	@Override
	public Image image()
	{
		
		//Sprite sprite=Sprite.sprite("Action Menu");
		
		BufferedImage image=new BufferedImage(((Battle)Main.currentMode).map().width()*Main.TILESIZE,((Battle)Main.currentMode).map().height()*Main.TILESIZE,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=image.createGraphics();
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		
		g.setColor(Color.red);
		
		for(int i=0;i<enemies.size();i++)
		{
			g.fillRect(enemies.get(i).x*Main.TILESIZE, enemies.get(i).y*Main.TILESIZE, Main.TILESIZE, Main.TILESIZE);
		}
		

		
		return(image);
	}

	private ArrayList<Point> enemies;
}
