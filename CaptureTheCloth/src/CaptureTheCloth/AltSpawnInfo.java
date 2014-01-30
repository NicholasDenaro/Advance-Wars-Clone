package CaptureTheCloth;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class AltSpawnInfo
{
	private CaptureTheCloth plugin;
	private String name;
	private String currentTeam;
	private Location flag;
	private Location currentFlagLocation;
	private ItemStack block;
	private Location spawn;
	protected long graceTime;
	protected long returnTime;
	
	public AltSpawnInfo(CaptureTheCloth plug, String n, Location fl, ItemStack bl, Location sp)
	{
		plugin=plug;
		name=n;
		currentTeam=null;
		flag=fl;
		currentFlagLocation=flag;
		block=bl;
		spawn=sp;
		graceTime=-1;
		returnTime=-1;
	}
	
	public String getName()
	{
		return(name);
	}
	
	public String getCurrentTeam()
	{
		return(currentTeam);
	}
	
	public void setCurrentTeam(String te)
	{
		currentTeam=te;
	}
	
	public Location getFlag()
	{
		return(flag);
	}
	
	public Location getCurrentFlagLocation()
	{
		return(currentFlagLocation);
	}
	
	public ItemStack getBlock()
	{
		return(block);
	}
	
	public void setCurrentFlagLocation(Location loc)
	{
		currentFlagLocation=loc;
	}
	
	public void setFlagCaptured(String n)
	{
		currentFlagLocation=null;
	}
	
	public Location getSpawn()
	{
		return(spawn);
	}
}
