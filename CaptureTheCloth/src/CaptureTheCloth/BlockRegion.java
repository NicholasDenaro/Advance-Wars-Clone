package CaptureTheCloth;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class BlockRegion
{
	protected Vector loc1;
	protected Vector loc2;
	
	public BlockRegion(Vector l1, Vector l2)
	{
		loc1=l1;
		loc2=l2;
	}
	
	public boolean isInRegion(Location loc)
	{
		System.out.println("loc1"+loc1);
		System.out.println("loc"+loc);
		System.out.println("loc2"+loc2);
		
		if(((loc.getX()>=loc1.getX()&&loc.getX()<=loc2.getX())||loc.getX()>=loc2.getX()&&loc.getX()<=loc1.getX())
				&&((loc.getY()>=loc1.getY()&&loc.getY()<=loc2.getY())||loc.getY()>=loc2.getY()&&loc.getY()<=loc1.getY())
				&&((loc.getZ()>=loc1.getZ()&&loc.getZ()<=loc2.getZ())||loc.getZ()>=loc2.getZ()&&loc.getZ()<=loc1.getZ()))
		{
			return(true);
		}
		return(false);
	}
}
