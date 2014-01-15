package denaro.nick.wars;

import java.awt.Point;

import denaro.nick.core.Identifiable;

public class Commander extends Identifiable
{
	public Commander(String name)
	{
		this.name=name;
		meleeAttackPower=100;
		rangedAttackPower=100;
		defencePower=100;
		rangeModifier=new Point(0,0);
	}
	
	public int meleeAttackPower()
	{
		return(meleeAttackPower);
	}
	
	public int rangedAttackPower()
	{
		return(rangedAttackPower);
	}
	
	public int defencePower()
	{
		return(defencePower);
	}
	
	public Point rangeModifier()
	{
		return(rangeModifier);
	}
	
	public String name()
	{
		return(name);
	}
	
	private String name;
	private int meleeAttackPower;
	private int rangedAttackPower;
	private int defencePower;
	private Point rangeModifier;
}
