package denaro.nick.rf4;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import denaro.nick.core.Entity;
import denaro.nick.core.Sprite;

public class Pickupable extends Entity
{
	public Pickupable(Sprite sprite,Double point)
	{
		super(sprite,point);
		held=false;
		thrown=false;
		height=0;
		speed=0;
		fallSpeed=0;
	}

	public void pickup(Point.Double point)
	{
		move(point);
		held=true;
	}
	
	public void drop(Point.Double point)
	{
		move(point);
		held=false;
		height=16;
		speed=0;
		fallSpeed=1;
	}
	
	public void thrown(Point.Double point, double direction)
	{
		move(point);
		height=16;
		this.direction=direction;
		speed=5;
		fallSpeed=0.5;
		thrown=true;
		held=false;
	}

	@Override
	public void tick()
	{
		
		if(height>0)
		{
			fallSpeed+=0.1;
			height-=fallSpeed;
			offset(new Point.Double(0,-height));
			Point.Double delta=new Point.Double(Math.cos(direction),Math.sin(direction));
			ArrayList<Entity> solids=Main.engine().location().entityList(Wall.class);
			move(delta,speed,solids);
		}
		else
		{
			height=0;
			offset(new Point.Double(0,-height));
		}
	}
	
	public void move(Point.Double delta, double speed, ArrayList<Entity> entities)
	{
		Point.Double start=point();
		start.y+=delta.y*speed;
		start.x+=delta.x*speed;
		if(this.collision(start,entities))
		{
			start.y-=delta.y*speed;
			start.y=(int)(start.y+0.5);
			
			start.x-=delta.x*speed;
			start.x=(int)(start.x+0.5);
			
			thrown=false;
			
		}
		move(start);
	}
	
	private double fallSpeed;
	private boolean held;
	private boolean thrown;
	private int height;
	private double speed;
	private double direction;
}
