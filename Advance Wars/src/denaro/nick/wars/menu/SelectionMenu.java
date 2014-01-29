package denaro.nick.wars.menu;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import denaro.nick.core.Entity;
import denaro.nick.core.GameMap;
import denaro.nick.core.GameView2D;
import denaro.nick.core.Identifiable;
import denaro.nick.core.Sprite;
import denaro.nick.wars.Main;

public class SelectionMenu<V extends Identifiable> extends Menu
{

	public SelectionMenu(Menu child, Point point)
	{
		super(child, point);
		cursor(new Point(0,0));
		selectionWidth=8;
		selectionHeight=2;
	}
	
	public void addSelections(GameMap<V> map)
	{
		selectionMap=map;
		selectionHeight=map.size()/selectionWidth+1;
	}
	
	public void selectionHeight(int selectionHeight)
	{
		this.selectionHeight=selectionHeight;
	}
	
	public int getSelection()
	{
		return(cursor().x+cursor().y*selectionWidth);
	}
	
	public int rows()
	{
		return(selectionHeight);
	}
	
	public int columns()
	{
		return(selectionWidth);
	}
	
	public Class type()
	{
		return(selectionMap.get(0).getClass());
	}
	
	@Override
	public void keyTyped(KeyEvent ke)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent ke)
	{
		if(ke.getKeyCode()==KeyEvent.VK_LEFT)
			moveCursorLeft();
		if(ke.getKeyCode()==KeyEvent.VK_RIGHT)
			moveCursorRight();
		if(ke.getKeyCode()==KeyEvent.VK_UP)
			moveCursorUp();
		if(ke.getKeyCode()==KeyEvent.VK_DOWN)
			moveCursorDown();
		
		
		if(ke.getKeyCode()==KeyEvent.VK_X)
		{
			buttonPressed(this);
			Main.closeMenu();
		}
		
		if(ke.getKeyCode()==KeyEvent.VK_Z)
		{
			Main.closeMenu();
		}
	}

	@Override
	public void keyReleased(KeyEvent ke)
	{
		// TODO Auto-generated method stub
		
	}
	
	public void drawSelections(BufferedImage image, Graphics2D g)
	{
		for(int a=0;a<selectionHeight;a++)
		{
			for(int i=0;i<selectionWidth;i++)
			{
				if(selectionMap.get(i+a*selectionWidth) instanceof Entity)
					g.drawImage(((Entity)selectionMap.get(i+a*selectionWidth)).image(), i*(Main.TILESIZE+4)+4-((Entity)selectionMap.get(i+a*selectionWidth)).sprite().anchor().x, a*(Main.TILESIZE+4)+4-((Entity)selectionMap.get(i+a*selectionWidth)).sprite().anchor().y, null);
			}
		}
		
		Main.swapPalette(image, null, 1);
	}
	
	@Override
	public Image image()
	{
		BufferedImage image=new BufferedImage(((GameView2D)Main.engine().view()).height()+4,rows()*20+4,BufferedImage.TYPE_INT_ARGB);
		if(child()!=null)
			return(image);
		Graphics2D g=image.createGraphics();
		
		g.setColor(Color.black);
		Composite oldComposite=g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));
		g.fillRect(0,0,image.getWidth()+4,image.getHeight());
		g.setComposite(oldComposite);
		
		drawSelections(image,g);
		
		Sprite cursor=Sprite.sprite("Cursor");
		g.drawImage(cursor.subimage(0),cursor().x*(Main.TILESIZE+4)+4-cursor.anchor().x, cursor().y*(Main.TILESIZE+4)+4-cursor.anchor().y,null);
		
		return(image);
	}
	
	private int selectionWidth, selectionHeight;
	
	private GameMap<V> selectionMap;
}
