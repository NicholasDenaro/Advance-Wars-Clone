package denaro.nick.rf4;

import java.awt.Point;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;

import denaro.nick.core.Entity;
import denaro.nick.core.EntityEvent;
import denaro.nick.core.EntityListener;
import denaro.nick.core.Sprite;

public class Item extends Pickupable implements EntityListener
{

	public Item(Sprite sprite,Double point, ItemType type)
	{
		super(sprite,point);
		this.type=type;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void tick()
	{
		super.tick();
	}
	
	@Override
	public void EntityMove(EntityEvent event)
	{
		Point.Double delta=event.movedDelta();
		moveDelta(delta);
	}

	@Override
	public void EntityDepthChange(EntityEvent event)
	{
		// TODO Auto-generated method stub
		
	}
	
	public ItemType type()
	{
		return(type);
	}
	
	private ArrayList<ItemAttribute> attributes;
	private ItemType type;
}

enum ItemType{misc,head,torso,feet,accessory,weapon,shield};