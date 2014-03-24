package denaro.nick.wars;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import denaro.nick.core.Entity;
import denaro.nick.core.Sprite;
import denaro.nick.wars.listener.UnitListener;

public class Unit extends Entity
{
	public Unit(Sprite sprite, double x, double y)
	{
		super(sprite,x,y);
		enabled=false;
		fuel=99;
		health=100;
		movement=1;
		vision=1;
		weapons=new ArrayList<UnitWeapon>();
		movementType=null;
		canCapture=false;
		canHide=false;
		hidden=false;
		attackRange=new Point(1,1);
		cargo=new Unit[0];
	}
	
	public void canHide(boolean canHide) throws UnitFinalizedException
	{
		if(finalized)
			throw new UnitFinalizedException(this);
		this.canHide=canHide;
	}
	
	public boolean canHide()
	{
		return(canHide);
	}
	
	public void hidden(boolean hidden)
	{
		this.hidden=hidden;
	}
	
	public boolean isHidden()
	{
		return(hidden);
	}
	
	public void addUnitListener(UnitListener listener)
	{
		if(unitListeners==null)
			unitListeners=new ArrayList<UnitListener>();
		
		if(!unitListeners.contains(listener))
			unitListeners.add(listener);
	}
	
	public void removeUnitListener(UnitListener listener)
	{
		if(unitListeners==null)
			unitListeners=new ArrayList<UnitListener>();
		
		unitListeners.remove(listener);
	}
	
	public void destroyUnit()
	{
		if(unitListeners==null)
			unitListeners=new ArrayList<UnitListener>();
		
		for(UnitListener listener:unitListeners)
			listener.unitDestroyed(this);
	}
	
	public void spawnUnit()
	{
		if(unitListeners==null)
			unitListeners=new ArrayList<UnitListener>();
		
		for(UnitListener listener:unitListeners)
			listener.unitSpawned(this);
	}
	
	public void getKill()
	{
		if(unitListeners==null)
			unitListeners=new ArrayList<UnitListener>();
		
		for(UnitListener listener:unitListeners)
			listener.unitKilled(this);
	}
	
	public int cost()
	{
		return(cost);
	}
	
	public void cost(int cost) throws UnitFinalizedException
	{
		if(finalized)
			throw new UnitFinalizedException(this);
		this.cost=cost;
	}
	
	public void addWeapon(UnitWeapon weapon)
	{
		weapons.add(weapon);
	}
	
	public UnitWeapon weapon(int weaponSlot)
	{
		return(weapons.get(weaponSlot));
	}
	
	public Team team()
	{
		return(team);
	}
	
	public void team(Team team)
	{
		this.team=team;
	}
	
	public boolean enabled()
	{
		return(enabled);
	}
	
	public void enabled(boolean enabled)
	{
		this.enabled=enabled;
	}
	
	public boolean canCapture()
	{
		return(canCapture);
	}
	
	public void canCapture(boolean canCapture)
	{
		this.canCapture=canCapture;
	}
	
	public int fuel()
	{
		return(fuel);
	}
	
	public void fuel(int fuel)
	{
		this.fuel=fuel;
	}
	
	public int health()
	{
		return(health);
	}
	
	public void health(int health)
	{
		this.health=health;
	}
	
	public void damage(int damage)
	{
		health-=damage;
	}
	
	public void vision(int vision) throws UnitFinalizedException
	{
		if(finalized)
			throw new UnitFinalizedException(this);
		this.vision=vision;
	}
	
	public int vision()
	{
		return(vision);
	}
	
	public int movement()
	{
		return(movement);
	}
	
	public void movement(int movement) throws UnitFinalizedException
	{
		if(finalized)
			throw new UnitFinalizedException(this);
		this.movement=movement;
	}
	
	public void defenceID(int defenceID) throws UnitFinalizedException
	{
		if(finalized)
			throw new UnitFinalizedException(this);
		this.defenceID=defenceID;
	}
	
	public int defenceID()
	{
		return(defenceID);
	}
	
	public MovementType movementType()
	{
		return(movementType);
	}
	
	public Point attackRange()
	{
		return(attackRange);
	}
	
