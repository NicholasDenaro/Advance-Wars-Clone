package denaro.nick.wars;


import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import denaro.nick.core.Focusable;
import denaro.nick.core.Location;
import denaro.nick.core.Sprite;

public class Map extends Location
{

	public Map(String name, int width, int height)
	{
		this.name=name;
		this.width=width;
		this.height=height;
		units=new Unit[width][height];
		terrain=new Terrain[width][height];
		//attackableUnits=null;
	}
	
	public String name()
	{
		return(name);
	}
	
	public Unit unit(int x, int y)
	{
		return(units[x][y]);
	}
	
	public void addUnit(Unit unit, int x, int y)
	{
		if(unit(x,y)==null)
			units[x][y]=unit;
	}
	
	public void setTerrain(Terrain terrain, int x, int y)
	{
		this.terrain[x][y]=terrain;
	}
	
	
	public int width()
	{
		return(width);
	}
	
	public int height()
	{
		return(height);
	}
	
	public Terrain terrain(int x, int y)
	{
		if(x>=0&&y>=0&&x<width&&y<height)
			return(terrain[x][y]);
		return(null);
	}
	
	public int buildingCount(Team team)
	{
		int count=0;
		for(int y=0;y<height;y++)
		{
			for(int x=0;x<width;x++)
			{
				if(terrain(x,y) instanceof Building)
				{
					Building building=(Building)terrain(x,y);
					if(building.team()==team)
						count++;
				}
			}
		}
		return(count);
	}
	
	
	
	public void moveUnit(Point start, Point end)
	{
		units[end.x][end.y]=unit(start.x,start.y);
		units[start.x][start.y]=null;
	}
	
	
	private int width, height;
	
	private String name;
	
	private Unit[][] units;
	
	private Terrain[][] terrain;
	
	//private ArrayList<Point> attackableUnits;

}
