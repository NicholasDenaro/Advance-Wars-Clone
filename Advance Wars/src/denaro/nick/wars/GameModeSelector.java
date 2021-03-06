package denaro.nick.wars;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import denaro.nick.core.controller.ControllerEvent;
import denaro.nick.server.Message;
import denaro.nick.wars.menu.MapOptionsMenu;
import denaro.nick.wars.menu.MinimapMenu;
import denaro.nick.wars.multiplayer.GameClient;
import denaro.nick.wars.multiplayer.MainServer;
import denaro.nick.wars.multiplayer.ServerClient;
import denaro.nick.wars.view.GameModeMenuView;


public class GameModeSelector extends GameMode
{
	public GameModeSelector()
	{
		main=new ArrayList<String>();
		main.add("New Battle");
		main.add("Load Battle");
		main.add("Edit Map");
		main.add("Create Map");
		main.add("Multiplayer");
		main.add("Quit");
		
		multiplayer=new ArrayList<String>();
		multiplayer.add("New Battle");
		multiplayer.add("Join Battle");
		multiplayer.add("Back");
		
		actions=main;
		cursor(new Point(0,0));
		state=SelectionState.MAIN;
		previousState=null;
	}
	
	public SelectionState state()
	{
		return(state);
	}
	
	public SelectionState previousState()
	{
		return(previousState);
	}
	
	public void changeState(SelectionState state)
	{
		previousState=this.state;
		this.state=state;
		if(this.state==SelectionState.MAIN)
			actions=main;
		cursor(new Point(0,0));
	}
	
	@Override
	public int columns()
	{
		return(0);
	}

	@Override
	public int rows()
	{
		return(actions.size());
	}

	public void moveCursorUp()
	{
		cursor().y=(cursor().y-1+rows())%rows();
		updateCursorListeners();
	}
	
	public void moveCursorDown()
	{
		cursor().y=(cursor().y+1+rows())%rows();
		updateCursorListeners();
	}
	
	public String action(int index)
	{
		return(actions.get((index+actions.size())%actions.size()));
	}
	
	public void addAction(String action)
	{
		actions.add(actions.size()-1,action);
	}
	
