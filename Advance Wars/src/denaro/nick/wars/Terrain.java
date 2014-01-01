package denaro.nick.wars;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import denaro.nick.core.Identifiable;

public class Terrain extends Identifiable
{
	public Terrain(String name, int[] movementCosts)
	{
		this(name);
		this.movementCosts=movementCosts;
	}
	
	public Terrain(String name)
	{
		this.name=name;
		this.movementCosts=defaultMovementCosts;
		hiding=false;
		visionBoost=0;
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
	
	public int movementCost(MovementType type)
	{
		switch(type)
		{
			case FOOT:
				return(movementCosts[0]);
			case MECH:
				return(movementCosts[1]);
			case WHEEL:
				return(movementCosts[2]);
			case TREAD:
				return(movementCosts[3]);
			case HELE:
				return(movementCosts[4]);
			case PLANE:
				return(movementCosts[5]);
			case BOAT:
				return(movementCosts[6]);
			case SUB:
				return(movementCosts[7]);
			case DEFAULT:
				return(movementCosts[8]);
			default:
				return(1);
		}
	}
	
	public Image image()
	{
		BufferedImage image=new BufferedImage(Main.TILESIZE,Main.TILESIZE,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=image.createGraphics();
		
		if(name.equals("Plain"))
			g.setColor(new Color(0,150,0));
		if(name.equals("Mountain"))
			g.setColor(new Color(120,75,0));
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		
		return(image);
	}
	
	private String name;
	
	private int[] movementCosts;
	
	private boolean hiding;
	
	private int visionBoost;
	
	private static final int[] defaultMovementCosts=new int[]{1,1,1,1,1,1,1,1,1};
}
