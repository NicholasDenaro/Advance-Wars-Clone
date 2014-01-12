package denaro.nick.wars;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import denaro.nick.core.Focusable;
import denaro.nick.core.Sprite;

public class Battle extends GameMode implements CursorListener, BuildingListener, UnitListener
{
	public Battle(Map map, ArrayList<Team> teams)
	{
		this.map=map;
		createListeners();
		this.teams=teams;
		
		turn=-1;
		cursor(new Point(0,0));
		fog=new boolean[map.width()][map.height()];
		weather=Weather.sunny;
		this.addCursorListener(this);
		nextTurn();
	}
	
	private void createListeners()
	{
		for(int a=0;a<map.height();a++)
		{
			for(int i=0;i<map.width();i++)
			{
				if(map.terrain(i, a) instanceof Building)
				{
					Building building=(Building)map.terrain(i,a);
					building.addBuildingListener(this);
				}
				if(map.unit(i,a)!=null)
				{
					map.unit(i,a).addUnitListener(this);
				}
			}
		}
	}
	
	public Map map()
	{
		return(map);
	}
	
	public Team whosTurn()
	{
		return(teams.get(turn));
	}
	
	public void nextTurn()
	{
		turn=++turn%teams.size();
		resetFog();
		clearFogForTeam(whosTurn());
		enableUnitsForTeam(whosTurn());
		
		int count=map.buildingCount(whosTurn());
		whosTurn().addFunds(count*1000);
	}
	
	//-------------------------------------------------------------------------------
	
	public boolean[][] attackableArea()
	{
		return(attackableArea);
	}
	
	public boolean attackableArea(int x, int y)
	{
		return(attackableArea[x][y]);
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
							if(selectedUnit.weapon1()!=null&&selectedUnit.weapon1().isEffectiveAggainst(map.unit(i,a).defenceID()))
								points.add(new Point(i,a));
							else if(selectedUnit.ammo()>0&&selectedUnit.weapon2().isEffectiveAggainst(map.unit(i,a).defenceID()))
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
	
	@Override
	public void buildingCaptured(Building building, Team oldTeam, Team newTeam)
	{
		if(building.hq())
		{
			teamLoses(oldTeam);
			
			//check if newTeam has won;
			if(map.teams().size()==1)
			{
				System.out.println("The "+newTeam.name()+" army is victorious!");
			}
		}
		else
		{
			building.team(newTeam);
			if(oldTeam!=null&&checkLoseConditions(oldTeam))
			{
				teamLoses(oldTeam);
			}
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
		
		//old damage calculation
		//double damage=(base*(aco/100.0)+r)*ahp/10.0*(((200.0-(dco+tdf*dhp))/100.0));
		
		//System.out.print("base: "+base+"-");
		
		double damage=100*(base/100.0*aco/100.0*(100+r)/100.0*ahp/10.0*(200-dco)/100.0*(10-tdf)/10.0);
		
		//System.out.println("damage: "+damage);
		
		return(damage);
	}
	
	public boolean canUnitAttack()
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
			Point start=path.first();
			Point end=path.last();
			if(map.unit(end.x,end.y)!=null&&!start.equals(end))
				return(false);
			else
				return(isEnemyNextToUnit(selectedUnit));
		}
	}
	
