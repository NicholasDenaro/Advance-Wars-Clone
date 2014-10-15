package denaro.nick.wars.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

import denaro.nick.core.controller.Controller;
import denaro.nick.core.controller.ControllerEvent;
import denaro.nick.core.controller.ControllerListener;
import denaro.nick.core.GameEngine;
import denaro.nick.core.view.GameView;
import denaro.nick.core.view.GameViewListener;
import denaro.nick.wars.Main;

public class KeyController extends Controller implements KeyListener, GameViewListener
{
	
	public KeyController()
	{
		super();
		if(defaultKeymap==null)
			createDefaultKeymap();
		keymap(defaultKeymap);
	}
	
	protected void createDefaultKeymap()
	{
		defaultKeymap=new HashMap<Integer,Integer>();
		defaultKeymap.put(KeyEvent.VK_UP,Main.UP);
		defaultKeymap.put(KeyEvent.VK_DOWN,Main.DOWN);
		defaultKeymap.put(KeyEvent.VK_LEFT,Main.LEFT);
		defaultKeymap.put(KeyEvent.VK_RIGHT,Main.RIGHT);
		defaultKeymap.put(KeyEvent.VK_X,Main.ACTION);
		defaultKeymap.put(KeyEvent.VK_Z,Main.BACK);
		defaultKeymap.put(KeyEvent.VK_ENTER,Main.START);
		defaultKeymap.put(KeyEvent.VK_HOME,Main.SELECT);
	}
	
	@Override
	public boolean init(GameEngine engine)
	{
		//System.out.println("adding...");
		engine.view().addKeyListener(this);
		engine.addGameViewListener(this);
		this.addControllerListener(engine);
		return(true);
		//System.out.println("added...?");
	}
	
	@Override
	public void viewChanged(GameView view)
	{
		view.addKeyListener(this);
	}
	
	@Override
	public void keyPressed(KeyEvent event)
	{
		//System.out.println("key pressed!");
		for(ControllerListener listener:listeners())
			listener.actionPerformed(new ControllerEvent(ControllerEvent.PRESSED,keymap().get(event.getKeyCode())));
	}

	@Override
	public void keyReleased(KeyEvent event)
	{
		for(ControllerListener listener:listeners())
			listener.actionPerformed(new ControllerEvent(ControllerEvent.RELEASED,keymap().get(event.getKeyCode())));
	}

	@Override
	public void keyTyped(KeyEvent event)
	{
		//System.out.println("key typed!");
	}
	
	//private HashMap<Integer,Integer> keymap;
	
	//public HashMap<Integer,Integer> defaultKeymap;
}
