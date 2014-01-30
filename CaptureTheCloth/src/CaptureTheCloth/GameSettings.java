package CaptureTheCloth;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

public class GameSettings
{
	private CaptureTheCloth plugin;
	private String name;
	protected int mapTop;
	protected int mapBottom;
	protected boolean enderPearlEnabled;
	protected Location spawnLocation;
	protected int spawnTime;
	private File configFile;
	protected ArrayList<BlockRegion> restrictedBlocks;
	
	
	public GameSettings(CaptureTheCloth ctc, String n)
	{
		plugin=ctc;
		name=n;

		spawnLocation=null;
		mapTop=256;
		mapBottom=0;
		enderPearlEnabled=true;
		restrictedBlocks=new ArrayList<BlockRegion>();
		
		spawnTime=0;
		
		configFile=new File(plugin.getDataFolder(),name+".yml");
		
		try
		{
			if(!configFile.exists())
				configFile.createNewFile();
			YamlConfiguration config=new YamlConfiguration();
			config.load(configFile);
			mapTop=config.getInt("Settings.maptop",256);
			mapBottom=config.getInt("Settings.mapbottom",0);
			enderPearlEnabled=config.getBoolean("Settings.enderpearlenabled",true);
			spawnTime=config.getInt("Settings.spawntime",0);
			spawnLocation=new Location(plugin.getServer().getWorld(n),config.getDouble("Settings.spawn.x"),config.getDouble("Settings.spawn.y"),config.getDouble("Settings.spawn.z"));
		
			if(config.contains("Settings.restricted"))
			{
				Set<String> keys=config.getConfigurationSection("Settings.restricted").getKeys(false);
				Iterator<String> it=keys.iterator();
				while(it.hasNext())
				{
					String key=it.next();
					Vector v1=(Vector) config.get("Settings.restricted."+key+".loc1");
					Vector v2=(Vector) config.get("Settings.restricted."+key+".loc2");
					restrictedBlocks.add(new BlockRegion(v1,v2));
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void addRestrictedLocation(Vector loc1, Vector loc2)
	{
		restrictedBlocks.add(new BlockRegion(loc1,loc2));
		YamlConfiguration config=new YamlConfiguration();
		try
		{
			config.load(configFile);
			
			config.set("Settings.restricted."+(restrictedBlocks.size()-1)+".loc1", loc1);
			config.set("Settings.restricted."+(restrictedBlocks.size()-1)+".loc2", loc2);
			
			config.save(configFile);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public boolean blockIsInRestrictedArea(Location loc)
	{
		for(int i=0;i<restrictedBlocks.size();i+=1)
		{
			if(restrictedBlocks.get(i).isInRegion(loc))
				return(true);
		}
		return(false);
	}
	
	public void setSpawnLocation(Location loc)
	{
		spawnLocation=loc;
		YamlConfiguration config=new YamlConfiguration();
		try
		{
			config.load(configFile);
			
			config.set("Settings.spawn.x", spawnLocation.getX());
			config.set("Settings.spawn.y", spawnLocation.getY());
			config.set("Settings.spawn.z", spawnLocation.getZ());
			
			config.save(configFile);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void setEnderPearlEnabled(boolean enabled)
	{
		enderPearlEnabled=enabled;
		YamlConfiguration config=new YamlConfiguration();
		try
		{
			config.load(configFile);
			
			config.set("Settings.enderpearlenabled", enderPearlEnabled);
			
			config.save(configFile);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
			
	}
	
	public void setMapBottom(Location loc)
	{
		spawnLocation=loc;
		YamlConfiguration config=new YamlConfiguration();
		try
		{
			config.load(configFile);
			
			config.set("Settings.mapbottom", loc.getY()-1);
			
			config.save(configFile);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void setMapTop(Location loc)
	{
		spawnLocation=loc;
		YamlConfiguration config=new YamlConfiguration();
		try
		{
			config.load(configFile);
			
			config.set("Settings.maptop", loc.getY()-1);
			
			config.save(configFile);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
