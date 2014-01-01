package denaro.nick.wars;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
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
		fuel=999;
		health=100;
		movement=1;
		vision=1;
		movementType=MovementType.DEFAULT;
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
	
	public void vision(int vision)
	{
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
	
	public void movement(int movement)
	{
		this.movement=movement;
	}
	
	public MovementType movementType()
	{
		return(movementType);
	}
	
	public void movementType(MovementType movementType)
	{
		this.movementType=movementType;
	}
	
	public static Unit copy(Unit other)
	{
		Unit unit=new Unit(other.sprite(),other.point(),other.team());
		unit.fuel=other.fuel;
		unit.health=other.health;
		unit.movement=other.movement;
		unit.movementType=other.movementType;
		return(unit);
	}

	public static Unit copy(Unit other, Team team)
	{
		Unit unit=new Unit(other.sprite(),other.point(),other.team());
		unit.fuel=other.fuel;
		unit.health=other.health;
		unit.movement=other.movement;
		unit.movementType=other.movementType;
		unit.vision=other.vision;
		unit.team=team;
		return(unit);
	}
	
	@Override
	public Image image()
	{
		BufferedImage image=new BufferedImage(sprite().width(),sprite().height(),BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=image.createGraphics();
		g.drawImage(super.image(), 0, 0, null);
		if(team!=null)
		{
			g.setColor(team.color());
			//Composite oldComposite=g.getComposite();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));
			g.fillRect(0, 0, sprite().width(), sprite().height());
		}
		
		if(!enabled)
		{
			g.setColor(Color.black);
			//Composite oldComposite=g.getComposite();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));
			g.fillRect(0, 0, sprite().width(), sprite().height());
		}
		
		return(image);
	}
	
	@Override
	public void tick()
	{
		// TODO Auto-generated method stub
		
	}

	private boolean enabled;
	private Team team;
	private int fuel;
	private int health;
	private int movement;
	private int vision;
	private MovementType movementType;
}

enum MovementType{DEFAULT,FOOT,MECH,WHEEL,TREAD,HELE,PLANE,BOAT,SUB};
