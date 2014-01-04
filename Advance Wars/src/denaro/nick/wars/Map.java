package denaro.nick.wars;


import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import denaro.nick.core.Focusable;
import denaro.nick.core.Location;
import denaro.nick.core.Sprite;

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
		attackableArea=null;
		attackableUnits=null;
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
	
	public Unit unitIfVisible(int x, int y)
	{
		if(weather.fog())
		{
			if(!fog(x,y))
				return(units[x][y]);
			else
				return(null);
		}
		else
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
	
	public int buildingCount(Team team)
	{
		int count=0;
		for(int y=0;y<height;y++)
		{
			for(int x=0;x<width;x++)
			{
				if(terrain[x][y] instanceof Building)
				{
					Building building=(Building)terrain[x][y];
					if(building.team()==team)
						count++;
				}
			}
		}
		return(count);
	}
	
	public Point cursor()
	{
		return(cursor);
	}
	
	public void cursor(Point point)
	{
		cursor=point;
	}
	
	public Unit selectedUnit()
	{
		return(selectedUnit);
	}
	
	public boolean moveableArea(int x, int y)
	{
		return(moveableArea[x][y]);
	}
	
	public boolean[][] moveableArea()
	{
		return(moveableArea);
	}
	
	public boolean[][] attackableArea()
	{
		return(attackableArea);
	}
	
	public boolean attackableArea(int x, int y)
	{
		return(attackableArea[x][y]);
	}
	
	public void createAttackableArea(int x, int y, Unit unit)
	{
		if(unit.attackRange().x==1)
		{
			attackableArea=new boolean[width][height];
			moveableArea=new boolean[width][height];
			createMoveableArea(x,y,unit,unit.movement());
			for(int a=0;a<height;a++)
			{
				for(int i=0;i<width;i++)
				{
					if(moveableArea[i][a])
					{
						if(i>0)
							attackableArea[i-1][a]=true;
						if(i+1<width)
							attackableArea[i+1][a]=true;
						if(a>0)
							attackableArea[i][a-1]=true;
						if(a+1<height)
							attackableArea[i][a+1]=true;
					}
				}
			}
			moveableArea=null;
		}
		else
		{
			attackableArea=new boolean[width][height];
			int minRange=unit.attackRange().x;
			int maxRange=unit.attackRange().y;
			for(int a=-maxRange;a<maxRange*2;a++)
			{
				for(int i=-maxRange;i<maxRange*2;i++)
				{
					int dist=Math.abs(i)+Math.abs(a);
					if(dist>=unit.attackRange().x&&dist<=unit.attackRange().y)
					{
						if((cursor.x+i>=0)&&(cursor.x+i<width)&&(cursor.y+a>=0)&&(cursor.y+a<height))
							attackableArea[cursor.x+i][cursor.y+a]=true;
					}
				}
			}
		}
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
				if(terrain[i][a] instanceof Building)
				{
					Building building=(Building)terrain[i][a];
					if(building.team()==team)
						fog[i][a]=true;
				}
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
	
	public boolean moveUnitAlongPath(Unit unit, Path path)
	{
		boolean trap=false;
		ArrayList<Point> points=path.points();
		if(points.size()>1)
		{
			//reset the capture for a building
			if(terrain[points.get(0).x][points.get(0).y] instanceof Building)
			{
				Building building=(Building)terrain[points.get(0).x][points.get(0).y];
				building.health(20);
			}
		}
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
				{
					//TODO Display message "Trap!"
					trap=true;
					break;
				}
			}
		}
		i--;
		if(!points.get(0).equals(points.get(i)))
		{
			if(units[points.get(i).x][points.get(i).y]==null)
			{
				units[points.get(i).x][points.get(i).y]=units[points.get(0).x][points.get(0).y];
				units[points.get(0).x][points.get(0).y]=null;
			}
			else
			{
				uniteUnit(points.get(0),points.get(i));
			}
		}
		unit.enabled(false);
		return(!trap);
	}
	
	public void clearFog(int x, int y, Unit unit, int count, boolean nextTo)
	{
		if(nextTo)
		{
			fog[x][y]=true;
			if(x>0)
				fog[x-1][y]=true;
			if(x+1<width)
				fog[x+1][y]=true;
			if(y>0)
				fog[x][y-1]=true;
			if(x+1<height)
				fog[x][y+1]=true;
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
	
	public boolean moveUnit()
	{
		boolean notTrap=this.moveUnitAlongPath(selectedUnit, path);
		selectedUnit=null;
		path=null;
		moveableArea=new boolean[width][height];
		return(notTrap);
	}
	
	public void uniteUnit(Point start, Point end)
	{
		if((units[start.x][start.y]!=null)&&(units[end.x][end.y]!=null))
		{
			units[end.x][end.y].uniteWith(units[start.x][start.y]);
			units[start.x][start.y]=null;
		}
	}
	
	public double calculateDamage(Unit attacker, Unit defender, Point defenderLocation)
	{
		double base=Unit.baseDamage(attacker,defender);
		int aco=attacker.team().commander().attackPower();
		int r=((int)(Math.random()*10));
		
		int ahp=attacker.health()/10;
		
		int dco=defender.team().commander().defencePower();
		int tdf=terrain[defenderLocation.x][defenderLocation.y].defence();
		int dhp=defender.health()/10;
		
		double damage=(base*(aco/100.0)+r)*ahp/10.0*(((200.0-(dco+tdf*dhp))/100.0));
		
		return(damage);
	}
	
	public void destroyUnit(Point point)
	{
		units[point.x][point.y]=null;
		//TODO dead particle effect;
		//TODO determine if team loses;
	}
	
	public void attackUnit(Point attackerPoint, Point defenderPoint)
	{
		Unit attacker=units[attackerPoint.x][attackerPoint.y];
		Unit defender=units[defenderPoint.x][defenderPoint.y];
		if(moveUnit())
		{
			double damage=calculateDamage(attacker,defender,defenderPoint);
			defender.damage((int)damage);
			
			if(defender.health()>0)
			{
				if(attacker.attackRange().y==1&&defender.attackRange().y==1)
				{
					damage=calculateDamage(defender,attacker,attackerPoint);
					attacker.damage((int)damage);
					if(attacker.health()<=0)
					{
						destroyUnit(attackerPoint);
					}
				}
			}
			else
			{
				destroyUnit(defenderPoint);
			}
		}
		//else was trapped
	}
	
	public boolean unitCanMove()
	{
		if(unitIfVisible(cursor.x,cursor.y)!=null&&units[cursor.x][cursor.y]!=selectedUnit)
			return(false);
		return(true);
	}
	
	public boolean unitCanUnite()
	{
		if(path.first().equals(path.last()))
			return(false);
		if(units[cursor.x][cursor.y]==null)
			return(false);
		else if(units[cursor.x][cursor.y].team()!=selectedUnit.team())
			return(false);
		else if(units[cursor.x][cursor.y].health()==100)
			return(false);
		return(true);
	}
	
	public ArrayList<Point> attackableUnits()
	{
		if(selectedUnit.attackRange().y!=1)
		{
			if(path.points().size()>1)
				return(null);
			else
			{
				ArrayList<Point> points=new ArrayList<Point>();
				createAttackableArea(path.last().x,path.last().y,selectedUnit);
				for(int a=0;a<height;a++)
				{
					for(int i=0;i<width;i++)
					{
						if(attackableArea[i][a]&&unitIfVisible(i,a)!=null&&units[i][a].team()!=selectedUnit.team())
						{
							points.add(new Point(i,a));
						}
					}
				}
				attackableArea=null;
				return(points);
			}
		}
		else
		{
			return(enemiesNextToUnit(selectedUnit));
		}
	}
	
	public boolean isEnemyNextToUnit(Unit unit)
	{
		return(!enemiesNextToUnit(unit).isEmpty());
	}
	
	public ArrayList<Point> enemiesNextToUnit(Unit unit)
	{
		ArrayList<Point> points=new ArrayList<Point>();
		if(path.last().x-1>=0)
			if(unitIfVisible(path.last().x-1,path.last().y)!=null&&units[path.last().x-1][path.last().y].team()!=unit.team())
				points.add(new Point(path.last().x-1,path.last().y));
		if(path.last().x+1<width)
			if(unitIfVisible(path.last().x+1,path.last().y)!=null&&units[path.last().x+1][path.last().y].team()!=unit.team())
				points.add(new Point(path.last().x+1,path.last().y));
		if(path.last().y-1>=0)
			if(unitIfVisible(path.last().x,path.last().y-1)!=null&&units[path.last().x][path.last().y-1].team()!=unit.team())
				points.add(new Point(path.last().x,path.last().y-1));
		if(path.last().y-1<height)
			if(unitIfVisible(path.last().x,path.last().y+1)!=null&&units[path.last().x][path.last().y+1].team()!=unit.team())
				points.add(new Point(path.last().x,path.last().y+1));
		return(points);
	}
	
	public boolean unitCanAttack()
	{
		if(selectedUnit.attackRange().x!=1)
		{
			if(path.points().size()>1)
				return(false);
			else
			{
				createAttackableArea(path.last().x,path.last().y,selectedUnit);
				for(int a=0;a<height;a++)
				{
					for(int i=0;i<width;i++)
					{
						if(attackableArea[i][a]&&units[i][a]!=null&&units[i][a].team()!=selectedUnit.team())
						{
							attackableArea=null;
							return(true);
						}
					}
				}
				attackableArea=null;
				return(false);
			}
		}
		else
		{
			return(isEnemyNextToUnit(selectedUnit));
		}
	}
	
	public void captureUnit(Unit unit, Point destination)
	{
		if(moveUnit())
		{
			Building building=(Building)terrain[destination.x][destination.y];
			building.damage((unit.health()+5)/10);
			if(building.health()<=0)
			{
				building.health(20);
				building.team(unit.team());
			}
		}
		//else was trapped
	}
	
	public boolean unitCanCapture()
	{
		if(!selectedUnit.canCapture())
			return(false);
		if(unitCanMove())
		{
			if(terrain[path.last().x][path.last().y] instanceof Building==false)
				return(false);
			else if(((Building)terrain[path.last().x][path.last().y]).team()==selectedUnit.team())
				return(false);
			else
				return(true);
		}
		return(false);
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
				else if(units[cursor.x][cursor.y]!=null)
				{
					moveableArea=new boolean[width][height];
					createMoveableArea(cursor.x,cursor.y,units[cursor.x][cursor.y],units[cursor.x][cursor.y].movement());
				}
				else if(terrain[cursor.x][cursor.y] instanceof Building)
				{
					Building building=(Building)terrain[cursor.x][cursor.y];
					Main.menu=new BuyMenu(null,new Point(0,Main.engine().view().getHeight()-Sprite.sprite("Buy Menu").height()),building);
					Main.engine().requestFocus(Main.menu);
				}
			}
			else if(cursor.equals(path.last()))
			{
				ArrayList<String> options=new ArrayList<String>();
				if(unitCanMove())
					options.add("Move");
				if(unitCanUnite())
					options.add("Unite");
				if(unitCanAttack())
					options.add("Attack");
				if(unitCanCapture())
					options.add("Capture");
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
			if(selectedUnit==null)
			{
				if((unitIfVisible(cursor.x,cursor.y)!=null))
				{
					if(attackableArea==null)
						createAttackableArea(cursor.x,cursor.y,units[cursor.x][cursor.y]);
				}
			}
			else
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
		if(ke.getKeyCode()==KeyEvent.VK_X)
		{
			if(moveableArea!=null&&path==null)
				moveableArea=null;
		}
		
		if(ke.getKeyCode()==KeyEvent.VK_Z)
		{
			attackableArea=null;
		}
		
	}
	
	private Point cursor;
	
	private Unit selectedUnit;
	
	private int width, height;
	
	private String name;
	
	private Unit[][] units;
	
	private Terrain terrain[][];
	
	private boolean[][] moveableArea;
	
	private boolean[][] attackableArea;
	
	private ArrayList<Point> attackableUnits;
	
	private Weather weather;
	
	private boolean[][] fog;
	
	private Path path;
}
