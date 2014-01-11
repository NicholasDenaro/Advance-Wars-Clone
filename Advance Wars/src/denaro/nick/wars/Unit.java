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
import java.util.ArrayList;

import denaro.nick.core.Entity;
import denaro.nick.core.Sprite;

public class Unit extends Entity
{

	public Unit(Sprite sprite, Double point, int unitID)
	{
		super(sprite, point);
		this.unitID=unitID;
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
	
	public int unitID()
	{
		return(unitID);
	}
	
	public static Unit copy(Unit other)
	{
		Unit unit=new Unit(other.sprite(),other.point(),other.unitID);
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
		unit.cargoCount=other.cargoCount;
		if(unit.cargo1!=null)
			unit.cargo1=copy(other.cargo1);
		if(unit.cargo2!=null)
			unit.cargo2=copy(other.cargo2);
		unit.imageIndex(other.imageIndex());
		return(unit);
	}

	public static Unit copy(Unit other, Team team)
	{
		Unit unit=copy(other);
		unit.team=team;
		if(unit.cargo1!=null)
			unit.cargo1.team(unit.team);
		if(unit.cargo2!=null)
			unit.cargo2.team(unit.team);
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
	private int unitID;
	
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
	
	private int cargoCount;
	private Unit cargo1;
	private Unit cargo2;
	
	private UnitWeapon weapon1;
	private UnitWeapon weapon2;
	
	private Point attackRange;
	
	private BufferedImage image;
	
	private ArrayList<UnitListener> unitListeners;
	
	public static int baseDamage(Unit attacker, Unit defender)
	{
		if(attacker.weapon2!=null&&attacker.ammo>0&&attacker.weapon2.isEffectiveAggainst(defender.unitID))
		{
			//use weapon2
			attacker.ammo--;
			return(baseAttackChart[attacker.weapon2.weaponID()][defender.unitID]);
		}
		else if(attacker.weapon1!=null)
		{
			//use weapon1
			return(baseAttackChart[attacker.weapon1.weaponID()][defender.unitID]);
		}
		else
		{
			System.out.println("ERROR: Unit should not have attacked!");
			return(0);//this shouldn't happen, but if it does, ok....?
		}
		//return(baseAttackChart[attacker.attackType+(attacker.usesAmmo?(attacker.ammo>0?1:0):0)][defender.defenceType+(defender.usesAmmo?(defender.ammo>0?1:0):0)]);
	}
	
	public static final int[][] baseAttackChart=new int[][]
		{
			new int[]{55,45,5,1,12,5,25,15,25,14},//infantry
			new int[]{65,55,55,15,85,65,85,70,85,75},//mech
			new int[]{65,55,6,1,18,6,35,32,35,20},//mech no ammo
			new int[]{75,70,55,15,85,65,85,70,85,75},//tank
			new int[]{75,70,6,1,40,6,30,45,55,45},//tank no ammo
			new int[]{},//md tank
			new int[]{},//md tank no ammo
			new int[]{70,65,6,1,35,4,28,45,55,45},//recon
			new int[]{},//anti-air
			new int[]{},//missiles
			new int[]{90,85,70,45,80,75,80,75,80,70},//artillery
			new int[]{},//rockets
			new int[]{}//apc
		};
	
}

enum MovementType{FOOT,MECH,TREAD,TIRES,SHIP,TRANS,AIR};
