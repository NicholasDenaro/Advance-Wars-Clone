package CaptureTheCloth;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public class TeamInfo
{
	private CaptureTheCloth plugin;
	private String name;
	private Location flag;
	private Location currentFlagLocation;
	private ItemStack block;
	private Location spawn;
	private int deaths;
	protected int points;
	protected long graceTime;
	protected long returnTime;
	protected int players;
	
	public TeamInfo(CaptureTheCloth plug, String n, Location fl, ItemStack bl, Location sp)
	{
		plugin=plug;
		name=n;
		flag=fl;
		currentFlagLocation=flag;
		block=bl;
		spawn=sp;
		deaths=0;
		points=0;
		graceTime=-1;
		returnTime=-1;
		players=0;
	}
	
	public String getName()
	{
		return(name);
	}
	
	public Location getFlag()
	{
		return(flag);
	}
	
	public Location getCurrentFlagLocation()
	{
		return(currentFlagLocation);
	}
	
	public void setCurrentFlagLocation(Location loc)
	{
		currentFlagLocation=loc;
	}
	
	public void setFlagCaptured(String n)
	{
		currentFlagLocation=null;
	}
	
	public ItemStack getBlock()
	{
		return(block);
	}
	
	public Location getSpawn()
	{
		return(spawn);
	}
	
	public void incrementDeaths()
	{
		deaths++;
	}
	
	public int getDeaths()
	{
		return(deaths);
	}
}
