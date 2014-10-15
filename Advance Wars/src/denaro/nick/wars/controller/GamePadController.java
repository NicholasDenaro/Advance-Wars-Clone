package denaro.nick.wars.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import denaro.nick.controllertest.XBoxAnalogEvent;
import denaro.nick.controllertest.XBoxController;
import denaro.nick.controllertest.XBoxControllerListener;
import denaro.nick.controllertest.XBoxButtonEvent;
import denaro.nick.core.controller.Controller;
import denaro.nick.core.controller.ControllerEvent;
import denaro.nick.core.controller.ControllerListener;
import denaro.nick.core.GameEngine;
import denaro.nick.core.view.GameView;
import denaro.nick.core.view.GameViewListener;
import denaro.nick.wars.Main;

public class GamePadController extends Controller implements XBoxControllerListener
{
	public GamePadController()
	{
		super();
		if(defaultKeymap==null)
			createDefaultKeymap();
		keymap(defaultKeymap);
		
		wait=-1;
		waitTime=defaultWaitTime;
	}
	
	protected void createDefaultKeymap()
	{
		defaultKeymap=new HashMap<Integer,Integer>();
		defaultKeymap.put(XBoxController.DPAD,-1);
		defaultKeymap.put(XBoxController.A,Main.ACTION);
		defaultKeymap.put(XBoxController.B,Main.BACK);
		defaultKeymap.put(XBoxController.START,Main.START);
		defaultKeymap.put(XBoxController.BACK,Main.SELECT);
		defaultKeymap.put(XBoxController.X,Main.ACTION2);
		defaultKeymap.put(XBoxController.Y,Main.ACTION3);
		defaultKeymap.put(XBoxController.LEFTBUMPER,Main.ACTION4);
	}
	
	@Override
	public boolean init(GameEngine engine)
	{
		//System.out.println("adding...");
		XBoxController controller=new XBoxController();
		controller.addXBoxControllerListener(this);
		this.addControllerListener(engine);
		return(controller.isConnected());
		//System.out.println("added...?");
	}
	
	//private HashMap<Integer,Integer> keymap;
	
	//public HashMap<Integer,Integer> defaultKeymap;

	@Override
	public void buttonPressed(XBoxButtonEvent event)
	{
		if(!keymap().containsKey(event.getButtonCode()))
			return;
		int key=keymap().get(event.getButtonCode());
		if(event.getButtonCode()==XBoxController.DPAD)
		{
			if(event.pollData()==XBoxController.DPAD_UP)
				key=Main.UP;
			else if(event.pollData()==XBoxController.DPAD_RIGHT)
				key=Main.RIGHT;
			else if(event.pollData()==XBoxController.DPAD_DOWN)
				key=Main.DOWN;
			else if(event.pollData()==XBoxController.DPAD_LEFT)
				key=Main.LEFT;
		}
		for(ControllerListener listener:listeners())
			listener.actionPerformed(new ControllerEvent(ControllerEvent.PRESSED,key));
	}

	@Override
	public void buttonReleased(XBoxButtonEvent event)
	{
		if(!keymap().containsKey(event.getButtonCode()))
			return;
		int key=keymap().get(event.getButtonCode());
		if(event.getButtonCode()==XBoxController.DPAD)
		{
			if(event.pollData()==0.25)
				key=Main.UP;
			else if(event.pollData()==0.5)
				key=Main.RIGHT;
			else if(event.pollData()==0.75)
				key=Main.DOWN;
			else if(event.pollData()==1)
				key=Main.LEFT;
		}
		for(ControllerListener listener:listeners())
			listener.actionPerformed(new ControllerEvent(ControllerEvent.RELEASED,key));
	}
	
	public void resetWait()
	{
		wait=-1;
		waitTime=defaultWaitTime;
		if(timer!=null)
		{
			timer.cancel();
			timer=null;
		}
	}

	@Override
	public void analogMoved(XBoxAnalogEvent event)
	{
		int key=-2;
		if(event.getButtonCode()==XBoxController.LEFTSTICK)
		{
			key=-1;
			if(event.pollDataX()>=0.75)
				key=Main.RIGHT;
			else if(event.pollDataX()<-0.75)
				key=Main.LEFT;
			else if(event.pollDataY()>=0.75)
				key=Main.DOWN;
			else if(event.pollDataY()<-0.75)
				key=Main.UP;
		}
		
		if(key>=0)
		{
			if(wait==key)
				return;
			else
			{
				resetWait();
			}
			wait=key;
			timer=new Timer();
			timer.schedule(new TimerTask(){

				@Override
				public void run()
				{
					wait=-1;
				}
				
			},waitTime);
			if(waitTime>100)
				waitTime-=50;
			for(ControllerListener listener:listeners())
				listener.actionPerformed(new ControllerEvent(ControllerEvent.PRESSED,key));
		}
		else if(key==-1)
		{
			resetWait();
		}
	}
	
	private int wait;
	private long waitTime;
	private Timer timer;
	
	private static final long defaultWaitTime=500;
}
