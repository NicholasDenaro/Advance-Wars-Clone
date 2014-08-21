package denaro.nick.wars.menu;

import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import denaro.nick.core.controller.ControllerEvent;
import denaro.nick.core.controller.ControllerListener;
import denaro.nick.core.Focusable;
import denaro.nick.wars.CursorUser;
import denaro.nick.wars.listener.MenuListener;

public abstract class Menu extends CursorUser implements Focusable, ControllerListener//KeyListener
{
	public Menu(Menu child, Point point)
	{
		this.child=child;
		this.point=point;
	}
	
	public void addMenuListener(MenuListener listener)
	{
		if(menuListeners==null)
			menuListeners=new ArrayList<MenuListener>();
		
		if(!menuListeners.contains(listener))
			menuListeners.add(listener);
	}
	
	public void removeMenuListener(MenuListener listener)
	{
		if(menuListeners==null)
			menuListeners=new ArrayList<MenuListener>();
		
		menuListeners.remove(listener);
	}
	
	public void buttonPressed(Menu menu)
	{
		if(menuListeners==null)
			menuListeners=new ArrayList<MenuListener>();
		
		for(MenuListener listener:menuListeners)
			listener.buttonPressed(menu);
	}
	
	public void menuClosed(Menu menu)
	{
		if(menuListeners==null)
			menuListeners=new ArrayList<MenuListener>();
		
		for(MenuListener listener:menuListeners)
			listener.menuClosed(menu);
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
	public void actionPerformed(ControllerEvent event)
	{
		
	}
	
	/*@Override
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
		
	}*/
	
	private Point point;
	
	private Menu child;
	
	private ArrayList<MenuListener> menuListeners;
}
