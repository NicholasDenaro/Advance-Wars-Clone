package denaro.nick.wars;

import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import denaro.nick.core.Focusable;

public class MapEditor extends GameMode implements MenuListener
{
	public MapEditor()
	{
		cursor(new Point(0,0));
		selectedType=SelectedType.UNIT;
		selected=0;
	}
	
	public int rows()
	{
		return(map.height());
	}
	
	public int columns()
	{
		return(map.width());
	}
	
	public Map map()
	{
		return(map);
	}
	
	public Image selected()
	{
		if(selectedType==SelectedType.TERRAIN)
		{
			if(Main.terrainMap.get(selected)!=null)
				return(Main.terrainMap.get(selected).image());
		}
		if(selectedType==SelectedType.UNIT)
		{
			if(Main.unitMap.get(selected)!=null)
				return(Main.unitMap.get(selected).image());
		}
		return(null);
	}
	
	public int selectedTeam()
	{
		return(selectedTeam);
	}
	
	public int selectedBase()
	{
		if(selectedType==SelectedType.UNIT)
		{
			return(1);
		}
		
		return(0);
	}

	public void createNewMap(String name, int width, int height)
	{
		map=new Map(name,width,height);
		for(int a=0;a<height;a++)
		{
			for(int i=0;i<width;i++)
			{
				map.setTerrain(Main.plain, i, a);
			}
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e)
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
		
		if(ke.getKeyCode()==KeyEvent.VK_A)
		{
			SelectionMenu<Unit> menu=new SelectionMenu<Unit>(null,new Point(0,0));
			menu.addSelections(Main.unitMap);
			menu.addMenuListener(this);
			Main.openMenu(menu);
		}
		
		if(ke.getKeyCode()==KeyEvent.VK_S)
		{
			SelectionMenu<Terrain> menu=new SelectionMenu<Terrain>(null,new Point(0,0));
			menu.addSelections(Main.terrainMap);
			menu.addMenuListener(this);
			Main.openMenu(menu);
		}
		
		if(ke.getKeyCode()==KeyEvent.VK_ENTER)
		{
			Main.saveMap(map);
		}
		
		if(ke.getKeyCode()==KeyEvent.VK_X)
		{
			if(selectedType==SelectedType.TERRAIN)
			{
				if(Main.terrainMap.get(selected)!=null)
				{
					if(Main.terrainMap.get(selected) instanceof Building==false)
						map.setTerrain(Main.terrainMap.get(selected), cursor().x, cursor().y);
					else
						map.setTerrain(Building.copy((Building)Main.terrainMap.get(selected),Main.teamMap.get(selectedTeam)), cursor().x, cursor().y);
				}
			}
			if(selectedType==SelectedType.UNIT)
			{
				if(Main.unitMap.get(selected)!=null)
					map.setUnit(Unit.copy(Main.unitMap.get(selected),Main.teamMap.get(selectedTeam),true),cursor().x,cursor().y);
			}
		}
		
		if(ke.getKeyCode()==KeyEvent.VK_Z)
		{
			if(selectedType==SelectedType.TERRAIN)
			{
				map.setTerrain(Main.terrainMap.get(0), cursor().x, cursor().y);
			}
			if(selectedType==SelectedType.UNIT)
			{
				map.setUnit(null,cursor().x,cursor().y);
			}
		}
		
		if(ke.getKeyCode()>=KeyEvent.VK_1&&ke.getKeyCode()<=KeyEvent.VK_1+Main.teamMap.size())
		{
			selectedTeam=ke.getKeyCode()-KeyEvent.VK_1;
			System.out.println("team: "+selectedTeam);
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void buttonPressed(Menu menu)
	{
		if(menu instanceof SelectionMenu)
		{
			SelectionMenu selectionMenu=(SelectionMenu)menu;
			selected=selectionMenu.getSelection();
			if(selectionMenu.type().isAssignableFrom(Unit.class))
			{
				selectedType=SelectedType.UNIT;
			}
			if(selectionMenu.type().isAssignableFrom(Terrain.class))
			{
				selectedType=SelectedType.TERRAIN;
			}
		}
	}

	@Override
	public void menuClosed(Menu menu)
	{
		// TODO Auto-generated method stub
		
	}
	
	private int selected;
	
	private SelectedType selectedType;
	
	private int selectedTeam;
	
	private Map map;
	
	enum SelectedType{UNIT,TERRAIN}
}
