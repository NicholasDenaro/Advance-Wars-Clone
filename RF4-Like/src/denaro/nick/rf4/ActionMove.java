package denaro.nick.rf4;

import java.awt.Point;

import denaro.nick.core.Entity;

public class ActionMove extends Action
{
	
	public ActionMove(double duration, double direction, double speed)
	{
		super(duration);
		this.direction=direction;
		this.speed=speed;
	}

	@Override
	public void act(Entity entity)
	{
		if(entity instanceof Player)
		{
			((Player)entity).move(new Point.Double(Math.cos(direction),Math.sin(direction)),speed,Main.engine().location().entityList(Solid.class));
		}
	}
	
	private double direction;
	private double speed;
}