	public void attackRange(Point attackRange) throws UnitFinalizedException
	{
		if(finalized)
			throw new UnitFinalizedException(this);
		this.attackRange=attackRange;
	}
	
	public void movementType(MovementType movementType) throws UnitFinalizedException
	{
		if(finalized)
			throw new UnitFinalizedException(this);
		this.movementType=movementType;
	}
	
	public boolean upkeep()
	{
		return(true);//TODO make this an actual upkeep, costing fuel
	}
	
	public void maxCargo(int cargoCount)
	{
		cargo=new Unit[cargoCount];
	}
	
	public int maxCargo()
	{
		return(cargo.length);
	}
	
	public int cargoCount()
	{
		int count=0;
		for(int i=0;i<maxCargo();i++)
		{
			if(cargo[i]!=null)
				count++;
		}
		return(count);
	}
	
	public void cargoType(ArrayList<Integer> cargoType) throws UnitFinalizedException
	{
		if(finalized)
			throw new UnitFinalizedException(this);
		this.cargoType=cargoType;
	}
	
	public void cargoType(Integer... cargoTypes) throws UnitFinalizedException
	{
		if(finalized)
			throw new UnitFinalizedException(this);
		
		cargoType=new ArrayList<Integer>();
		for(Integer id:cargoTypes)
			cargoType.add(id);
	}
	
	public boolean canHoldCargo(int cargoID)
	{
		if(cargoType==null)
			return(false);
		return(cargoType.contains(cargoID));
	}
	
	public Unit cargo(int slot)
	{
		return(cargo[slot]);
	}
	
	public int addCargo(Unit unit)
	{
		int i=0;
		boolean added=false;
		while(!added&&i<cargo.length)
		{
			if(cargo[i]==null)
			{
				cargo[i]=unit;
				added=true;
				return(i);
			}
			i++;
		}
		return(-1);
	}
	
	public void setCargo(Unit unit, int slot)
	{
		cargo[slot]=unit;
	}
	
	public void dropCargo(int slot)
	{
		cargo[slot]=null;
	}
	
	public boolean hasCargo()
	{
		if(cargo==null)
			return(false);
		for(int i=0;i<cargo.length;i++)
		{
			if(cargo[i]!=null)
				return(true);
		}
		return(false);
	}
	
	public int numberOfWeapons()
	{
		return(weapons.size());
	}
	
	public boolean hasCargoSpace()
	{
		for(int i=0;i<cargo.length;i++)
		{
			if(cargo[i]==null)
				return(true);
		}
		return(false);
	}
	
	public void heal(int heal)
	{
		health+=heal;
		if(health>100)
			health=100;
	}
	
	public void resupply()
	{
		fuel=maxFuel;
		for(int i=0;i<numberOfWeapons();i++)
		{
			weapons.get(i).fillAmmo();
		}
	}
	
	public void uniteWith(Unit other)
	{
		health+=other.health;
		if(health>100)
			health=100;
		
		fuel+=other.fuel;
		if(fuel>maxFuel)
			fuel=maxFuel;
		
		enabled=false;
	}
	
	public static Unit copy(Unit other)
	{
		Unit unit=new Unit(other.sprite(),other.x(),other.y());
		unit.id(other.id());
		unit.cost=other.cost;
		unit.team=other.team;
		unit.canCapture=other.canCapture;
		unit.canHide=other.canHide;
		unit.hidden=other.hidden;
		unit.fuel=other.fuel;
		unit.maxFuel=other.maxFuel;
		unit.health=other.health;
		unit.movement=other.movement;
		unit.movementType=other.movementType;
		unit.vision=other.vision;
		unit.attackRange=other.attackRange;
		unit.weapons=new ArrayList<UnitWeapon>();
		for(int i=0;i<other.numberOfWeapons();i++)
			unit.addWeapon(UnitWeapon.copy(other.weapon(i)));
		unit.defenceID=other.defenceID;
		unit.cargoType=other.cargoType;
		if(other.cargo!=null)
		{
			unit.cargo=new Unit[other.cargo.length];
			for(int i=0;i<unit.cargo.length;i++)
				if(other.cargo[i]!=null)
					unit.cargo[i]=Unit.copy(other.cargo[i]);
		}
		unit.imageIndex(other.imageIndex());
		return(unit);
	}

