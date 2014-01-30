package CaptureTheCloth;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class CaptureTheCloth extends JavaPlugin
{
	private CommandHandler commandHandler;
	
	private HashMap<String,TeamInfo> teams;
	private HashMap<String,AltSpawnInfo> altSpawns;
	private HashMap<String,PlayerInfo> players;
	private HashMap<Location,String> blocks;
	protected String building;
	
	protected GameSettings settings;
	
	public void onEnable()
	{
		commandHandler=new CommandHandler(this);
		getCommand("LoadSettings").setExecutor(commandHandler);
		getCommand("EnableEnderPearls").setExecutor(commandHandler);
		getCommand("RestrictRegion").setExecutor(commandHandler);
		getCommand("SetMapBottom").setExecutor(commandHandler);
		getCommand("SetMapTop").setExecutor(commandHandler);
		getCommand("SetSpawnTime").setExecutor(commandHandler);
		getCommand("SetSpawn").setExecutor(commandHandler);
		getCommand("NewAltSpawn").setExecutor(commandHandler);
		getCommand("NewTeam").setExecutor(commandHandler);
		getCommand("SetTeam").setExecutor(commandHandler);
		getCommand("SetLoadout").setExecutor(commandHandler);
		getCommand("KillAll").setExecutor(commandHandler);
		getCommand("SetBuild").setExecutor(commandHandler);
		getCommand("ResetMap").setExecutor(commandHandler);
		teams=new HashMap<String,TeamInfo>();
		altSpawns=new HashMap<String,AltSpawnInfo>();
		players=new HashMap<String,PlayerInfo>();
		blocks=new HashMap<Location,String>();
		getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
		getServer().getPluginManager().registerEvents(new BattleListener(this), this);
		getServer().getPluginManager().registerEvents(new BlockListener(this), this);
		getServer().getPluginManager().registerEvents(new WarriorListener(this), this);
		getServer().getPluginManager().registerEvents(new MagicianListener(this), this);
		getServer().getPluginManager().registerEvents(new ArcherListener(this), this);
		getServer().getPluginManager().registerEvents(new BuilderListener(this), this);
		getServer().getPluginManager().registerEvents(new SupportListener(this), this);
		
		building="off";
		//spawnLocation=null;
		
		
		settings=null;
		
		FileConfiguration config=getConfig();
		
		if(config.contains("Blocks"))
		{
			Set<String> keys=config.getConfigurationSection("Blocks").getKeys(false);
			Iterator<String> it=keys.iterator();
			while(it.hasNext())
			{
				String key=it.next();
				World w=getServer().getWorld(key);
				if(w!=null)
				{
					Set<String> locations=config.getConfigurationSection("Blocks."+key).getKeys(false);
					Iterator<String> it2=locations.iterator();
					while(it2.hasNext())
					{
						String loc=it2.next();
						//System.out.println("loc: "+loc);
						Location l=new Location(w,config.getDouble("Blocks."+key+"."+loc+".x"),config.getDouble("Blocks."+key+"."+loc+".y"),config.getDouble("Blocks."+key+"."+loc+".z"));
						//System.out.println("  "+l);
						blocks.put(l, "");
					}
					break;
				}
			}
		}
		if(config.contains("Teams"))
		{
			Set<String> keys=config.getConfigurationSection("Teams").getKeys(false);
			Iterator<String> it=keys.iterator();
			while(it.hasNext())
			{
				String key=it.next();
				World flagWorld=getServer().getWorld(config.getString("Teams."+key+".flag.world"));
				if(flagWorld!=null)
				{
					Location flagLocation=new Location(flagWorld,config.getDouble("Teams."+key+".flag.x"),config.getDouble("Teams."+key+".flag.y"),config.getDouble("Teams."+key+".flag.z"));
					ItemStack block=(ItemStack)config.get("Teams."+key+".block");
					World spawnWorld=getServer().getWorld(config.getString("Teams."+key+".spawn.world"));
					if(spawnWorld!=null)
					{
						Location spawnLocation=new Location(spawnWorld,config.getDouble("Teams."+key+".spawn.x"),config.getDouble("Teams."+key+".spawn.y"),config.getDouble("Teams."+key+".spawn.z"));
						TeamInfo team=new TeamInfo(this,key,flagLocation,block,spawnLocation);
						teams.put(key, team);
						System.out.println("Team "+key+" has been loaded");
					}
					else
					{
						System.out.println("ERROR: The spawn's world is not loaded.");
					}
				}
				else
				{
					System.out.println("ERROR: The flag's world is not loaded.");
				}
			}
		}
		if(config.contains("AltSpawns"))
		{
			Set<String> keys=config.getConfigurationSection("AltSpawns").getKeys(false);
			Iterator<String> it=keys.iterator();
			while(it.hasNext())
			{
				String key=it.next();
				World flagWorld=getServer().getWorld(config.getString("AltSpawns."+key+".flag.world"));
				if(flagWorld!=null)
				{
					Location flagLocation=new Location(flagWorld,config.getDouble("AltSpawns."+key+".flag.x"),config.getDouble("AltSpawns."+key+".flag.y"),config.getDouble("AltSpawns."+key+".flag.z"));
					ItemStack block=(ItemStack)config.get("AltSpawns."+key+".block");
					World spawnWorld=getServer().getWorld(config.getString("AltSpawns."+key+".spawn.world"));
					if(spawnWorld!=null)
					{
						Location spawnLocation=new Location(spawnWorld,config.getDouble("AltSpawns."+key+".spawn.x"),config.getDouble("AltSpawns."+key+".spawn.y"),config.getDouble("AltSpawns."+key+".spawn.z"));
						AltSpawnInfo spawn=new AltSpawnInfo(this,key,flagLocation,block,spawnLocation);
						altSpawns.put(key, spawn);
						System.out.println("AltSpawn "+key+" has been loaded");
					}
					else
					{
						System.out.println("ERROR: The spawn's world is not loaded.");
					}
				}
				else
				{
					System.out.println("ERROR: The flag's world is not loaded.");
				}
			}
		}
	}
	
	public void onLoad()
	{
		
		teams=new HashMap<String,TeamInfo>();
		altSpawns=new HashMap<String,AltSpawnInfo>();
		players=new HashMap<String,PlayerInfo>();
		blocks=new HashMap<Location,String>();
		
		
		settings=null;
		
		FileConfiguration config=getConfig();
		
		if(config.contains("Blocks"))
		{
			Set<String> keys=config.getConfigurationSection("Blocks").getKeys(false);
			Iterator<String> it=keys.iterator();
			while(it.hasNext())
			{
				String key=it.next();
				World w=getServer().getWorld(key);
				if(w!=null)
				{
					Set<String> locations=config.getConfigurationSection("Blocks."+key).getKeys(false);
					Iterator<String> it2=locations.iterator();
					while(it2.hasNext())
					{
						String loc=it2.next();
						Location l=new Location(w,config.getDouble("Blocks."+key+loc+".x"),config.getDouble("Blocks."+key+loc+".y"),config.getDouble("Blocks."+key+loc+".z"));
						blocks.put(l, "");
					}
					break;
				}
			}
		}
		if(config.contains("Teams"))
		{
			Set<String> keys=config.getConfigurationSection("Teams").getKeys(false);
			Iterator<String> it=keys.iterator();
			while(it.hasNext())
			{
				String key=it.next();
				World flagWorld=getServer().getWorld(config.getString("Teams."+key+".flag.world"));
				if(flagWorld!=null)
				{
					Location flagLocation=new Location(flagWorld,config.getDouble("Teams."+key+".flag.x"),config.getDouble("Teams."+key+".flag.y"),config.getDouble("Teams."+key+".flag.z"));
					ItemStack block=(ItemStack)config.get("Teams."+key+".block");
					World spawnWorld=getServer().getWorld(config.getString("Teams."+key+".spawn.world"));
					if(spawnWorld!=null)
					{
						Location spawnLocation=new Location(spawnWorld,config.getDouble("Teams."+key+".spawn.x"),config.getDouble("Teams."+key+".spawn.y"),config.getDouble("Teams."+key+".spawn.z"));
						TeamInfo team=new TeamInfo(this,key,flagLocation,block,spawnLocation);
						teams.put(key, team);
						System.out.println("Team "+key+" has been loaded");
					}
					else
					{
						System.out.println("ERROR: The spawn's world is not loaded.");
					}
				}
				else
				{
					System.out.println("ERROR: The flag's world is not loaded.");
				}
			}
		}
		if(config.contains("AltSpawns"))
		{
			Set<String> keys=config.getConfigurationSection("AltSpawns").getKeys(false);
			Iterator<String> it=keys.iterator();
			while(it.hasNext())
			{
				String key=it.next();
				World flagWorld=getServer().getWorld(config.getString("AltSpawns."+key+".flag.world"));
				if(flagWorld!=null)
				{
					Location flagLocation=new Location(flagWorld,config.getDouble("AltSpawns."+key+".flag.x"),config.getDouble("AltSpawns."+key+".flag.y"),config.getDouble("AltSpawns."+key+".flag.z"));
					ItemStack block=(ItemStack)config.get("AltSpawns."+key+".block");
					World spawnWorld=getServer().getWorld(config.getString("AltSpawns."+key+".spawn.world"));
					if(spawnWorld!=null)
					{
						Location spawnLocation=new Location(spawnWorld,config.getDouble("AltSpawns."+key+".spawn.x"),config.getDouble("AltSpawns."+key+".spawn.y"),config.getDouble("AltSpawns."+key+".spawn.z"));
						AltSpawnInfo spawn=new AltSpawnInfo(this,key,flagLocation,block,spawnLocation);
						altSpawns.put(key, spawn);
						System.out.println("AltSpawn "+key+" has been loaded");
					}
					else
					{
						System.out.println("ERROR: The spawn's world is not loaded.");
					}
				}
				else
				{
					System.out.println("ERROR: The flag's world is not loaded.");
				}
			}
		}
	}
	
	public void onDisable()
	{
		FileConfiguration config=getConfig();
		Iterator<Location> it=blocks.keySet().iterator();
		int i=0;
		config.set("Blocks", null);
		saveConfig();
		while(it.hasNext())
		{
			Location loc=it.next();
			config.set("Blocks."+loc.getWorld().getName()+"."+i+".x",loc.getX());
			config.set("Blocks."+loc.getWorld().getName()+"."+i+".y",loc.getY());
			config.set("Blocks."+loc.getWorld().getName()+"."+i+".z",loc.getZ());
			i++;
		}
		saveConfig();
	}
	
	public PlayerInfo getPlayer(String name)
	{
		if(players.containsKey(name))
			return(players.get(name));
		PlayerInfo info=new PlayerInfo(this,name);
		players.put(name, info);
		info.save();
		return(info);
	}
	
	public TeamInfo getTeam(String name)
	{
		if(teams.containsKey(name))
			return(teams.get(name));
		else
			return(null);
	}
	
	public AltSpawnInfo getAltSpawn(String name)
	{
		if(altSpawns.containsKey(name))
			return(altSpawns.get(name));
		else
			return(null);
	}
	
	public TeamInfo getRandomTeam()
	{
		/*Collection<TeamInfo> t=teams.values();
		int r=(int) (Math.random()*t.size());
		Iterator<TeamInfo> it=t.iterator();
		for(int i=0;i<r;i+=1)
			it.next();
		return(it.next());*/
		Collection<TeamInfo> t=teams.values();
		Iterator<TeamInfo> it=t.iterator();
		TeamInfo lowTeam=it.next();
		while(it.hasNext())
		{
			TeamInfo tInfo=it.next();
			if(lowTeam.players>tInfo.players)
				lowTeam=tInfo;
		}
		return(lowTeam);
	}
	
	public TeamInfo blockIsFlag(Location loc)
	{
		Iterator<TeamInfo> it=teams.values().iterator();
		while(it.hasNext())
		{
			TeamInfo team=it.next();
			//System.out.println("clicked: "+loc+"\nactual: "+team.getCurrentFlagLocation());
			Location flagLoc=team.getCurrentFlagLocation();
			if((flagLoc!=null)&&(loc.distance(flagLoc)==0))
			{
				//System.out.println("returning team: "+team.getName());
				return(team);
			}
		}
		return(null);
	}
	
	public AltSpawnInfo blockIsAltFlag(Location loc)
	{
		Iterator<AltSpawnInfo> it=altSpawns.values().iterator();
		while(it.hasNext())
		{
			AltSpawnInfo spawn=it.next();
			//System.out.println("clicked: "+loc+"\nactual: "+team.getCurrentFlagLocation());
			Location flagLoc=spawn.getCurrentFlagLocation();
			if((flagLoc!=null)&&(loc.distance(flagLoc)==0))
			{
				//System.out.println("returning team: "+team.getName());
				return(spawn);
			}
		}
		return(null);
	}
	
	public boolean blockIsInRestrictedArea(Location loc)
	{
		return(settings.blockIsInRestrictedArea(loc));
	}
	
	public void addRestrictedLocation(Location loc1, Location loc2)
	{
		settings.addRestrictedLocation(loc1.toVector(),loc2.toVector());
	}
	
	public void createTeam(String name,Location flag, ItemStack block, Location spawn)
	{
		TeamInfo team=new TeamInfo(this,name,flag,block,spawn);
		teams.put(name, team);
		FileConfiguration config=getConfig();
		config.set("Teams."+name+".flag.world", flag.getWorld().getName());
		config.set("Teams."+name+".flag.x", flag.getX());
		config.set("Teams."+name+".flag.y", flag.getY());
		config.set("Teams."+name+".flag.z", flag.getZ());
		config.set("Teams."+name+".block", block);
		config.set("Teams."+name+".spawn.world",spawn.getWorld().getName());
		config.set("Teams."+name+".spawn.x", spawn.getX());
		config.set("Teams."+name+".spawn.y", spawn.getY());
		config.set("Teams."+name+".spawn.z", spawn.getZ());
		saveConfig();
	}
	
	public void createAltSpawn(String name,Location flag, ItemStack block, Location spawn)
	{
		AltSpawnInfo team=new AltSpawnInfo(this,name,flag,block,spawn);
		altSpawns.put(name, team);
		FileConfiguration config=getConfig();
		config.set("AltSpawns."+name+".flag.world", flag.getWorld().getName());
		config.set("AltSpawns."+name+".flag.x", flag.getX());
		config.set("AltSpawns."+name+".flag.y", flag.getY());
		config.set("AltSpawns."+name+".flag.z", flag.getZ());
		config.set("AltSpawns."+name+".block", block);
		config.set("AltSpawns."+name+".spawn.world",spawn.getWorld().getName());
		config.set("AltSpawns."+name+".spawn.x", spawn.getX());
		config.set("AltSpawns."+name+".spawn.y", spawn.getY());
		config.set("AltSpawns."+name+".spawn.z", spawn.getZ());
		saveConfig();
	}
	
	public void setSpawnLocation(Location loc)
	{
		settings.setSpawnLocation(loc);
	}
	
	public void setMapBottom(Location loc)
	{
		settings.setMapBottom(loc);
	}
	
	public void setMapTop(Location loc)
	{
		settings.setMapTop(loc);
	}
	
	public Location getSpawnLocation()
	{
		return(settings.spawnLocation);
	}
	
	public boolean breakableBlock(Location l)
	{
		if(blocks.containsKey(l))
		{
			blocks.remove(l);
			return(true);
		}
		return(false);
	}
	
	public void addBreakableBlock(Location l, String pl)
	{
		blocks.put(l, pl);
	}
	
	public void removeBreakableBlock(Location l)
	{
		blocks.remove(l);
	}
	
	public void resetBreakableBlocks()
	{
		blocks.clear();
	}
}
