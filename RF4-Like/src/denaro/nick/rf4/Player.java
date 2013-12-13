package denaro.nick.rf4;

import java.awt.Color;
import java.awt.List;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Stack;

import denaro.nick.core.Entity;
import denaro.nick.core.Focusable;
import denaro.nick.core.GameEngineByTick;
import denaro.nick.core.Sprite;

public class Player extends Entity implements Focusable, KeyListener, Solid
{

	public Player(Sprite sprite,Double point)
	{
		super(sprite,point);
		direction=new Point.Double(0,0);
		keys=new boolean[KeyEvent.KEY_LAST];
		actions=new Stack<Action>();
		equipment=new Equipment();
		heldItem=null;
		combo=0;
	}

	@Override
	public void tick()
	{
		Point.Double delta=new Point.Double(0,0);
		double speed=1;
		
		if(keys[KeyEvent.VK_UP])
		{
			delta.y-=1;
		}
		if(keys[KeyEvent.VK_DOWN])
		{
			delta.y+=1;
		}
		if(keys[KeyEvent.VK_LEFT])
		{
			delta.x-=1;
		}
		if(keys[KeyEvent.VK_RIGHT])
		{
			delta.x+=1;
		}
		
		if(actions.isEmpty())
		{
			
			if(magnitude(delta)!=0)
			{
				ArrayList<Entity> solids=Main.engine().location().entityList(Wall.class);
				move(toUnitVector(delta),speed,solids);
				direction=toUnitVector(delta);
			}
		}
		else
		{
			Action action=actions.peek();
			if(!action.finished())
			{
				action.act(this);
				
			}
			else
			{
				actions.pop();
				cooldown=(long)(System.currentTimeMillis()+GameEngineByTick.MILLISECOND*0.2);
				if(actions.size()>0)
					actions.peek().start();
			}
			
		}
	}
	
	public void move(Point.Double delta, double speed, ArrayList<Entity> entities)
	{
		Point.Double start=point();
		start.y+=delta.y*speed;
		if(this.collision(start,entities))
		{
			start.y-=delta.y*speed;
			start.y=(int)(start.y+0.5);
		}
		start.x+=delta.x*speed;
		if(this.collision(start,entities))
		{
			start.x-=delta.x*speed;
			start.x=(int)(start.x+0.5);
		}
		move(start);
	}
	
	public boolean moving()
	{
		return(keys[KeyEvent.VK_UP]||keys[KeyEvent.VK_DOWN]||keys[KeyEvent.VK_LEFT]||keys[KeyEvent.VK_RIGHT]);
	}

	@Override
	public void keyPressed(KeyEvent event)
	{
		keys[event.getKeyCode()]=true;
		if(keys[KeyEvent.VK_SHIFT])
		{
			if(System.currentTimeMillis()>cooldown)
			if(actions.isEmpty())
				actions.push(new ActionMove(0.1,Math.atan2(direction.y,direction.x),3).start());
		}
		if(keys[KeyEvent.VK_Z])
		{
			if(heldItem==null)
			{
				if(System.currentTimeMillis()>cooldown)
				if(actions.isEmpty())
				{
					if(equipment.weapon()!=null)
					{
						System.out.println("swing!");
						actions.push(new ActionAttack(0.2,point(),direction,equipment.weapon(),combo).start());
					}
				}
			}
			else
			{
				if(equipment.weapon()==null)
				{
					equipment.equip(heldItem);
					System.out.println(equipment.weapon());
					Main.engine().removeEntity(heldItem,Main.engine().location());
					dropItem();//doesn't actually drop
				}
			}
		}
		if(keys[KeyEvent.VK_X])
		{
			if(heldItem==null)
			{
				ArrayList<Entity> items=Main.engine().location().entityList(Pickupable.class);
				//create the collision box
				Area rect=new Area(new Rectangle2D.Double(0,0,8,8));
				AffineTransform at=new AffineTransform();
				double dir=Math.atan2(direction.y,direction.x);
				
				
				at.translate(point().x+8*Math.cos(dir),point().y+8*Math.min(Math.sin(dir),0));
				
				at.rotate(dir);
				at.translate(0,-4);
				
				
				rect.transform(at);
				Particle particle=new Particle(rect,Color.pink,1);
				Main.engine().addEntity(particle,Main.engine().location());
				//check for collision with items
				for(Entity entity:items)
				{
					Item item=(Item)entity;
					
					
					if(item.collision(null,rect))
					{
						pickupItem(item);
						item.pickup(new Point.Double(point().x-item.sprite().width()/2,point().y-sprite().height()-item.sprite().height()));
						break;
					}
				}
			}
			else
			{
				double dir=Math.atan2(direction.y,direction.x);
				if(!moving())
					heldItem.drop(new Point.Double(point().x-heldItem.sprite().width()/2+16*Math.cos(dir),point().y-heldItem.sprite().height()+16*Math.max(Math.sin(dir),-0.5)));	
				else
					heldItem.thrown(new Point.Double(point().x-heldItem.sprite().width()/2,point().y-heldItem.sprite().height()),Math.atan2(direction.y,direction.x));
				dropItem();
			}
		}
	}

	public void pickupItem(Item item)
	{
		if(heldItem==null)
		{
			this.addListener(item);//picked up
			heldItem=item;
		}
	}
	
	public void dropItem()
	{
		if(heldItem!=null)
		{
			this.removeListener(heldItem);//dropped
			heldItem=null;
		}
	}
	
	@Override
	public void keyReleased(KeyEvent event)
	{
		keys[event.getKeyCode()]=false;
	}

	@Override
	public void keyTyped(KeyEvent event)
	{
		// TODO Auto-generated method stub
		
	}
	
	private double magnitude(Point.Double point)
	{
		return(point.distance(0,0));
	}
	
	private Point.Double toUnitVector(Point.Double point)
	{
		double magnitude=magnitude(point);
		Point.Double output=new Point.Double(point.x/magnitude,point.y/magnitude);
	
		return(output);
	}
	
	private boolean keys[];
	private Point.Double direction;
	private Stack<Action> actions;
	private long cooldown;
	private Equipment equipment;
	private Item heldItem;
	private byte combo;
}
