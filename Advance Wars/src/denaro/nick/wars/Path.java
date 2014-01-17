package denaro.nick.wars;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.util.ArrayList;

import denaro.nick.core.Sprite;

public class Path
{
	public Path(MovementType movementType, int cost)
	{
		this.movementType=movementType;
		points=new ArrayList<Point>();
		this.cost=cost;
	}
	
	public void start(int x, int y)
	{
		points.add(new Point(x,y));
	}
	
	public void addPoint(int x, int y)
	{
		Point to=new Point(x,y);
		if(to.distance(points.get(points.size()-1))==1)
		{
			Map map=(Map)Main.engine().location();
			if(cost-map.terrain(x, y).movementCost(movementType)>=0)
			{
				points.add(to);
				cost-=map.terrain(x, y).movementCost(movementType);
				buildImage(map.width(),map.height());
			}
		}
	}
	
	public boolean isValid(Map map)
	{
		Unit unit=map.unit(first().x,first().y);
		for(int i=1;i<points.size();i++)
		{
			Point prev=points.get(i-1);
			Point curr=points.get(i);
			int diff=Math.abs(prev.x-curr.x)+Math.abs(prev.y-curr.y);
			if(diff!=1)
				return(false);
			if(map.terrain(curr.x,curr.y).movementCost(movementType)==99)
				return(false);
			if(map.unit(curr.x,curr.y)!=null&&!Team.sameTeam(unit.team(),map.unit(curr.x,curr.y).team()))
				return(false);
		}
		return(true);
	}
	
	public ArrayList<Point> points()
	{
		return(points);
	}
	
	public Point first()
	{
		return(points.get(0));
	}
	
	public Point last()
	{
		return(points.get(points.size()-1));
	}
	
	public void buildImage(int width, int height)
	{
		image=new BufferedImage(width*Main.TILESIZE,height*Main.TILESIZE,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=image.createGraphics();
		Point direction;
		int i=0;
		
		for(i=1;i<points.size()-1;i++)
		{
			Point prev=points.get(i-1);
			Point current=points.get(i);
			Point next=points.get(i+1);
			Point from=new Point(current.x-prev.x,current.y-prev.y);
			Point to=new Point(next.x-current.x,next.y-current.y);
			if(from.distance(to)<2&&from.distance(to)>1)
				direction=new Point(1+(from.x-to.x),1+(from.y-to.y));
			else if(from.distance(to)==0)
				direction=new Point(1+(to.x),1+(to.y));
			else
				direction=new Point(1,1);
			
			try
			{
				g.drawImage(Sprite.sprite("Path").subimage(direction.x,direction.y), current.x*Main.TILESIZE, current.y*Main.TILESIZE, null);
			}
			catch(RasterFormatException ex)
			{
				ex.printStackTrace();
				System.out.println("direction: "+direction);
			}
		}
		
		Point current=points.get(i-1);
		Point next=points.get(i);
		direction=new Point(1+(next.x-current.x),1+(next.y-current.y));
		g.drawImage(Sprite.sprite("Arrow").subimage(direction.x,direction.y), next.x*Main.TILESIZE, next.y*Main.TILESIZE, null);
	}
	
	public MovementType movementType()
	{
		return(movementType);
	}
	
	public Image image(int width, int height)
	{
		return(image);
	}
	
	private BufferedImage image;
	private MovementType movementType;
	private ArrayList<Point> points;
	private int cost;
}
