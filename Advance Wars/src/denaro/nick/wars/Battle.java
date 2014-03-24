package denaro.nick.wars;

import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;

import denaro.nick.core.ControllerEvent;
import denaro.nick.core.GameView2D;
import denaro.nick.core.Sprite;
import denaro.nick.wars.listener.BuildingListener;
import denaro.nick.wars.listener.CursorListener;
import denaro.nick.wars.listener.UnitListener;
import denaro.nick.wars.menu.ActionMenu;
import denaro.nick.wars.menu.BuyMenu;
import denaro.nick.wars.menu.Menu;
import denaro.nick.wars.view.BattleView;
import denaro.nick.wars.view.MapView;

public class Battle extends GameMode implements CursorListener, BuildingListener, UnitListener
{
	public Battle(Map map, Team[] teams/*ArrayList<Team> teams*/, BattleSettings settings)
	{	
		this.map=map;
		this.teams=teams;
		this.settings=settings;
		
		statistics=new BattleStatistics(null,new Point(0,0),teams);
		createListeners();
		
		actionQueue=new LinkedList<BattleAction>();
		
		turn=-1;
		day=0;
		cursor(new Point(0,0));
		fog=new boolean[map.width()][map.height()];
		this.addCursorListener(this);
	}
	
	public void addBattleListener(BattleListener listener)
	{
		if(battleListeners==null)
			battleListeners=new ArrayList<BattleListener>();
		
		if(!battleListeners.contains(listener))
			battleListeners.add(listener);
	}
	
	public void removeBattleListener(BattleListener listener)
	{
		if(battleListeners==null)
			battleListeners=new ArrayList<BattleListener>();
		
		battleListeners.remove(listener);
	}
	
	public void battleEnd()
	{
		if(battleListeners==null)
			battleListeners=new ArrayList<BattleListener>();
		
		for(BattleListener listener:battleListeners)
			listener.battleEnd();
		
		if(battleListeners.isEmpty())
		{
			final Battle battle=this;
			BattleAction action=new BattleAction()
			{
				@Override
				public void callFunction()
				{
					Main.endBattle(battle);
				}
			};
			addAction(action);
		}
	}
	public void start()
	{
		if(turn==-1)
		{
			for(int i=0;i<teams.length;i++)
				teams[i].addFunds(settings.startingFunds());
			nextTurn();
		}
		started=true;
	}
	
	public boolean started()
	{
		return(started);
	}
	
	public BattleStatistics statistics()
	{
		return(statistics);
	}
	
	public void pushAction(BattleAction action)
	{
		action.init();
		actionQueue.push(action);
	}
	
	public void addAction(BattleAction action)
	{
		action.init();
		actionQueue.add(action);
	}
	
	public boolean isInputLocked()
	{
		if(!started)
		{
			//System.out.println("ERROR: the battle needs to be started!");
			return(true);
		}
		
		if(!actionQueue.isEmpty())
			return(true);
		
		return(false);
	}
	
