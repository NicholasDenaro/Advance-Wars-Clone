package denaro.nick.wars;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import denaro.nick.core.Focusable;
import denaro.nick.core.Sprite;

public class Battle implements KeyListener, Focusable
{
	public Battle(Map map, Team[] teams)
	{
		this.map=map;
		this.teams=teams;
		turn=-1;
		cursor=new Point(0,0);
		fog=new boolean[map.width()][map.height()];
		weather=Weather.sunny;
		nextTurn();
	}
	
	public Map map()
	{
		return(map);
	}
	
	public Team whosTurn()
	{
		return(teams[turn]);
	}
	
	public void nextTurn()
	{
		turn=++turn%teams.length;
		resetFog();
		clearFogForTeam(whosTurn());
		enableUnitsForTeam(whosTurn());
		
		int count=map.buildingCount(whosTurn());
		whosTurn().addFunds(count*1000);
	}
	
	//-------------------------------------------------------------------------------
	
	public void enableUnitsForTeam(Team team)
	{
		for(int a=0;a<map.height();a++)
		{
			for(int i=0;i<map.width();i++)
			{
				if(map.unit(i,a)!=null)
				{
					if(map.unit(i,a).team()==team)
					{
						map.unit(i,a).enabled(true);
					}
				}
			}
		}
	}
	
	public Path path()
	{
		return(path);
	}
	
	public Unit selectedUnit()
	{
		return(selectedUnit);
	}
	
	public Point cursor()
	{
		return(cursor);
	}
	
