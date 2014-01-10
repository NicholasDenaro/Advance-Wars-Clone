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

public class AttackMenu extends Menu
{

	public AttackMenu(Menu child, Point point, ArrayList<Point> enemies)
	{
		super(child, point);
		this.enemies=enemies;
		cursor(new Point(0,0));
		Main.battle.cursor(enemies.get(cursor().y));
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
	public void keyPressed(KeyEvent ke)
	{
		if(ke.getKeyCode()==KeyEvent.VK_UP)
		{
			//cursor=(--cursor+enemies.size())%enemies.size();
			moveCursorUp();
			Main.battle.cursor(enemies.get(cursor().y));
		}
		if(ke.getKeyCode()==KeyEvent.VK_DOWN)
		{
			//cursor=++cursor%enemies.size();
			moveCursorDown();
			Main.battle.cursor(enemies.get(cursor().y));
		}
		
		if(ke.getKeyCode()==KeyEvent.VK_X)
		{
			Main.battle.attackUnit(Main.battle.path().first(),enemies.get(cursor().y));
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
		
		BufferedImage image=new BufferedImage(Main.battle.map().width()*Main.TILESIZE,Main.battle.map().height()*Main.TILESIZE,BufferedImage.TYPE_INT_ARGB);
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
