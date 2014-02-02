package denaro.nick.wars;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

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
		imageIndex(7);
		tiles=new ArrayList<Integer>();
		color=new Color(255,255,255);
	}
	
	public void update(Terrain other)
	{
		//TODO maybe something?
	}
	
	public void addTiles(int... tiles)
	{
		for(int t:tiles)
			this.tiles.add(t);
	}
	
	public ArrayList<Integer> tiles()
	{
		return(tiles);
	}
	
	public boolean isDirectional()
	{
		return(!tiles.isEmpty());
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
	
	public Image image()
	{
		return(sprite().subimage(imageIndex()));
	}
	
	public Image image(int index)
	{
		if(index==-1)
			index=imageIndex();
		return(sprite().subimage(index));
	}
	
	public int direction(Map map, int x, int y)
	{
		//TODO get the direction
		
		return(imageIndex());
	}
	
	public void color(Color color)
	{
		this.color=color;
	}
	
	public Color color()
	{
		return(color);
	}
	
	private String name;
	
	private int[] movementCosts;
	
	private int defence;
	
	private boolean hiding;
	
	private int visionBoost;
	
	private Color color;
	
	private ArrayList<Integer> tiles;
	
	//private ArrayList<int[][]> tileMap;
	
	public static final int[] defaultMovementCosts=new int[]{1,1,1,1,1,1,1};

	@Override
	public void tick()
	{
		// TODO if terrain is animated?
		
	}
}
