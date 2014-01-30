package CaptureTheCloth;

import java.util.ArrayList;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class PlayerInfo
{
	private CaptureTheCloth plugin;
	private String name;
	private String team;
	private int kills;
	private int deaths;
	private String loadout;
	//private ArrayList<Arrow> arrows;
	protected boolean creatingTeam;
	protected String creatingTeamName;
	protected Location creatingTeamFlag;
	protected ItemStack creatingTeamBlockType;
	protected Location creatingTeamSpawn;
	
	protected boolean creatingAltSpawn;
	protected String creatingAltSpawnName;
	protected Location creatingAltSpawnFlag;
	protected ItemStack creatingAltSpawnBlockType;
	protected Location creatingAltSpawnSpawn;
	
	protected String flagTeam;
	protected String flagAlt;
	protected int flagBlockType;
	protected byte flagBlockData;
	
	
	protected boolean inSpawn;
	protected boolean canSpawn;
	
	protected boolean restrictingRegion;
	protected Location restrictingLoc1;
	protected Location restrictingLoc2;
	
	
	protected int taskID;
	
	
	public PlayerInfo(CaptureTheCloth plug, String n)
	{
		plugin=plug;
		name=n;
		team=null;
		loadout=null;
		//arrows=new ArrayList<Arrow>();
		creatingTeam=false;
		creatingTeamName=null;
		creatingTeamFlag=null;
		creatingTeamBlockType=null;
		creatingTeamSpawn=null;
		
		creatingAltSpawn=false;
		creatingAltSpawnName=null;
		creatingAltSpawnFlag=null;
		creatingAltSpawnBlockType=null;
		creatingAltSpawnSpawn=null;
		
		restrictingRegion = false;
		restrictingLoc1=null;
		restrictingLoc1=null;
		
		flagTeam=null;
		flagAlt=null;
		flagBlockType=Material.AIR.getId();
		flagBlockData=0;
		
		inSpawn=false;
		canSpawn=true;
		taskID=-1;
		
	}
	
	public String getName()
	{
		return(name);
	}
	
	public String getTeam()
	{
		return(team);
	}
	
	public void captureFlag(String te, Block b)
	{
		flagTeam=te;
		flagBlockType=b.getType().getId();
		flagBlockData=b.getData();
	}
	
	public void captureAltFlag(String a, Block b)
	{
		flagAlt=a;
		flagBlockType=b.getType().getId();
		flagBlockData=b.getData();
	}
	
	public void setTeam(String t)
	{
		team=t;
		TeamInfo tInfo;
		if((tInfo=plugin.getTeam(t))!=null)
		{
			plugin.getServer().getPlayer(name).getInventory().setHelmet(new ItemStack(tInfo.getBlock()));
			plugin.getServer().getPlayer(name).updateInventory();
			//System.out.println("Yep, should have added block");
		}
	}
	
	public String getLoadout()
	{
		return(loadout);
	}
	
	public void setLoadout(String load)
	{
		loadout=load;
		final Player player=plugin.getServer().getPlayer(name);
		for(PotionEffectType type:PotionEffectType.values())
  		{
  			if(type!=null)
  				player.addPotionEffect(new PotionEffect(type,1,1),true);
  		}
		player.sendMessage("You are now a "+load);
		player.getInventory().clear();
		player.updateInventory();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
		if(load.equals("Archer"))
		{
			ItemStack item=new ItemStack(Material.BOW,1);
			item.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
			item.addEnchantment(Enchantment.ARROW_INFINITE, 1);
			player.getInventory().setItem(0, item);
			player.getInventory().setItem(1, new ItemStack(Material.ARROW,1));
			Potion pot=new Potion(PotionType.INVISIBILITY);
			player.getInventory().setItem(2, pot.toItemStack(1));
			player.getInventory().setChestplate(null);
		}
		else if(load.equals("Magician"))
		{
			Potion pot=new Potion(PotionType.INSTANT_DAMAGE);
			pot.setSplash(true);
			player.getInventory().setItem(0, pot.toItemStack(1));
			player.getInventory().setItem(1, new ItemStack(Material.BLAZE_ROD,1));
			pot=new Potion(PotionType.POISON);
			pot.setSplash(true);
			player.getInventory().setItem(2, pot.toItemStack(1));
			pot=new Potion(PotionType.SLOWNESS);
			pot.setSplash(true);
			player.getInventory().setItem(3, pot.toItemStack(1));
			player.getInventory().setItem(4, new ItemStack(Material.ENDER_PEARL,1));
			player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
			player.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
		}
		else if(load.equals("Warrior"))
		{
			ItemStack item=new ItemStack(Material.IRON_SWORD,1);
			//item.addEnchantment(Enchantment.DAMAGE_ALL, 3);
			player.getInventory().setItem(0, item);
			Potion pot=new Potion(PotionType.INSTANT_HEAL);
			player.getInventory().setItem(1, pot.toItemStack(1));
			pot=new Potion(PotionType.FIRE_RESISTANCE);
			player.getInventory().setItem(2, pot.toItemStack(1));
			player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
			player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
			PotionEffect effect=new PotionEffect(PotionEffectType.SLOW,99999,0);
	  		player.addPotionEffect(effect, true);
			
		}
		else if(load.equals("Builder"))
		{
			player.getInventory().setItem(0, new ItemStack(Material.IRON_PICKAXE,1));
			player.getInventory().setItem(1, new ItemStack(Material.COBBLESTONE,32));
			player.getInventory().setItem(2, new ItemStack(Material.LADDER,16));
			player.getInventory().setItem(3, new ItemStack(Material.TNT,1));
		}
		else if(load.equals("Support"))
		{
			player.getInventory().setItem(0, new ItemStack(Material.GOLD_SWORD,1));
			Potion pot=new Potion(PotionType.INSTANT_HEAL);
			pot.setSplash(true);
			player.getInventory().setItem(1, pot.toItemStack(1));
			pot=new Potion(PotionType.REGEN);
			pot.setSplash(true);
			player.getInventory().setItem(2, pot.toItemStack(1));
			pot=new Potion(PotionType.STRENGTH);
			pot.setSplash(true);
			player.getInventory().setItem(3, pot.toItemStack(1));
		}
		player.updateInventory();
	}
	
	/*public void addArrow(Arrow arrow)
	{
		arrows.add(arrow);
	}
	
	public void removeArrows()
	{
		for(Arrow arrow:arrows)
		{
			arrow.remove();
		}
	}*/
	
	public static void clearPotions(Player player)
	{
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,1,1), true);
  		player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,1,1), true);
  		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,1,1), true);
  		player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,1,1), true);
  		player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,1,1), true);
  		player.addPotionEffect(new PotionEffect(PotionEffectType.POISON,1,1), true);
	}
	
	public void save()
	{
		FileConfiguration config=plugin.getConfig();
		config.set("Players."+name+".kills", kills);
		config.set("Players."+name+".deaths", deaths);
		plugin.saveConfig();
	}
	
	public void load()
	{
		FileConfiguration config=plugin.getConfig();
		kills=config.getInt("Players."+name+".kills");
		deaths=config.getInt("Players."+name+".deaths");
	}
}