	public boolean canUnitCapture()
	{
		if(!selectedUnit.canCapture())
			return(false);
		if(canUnitMove())
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
	
	public boolean canUnitLoad()
	{
		if(path.first().equals(path.last()))
			return(false);
		if(map.unit(cursor().x,cursor().y)==null)
			return(false);
		else if(map.unit(cursor().x,cursor().y).team()!=selectedUnit.team())
			return(false);
		else if(!map.unit(cursor().x,cursor().y).canHoldCargo(selectedUnit.id()))
			return(false);
		else if(!map.unit(cursor().x,cursor().y).hasCargoSpace())
			return(false);
		return(true);
	}
	
	public boolean canUnitMove()
	{
		if(unitIfVisible(cursor().x,cursor().y)!=null&&map.unit(cursor().x,cursor().y)!=selectedUnit)
			return(false);
		return(true);
	}
	
	public boolean canUnitUnite()
	{
		if(path.first().equals(path.last()))
			return(false);
		if(map.unit(cursor().x,cursor().y)==null)
			return(false);
		else if(map.unit(cursor().x,cursor().y).team()!=selectedUnit.team())
			return(false);
		else if(map.unit(cursor().x,cursor().y).health()==100)
			return(false);
		else if(map.unit(cursor().x, cursor().y).id()!=selectedUnit.id())
			return(false);
		return(true);
	}
	
	public boolean canUnitUnload()
	{
		if(!selectedUnit.hasCargo())
			return(false);
		for(int i=0;i<selectedUnit.cargoCount();i++)
			if(unitCanBePlaced(selectedUnit.cargo(i),cursor()))
				return(true);
		return(false);
	}
	
	public boolean checkLoseConditions(Team team)
	{
		boolean hasHQ=map.teamHasHQ(team);
		boolean hasUnits=map.teamHasUnits(team);
		boolean hasBases=map.teamHasBuilding(team,Main.base.id());
		//System.out.println("hq: "+hasHQ+" - units: "+hasUnits);
		if(!hasHQ&&!(hasUnits||hasBases))
		{
			return(true);
		}
		return(false);
	}
	
	public void clearFog(int x, int y, Unit unit)
	{
		clearFog(x,y,unit,unit.vision()+map.terrain(x,y).visionBoost()-weather.visionLoss(),true);
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
			if(y+1<map.height())
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
			//int minRange=unit.attackRange().x;
			int maxRange=unit.attackRange().y;
			for(int a=-maxRange;a<maxRange*2;a++)
			{
				for(int i=-maxRange;i<maxRange*2;i++)
				{
					int dist=Math.abs(i)+Math.abs(a);
					if(dist>=unit.attackRange().x&&dist<=unit.attackRange().y)
					{
						if((cursor().x+i>=0)&&(cursor().x+i<map.width())&&(cursor().y+a>=0)&&(cursor().y+a<map.height()))
							attackableArea[cursor().x+i][cursor().y+a]=true;
					}
				}
			}
		}
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
	
	public int columns()
	{
		return(map.width());
	}
	
	public void cursorMoved(Point cursor)
	{
		if(path!=null&&moveableArea[cursor.x][cursor.y])
			path.addPoint(cursor.x, cursor.y);
	}
	
	public void destroyUnit(Point point)
	{
		Unit unit=map.unit(point.x,point.y);
		map.setUnit(null,point.x,point.y);
		unit.destroyUnit();
		
		//TODO dead particle effect;
	}
	
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
	
	public boolean fog(int x, int y)
	{
		return(!fog[x][y]);
	}
	
	public boolean isEnemyNextToUnit(Unit unit)
	{
		return(!enemiesNextToUnit(unit).isEmpty());
	}
	
	public void loadUnit()
	{
		Unit unit=selectedUnit;
		if(moveUnit())
		{
			System.out.println("loaded!?");
			map.unit(cursor().x,cursor().y).addCargo(unit);
		}
		//else was trapped
	}
	
	public boolean[][] moveableArea()
	{
		return(moveableArea);
	}
	
	public boolean moveableArea(int x, int y)
	{
		return(moveableArea[x][y]);
	}
	
	public boolean moveUnit()
	{
		boolean notTrap=this.moveUnitAlongPath(selectedUnit, path);
		selectedUnit=null;
		path=null;
		moveableArea=new boolean[map.width()][map.height()];
		return(notTrap);
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
	
	public Path path()
	{
		return(path);
	}
	
	public void replaceTeam(Team oldTeam, Team newTeam)
	{
		for(int a=0;a<map.height();a++)
		{
			for(int i=0;i<map.width();i++)
			{
				if(map.terrain(i, a) instanceof Building)
				{
					Building building=(Building)map.terrain(i,a);
					if(building.team()==oldTeam)
						building.team(newTeam);
				}
			}
		}
	}
	
	public void resetFog()
	{
		fog=new boolean[map.width()][map.height()];
	}
	
	public int rows()
	{
		return(map.height());
	}
	
	public Unit selectedUnit()
	{
		return(selectedUnit);
	}
	
	public void spawnUnit(Unit unit, Point spawnLocation)
	{
		unit.addUnitListener(this);
		map.addUnit(unit,cursor().x,cursor().y);
		clearFog(Main.battle.cursor().x, Main.battle.cursor().y, unit);
	}
	
	public void teamLoses(Team team)
	{
		System.out.println("The "+team.name()+" army has been defeated!");
		replaceTeam(team,null);
		teams.remove(team);
	}
	
	@Override
	public void unitAttacked(Unit unit, int damage)
	{
		// TODO Auto-generated method stub
		
	}
	
	public boolean unitCanBePlaced(Unit unit, Point center)
	{
		if(center.x>0)
			if(map.terrain(center.x-1,center.y).movementCost(unit.movementType())!=99)
				return(true);
		if(center.x<map.width())
			if(map.terrain(center.x+1,center.y).movementCost(unit.movementType())!=99)
				return(true);
		if(center.y>0)
			if(map.terrain(center.x,center.y-1).movementCost(unit.movementType())!=99)
				return(true);
		if(center.y<map.height())
			if(map.terrain(center.x,center.y+1).movementCost(unit.movementType())!=99)
				return(true);
		return(false);
	}
	
	public void unitCaptureBuilding(Unit unit, Point destination)
	{
		if(moveUnit())
		{
			Building building=(Building)map.terrain(destination.x,destination.y);
			building.damage((unit.health()+5)/10);
			if(building.health()<=0)
			{
				building.health(20);
				building.buildingCaptured(unit.team());
			}
		}
		//else was trapped
	}
	
	@Override
	public void unitCreated(Unit unit, Point location)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void unitDestroyed(Unit unit)
	{
		System.out.println("unit destroyed!");
		//check if team can still fight;
		if(checkLoseConditions(unit.team()))
		{
			teamLoses(unit.team());
		}
	}

	public void uniteUnit(Point start, Point end)
	{
		if((map.unit(start.x,start.y)!=null)&&(map.unit(end.x,end.y)!=null))
		{
			map.unit(end.x,end.y).uniteWith(map.unit(start.x,start.y));
			map.setUnit(null,start.x,start.y);
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
	
	public ArrayList<Point> unloadablePoints(Unit unit, Point center)
	{
		ArrayList<Point> points=new ArrayList<Point>();
		if(center.x>0)
			if(map.terrain(center.x-1,center.y).movementCost(unit.movementType())!=99)
				points.add(new Point(center.x-1,center.y));
		if(center.x<map.width())
			if(map.terrain(center.x+1,center.y).movementCost(unit.movementType())!=99)
				points.add(new Point(center.x+1,center.y));
		if(center.y>0)
			if(map.terrain(center.x,center.y-1).movementCost(unit.movementType())!=99)
				points.add(new Point(center.x,center.y-1));
		if(center.y<map.height())
			if(map.terrain(center.x,center.y+1).movementCost(unit.movementType())!=99)
				points.add(new Point(center.x,center.y+1));
		return(points);
	}
	
	public void unloadUnit(int cargoslot, Point point)
	{
		Unit selected=selectedUnit;
		if(moveUnit())
		{
			map.addUnit(selected.cargo(cargoslot),point.x,point.y);
			selected.dropCargo(cargoslot);
		}
		//else was trapped
	}
	
	public void weather(Weather weather)
	{
		this.weather=weather;
	}
	
	public Weather weather()
	{
		return(weather);
	}
	
	@Override
	public void keyTyped(KeyEvent ke)
	{
		//empty
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
		
		Point cursor=cursor();
		
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
				if(canUnitCapture())
					options.add("Capture");
				if(canUnitAttack())
					options.add("Attack");
				if(canUnitMove())
					options.add("Move");
				if(canUnitUnite())
					options.add("Unite");
				if(canUnitLoad())
					options.add("Load");
				if(canUnitUnload())
					options.add("Unload");
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
	
	private boolean[][] moveableArea;
	
	private boolean[][] attackableArea;
	
	private boolean[][] fog;
	
	private Weather weather;
	
	private int turn;
	
	private ArrayList<Team> teams;
}