	public int numberOfActions()
	{
		return(rows());
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
			
			if(event.code()==Main.ACTION)
			{
				if(state==SelectionState.MAIN)
				{
					if(actions.get(cursor().y).equals("New Battle"))
					{
						changeState(SelectionState.NEW);
						actions=Main.getMapList();
						cursor(new Point(0,0));
						GameModeMenuView view=(GameModeMenuView)Main.engine().view();
						Main.openMenu(new MinimapMenu(null,new Point(view.width()/2,view.height()/3)));
						Main.engine().requestFocus(this);
					}
					else if(actions.get(cursor().y).equals("Load Battle"))
					{
						changeState(SelectionState.LOAD);
						actions=Main.getBattleList();
						cursor(new Point(0,0));
					}
					else if(actions.get(cursor().y).equals("Edit Map"))
					{
						changeState(SelectionState.EDIT);
						actions=Main.getMapList();
						cursor(new Point(0,0));
						GameModeMenuView view=(GameModeMenuView)Main.engine().view();
						Main.openMenu(new MinimapMenu(null,new Point(view.width()/2,view.height()/3)));
						Main.engine().requestFocus(this);
					}
					else if(actions.get(cursor().y).equals("Create Map"))
					{
						changeState(SelectionState.CREATE);
						Main.createEditor(null);
						cursor(new Point(0,0));
					}
					else if(actions.get(cursor().y).equals("Multiplayer"))
					{
						changeState(SelectionState.MULTIPLAYER);
						cursor(new Point(0,0));
						actions=multiplayer;
						try
						{
							String hostname=MainServer.hostname;
							int port=MainServer.port;
							if(hostname==null)
							{
								System.out.print("hostname:port| ");
								hostname=Main.getInput();
								port=new Integer(hostname.substring(hostname.indexOf(':')+1));
								hostname=hostname.substring(0,hostname.indexOf(':'));
							}
							Socket socket=new Socket(hostname,port);
							Main.client=new GameClient(socket);
							Main.client.start();
						}
						catch(IOException ex)
						{
							ex.printStackTrace();
						}
					}
					else if(actions.get(cursor().y).equals("Quit"))
					{
						System.exit(0);
					}
				}
				else if(state==SelectionState.NEW)
				{
					MapOptionsMenu menu=new MapOptionsMenu(null,new Point(0,0),actions.get(cursor().y));
					Main.closeMenu();
					Main.openMenu(menu);
					Main.engine().requestFocus(menu);
				}
				else if(state==SelectionState.LOAD)
				{
					Battle battle=Main.loadBattle(actions.get(cursor().y),null);
					Main.startBattle(battle);
				}
				else if(state==SelectionState.EDIT)
				{
					Main.closeMenu();
					Map map=Main.loadMap(actions.get(cursor().y));
					Main.createEditor(map);
				}
				else if(state==SelectionState.MULTIPLAYER)
				{
					if(actions.get(cursor().y).equals("New Battle"))
					{
						actions=Main.getMapList();
						changeState(SelectionState.NEW);
					}
					else if(actions.get(cursor().y).equals("Join Battle"))
					{
						actions=new ArrayList<String>();
						actions.add("Back");
						Message mes=new Message(ServerClient.SESSIONS);
						Main.client.addMessage(mes);
						Main.client.sendMessages();
					}
					else if(actions.get(cursor().y).equals("Back"))
					{
						changeState(SelectionState.MAIN);
						actions=main;
					}
					else
					{
						Message mes=new Message(ServerClient.JOINSESSION);
						mes.addString(actions.get(cursor().y));
						Main.client.addMessage(mes);
						Main.client.sendMessages();
					}
				}
			}
			
			if(event.code()==Main.BACK)
			{
				changeState(SelectionState.MAIN);
				actions=main;
				Main.closeAllMenus();
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
		
		if(ke.getKeyCode()==KeyEvent.VK_X)
		{
			if(state==SelectionState.MAIN)
			{
				if(actions.get(cursor().y).equals("New Battle"))
				{
					changeState(SelectionState.NEW);
					actions=Main.getMapList();
					cursor(new Point(0,0));
					GameModeMenuView view=(GameModeMenuView)Main.engine().view();
					Main.openMenu(new MinimapMenu(null,new Point(view.width()/2,view.height()/3)));
					Main.engine().requestFocus(this);
				}
				else if(actions.get(cursor().y).equals("Load Battle"))
				{
					changeState(SelectionState.LOAD);
					actions=Main.getBattleList();
					cursor(new Point(0,0));
				}
				else if(actions.get(cursor().y).equals("Edit Map"))
				{
					changeState(SelectionState.EDIT);
					actions=Main.getMapList();
					cursor(new Point(0,0));
					GameModeMenuView view=(GameModeMenuView)Main.engine().view();
					Main.openMenu(new MinimapMenu(null,new Point(view.width()/2,view.height()/3)));
					Main.engine().requestFocus(this);
				}
				else if(actions.get(cursor().y).equals("Create Map"))
				{
					changeState(SelectionState.CREATE);
					Main.createEditor(null);
					cursor(new Point(0,0));
				}
				else if(actions.get(cursor().y).equals("Multiplayer"))
				{
					changeState(SelectionState.MULTIPLAYER);
					cursor(new Point(0,0));
					actions=multiplayer;
					try
					{
						String hostname=MainServer.hostname;
						int port=MainServer.port;
						if(hostname==null)
						{
							System.out.print("hostname:port| ");
							hostname=Main.getInput();
							port=new Integer(hostname.substring(hostname.indexOf(':')+1));
							hostname=hostname.substring(0,hostname.indexOf(':'));
						}
						Socket socket=new Socket(hostname,port);
						Main.client=new GameClient(socket);
						Main.client.start();
					}
					catch(IOException ex)
					{
						ex.printStackTrace();
					}
				}
				else if(actions.get(cursor().y).equals("Quit"))
				{
					System.exit(0);
				}
			}
			else if(state==SelectionState.NEW)
			{
				MapOptionsMenu menu=new MapOptionsMenu(null,new Point(0,0),actions.get(cursor().y));
				Main.closeMenu();
				Main.openMenu(menu);
				Main.engine().requestFocus(menu);
			}
			else if(state==SelectionState.LOAD)
			{
				Battle battle=Main.loadBattle(actions.get(cursor().y),null);
				Main.startBattle(battle);
			}
			else if(state==SelectionState.EDIT)
			{
				Main.closeMenu();
				Map map=Main.loadMap(actions.get(cursor().y));
				Main.createEditor(map);
			}
			else if(state==SelectionState.MULTIPLAYER)
			{
				if(actions.get(cursor().y).equals("New Battle"))
				{
					actions=Main.getMapList();
					changeState(SelectionState.NEW);
				}
				else if(actions.get(cursor().y).equals("Join Battle"))
				{
					actions=new ArrayList<String>();
					actions.add("Back");
					Message mes=new Message(ServerClient.SESSIONS);
					Main.client.addMessage(mes);
					Main.client.sendMessages();
				}
				else if(actions.get(cursor().y).equals("Back"))
				{
					changeState(SelectionState.MAIN);
					actions=main;
				}
				else
				{
					Message mes=new Message(ServerClient.JOINSESSION);
					mes.addString(actions.get(cursor().y));
					Main.client.addMessage(mes);
					Main.client.sendMessages();
				}
			}
		}
		
		if(ke.getKeyCode()==KeyEvent.VK_Z)
		{
			changeState(SelectionState.MAIN);
			actions=main;
			Main.closeAllMenus();
		}
	}
	
	@Override
	public void keyReleased(KeyEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}*/
	
	private ArrayList<String> actions;
	private static ArrayList<String> main;
	private static ArrayList<String> multiplayer;
	private SelectionState state;
	private SelectionState previousState;
	
	public enum SelectionState{MAIN,NEW,LOAD,EDIT,CREATE,MULTIPLAYER}
}

