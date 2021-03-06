package denaro.nick.wars;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
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
		terrainDirections=new TerrainDirections(this);
	}
	
	public BufferedImage minimap()
	{
		BufferedImage image=new BufferedImage(width*Main.MINIMAPSIZE,height*Main.MINIMAPSIZE,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=image.createGraphics();
		
		for(int a=0;a<height;a++)
		{
			for(int i=0;i<width;i++)
			{
				Terrain terrain=terrain(i,a);
				g.setColor(terrain.color());
				if(terrain instanceof Building)
				{
					Building building=(Building)terrain;
					if(building.team()!=null)
						g.setColor(Main.colorPalette.getColor(1+building.team().id()));
					else
						g.setColor(Main.colorPalette.getColor(0));
				}
				g.fillRect(i*Main.MINIMAPSIZE,a*Main.MINIMAPSIZE,Main.MINIMAPSIZE,Main.MINIMAPSIZE);
				if(terrain instanceof Building)
				{
					g.setColor(Color.black);
					g.drawRect(i*Main.MINIMAPSIZE,a*Main.MINIMAPSIZE,Main.MINIMAPSIZE-1,Main.MINIMAPSIZE-1);
				}
			}
		}
		
		for(int a=0;a<height;a++)
		{
			for(int i=0;i<width;i++)
			{
				Unit unit=unit(i,a);
				if(unit!=null)
				{
					g.setColor(Main.colorPalette.getColor(unit.team().id()+1));
					g.fillOval(i*Main.MINIMAPSIZE,a*Main.MINIMAPSIZE,Main.MINIMAPSIZE,Main.MINIMAPSIZE);
					g.setColor(Color.black);
					g.drawOval(i*Main.MINIMAPSIZE,a*Main.MINIMAPSIZE,Main.MINIMAPSIZE-1,Main.MINIMAPSIZE-1);
				}
			}
		}
		
		g.dispose();
		return(image);
	}
	
	public TerrainDirections terrainDirections()
	{
		return(terrainDirections);
	}
	
	@Override
	public void tick()
	{
		super.tick();
		if(Main.currentMode instanceof Battle)
		{
			Battle battle=(Battle)Main.currentMode;
			battle.performAction();
		}
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
		{
			units[x][y]=unit;
			if(unit!=null)
			{
				if(Main.engine()!=null)
					Main.engine().addEntity(unit,this);
				unit.move(x*Main.TILESIZE,y*Main.TILESIZE);
			}
		}
		else
			System.out.println("ERROR: Unit already exists at this location: "+x+", "+y);
	}
	
	public void setUnit(Unit unit, int x, int y)
	{
		if(Main.engine()!=null)
		{
			if(units[x][y]!=null)
				Main.engine().removeEntity(units[x][y],this);
			if(unit!=null)
			{
				Main.engine().addEntity(unit,this);
				unit.move(x*Main.TILESIZE,y*Main.TILESIZE);
			}
		}
		units[x][y]=unit;
	}
	
	public boolean checkIfHQExists(Team team)
	{
		for(int y=0;y<height;y++)
		{
			for(int x=0;x<width;x++)
			{
				if(terrain(x,y) instanceof Building)
				{
					Building building=(Building)terrain(x,y);
					if(building.hq()&&Team.sameTeam(building.team(),team))
						return(true);
				}
			}
		}
		return(false);
	}
	
	public void setTerrain(Terrain terrain, int x, int y)
	{
		if(terrain instanceof Building)
		{
			Building building=(Building)terrain;
			if(building.hq()&&(checkIfHQExists(building.team())||building.team()==null))
			{
				if(building.team()==null)
					System.out.println("ERROR: HQ must be on a team");
				else
					System.out.println("ERROR: HQ already exists for team: "+building.team().name());
			}
			else
			{
				this.terrain[x][y]=terrain;
			}
		}
		else
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
	
	public ArrayList<Integer> teams()
	{
		ArrayList<Integer> teams=new ArrayList<Integer>();
		for(int y=0;y<height;y++)
		{
			for(int x=0;x<width;x++)
			{
				if(terrain(x,y) instanceof Building)
				{
					Building building=(Building)terrain(x,y);
					Team team=building.team();
					if(team!=null)
					{
						if(!teams.contains(team.id()))
						{
							teams.add(building.team().id());
						}
					}
				}
				if(unit(x,y)!=null)
				{
					if(unit(x,y).team()!=null)
					{
						if(!teams.contains(unit(x,y).team().id()))
						{
							teams.add(unit(x,y).team().id());
						}
					}
				}
			}
		}
		
		return(teams);
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
					if(Team.sameTeam(building.team(),team))
						count++;
				}
			}
		}
		return(count);
	}
	
	public boolean teamHasBuilding(Team team, int buildingId)
	{
		for(int y=0;y<height;y++)
		{
			for(int x=0;x<width;x++)
			{
				if(terrain(x,y) instanceof Building)
				{
					Building building=(Building)terrain(x,y);
					if(Team.sameTeam(building.team(),team)&&building.id()==buildingId)
						return(true);
				}
			}
		}
		return(false);
	}
	
	public boolean teamHasHQ(Team team)
	{
		for(int y=0;y<height;y++)
		{
			for(int x=0;x<width;x++)
			{
				if(terrain(x,y) instanceof Building)
				{
					Building building=(Building)terrain(x,y);
					if(Team.sameTeam(building.team(),team)&&building.hq())
						return(true);
				}
			}
		}
		return(false);
	}
	
	public boolean teamHasUnits(Team team)
	{
		for(int y=0;y<height;y++)
		{
			for(int x=0;x<width;x++)
			{
				if(unit(x,y)!=null)
				{
					if(Team.sameTeam(unit(x,y).team(),team))
						return(true);
				}
			}
		}
		return(false);
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
	
	private TerrainDirections terrainDirections;

}
