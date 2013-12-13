package denaro.nick.rf4;

import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.util.ArrayList;

import denaro.nick.core.Entity;
import denaro.nick.core.Sprite;

public class ActionAttack extends Action
{
	
	public ActionAttack(double duration, Point.Double location, Point.Double direction, Weapon weapon, int combo)
	{
		super(duration);
		this.location=location;
		this.direction=Math.atan2(direction.y,direction.x);
		this.attack=weapon.weapon();
		this.combo=combo;
		Main.engine().addEntity(new Particle(sprite(attack),location,duration),Main.engine().location());
	}
	
	public static Sprite sprite(WeaponType attack)
	{
		switch(attack)
		{
			case sword:
				return(Sprite.sprite("Sword"));
			default:
				return(null);
		}
	}
	
	@Override
	public void act(Entity entity)
	{
		ArrayList<Entity> entities=Main.engine().location().entityList(Attackable.class);
		//check for collisions etc.
	}
	
	private int combo;
	private WeaponType attack;
	private Point.Double location;
	private double direction;
}
