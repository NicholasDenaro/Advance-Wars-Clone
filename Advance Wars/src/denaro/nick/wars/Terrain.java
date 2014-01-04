package denaro.nick.wars;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import denaro.nick.core.Entity;
import denaro.nick.core.Identifiable;
import denaro.nick.core.Sprite;

public class Terrain extends Entity
{
	public Terrain(String name, int[] movementCosts)
	{
		this(name);
		this.movementCosts=movementCosts;
	}
	
	public Terrain(String name)
	{
		super(Sprite.sprite("Terrain"),null);
		this.name=name;
		this.movementCosts=defaultMovementCosts;
		hiding=false;
		visionBoost=0;
		defence=0;
	}
	
	public String name()
	{
		return(name);
	}
	
	public boolean hiding()
	{
		return(hiding);
	}
	
	public void hiding(boolean hiding)
	{
		this.hiding=hiding;
	}
	
	public void visionBoost(int visionBoost)
	{
		this.visionBoost=visionBoost;
	}
	
	public int visionBoost()
	{
		return(visionBoost);
	}
	
	public void movementCosts(int[] movementCosts)
	{
		this.movementCosts=movementCosts;
	}
	
	public int[] movementCosts()
	{
		return(movementCosts);
	}
	
	public int defence()
	{
		return(defence);
	}
	
	public void defence(int defence)
	{
		this.defence=defence;
	}
	
	public int movementCost(MovementType type)
	{
		switch(type)
		{
			case FOOT:
				return(movementCosts[0]);
			case MECH:
				return(movementCosts[1]);
			case TREAD:
				return(movementCosts[2]);
			case TIRES:
				return(movementCosts[3]);
			case SHIP:
				return(movementCosts[4]);
			case TRANS:
				return(movementCosts[5]);
			case AIR:
				return(movementCosts[6]);
			default:
				return(1);
		}
	}
	
	private String name;
	
	private int[] movementCosts;
	
	private int defence;
	
	private boolean hiding;
	
	private int visionBoost;
	
	public static final int[] defaultMovementCosts=new int[]{1,1,1,1,1,1,1};

	@Override
	public void tick()
	{
		// TODO Auto-generated method stub
		
	}
}
