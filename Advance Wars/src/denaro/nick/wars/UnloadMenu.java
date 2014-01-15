package denaro.nick.wars;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import denaro.nick.core.Sprite;

public class UnloadMenu extends Menu
{

	public UnloadMenu(Menu child, Point point, int cargoSlot)
	{
		super(child, point);
		this.cargoSlot=cargoSlot;
		this.points=((Battle)Main.currentMode).unloadablePoints(((Battle)Main.currentMode).selectedUnit().cargo(cargoSlot),((Battle)Main.currentMode).path().last());
		cursor(new Point(0,0));
		Main.currentMode.cursor(points.get(cursor().y));
	}
	
	public ArrayList<Point> points()
	{
		return(points);
	}
	
	public int rows()
	{
		return(points.size());
	}
	
	public int columns()
	{
		return(0);
	}
	
	@Override
	public void keyPressed(KeyEvent ke)
	{
		if(ke.getKeyCode()==KeyEvent.VK_UP)
		{
			moveCursorUp();
			Main.currentMode.cursor(points.get(cursor().y));
		}
		if(ke.getKeyCode()==KeyEvent.VK_DOWN)
		{
			moveCursorDown();
			Main.currentMode.cursor(points.get(cursor().y));
		}
		
		if(ke.getKeyCode()==KeyEvent.VK_X)
		{
			((Battle)Main.currentMode).unloadUnit(cargoSlot,points.get(cursor().y));
			Main.closeMenu();
			Main.closeMenu();
			Main.closeMenu();
		}
		
		if(ke.getKeyCode()==KeyEvent.VK_Z)
		{
			Main.closeMenu();
		}
	}
	
	
	@Override
	public Image image()
	{
		
		//Sprite sprite=Sprite.sprite("Action Menu");
		
		BufferedImage image=new BufferedImage(((Battle)Main.currentMode).map().width()*Main.TILESIZE,((Battle)Main.currentMode).map().height()*Main.TILESIZE,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=image.createGraphics();
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		
		g.setColor(Color.cyan);
		
		for(int i=0;i<points.size();i++)
		{
			g.fillRect(points.get(i).x*Main.TILESIZE, points.get(i).y*Main.TILESIZE, Main.TILESIZE, Main.TILESIZE);
		}
		
		return(image);
	}

	private int cargoSlot;
	private ArrayList<Point> points;
}