	public void performAction()
	{
		if(!actionQueue.isEmpty())
		{
			actionQueue.peek().callFunction();
			if(actionQueue.peek().shouldEnd())
				actionQueue.pop();
		}
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
					building.addBuildingListener(statistics);
				}
				if(map.unit(i,a)!=null)
				{
					map.unit(i,a).addUnitListener(this);
					map.unit(i,a).addUnitListener(statistics);
				}
			}
		}
	}
	
	public Map map()
	{
		return(map);
	}
	
	public void turn(int turn)
	{
		this.turn=turn;
	}
	
	public void fog(boolean[][] fog)
	{
		this.fog=fog;
	}
	
	public Team whosTurn()
	{
		return(teams[turn]);
	}
	
	public int turn()
	{
		return(turn);
	}
	
	public void teams(Team[] teams)
	{
		this.teams=teams;
		statistics.teams(teams);
	}
	
	/**
	 * Acessor for specific team
	 * @param team - the team to compare
	 * @return - the team that is in the list
	 */
	public Team team(Team team)
	{
		if(team==null)
			return(null);
		for(int i=0;i<teams.length;i++)
		{
			if(Team.sameTeam(teams[i],team))
				return(teams[i]);
		}
		System.out.println("ERROR: should have returned a team.");
		return(null);
	}
	
	public void newDay()
	{
		day++;
	}
	
	public void endTurn()
	{
		nextTurn();
	}
	
	public Team myTeam()
	{
		return(whosTurn());
	}
	
	public void nextTurn()
	{
		turn=++turn%teams.length;
		if(turn==0)
		{
			newDay();
		}
		resetFog();
		clearFogForTeam(myTeam());
		enableUnitsForTeam(myTeam());
		
		int count=map.buildingCount(whosTurn());
		whosTurn().addFunds(count*settings.fundsPerTurn());
		
		for(int a=0;a<map.height();a++)
		{
			for(int i=0;i<map.width();i++)
			{
				Unit unit=map.unit(i,a);
				if(unit!=null)
				{
					if(map.terrain(i,a) instanceof Building)
					{
						Building building=(Building)map.terrain(i,a);
						Team team=whosTurn();
						if(Team.sameTeam(building.team(),team))
						{
							if(unit.movementType()!=MovementType.AIR||building.id()==Main.airport.id())
							{
								if(unit.health()<100)
								{
									int cost=unit.cost();//TODO add possibility to reduce based on commander?
									int healed=1;//heals 2 times
									while(healed-->=0&&team.funds()>cost/10)
									{
										unit.heal(10);
										team.addFunds(-cost/10);
									}
								}
								unit.resupply();
							}
						}
					}
					//resupply units around apcs
					if(Main.stringToUnitID.get("apc")==unit.id())
					{
						if(i>0)
							if(map.unit(i-1,a)!=null)
								map.unit(i-1,a).resupply();
						if(i+1<map.width())
							if(map.unit(i+1,a)!=null)
								map.unit(i+1,a).resupply();
						if(a>0)
							if(map.unit(i,a-1)!=null)
								map.unit(i,a-1).resupply();
						if(a+1<map.height())
							if(map.unit(i,a+1)!=null)
								map.unit(i,a+1).resupply();
					}
				}
			}
			
		}
		for(int a=0;a<map.height();a++)
		{
			for(int i=0;i<map.width();i++)
			{
				Unit unit=map.unit(i,a);
				if(unit!=null)
				{
					if(unit.upkeep()==false)
					{
						destroyUnit(new Point(i,a));
					}
				}
			}
		}
	}
	
	//------------------------------------------------------------------------------
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
						if(attackableArea[i][a]&&unitIfVisible(i,a)!=null&&!Team.sameTeam(map.unit(i,a).team(),selectedUnit.team()))
						{
							for(int w=0;w<selectedUnit.numberOfWeapons();w++)
							{
								if(selectedUnit.weapon(w).hasAmmo()&&selectedUnit.weapon(w).isEffectiveAggainst(map.unit(i,a).defenceID()))
								{
									points.add(new Point(i,a));
								}
							}
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
	
	public boolean attackUnit(final Point attackerPoint, final Point defenderPoint)
	{
		final Point endPosition=path.last();
		final Unit attacker=map.unit(attackerPoint.x,attackerPoint.y);
		final Unit defender=map.unit(defenderPoint.x,defenderPoint.y);
		if(moveUnit())
		{
			
			BattleAction action=new BattleAction()
			{
				@Override
				public void callFunction()
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
								defender.getKill();
								destroyUnit(endPosition);
							}
						}
					}
					else
					{
						attacker.getKill();
						destroyUnit(defenderPoint);
					}
				}
			};
			if(Main.engine()!=null)
				addAction(action);
			else
				action.callFunction();
			return(true);
		}
		return(false);
	}
	
	@Override
	public void buildingCaptured(Building building, Team oldTeam, Team newTeam)
	{
		if(building.hq())
		{
			this.replaceTeam(oldTeam,newTeam);
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
		int aco;
		if(attacker.attackRange().y==1)
		{
			//System.out.println(attacker.team().name());
			//System.out.println(attacker.team().commander().name());
			if(attacker.team()!=null)
				aco=attacker.team().commander().meleeAttackPower();
			else
				aco=100;
		}
		else
		{
			if(attacker.team()!=null)
				aco=attacker.team().commander().rangedAttackPower();
			else
				aco=100;
		}
		int r=((int)(Math.random()*10));
		
		int ahp=attacker.health();
		
		int dco;
		if(defender.team()!=null)
			dco=defender.team().commander().defencePower();
		else
			dco=100;
		int tdf=map.terrain(defenderLocation.x,defenderLocation.y).defence();
		int dhp=defender.health();
		
		//old damage calculation
		//double damage=(base*(aco/100.0)+r)*ahp/10.0*(((200.0-(dco+tdf*dhp))/100.0));
		
		//System.out.print("base: "+base+"-");
		
		double damage=100*(base/100.0*aco/100.0*(100+r)/100.0*ahp/100.0*(200-dco)/100.0*(10-tdf)/10.0);
		
		System.out.println("damage: "+damage);
		
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
						if(attackableArea[i][a]&&map.unit(i,a)!=null&&!Team.sameTeam(map.unit(i,a).team(),selectedUnit.team()))
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
	
	public boolean canUnitHide()
	{
		return(canUnitMove()&&selectedUnit.canHide()&&!selectedUnit.isHidden());
	}
	
	public boolean canUnitUnHide()
	{
		return(canUnitMove()&&selectedUnit.canHide()&&selectedUnit.isHidden());
	}
	
	public boolean canUnitLoad()
	{
		if(path.first().equals(path.last()))
			return(false);
		if(map.unit(cursor().x,cursor().y)==null)
			return(false);
		else if(!Team.sameTeam(map.unit(cursor().x,cursor().y).team(),selectedUnit.team()))
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
		else if(!Team.sameTeam(map.unit(cursor().x,cursor().y).team(),selectedUnit.team()))
			return(false);
		else if(map.unit(cursor().x,cursor().y).health()==100)
			return(false);
		else if(map.unit(cursor().x, cursor().y).id()!=selectedUnit.id())
			return(false);
		return(true);
	}
	
	public boolean canUnitUnload(Unit unit, Point center)
	{
		if(!unit.hasCargo())
			return(false);
		for(int i=0;i<unit.maxCargo();i++)
		{
			if(unit.cargo(i)!=null&&unitCanBePlaced(unit.cargo(i),center))
				return(true);
		}
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
		clearFog(x,y,unit,unit.vision()+((unit.movementType()!=MovementType.AIR)?map.terrain(x,y).visionBoost():0)-Main.weatherMap.get(settings.weather()).visionLoss(),true);
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
					if(Team.sameTeam(building.team(),team))
						fog[i][a]=true;
				}
				if(map.unit(i,a)!=null)
				{
					if(Team.sameTeam(map.unit(i,a).team(),team))
					{
						clearFog(i,a,map.unit(i,a));
					}
				}
			}
		}
	}
	
	public void clearMovement()
	{
		selectedUnit=null;
		path=null;
		moveableArea=new boolean[map.width()][map.height()];
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
		
		if(fog(x,y)||(unitIfVisible(x,y)==null)||(Team.sameTeam(map.unit(x,y).team(),unit.team())))
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
		if(path!=null&&moveableArea!=null&&moveableArea[cursor.x][cursor.y])
			path.addPoint(cursor.x, cursor.y);
	}
	
	public int day()
	{
		return(day);
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
					if(Team.sameTeam(map.unit(i,a).team(),team))
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
			if(unitIfVisible(path.last().x-1,path.last().y)!=null&&!Team.sameTeam(map.unit(path.last().x-1,path.last().y).team(),unit.team()))
				points.add(new Point(path.last().x-1,path.last().y));
		if(path.last().x+1<map.width())
			if(unitIfVisible(path.last().x+1,path.last().y)!=null&&!Team.sameTeam(map.unit(path.last().x+1,path.last().y).team(),unit.team()))
				points.add(new Point(path.last().x+1,path.last().y));
		if(path.last().y-1>=0)
			if(unitIfVisible(path.last().x,path.last().y-1)!=null&&!Team.sameTeam(map.unit(path.last().x,path.last().y-1).team(),unit.team()))
				points.add(new Point(path.last().x,path.last().y-1));
		if(path.last().y+1<map.height())
			if(unitIfVisible(path.last().x,path.last().y+1)!=null&&!Team.sameTeam(map.unit(path.last().x,path.last().y+1).team(),unit.team()))
				points.add(new Point(path.last().x,path.last().y+1));
		return(points);
	}
	
	public ArrayList<Point> enemiesNextToUnit(Point p)
	{
		ArrayList<Point> points=new ArrayList<Point>();
		Unit unit=map.unit(p.x,p.y);
		if(p.x-1>=0)
			if(unitIfVisible(p.x-1,p.y)!=null&&!Team.sameTeam(map.unit(p.x-1,p.y).team(),unit.team()))
				points.add(new Point(path.last().x-1,path.last().y));
		if(p.x+1<map.width())
			if(unitIfVisible(p.x+1,p.y)!=null&&!Team.sameTeam(map.unit(p.x+1,p.y).team(),unit.team()))
				points.add(new Point(p.x+1,p.y));
		if(p.y-1>=0)
			if(unitIfVisible(p.x,p.y-1)!=null&&!Team.sameTeam(map.unit(p.x,p.y-1).team(),unit.team()))
				points.add(new Point(p.x,p.y-1));
		if(p.y+1<map.height())
			if(unitIfVisible(p.x,p.y+1)!=null&&!Team.sameTeam(map.unit(p.x,p.y+1).team(),unit.team()))
				points.add(new Point(p.x,p.y+1));
		return(points);
	}
	
	public boolean fog(int x, int y)
	{
		if(!settings.fogOfWar())
			return(false);
		return(!fog[x][y]);
	}
	
	public boolean hideUnit()
	{
		Unit unit=selectedUnit;
		boolean notTrap=moveUnit();
		if(notTrap)
			unit.hidden(true);
		return(notTrap);
	}
	
	public boolean unHideUnit()
	{
		Unit unit=selectedUnit;
		boolean notTrap=moveUnit();
		if(notTrap)
			unit.hidden(false);
		return(notTrap);
	}
	
	public boolean isEnemyNextToUnit(Unit unit)
	{
		return(!enemiesNextToUnit(unit).isEmpty());
	}
	
	public boolean loadUnit()
	{
		final Unit unit=selectedUnit;
		final Unit loader=map.unit(path.last().x,path.last().y);
		final boolean loaderEnabled=loader.enabled();
		System.out.println("loader: "+loaderEnabled);
		if(moveUnit())
		{
			
			BattleAction action=new BattleAction()
			{
				@Override
				public void callFunction()
				{
					map.unit(cursor().x,cursor().y).addCargo(unit);
					if(loaderEnabled)
					{
						System.out.println("loader: "+loaderEnabled);
						loader.enabled(true);
					}
				}
			};
			if(Main.engine()!=null)
				addAction(action);
			else
				action.callFunction();
			return(true);
		}
		return(false);
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
		clearMovement();
		return(notTrap);
	}
	
	public boolean moveUnitAlongPath(final Unit unit,Path path)
	{
		boolean trap=false;
		final ArrayList<Point> points=path.points();
		final Path unitpath=new Path(map,unit.movementType(),unit.movement());
		unitpath.start(path.first().x,path.first().y);
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
				if(Team.sameTeam(unit.team(),myTeam()))
					clearFog(points.get(i).x,points.get(i).y,unit);
			}
			else
			{
				if(!Team.sameTeam(unit.team(),map.unit(points.get(i).x,points.get(i).y).team()))
				{
					//TODO Display message "Trap!"
					trap=true;
					break;
				}
			}
			unitpath.start(points.get(i).x,points.get(i).y);
		}
		BattleAction action=new BattleAction()
		{
			@Override
			public void init()
			{
				count=0;
				pathindex=0;
			}
			
			@Override
			public void callFunction()
			{
				if(pathindex+1<unitpath.size())
				{
					int speed=settings.animationSpeed();
					Point start=unitpath.get(pathindex);
					Point end=unitpath.get(pathindex+1);
					unit.moveDelta(speed*(end.x-start.x),speed*(end.y-start.y));
					count++;
					if(count>=Main.TILESIZE/speed)
					{
						count=0;
						pathindex++;
					}
				}
			}

			@Override
			public boolean shouldEnd()
			{
				return pathindex>=unitpath.size()-1;
			}
			
			private int count;
			private int pathindex;
		};
		pushAction(action);
		i--;
		if(!points.get(0).equals(points.get(i)))
		{
			if(map.unit(points.get(i).x,points.get(i).y)==null)
			{
				map.moveUnit(points.get(0),points.get(i));
			}
			else if(map.unit(points.get(i).x,points.get(i).y).id()==unit.id())
			{
				uniteUnit(points.get(0),points.get(i));
			}
			else
			{
				action=new BattleAction()
				{

					@Override
					public void callFunction()
					{
						map.setUnit(null,points.get(0).x,points.get(0).y);
					}
					
				};
				if(Main.engine()!=null)
					addAction(action);
				else
					action.callFunction();
			}
		}
		System.out.println(points.get(i));
		
		unit.enabled(false);
		return(!trap);
	}
	
	public Path path()
	{
		return(path);
	}
	
	public void path(Path path)
	{
		this.path=path;
	}
	
	public boolean purchaseUnit(Unit unit)
	{
		if(whosTurn().funds()>=unit.cost())
		{
			whosTurn().addFunds(-unit.cost());
			Unit adding=Unit.copy(unit,whosTurn());
			spawnUnit(adding, cursor());
			return(true);
		}
		return(false);
	}
	
	public void removeTeam(Team team)
	{
		for(int i=0;i<teams.length;i++)
		{
			if(teams[i]==team)
				teams[i]=null;
		}
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
					if(Team.sameTeam(building.team(),oldTeam))
					{
						building.team(newTeam);
						if(building.hq())
						{
							map.setTerrain(Building.copy(Main.city,newTeam),i,a);
						}
					}
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
	
	public void selectedUnit(Unit unit)
	{
		selectedUnit=unit;
	}
	
	public BattleSettings settings()
	{
		return(settings);
	}
	
	public void spawnUnit(Unit unit, Point spawnLocation)
	{
		unit.addUnitListener(this);
		unit.addUnitListener(statistics);
		map.addUnit(unit,spawnLocation.x,spawnLocation.y);
		unit.spawnUnit();
		clearFog(spawnLocation.x, spawnLocation.y, unit);
	}
	
	public Team[] teams()
	{
		return(teams);
	}
	
	public int teamsLeft()
	{
		int count=0;
		for(int i=0;i<teams.length;i++)
		{
			if(teams[i]!=null)
				count++;
		}
		return(count);
	}
	
	public void teamLoses(Team team)
	{
		final String text="The "+team.name()+" army has been defeated";
		System.out.println(text);
		replaceTeam(team,null);
		removeTeam(team);
		
		if(Main.engine()!=null)
		{
			final Battle battle=this;
			MapView view=(MapView)Main.engine().view();
			final Menu banner=new Menu(null,new Point(view.width()/2,0))
			{
	
				@Override
				public Image image()
				{
					return(img);
				}
				
				Image img=GameFont.fonts.get("Map Font").stringToImage(text);
	
				@Override
				public int columns()
				{
					// TODO Auto-generated method stub
					return 0;
				}
	
				@Override
				public int rows()
				{
					// TODO Auto-generated method stub
					return 0;
				}
			};
			banner.point().x-=banner.image().getWidth(null)/2;
			BattleAction action=new BattleAction()
			{
				@Override
				public void init()
				{
					count=60*5;
				}
				
				@Override
				public void callFunction()
				{
					MapView view=(MapView)Main.engine().view();
					if(banner.point().y<view.height()/3)
						banner.point().y+=3;
					count--;
					if(count<=1)
						Main.closeMenu();
					else if(Main.menu==null)
						Main.openMenu(banner);
				}
				
				@Override
				public boolean shouldEnd()
				{
					return(count<=0);
				}
				
				int count;
			};
			addAction(action);
		}
		
		if(battleListeners==null)
			battleListeners=new ArrayList<BattleListener>();
		
		for(BattleListener listener:battleListeners)
			listener.teamLoses(team);
		
		if(teamsLeft()==1)
			battleEnd();
	}
	
	@Override
	public void unitAttacked(Unit unit, int damage)
	{
		//TODO UnitListener requirement
	}
	
	public boolean unitCanBePlaced(Unit unit, Point center)
	{
		if(center.x>0)
			if(map.terrain(center.x-1,center.y).movementCost(unit.movementType())!=99)
				if(map.unit(center.x-1,center.y)==null)
					return(true);
		if(center.x<map.width()-1)
			if(map.terrain(center.x+1,center.y).movementCost(unit.movementType())!=99)
				if(map.unit(center.x+1,center.y)==null)
					return(true);
		if(center.y>0)
			if(map.terrain(center.x,center.y-1).movementCost(unit.movementType())!=99)
				if(map.unit(center.x,center.y-1)==null)
					return(true);
		if(center.y<map.height()-1)
			if(map.terrain(center.x,center.y+1).movementCost(unit.movementType())!=99)
				if(map.unit(center.x,center.y+1)==null)
					return(true);
		return(false);
	}
	
	public boolean unitCaptureBuilding(final Unit unit, final Point destination)
	{
		if(moveUnit())
		{
			BattleAction action=new BattleAction()
			{
				@Override
				public void callFunction()
				{
					Building building=(Building)map.terrain(destination.x,destination.y);
					building.damage((unit.health()+5)/10);
					if(building.health()<=0)
					{
						building.health(20);
						building.buildingCaptured(unit.team());
					}
				}
			};
			if(Main.engine()!=null)
				addAction(action);
			else
				action.callFunction();
			
			return(true);
		}
		return(false);
		//else was trapped
	}
	
	@Override
	public void unitCreated(Unit unit, Point location)
	{

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

	public void uniteUnit(final Point start, final Point end)
	{
		if((map.unit(start.x,start.y)!=null)&&(map.unit(end.x,end.y)!=null))
		{
			BattleAction action=new BattleAction()
			{
				@Override
				public void callFunction()
				{
					map.unit(end.x,end.y).uniteWith(map.unit(start.x,start.y));
					map.setUnit(null,start.x,start.y);
				}
			};
			if(Main.engine()!=null)
				addAction(action);
			else
				action.callFunction();
		}
	}
	
	public Unit unitIfVisible(int x, int y)
	{
		if(map.unit(x,y)==null)
			return(null);
		
		if(map.unit(x,y).isHidden()&&!Team.sameTeam(map.unit(x,y).team(),myTeam()))
			return(null);
		
		if(!settings.fogOfWar())
			return(map.unit(x,y));
		
		if(Main.weatherMap.get(settings.weather()).fog())
		{
			if(!fog(x,y))
				return(map.unit(x,y));
			else
				return(null);
		}
		else
			return(map.unit(x,y));
	}
	
	@Override
	public void unitKilled(Unit unit)
	{
		//EMPTY
	}
	
	@Override
	public void unitSpawned(Unit unit)
	{
		//EMPTY
	}
	
	public ArrayList<Point> unloadablePoints(Unit unit, Point center)
	{
		ArrayList<Point> points=new ArrayList<Point>();
		if(center.x>0)
			if(map.terrain(center.x-1,center.y).movementCost(unit.movementType())!=99)
				if(map.unit(center.x-1,center.y)==null)
					points.add(new Point(center.x-1,center.y));
		if(center.x<map.width()-1)
			if(map.terrain(center.x+1,center.y).movementCost(unit.movementType())!=99)
				if(map.unit(center.x+1,center.y)==null)
					points.add(new Point(center.x+1,center.y));
		if(center.y>0)
			if(map.terrain(center.x,center.y-1).movementCost(unit.movementType())!=99)
				if(map.unit(center.x,center.y-1)==null)
					points.add(new Point(center.x,center.y-1));
		if(center.y<map.height()-1)
			if(map.terrain(center.x,center.y+1).movementCost(unit.movementType())!=99)
				if(map.unit(center.x,center.y+1)==null)
					points.add(new Point(center.x,center.y+1));
		return(points);
	}
	
	public boolean unloadUnit(final int cargoslot, final Point point)
	{
		final Unit selected=selectedUnit;
		final Point unloader=new Point(path.last().x,path.last().y);
		if(moveUnit())
		{
			BattleAction action=new BattleAction()
			{
				@Override
				public void callFunction()
				{
					map.addUnit(selected.cargo(cargoslot),point.x,point.y);
					selected.dropCargo(cargoslot);
					if(Main.engine()!=null)
					{
						ArrayList<String> actions=new ArrayList<String>();
						boolean canUnload=canUnitUnload(selected,unloader);
						System.out.println("can unload: "+canUnload);
						System.out.println("units in unloader: "+selected.cargoCount());
						if(canUnload)
						{
							actions.add("Unload");
							actions.add("Cancel");
						}
						if(!actions.isEmpty())
						{
							System.out.println("additional unload!");
							Main.closeAllMenus();
							selectedUnit=selected;
							path=new Path(map,selected.movementType(),selected.movement());
							path.start(unloader.x,unloader.y);
							Main.openMenu(new ActionMenu(null,new Point(unloader.x*Main.TILESIZE,unloader.y*Main.TILESIZE),actions));
						}
					}
				}
			};
			if(Main.engine()!=null)
				addAction(action);
			else
				action.callFunction();
			return(true);
		}
		return(false);
	}
	
	public void weather(Weather weather)
	{
		settings.weather(weather.id());
	}
	
	public Weather weather()
	{
		return(Main.weatherMap.get(settings.weather()));
	}
	
	@Override
	public void actionPerformed(ControllerEvent event)
	{
		if(event.action()==ControllerEvent.PRESSED)
		{
			if(isInputLocked())
				return;
			
			if(event.code()==Main.LEFT)
				moveCursorLeft();
			if(event.code()==Main.RIGHT)
				moveCursorRight();
			if(event.code()==Main.UP)
				moveCursorUp();
			if(event.code()==Main.DOWN)
				moveCursorDown();
			
			Point cursor=cursor();
			
			if(event.code()==Main.SELECT)
			{
				//TODO make this part of a menu
				Main.saveBattle(this);
			}
			
			if(event.code()==Main.ACTION)
			{
				if(selectedUnit==null)
				{
					if((map.unit(cursor.x,cursor.y)!=null)&&(map.unit(cursor.x,cursor.y).enabled())&&Team.sameTeam(map.unit(cursor.x,cursor.y).team(),whosTurn()))
					{
						selectedUnit=map.unit(cursor.x,cursor.y);
						moveableArea=new boolean[map.width()][map.height()];
						createMoveableArea(cursor.x,cursor.y,map.unit(cursor.x,cursor.y),map.unit(cursor.x,cursor.y).movement());
						path=new Path(map,map.unit(cursor.x,cursor.y).movementType(),map.unit(cursor.x,cursor.y).movement());
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
						if(building.canSpawnUnits()&&Team.sameTeam(building.team(),whosTurn()))
						{
							Main.menu=new BuyMenu(null,new Point(((BattleView)Main.engine().view()).view().x*Main.TILESIZE,((BattleView)Main.engine().view()).view().y*Main.TILESIZE-Main.TILESIZE+((GameView2D)Main.engine().view()).height()-Sprite.sprite("Buy Menu").height()),building);
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
					if(canUnitHide())
						options.add("Hide");
					if(canUnitUnHide())
						options.add("UnHide");
					if(canUnitUnite())
						options.add("Unite");
					if(canUnitLoad())
						options.add("Load");
					if(canUnitUnload(selectedUnit,cursor()))
						options.add("Unload");
					options.add("Cancel");
					//show menu
					if(options.size()>1)
					{
						Main.openMenu(new ActionMenu(null,new Point(cursor.x*Main.TILESIZE,cursor.y*Main.TILESIZE),options));
					}
				}
			}
			
			if(event.code()==Main.BACK)
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
			
			if(event.code()==Main.START)
			{
				//TODO make this part of a menu
				endTurn();
			}
		}
		else if(event.action()==ControllerEvent.RELEASED)
		{
			if(event.code()==Main.ACTION)
			{
				if(moveableArea!=null&&path==null)
					moveableArea=null;
			}
			
			if(event.code()==Main.BACK)
			{
				attackableArea=null;
			}
		}
	}
	
	/*@Override
	public void keyPressed(KeyEvent ke)
	{
		if(isInputLocked())
			return;
		
		if(ke.getKeyCode()==KeyEvent.VK_LEFT)
			moveCursorLeft();
		if(ke.getKeyCode()==KeyEvent.VK_RIGHT)
			moveCursorRight();
		if(ke.getKeyCode()==KeyEvent.VK_UP)
			moveCursorUp();
		if(ke.getKeyCode()==KeyEvent.VK_DOWN)
			moveCursorDown();
		
		Point cursor=cursor();
		
		if(ke.getKeyCode()==KeyEvent.VK_HOME)
		{
			//TODO make this part of a menu
			Main.saveBattle(this);
		}
		
		if(ke.getKeyCode()==KeyEvent.VK_X)
		{
			if(selectedUnit==null)
			{
				if((map.unit(cursor.x,cursor.y)!=null)&&(map.unit(cursor.x,cursor.y).enabled())&&Team.sameTeam(map.unit(cursor.x,cursor.y).team(),whosTurn()))
				{
					selectedUnit=map.unit(cursor.x,cursor.y);
					moveableArea=new boolean[map.width()][map.height()];
					createMoveableArea(cursor.x,cursor.y,map.unit(cursor.x,cursor.y),map.unit(cursor.x,cursor.y).movement());
					path=new Path(map,map.unit(cursor.x,cursor.y).movementType(),map.unit(cursor.x,cursor.y).movement());
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
					if(building.canSpawnUnits()&&Team.sameTeam(building.team(),whosTurn()))
					{
						Main.menu=new BuyMenu(null,new Point(((BattleView)Main.engine().view()).view().x*Main.TILESIZE,((BattleView)Main.engine().view()).view().y*Main.TILESIZE-Main.TILESIZE+((GameView2D)Main.engine().view()).height()-Sprite.sprite("Buy Menu").height()),building);
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
				if(canUnitHide())
					options.add("Hide");
				if(canUnitUnHide())
					options.add("UnHide");
				if(canUnitUnite())
					options.add("Unite");
				if(canUnitLoad())
					options.add("Load");
				if(canUnitUnload(selectedUnit,cursor()))
					options.add("Unload");
				options.add("Cancel");
				//show menu
				if(options.size()>1)
				{
					Main.openMenu(new ActionMenu(null,new Point(cursor.x*Main.TILESIZE,cursor.y*Main.TILESIZE),options));
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
			//TODO make this part of a menu
			endTurn();
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
		
	}*/
	
	//-------------------------------------------------------------------------------
	private LinkedList<BattleAction> actionQueue;
	
	private Map map;
	
	private int day;
	
	private Unit selectedUnit;
	
	private Path path;
	
	private boolean[][] moveableArea;
	
	private boolean[][] attackableArea;
	
	private boolean[][] fog;
	
	private int turn;
	
	private BattleSettings settings;
	
	//private ArrayList<Team> teams;
	private Team[] teams;
	
	private boolean started;
	
	private ArrayList<BattleListener> battleListeners;
	
	private BattleStatistics statistics;
}
