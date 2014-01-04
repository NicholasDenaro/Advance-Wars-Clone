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

import denaro.nick.core.Entity;
import denaro.nick.core.Sprite;

public class Unit extends Entity
{

	public Unit(Sprite sprite, Double point, Team team)
	{
		super(sprite, point);
		this.team=team;
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
	
	public void attackType(int attackType)
	{
		this.attackType=attackType;
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
	
	public static Unit copy(Unit other)
	{
		Unit unit=new Unit(other.sprite(),other.point(),other.team());
		unit.canCapture=other.canCapture;
		unit.fuel=other.fuel;
		unit.maxFuel=other.maxFuel;
		unit.usesAmmo=other.usesAmmo;
		unit.ammo=other.ammo;
		unit.maxAmmo=other.maxAmmo;
		unit.health=other.health;
		unit.movement=other.movement;
		unit.movementType=other.movementType;
		unit.vision=other.vision;
		unit.attackRange=other.attackRange;
		unit.attackType=other.attackType;
		unit.imageIndex(other.imageIndex());
		return(unit);
	}

	public static Unit copy(Unit other, Team team)
	{
		Unit unit=copy(other);
		unit.team=team;
		return(unit);
	}
	
	@Override
	public Image image()
	{
		BufferedImage image=new BufferedImage(sprite().width(),sprite().height(),BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=image.createGraphics();
		g.drawImage(super.image(), 0, 0, null);
		Composite oldComposite=g.getComposite();
		if(team!=null)
		{
			Main.swapPalette(image,team,1);
		}
		
		if(!enabled)
		{
			g.setColor(Color.black);
			//Composite oldComposite=g.getComposite();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));
			g.fillRect(0, 0, sprite().width(), sprite().height());
		}
		
		g.setComposite(oldComposite);
		
		int hp=(health+5)/10;
		if(hp!=10)
		{
			if(hp==0)
				hp=1;
			g.drawImage(GameFont.fonts.get("Map Font").stringToImage(""+hp), sprite().width()-8, sprite().height()-8, null);
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
	private int attackType;
	
	private Point attackRange;
	
	
	public static int baseDamage(Unit attacker, Unit defender)
	{
		return(baseAttackChart[attacker.attackType+(attacker.usesAmmo?(attacker.ammo>0?1:0):0)][defender.attackType+(defender.usesAmmo?(defender.ammo>0?1:0):0)]);
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