	public static Unit copy(Unit other, Team team)
	{
		Unit unit=copy(other);
		unit.team=team;
		if(other.cargo!=null)
		{
			unit.cargo=new Unit[other.cargo.length];
			for(int i=0;i<unit.cargo.length;i++)
				if(other.cargo[i]!=null)
					unit.cargo[i]=Unit.copy(other.cargo[i],team);
		}
		return(unit);
	}
	
	public static Unit copy(Unit other, Team team, boolean enabled)
	{
		Unit unit=copy(other,team);
		unit.enabled=enabled;
		return(unit);
	}
	
	@Override
	public Image image()
	{
		if(image==null)
		{
			image=new BufferedImage(sprite().width(),sprite().height(),BufferedImage.TYPE_INT_ARGB);
			Graphics2D g=image.createGraphics();
			g.drawImage(super.image(), 0, 0, null);
			Composite oldComposite=g.getComposite();
			if(team==null)
			{
				Main.swapPalette(image,null,1);
			}
			else
			{
				Main.swapPalette(image,team,1);
			}
			g.dispose();
		}
		return(image);
	}
	
	@Override
	public void tick()
	{
		//empty
	}
	
	public void complete()
	{
		finalized=true;
		maxFuel=fuel;
		image();
	}

	private boolean finalized;
	
	private boolean canCapture;
	private boolean canHide;
	private boolean hidden;
	private boolean enabled;
	private Team team;
	private int fuel, maxFuel;
	private int health;
	private int movement;
	private int vision;
	private MovementType movementType;
	private int cost;
	
	private ArrayList<Integer> cargoType;
	private Unit[] cargo;
	
	private ArrayList<UnitWeapon> weapons;
	private int defenceID;
	
	private Point attackRange;
	
	private BufferedImage image;
	
	private ArrayList<UnitListener> unitListeners;
	
	private static void loadBaseAttackChart()
	{
		baseAttackChart=new ArrayList<ArrayList<Integer>>();
		try
		{
			BufferedReader in=new BufferedReader(new InputStreamReader(new FileInputStream("resources/Damage Chart.txt")));
			String line;
			int count=0;
			while((line=in.readLine())!=null)
			{
				baseAttackChart.add(new ArrayList<Integer>());
				String tag=line.substring(0,line.indexOf(']')+1);
				line=line.substring(line.indexOf(']')+1);
				while(line.length()>0)
				{
					int value;
					int index=line.indexOf(',');
					if(line.indexOf('|')!=-1&&line.indexOf('|')<index)
						index=line.indexOf('|');
					if(index==-1)
					{
						value=new Integer(line);
						line="";
					}
					else
					{
						value=new Integer(line.substring(0,index));
						line=line.substring(index+1);
					}
					
					baseAttackChart.get(count).add(value);
					
				}
				count++;
			}
		}
		catch(FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int baseDamage(Unit attacker, Unit defender)
	{
		if(baseAttackChart==null)
		{
			loadBaseAttackChart();
		}
		
		for(int i=0;i<attacker.numberOfWeapons();i++)
		{
			if(attacker.weapon(i).hasAmmo()&&attacker.weapon(i).isEffectiveAggainst(defender.defenceID))
			{
				attacker.weapon(i).useAmmo();
				return(baseAttackChart.get(attacker.weapon(i).weaponID()).get(defender.defenceID()));
			}
		}

		System.out.println("ERROR: Unit should not have attacked!");
		return(0);//this shouldn't happen, but if it does, ok....?
	}
	
	public static int numberOfAttackableUnits(int id)
	{
		if(baseAttackChart==null)
		{
			loadBaseAttackChart();
		}
		return(baseAttackChart.get(id).size());
	}
	
	public static int baseAttack(int attackerID, int defenderID)
	{
		return(baseAttackChart.get(attackerID).get(defenderID));
	}
	
	private static ArrayList<ArrayList<Integer>> baseAttackChart;
}

enum MovementType{FOOT,MECH,TREAD,TIRES,SHIP,TRANS,AIR};
