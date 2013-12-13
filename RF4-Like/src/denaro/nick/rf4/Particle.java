package denaro.nick.rf4;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import denaro.nick.core.Entity;
import denaro.nick.core.Sprite;


public class Particle extends Entity
{

	public Particle(Sprite sprite, Double point,double duration)
	{
		super(sprite,point);
		this.duration=(long)(Main.engine().MILLISECOND*duration);
		start=-1;
	}
	
	public Particle(Shape shape, Double point, double direction, Color color, double duration)
	{
		super(null,point);
		BufferedImage image;
		Rectangle2D rect=shape.getBounds2D();
		//System.out.println(rect);
		//image=new BufferedImage(rect.width+(rect.x<0?-rect.x:0),rect.height+(rect.y<0?-rect.y:0),BufferedImage.TYPE_INT_ARGB);
		//image=new BufferedImage(rect.width,rect.height,BufferedImage.TYPE_INT_ARGB);
		int size=(int)Math.max(rect.getWidth(),rect.getHeight());
		image=new BufferedImage(size*2,size*2,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=image.createGraphics();
		AffineTransform at=new AffineTransform();
		g.setColor(Color.GREEN);
		g.drawRect(0,0,image.getWidth()-1,image.getHeight()-1);
		
		at.translate(size,size);
		at.rotate(direction);
		
		g.setTransform(at);
		g.setColor(color);
		g.draw(shape);
		sprite(new Sprite("Particle",image,image.getWidth(),image.getHeight(),new Point(size,size+8)));
	
		this.duration=(long)(Main.engine().MILLISECOND*duration);
		start=-1;
	}
	
	public Particle(Shape shape, Color color, double duration)
	{
		super(null,new Point.Double(shape.getBounds().getLocation().x,shape.getBounds().getLocation().y));
		BufferedImage image;
		Rectangle2D rect=shape.getBounds2D();

		image=new BufferedImage((int)rect.getWidth()+1,(int)rect.getHeight()+1,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=image.createGraphics();
		
		AffineTransform at=new AffineTransform();
		at.translate(-rect.getX(),-rect.getY());
		g.setTransform(at);
		
		g.setColor(Color.GREEN);
		g.drawRect(0,0,image.getWidth()-1,image.getHeight()-1);
		
		g.setColor(color);
		g.draw(shape);
		sprite(new Sprite("Particle",image,image.getWidth(),image.getHeight(),new Point(0,0)));
		
		this.duration=(long)(Main.engine().MILLISECOND*duration);
		start=-1;
	}

	@Override
	public void tick()
	{
		if(start==-1)
		{
			start=System.currentTimeMillis();
		}
		else
		{
			if(System.currentTimeMillis()-start>duration)
			{
				Main.engine().removeEntity(this,Main.engine().location());
			}
		}
	}
	
	private long start;
	private long duration;
}
