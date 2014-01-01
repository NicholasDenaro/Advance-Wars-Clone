package denaro.nick.wars;

import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import denaro.nick.core.Focusable;

public class Menu implements Focusable, KeyListener
{
	public Menu(Menu child, Point point)
	{
		this.child=child;
		this.point=point;
	}
	
	public Point point()
	{
		return(point);
	}

	public Image image()
	{
		return(null);
	}
	
	public void child(Menu child)
	{
		this.child=child;
	}
	
	public Menu child()
	{
		return(child);
	}
	
	@Override
	public void keyTyped(KeyEvent ke)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent ke)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent ke)
	{
		// TODO Auto-generated method stub
		
	}
	
	private Point point;
	
	private Menu child;
	
}
