package denaro.nick.rf4;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D.Double;

public class Attack
{
	public Attack(String name, Shape shape)
	{
		this.name=name;
		this.shape=shape;
	}
	
	public Area shape()
	{
		return(new Area(shape));
	}
	
	private String name;
	private Shape shape;
	
	public static Attack sword=new Attack("Swing",new QuadCurve2D.Double( 16*Math.cos(-Math.PI*3/4), -16*Math.sin(-Math.PI*3/4), 32, 12, 20*Math.cos(Math.PI/3), -20*Math.sin(Math.PI/3)));
}


