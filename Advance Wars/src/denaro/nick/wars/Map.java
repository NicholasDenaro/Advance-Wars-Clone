package denaro.nick.wars;


import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import denaro.nick.core.Focusable;
import denaro.nick.core.Location;

public class Map extends Location implements KeyListener, Focusable
{

	public Map(String name, int width, int height)
	{
		this.name=name;
		this.width=width;
		this.height=height;
		units=new Unit[width][height];
		terrain=new Terrain[width][height];
		fog=new boolean[width][height];
		path=null;
		cursor=new Point(0,0);
		moveableArea=null;
		weather=Weather.sunny;
	}
	
	public String name()
	{
		return(name);
	}
	
	public Path path()
	{
		return(path);
	}
	
	public void weather(Weather weather)
	{
		this.weather=weather;
	}
	
	public Weather weather()
	{
		return(weather);
	}
	
	public Unit unit(int x, int y)
	{
		return(units[x][y]);
	}
	
	public void addUnit(Unit unit, int x, int y)
	{
		if(units[x][y]==null)
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
	
	public Point cursor()
	{
		return(cursor);
	}
	
	public boolean moveableArea(int x, int y)
	{
		return(moveableArea[x][y]);
	}
	
	
	public void createMoveableArea(int x, int y, Unit unit, int count)
	{
		
		if(fog(x,y)||(units[x][y]==null)||(units[x][y].team()==unit.team()))
		{
			if(count>=0)
			{
				moveableArea[x][y]=true;
				if(x>0)
					createMoveableArea(x-1,y,unit,count-terrain[x-1][y].movementCost(unit.movementType()));
				if(x+1<width)
					createMoveableArea(x+1,y,unit,count-terrain[x+1][y].movementCost(unit.movementType()));
				if(y>0)
					createMoveableArea(x,y-1,unit,count-terrain[x][y-1].movementCost(unit.movementType()));
				if(y+1<height)
					createMoveableArea(x,y+1,unit,count-terrain[x][y+1].movementCost(unit.movementType()));
			}
		}
	}
	
	public boolean fog(int x, int y)
	{
		return(!fog[x][y]);
	}
	
	public void resetFog()
	{
		fog=new boolean[width][height];
	}
	
	public void clearFogForTeam(Team team)
	{
		for(int a=0;a<height;a++)
		{
			for(int i=0;i<width;i++)
			{
				if(units[i][a]!=null)
				{
					if(units[i][a].team()==team)
					{
						clearFog(i,a,units[i][a]);
					}
				}
			}
		}
	}
	
	public void clearFog(int x, int y, Unit unit)
	{
		if(weather.fog())
			clearFog(x,y,unit,unit.vision()+terrain[x][y].visionBoost()-weather.visionLoss(),true);
	}
	
	public void enableUnitsForTeam(Team team)
	{
		for(int a=0;a<height;a++)
		{
			for(int i=0;i<width;i++)
			{
				if(units[i][a]!=null)
				{
					if(units[i][a].team()==team)
					{
						units[i][a].enabled(true);
					}
				}
			}
		}
	}
	
	public void moveUnitAlongPath(Unit unit, Path path)
	{
		ArrayList<Point> points=path.points();
		int i=1;
		for(;i<points.size();i++)
		{
			if(units[points.get(i).x][points.get(i).y]==null)
			{
				clearFog(points.get(i).x,points.get(i).y,unit);
			}
			else
			{
				if(unit.team()!=units[points.get(i).x][points.get(i).y].team())
					break;
			}
		}
		units[points.get(--i).x][points.get(i).y]=units[points.get(0).x][points.get(0).y];
		units[points.get(0).x][points.get(0).y]=null;
		unit.enabled(false);
	}
	
	public void clearFog(int x, int y, Unit unit, int count, boolean nextTo)
	{
		if(nextTo)
		{
			fog[x][y]=true;
		}
		if(count>=0)
		{
			if(terrain(x,y)!=null)
				if(!terrain[x][y].hiding())
					fog[x][y]=true;
			if(x>0)
				clearFog(x-1,y,unit,count-1,false);
			if(x+1<width)
				clearFog(x+1,y,unit,count-1,false);
			if(y>0)
				clearFog(x,y-1,unit,count-1,false);
			if(x+1<height)
				clearFog(x,y+1,unit,count-1,false);
		}
	}
	
	public void moveCursorLeft()
	{
		if(cursor.x>0)
			cursor.x--;
		if(path!=null&&moveableArea[cursor.x][cursor.y])
			path.addPoint(cursor.x, cursor.y);
	}
	
	public void moveCursorRight()
	{
		if(cursor.x<width-1)
			cursor.x++;
		if(path!=null&&moveableArea[cursor.x][cursor.y])
			path.addPoint(cursor.x, cursor.y);
	}
	
	public void moveCursorUp()
	{
		if(cursor.y>0)
			cursor.y--;
		if(path!=null&&moveableArea[cursor.x][cursor.y])
			path.addPoint(cursor.x, cursor.y);
	}
	
	public void moveCursorDown()
	{
		if(cursor.y<height-1)
			cursor.y++;
		if(path!=null&&moveableArea[cursor.x][cursor.y])
			path.addPoint(cursor.x, cursor.y);
	}
	
	@Override
	public void keyTyped(KeyEvent ke)
	{
		//empty
	}
	
	public void moveUnit()
	{
		this.moveUnitAlongPath(selectedUnit, path);
		selectedUnit=null;
		path=null;
		moveableArea=new boolean[width][height];
	}
	
	public boolean unitCanMove()
	{
		if(units[cursor.x][cursor.y]!=null)
			return(false);
		return(true);
	}
	
	public boolean unitCanUnite()
	{
		if(units[cursor.x][cursor.y]==null)
			return(false);
		else if(units[cursor.x][cursor.y].team()!=selectedUnit.team())
			return(false);
		return(true);
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
			if(selectedUnit==null)
			{
				if((units[cursor.x][cursor.y]!=null)&&(units[cursor.x][cursor.y].enabled())&&(units[cursor.x][cursor.y].team()==Main.battle.whosTurn()))
				{
					selectedUnit=units[cursor.x][cursor.y];
					moveableArea=new boolean[width][height];
					createMoveableArea(cursor.x,cursor.y,units[cursor.x][cursor.y],units[cursor.x][cursor.y].movement());
					path=new Path(units[cursor.x][cursor.y].movementType(),units[cursor.x][cursor.y].movement());
					path.start(cursor.x,cursor.y);
				}
			}
			else if(cursor.equals(path.last()))
			{
				ArrayList<String> options=new ArrayList<String>();
				if(unitCanMove())
					options.add("Move");
				if(unitCanUnite())
					options.add("Unite");
				/*if(unitCanAttack())
					options.add("Attack");
				if(unitCanCapture())
					options.add("Capture");*/
				options.add("Cancel");
				//show menu
				if(options.size()>1)
				{
					Main.menu=new ActionMenu(null,new Point(cursor.x*Main.TILESIZE,cursor.y*Main.TILESIZE),options);
					Main.engine().requestFocus(Main.menu);
				}
			}
		}
		
		if(ke.getKeyCode()==KeyEvent.VK_Z)
		{
			if(selectedUnit!=null)
			{
				selectedUnit=null;
				path=null;
				moveableArea=new boolean[width][height];
			}
		}
		
		if(ke.getKeyCode()==KeyEvent.VK_ENTER)
		{
			Main.battle.nextTurn();
		}
	}

	@Override
	public void keyReleased(KeyEvent ke)
	{
		// TODO Auto-generated method stub
		
	}
	
	private Point cursor;
	
	private Unit selectedUnit;
	
	private int width, height;
	
	private String name;
	
	private Unit[][] units;
	
	private Terrain terrain[][];
	
	private boolean[][] moveableArea;
	
	private Weather weather;
	
	private boolean[][] fog;
	
	private Path path;
}
