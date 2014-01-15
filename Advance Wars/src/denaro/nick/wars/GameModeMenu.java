package denaro.nick.wars;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;


public class GameModeMenu extends GameMode
{
	public GameModeMenu()
	{
		main=new ArrayList<String>();
		main.add("New Battle");
		main.add("Load Battle");
		main.add("Edit Map");
		main.add("Create Map");
		//actions.add("Multiplayer");
		main.add("Quit");
		actions=main;
		cursor(new Point(0,0));
		state=SelectionState.MAIN;
	}
	
	public SelectionState state()
	{
		return(state);
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
	
	public int numberOfActions()
	{
		return(rows());
	}
	
	@Override
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
					state=SelectionState.NEW;
					actions=Main.getMapList();
					cursor(new Point(0,0));
				}
				else if(actions.get(cursor().y).equals("Load Battle"))
				{
					state=SelectionState.LOAD;
					actions=Main.getBattleList();
					cursor(new Point(0,0));
				}
				else if(actions.get(cursor().y).equals("Edit Map"))
				{
					state=SelectionState.EDIT;
					actions=Main.getMapList();
					cursor(new Point(0,0));
				}
				else if(actions.get(cursor().y).equals("Create Map"))
				{
					state=SelectionState.CREATE;
					Main.createEditor(null);
					cursor(new Point(0,0));
				}
				else if(actions.get(cursor().y).equals("Multiplayer"))
				{
					state=SelectionState.MULTIPLAYER;
					cursor(new Point(0,0));
				}
				else if(actions.get(cursor().y).equals("Quit"))
				{
					System.exit(0);
				}
			}
			else if(state==SelectionState.NEW)
			{
				MapSelectionMenu menu=new MapSelectionMenu(null,new Point(0,0),actions.get(cursor().y));
				Main.openMenu(menu);
				Main.engine().requestFocus(menu);
			}
			else if(state==SelectionState.LOAD)
			{
				Battle battle=Main.loadBattle(actions.get(cursor().y));
				Main.startBattle(battle);
			}
			else if(state==SelectionState.EDIT)
			{
				Map map=Main.loadMap(actions.get(cursor().y));
				Main.createEditor(map);
			}
		}
		
		if(ke.getKeyCode()==KeyEvent.VK_Z)
		{
			state=SelectionState.MAIN;
			actions=main;
		}
	}
	
	@Override
	public void keyReleased(KeyEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}
	
	private ArrayList<String> actions;
	private static ArrayList<String> main;
	private SelectionState state;
	
}

enum SelectionState{MAIN,NEW,LOAD,EDIT,CREATE,MULTIPLAYER}