	public void cursor(Point point)
	{
		cursor=point;
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
	
	public void weather(Weather weather)
	{
		this.weather=weather;
	}
	
	public Weather weather()
	{
		return(weather);
	}
	
	public boolean fog(int x, int y)
	{
		return(!fog[x][y]);
	}
	
	public void resetFog()
	{
		fog=new boolean[map.width()][map.height()];
	}
	
	public void clearFog(int x, int y, Unit unit, int count, boolean nextTo)
	{
		if(nextTo)
		{
			fog[x][y]=true;
			if(x>0)
				fog[x-1][y]=true;
			if(x+1<map.width())
				fog[x+1][y]=true;
			if(y>0)
				fog[x][y-1]=true;
			if(y+1<map.height())
				fog[x][y+1]=true;
		}
		if(count>=0)
		{
			if(map.terrain(x,y)!=null)
				if(!map.terrain(x,y).hiding())
					fog[x][y]=true;
			if(x>0)
				clearFog(x-1,y,unit,count-1,false);
			if(x+1<map.width())
				clearFog(x+1,y,unit,count-1,false);
			if(y>0)
				clearFog(x,y-1,unit,count-1,false);
			if(x+1<map.height())
				clearFog(x,y+1,unit,count-1,false);
		}
	}
	
	public void clearFogForTeam(Team team)
	{
		for(int a=0;a<map.height();a++)
		{
			for(int i=0;i<map.width();i++)
			{
				if(map.terrain(i,a) instanceof Building)
				{
					Building building=(Building)map.terrain(i,a);
					if(building.team()==team)
						fog[i][a]=true;
				}
				if(map.unit(i,a)!=null)
				{
					if(map.unit(i,a).team()==team)
					{
						clearFog(i,a,map.unit(i,a));
					}
				}
			}
		}
	}
	
	public Unit unitIfVisible(int x, int y)
	{
		if(weather.fog())
		{
			if(!fog(x,y))
				return(map.unit(x,y));
			else
				return(null);
		}
		else
			return(map.unit(x,y));
	}
	
	public void clearFog(int x, int y, Unit unit)
	{
		clearFog(x,y,unit,unit.vision()+map.terrain(x,y).visionBoost()-weather.visionLoss(),true);
	}
	
	public void createMoveableArea(int x, int y, Unit unit, int count)
	{
		
		if(fog(x,y)||(map.unit(x,y)==null)||(map.unit(x,y).team()==unit.team()))
		{
			if(count>=0)
			{
				moveableArea[x][y]=true;
				if(x>0)
					createMoveableArea(x-1,y,unit,count-map.terrain(x-1,y).movementCost(unit.movementType()));
				if(x+1<map.width())
					createMoveableArea(x+1,y,unit,count-map.terrain(x+1,y).movementCost(unit.movementType()));
				if(y>0)
					createMoveableArea(x,y-1,unit,count-map.terrain(x,y-1).movementCost(unit.movementType()));
				if(y+1<map.height())
					createMoveableArea(x,y+1,unit,count-map.terrain(x,y+1).movementCost(unit.movementType()));
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
			if(map.terrain(points.get(0).x,points.get(0).y) instanceof Building)
			{
				Building building=(Building)map.terrain(points.get(0).x,points.get(0).y);
				building.health(20);
			}
		}
		int i=1;
		for(;i<points.size();i++)
		{
			if(map.unit(points.get(i).x,points.get(i).y)==null)
			{
				clearFog(points.get(i).x,points.get(i).y,unit);
			}
			else
			{
				if(unit.team()!=map.unit(points.get(i).x,points.get(i).y).team())
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
			if(map.unit(points.get(i).x,points.get(i).y)==null)
			{
				map.moveUnit(points.get(0),points.get(i));
				/*units[points.get(i).x][points.get(i).y]=unit(points.get(0).x,points.get(0).y);
				units[points.get(0).x][points.get(0).y]=null;*/
			}
			else
			{
				uniteUnit(points.get(0),points.get(i));
			}
		}
		unit.enabled(false);
		return(!trap);
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
		moveableArea=new boolean[map.width()][map.height()];
		return(notTrap);
	}
	
	public void uniteUnit(Point start, Point end)
	{
		if((map.unit(start.x,start.y)!=null)&&(map.unit(end.x,end.y)!=null))
		{
			map.unit(end.x,end.y).uniteWith(map.unit(start.x,start.y));
			map.addUnit(null,start.x,start.y);
		}
	}
	
	public double calculateDamage(Unit attacker, Unit defender, Point defenderLocation)
	{
		double base=Unit.baseDamage(attacker,defender);
		int aco=attacker.team().commander().attackPower();
		int r=((int)(Math.random()*10));
		
		int ahp=attacker.health()/10;
		
		int dco=defender.team().commander().defencePower();
		int tdf=map.terrain(defenderLocation.x,defenderLocation.y).defence();
		int dhp=defender.health()/10;
		
		double damage=(base*(aco/100.0)+r)*ahp/10.0*(((200.0-(dco+tdf*dhp))/100.0));
		
		return(damage);
	}
	
	public void destroyUnit(Point point)
	{
		//units[point.x][point.y]=null;
		map.addUnit(null,point.x,point.y);
		//TODO dead particle effect;
		//TODO determine if team loses;
	}
	
	public void createAttackableArea(int x, int y, Unit unit)
	{
		if(unit.attackRange().x==1)
		{
			attackableArea=new boolean[map.width()][map.height()];
			moveableArea=new boolean[map.width()][map.height()];
			createMoveableArea(x,y,unit,unit.movement());
			for(int a=0;a<map.height();a++)
			{
				for(int i=0;i<map.width();i++)
				{
					if(moveableArea[i][a])
					{
						if(i>0)
							attackableArea[i-1][a]=true;
						if(i+1<map.width())
							attackableArea[i+1][a]=true;
						if(a>0)
							attackableArea[i][a-1]=true;
						if(a+1<map.height())
							attackableArea[i][a+1]=true;
					}
				}
			}
			moveableArea=null;
		}
		else
		{
			attackableArea=new boolean[map.width()][map.height()];
			int minRange=unit.attackRange().x;
			int maxRange=unit.attackRange().y;
			for(int a=-maxRange;a<maxRange*2;a++)
			{
				for(int i=-maxRange;i<maxRange*2;i++)
				{
					int dist=Math.abs(i)+Math.abs(a);
					if(dist>=unit.attackRange().x&&dist<=unit.attackRange().y)
					{
						if((cursor.x+i>=0)&&(cursor.x+i<map.width())&&(cursor.y+a>=0)&&(cursor.y+a<map.height()))
							attackableArea[cursor.x+i][cursor.y+a]=true;
					}
				}
			}
		}
	}
	
	public void attackUnit(Point attackerPoint, Point defenderPoint)
	{
		Unit attacker=map.unit(attackerPoint.x,attackerPoint.y);
		Unit defender=map.unit(defenderPoint.x,defenderPoint.y);
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
		if(unitIfVisible(cursor.x,cursor.y)!=null&&map.unit(cursor.x,cursor.y)!=selectedUnit)
			return(false);
		return(true);
	}
	
	public boolean unitCanUnite()
	{
		if(path.first().equals(path.last()))
			return(false);
		if(map.unit(cursor.x,cursor.y)==null)
			return(false);
		else if(map.unit(cursor.x,cursor.y).team()!=selectedUnit.team())
			return(false);
		else if(map.unit(cursor.x,cursor.y).health()==100)
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
				for(int a=0;a<map.height();a++)
				{
					for(int i=0;i<map.width();i++)
					{
						if(attackableArea[i][a]&&unitIfVisible(i,a)!=null&&map.unit(i,a).team()!=selectedUnit.team())
						{
							if(selectedUnit.weapon1()!=null&&selectedUnit.weapon1().isEffectiveAggainst(map.unit(i,a).unitID()))
								points.add(new Point(i,a));
							else if(selectedUnit.ammo()>0&&selectedUnit.weapon2().isEffectiveAggainst(map.unit(i,a).unitID()))
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
			if(unitIfVisible(path.last().x-1,path.last().y)!=null&&map.unit(path.last().x-1,path.last().y).team()!=unit.team())
				points.add(new Point(path.last().x-1,path.last().y));
		if(path.last().x+1<map.width())
			if(unitIfVisible(path.last().x+1,path.last().y)!=null&&map.unit(path.last().x+1,path.last().y).team()!=unit.team())
				points.add(new Point(path.last().x+1,path.last().y));
		if(path.last().y-1>=0)
			if(unitIfVisible(path.last().x,path.last().y-1)!=null&&map.unit(path.last().x,path.last().y-1).team()!=unit.team())
				points.add(new Point(path.last().x,path.last().y-1));
		if(path.last().y+1<map.height())
			if(unitIfVisible(path.last().x,path.last().y+1)!=null&&map.unit(path.last().x,path.last().y+1).team()!=unit.team())
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
				for(int a=0;a<map.height();a++)
				{
					for(int i=0;i<map.width();i++)
					{
						if(attackableArea[i][a]&&map.unit(i,a)!=null&&map.unit(i,a).team()!=selectedUnit.team())
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
			Building building=(Building)map.terrain(destination.x,destination.y);
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
			if(map.terrain(path.last().x,path.last().y) instanceof Building==false)
				return(false);
			else if(((Building)map.terrain(path.last().x,path.last().y)).team()==selectedUnit.team())
				return(false);
			else
				return(true);
		}
		return(false);
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
		if(cursor.x<map.width()-1)
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
		if(cursor.y<map.height()-1)
			cursor.y++;
		if(path!=null&&moveableArea[cursor.x][cursor.y])
			path.addPoint(cursor.x, cursor.y);
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
				if((map.unit(cursor.x,cursor.y)!=null)&&(map.unit(cursor.x,cursor.y).enabled())&&(map.unit(cursor.x,cursor.y).team()==Main.battle.whosTurn()))
				{
					selectedUnit=map.unit(cursor.x,cursor.y);
					moveableArea=new boolean[map.width()][map.height()];
					createMoveableArea(cursor.x,cursor.y,map.unit(cursor.x,cursor.y),map.unit(cursor.x,cursor.y).movement());
					path=new Path(map.unit(cursor.x,cursor.y).movementType(),map.unit(cursor.x,cursor.y).movement());
					path.start(cursor.x,cursor.y);
				}
				else if(unitIfVisible(cursor.x,cursor.y)!=null)
				{
					moveableArea=new boolean[map.width()][map.height()];
					createMoveableArea(cursor.x,cursor.y,map.unit(cursor.x,cursor.y),map.unit(cursor.x,cursor.y).movement());
				}
				else if(map.terrain(cursor.x,cursor.y) instanceof Building)
				{
					Building building=(Building)map.terrain(cursor.x,cursor.y);
					if(building.team()==Main.battle.whosTurn())
					{
						Main.menu=new BuyMenu(null,new Point(0,Main.engine().view().getHeight()-Sprite.sprite("Buy Menu").height()),building);
						Main.engine().requestFocus(Main.menu);
					}
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
						createAttackableArea(cursor.x,cursor.y,map.unit(cursor.x,cursor.y));
				}
			}
			else
			{
				selectedUnit=null;
				path=null;
				moveableArea=new boolean[map.width()][map.height()];
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
	
	//-------------------------------------------------------------------------------
	private Map map;
	
	private Unit selectedUnit;
	
	private Path path;
	
	private Point cursor;
	
	private boolean[][] moveableArea;
	
	private boolean[][] attackableArea;
	
	private boolean[][] fog;
	
	private Weather weather;
	
	private int turn;
	
	private Team[] teams;
}
