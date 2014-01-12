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

public class Unit extends Entity
{

	public Unit(Sprite sprite, Double point)
	{
		super(sprite, point);
		enabled=false;
		fuel=99;
		health=100;
		movement=1;
		vision=1;
		ammo=0;
		maxAmmo=0;
		usesAmmo=false;
		movementType=null;
		canCapture=false;
		attackRange=new Point(1,1);
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
	
	public void weapon1(UnitWeapon weapon)
	{
		this.weapon1=weapon;
	}
	
	public void weapon2(UnitWeapon weapon)
	{
		this.weapon2=weapon;
	}
	
	public UnitWeapon weapon1()
	{
		return(weapon1);
	}
	
	public UnitWeapon weapon2()
	{
		return(weapon2);
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
	
	public int ammo()
	{
		return(ammo);
	}
	
	public void ammo(int ammo)
	{
		if(!finalized)
			this.usesAmmo=true;
		this.ammo=ammo;
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
	
	public void cargoCount(int cargoCount)
	{
		cargo=new Unit[cargoCount];
	}
	
	public int cargoCount()
	{
		return(cargo.length);
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
		return(cargoType.contains(cargoID));
	}
	
	public Unit cargo(int slot)
	{
		return(cargo[slot]);
	}
	
	public void addCargo(Unit unit)
	{
		int i=0;
		boolean added=false;
		while(!added&&i<cargo.length)
		{
			if(cargo[i]==null)
			{
				cargo[i]=unit;
				added=true;
			}
		}
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
	
	public boolean hasCargoSpace()
	{
		for(int i=0;i<cargo.length;i++)
		{
			if(cargo[i]==null)
				return(true);
		}
		return(false);
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
		Unit unit=new Unit(other.sprite(),other.point());
		unit.id(other.id());
		unit.team=other.team;
		unit.canCapture=other.canCapture;
		unit.fuel=other.fuel;
		unit.maxFuel=other.maxFuel;
		unit.usesAmmo=other.usesAmmo;
		unit.ammo=other.ammo;
		unit.maxAmmo=other.maxAmmo;
		unit.usesAmmo=other.usesAmmo;
		unit.health=other.health;
		unit.movement=other.movement;
		unit.movementType=other.movementType;
		unit.vision=other.vision;
		unit.attackRange=other.attackRange;
		unit.weapon1=other.weapon1;
		unit.weapon2=other.weapon2;
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
			if(team!=null)
			{
				Main.swapPalette(image,team,1);
			}
			else
			{
				Main.swapPalette(image,null,1);
			}
			
			/*if(!enabled)
			{
				g.setColor(Color.black);
				//Composite oldComposite=g.getComposite();
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));
				g.fillRect(0, 0, sprite().width(), sprite().height());
			}
			g.setComposite(oldComposite);*/
			
			/*int hp=(health+5)/10;
			if(hp!=10)
			{
				if(hp==0)
					hp=1;
				g.drawImage(GameFont.fonts.get("Map Font").stringToImage(""+hp), sprite().width()-8, sprite().height()-8, null);
			}*/
			
			g.dispose();
		}
		return(image);
	}
	
	@Override
	public void tick()
	{
		// TODO Auto-generated method stub
		
	}
	
	public void finalize()
	{
		finalized=true;
		maxFuel=fuel;
	}

	private boolean finalized;
	
	private boolean canCapture;
	private boolean enabled;
	private Team team;
	private int fuel, maxFuel;
	private boolean usesAmmo;
	private int ammo, maxAmmo;
	private int health;
	private int movement;
	private int vision;
	private MovementType movementType;
	
	private ArrayList<Integer> cargoType;
	private Unit cargo[];
	
	private UnitWeapon weapon1;
	private UnitWeapon weapon2;
	private int defenceID;
	
	private Point attackRange;
	
	private BufferedImage image;
	
	private ArrayList<UnitListener> unitListeners;
	
	private static void loadBaseAttackChart()
	{
		baseAttackChart=new ArrayList<ArrayList<Integer>>();
		try
		{
			BufferedReader in=new BufferedReader(new InputStreamReader(new FileInputStream("Damage Chart.txt")));
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
		
		if(attacker.weapon2!=null&&attacker.ammo>0&&attacker.weapon2.isEffectiveAggainst(defender.defenceID()))
		{
			//use weapon2
			attacker.ammo--;
			return(baseAttackChart.get(attacker.weapon2.weaponID()).get(defender.defenceID()));
		}
		else if(attacker.weapon1!=null)
		{
			//use weapon1
			return(baseAttackChart.get(attacker.weapon1.weaponID()).get(defender.defenceID()));
		}
		else
		{
			System.out.println("ERROR: Unit should not have attacked!");
			return(0);//this shouldn't happen, but if it does, ok....?
		}
		//return(baseAttackChart[attacker.attackType+(attacker.usesAmmo?(attacker.ammo>0?1:0):0)][defender.defenceType+(defender.usesAmmo?(defender.ammo>0?1:0):0)]);
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
