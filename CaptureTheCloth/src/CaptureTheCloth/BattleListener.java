package CaptureTheCloth;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BattleListener implements Listener
{
	private static final int TICK = 20;
	private CaptureTheCloth plugin;
	
	public BattleListener(CaptureTheCloth plug)
	{
		plugin=plug;
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
	{
		
		String team=null;
		Arrow arrow=null;
		if(event.getDamager() instanceof Player)
		{
			Player attacker=(Player)event.getDamager();
			if(attacker.getInventory().getItemInHand().getType()==Material.IRON_PICKAXE);
				event.setDamage(event.getDamage()/2);
			team=plugin.getPlayer(attacker.getName()).getTeam();
			if(team==null)
				event.setCancelled(true);
		}
		if(event.getDamager() instanceof Arrow)
		{
			arrow=(Arrow)event.getDamager();
			if(arrow.getShooter() instanceof Player)
			{
				Player attacker=(Player)arrow.getShooter();
				team=plugin.getPlayer(attacker.getName()).getTeam();
				if(team==null)
					event.setCancelled(true);
			}
		}
		if(team!=null)
		{
			if(event.getEntity() instanceof Player)
			{
				Player defender=(Player)event.getEntity();
				PlayerInfo pInfo=plugin.getPlayer(defender.getName());
				if(team.equals(pInfo.getTeam()))
				{
					event.setCancelled(true);
				}
				else if(pInfo.inSpawn)
				{
					event.setCancelled(true);
				}
				else
				{
					if(pInfo.getLoadout().equals("Warrior"))
					{
						defender.damage(event.getDamage());
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		Player player=event.getEntity();
		final PlayerInfo pInfo=plugin.getPlayer(player.getName());
		
		//set the respawn timer
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
				new Runnable()
				{
				  	public void run()
				  	{
				  		pInfo.canSpawn=true;
				  	}
				}, plugin.settings.spawnTime*TICK);
		
		//test for flags
		if(pInfo.flagTeam!=null)
		{
			plugin.getServer().broadcastMessage(pInfo.getName()+" has dropped "+pInfo.flagTeam+" team's flag!");
			Block b=player.getWorld().getBlockAt(player.getLocation());
			while(b.getType()!=Material.AIR)
				b=player.getWorld().getBlockAt(b.getLocation().add(0,1,0));
			b.setTypeIdAndData(pInfo.flagBlockType, pInfo.flagBlockData, false);
			final TeamInfo tInfo=plugin.getTeam(pInfo.flagTeam);
			pInfo.flagTeam=null;
			pInfo.flagBlockType=Material.AIR.getId();
			tInfo.setCurrentFlagLocation(b.getLocation());
			tInfo.graceTime=System.currentTimeMillis()+5*1000;
			tInfo.returnTime=System.currentTimeMillis()+20*1000;
			Bukkit.getScheduler().cancelTask(pInfo.taskID);
			pInfo.taskID=-1;
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
				new Runnable()
				{
				  	public void run()
				  	{
				  		if(System.currentTimeMillis()>=tInfo.graceTime)
						{
				  			if(tInfo.getCurrentFlagLocation()!=null)
				  			{
				  				tInfo.graceTime=-1;
				  				plugin.getServer().broadcastMessage(tInfo.getName()+"'s flag may be returned!");
				  			}
						}
				  	}
				}, 6*TICK);
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
				new Runnable()
				{
				  	public void run()
				  	{
				  		if(System.currentTimeMillis()>=tInfo.returnTime)
						{
				  			if(tInfo.getCurrentFlagLocation()!=null&&!tInfo.getFlag().equals(tInfo.getCurrentFlagLocation()))
				  			{
				  				Block flag=tInfo.getCurrentFlagLocation().getBlock();
				  				tInfo.getFlag().getBlock().setTypeIdAndData(flag.getType().getId(), flag.getData(), false);
				  				flag.setType(Material.AIR);
				  				tInfo.setCurrentFlagLocation(tInfo.getFlag());
				  				plugin.getServer().broadcastMessage(tInfo.getName()+"'s flag was returned to it's post!");
				  			}
						}
				  	}
				}, 21*TICK);
		}
		if(pInfo.flagAlt!=null)
		{
			final AltSpawnInfo aInfo=plugin.getAltSpawn(pInfo.flagAlt);
			if(aInfo.getCurrentTeam()!=null)
				plugin.getServer().broadcastMessage(pInfo.getName()+" has dropped "+aInfo.getCurrentTeam()+" team's "+aInfo.getName()+" flag!");
			else
				plugin.getServer().broadcastMessage(pInfo.getName()+" has dropped the "+aInfo.getName()+" flag!");
			Block b=player.getWorld().getBlockAt(player.getLocation());
			while(b.getType()!=Material.AIR)
				b=player.getWorld().getBlockAt(b.getLocation().add(0,1,0));
			b.setTypeIdAndData(pInfo.flagBlockType, pInfo.flagBlockData, false);
			///final TeamInfo tInfo=plugin.getTeam(pInfo.flagTeam);
			pInfo.flagAlt=null;
			pInfo.flagBlockType=Material.AIR.getId();
			aInfo.setCurrentFlagLocation(b.getLocation());
			aInfo.graceTime=System.currentTimeMillis()+5*1000;
			aInfo.returnTime=System.currentTimeMillis()+20*1000;
			Bukkit.getScheduler().cancelTask(pInfo.taskID);
			pInfo.taskID=-1;
			if(aInfo.getCurrentTeam()!=null)
			{
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
					new Runnable()
					{
					  	public void run()
					  	{
					  		if(System.currentTimeMillis()>=aInfo.graceTime)
							{
					  			if(aInfo.getCurrentFlagLocation()!=null)
					  			{
					  				aInfo.graceTime=-1;
					  				plugin.getServer().broadcastMessage(aInfo.getCurrentTeam()+"' "+aInfo.getName()+" flag may be returned!");
					  			}
							}
					  	}
					}, 6*TICK);
			}
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
				new Runnable()
				{
				  	public void run()
				  	{
				  		if(System.currentTimeMillis()>=aInfo.returnTime)
						{
				  			if(aInfo.getCurrentFlagLocation()!=null&&!aInfo.getFlag().equals(aInfo.getCurrentFlagLocation()))
				  			{
				  				Block flag=aInfo.getCurrentFlagLocation().getBlock();
				  				aInfo.getFlag().getBlock().setTypeIdAndData(flag.getType().getId(), flag.getData(), false);
				  				flag.setType(Material.AIR);
				  				aInfo.setCurrentFlagLocation(aInfo.getFlag());
				  				if(aInfo.getCurrentTeam()!=null)
				  					plugin.getServer().broadcastMessage(aInfo.getCurrentTeam()+" team's "+aInfo.getName()+" flag was returned to it's post!");
				  				else
				  					plugin.getServer().broadcastMessage("The "+aInfo.getName()+" flag was returned to it's post!");
				  			}
						}
				  	}
				}, 21*TICK);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		Player player=event.getPlayer();
		player.getInventory().clear();
		PlayerInfo pInfo=plugin.getPlayer(player.getName());
		Bukkit.getScheduler().cancelTask(pInfo.taskID);
		pInfo.taskID=-1;
		TeamInfo pTeam=plugin.getTeam(pInfo.getTeam());
		if(pTeam!=null)
			pTeam.players--;
		if(pInfo.flagTeam!=null)
		{
			plugin.getServer().broadcastMessage(pInfo.getName()+" has dropped "+pInfo.flagTeam+" team's flag!");
			Block b=player.getWorld().getBlockAt(player.getLocation());
			while(b.getType()!=Material.AIR)
				b=player.getWorld().getBlockAt(b.getLocation().add(0,1,0));
			b.setTypeIdAndData(pInfo.flagBlockType, pInfo.flagBlockData, false);
			final TeamInfo tInfo=plugin.getTeam(pInfo.flagTeam);
			pInfo.flagTeam=null;
			pInfo.flagBlockType=Material.AIR.getId();
			tInfo.setCurrentFlagLocation(b.getLocation());
			tInfo.graceTime=System.currentTimeMillis()+5*1000;
			tInfo.returnTime=System.currentTimeMillis()+20*1000;
			Bukkit.getScheduler().cancelTask(pInfo.taskID);
			pInfo.taskID=-1;
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
				new Runnable()
				{
				  	public void run()
				  	{
				  		if(System.currentTimeMillis()>=tInfo.graceTime)
						{
				  			if(tInfo.getCurrentFlagLocation()!=null)
				  			{
				  				tInfo.graceTime=-1;
				  				plugin.getServer().broadcastMessage(tInfo.getName()+"'s flag may be returned!");
				  			}
						}
				  	}
				}, 6*TICK);
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
				new Runnable()
				{
				  	public void run()
				  	{
				  		if(System.currentTimeMillis()>=tInfo.returnTime)
						{
				  			if(tInfo.getCurrentFlagLocation()!=null&&!tInfo.getFlag().equals(tInfo.getCurrentFlagLocation()))
				  			{
				  				Block flag=tInfo.getCurrentFlagLocation().getBlock();
				  				tInfo.getFlag().getBlock().setTypeIdAndData(flag.getType().getId(), flag.getData(), false);
				  				flag.setType(Material.AIR);
				  				tInfo.setCurrentFlagLocation(tInfo.getFlag());
				  				plugin.getServer().broadcastMessage(tInfo.getName()+"'s flag was returned to it's post!");
				  			}
						}
				  	}
				}, 21*TICK);
		}
		if(pInfo.flagAlt!=null)
		{
			final AltSpawnInfo aInfo=plugin.getAltSpawn(pInfo.flagAlt);
			if(aInfo.getCurrentTeam()!=null)
				plugin.getServer().broadcastMessage(pInfo.getName()+" has dropped "+aInfo.getCurrentTeam()+" team's "+aInfo.getName()+" flag!");
			else
				plugin.getServer().broadcastMessage(pInfo.getName()+" has dropped the "+aInfo.getName()+" flag!");
			Block b=player.getWorld().getBlockAt(player.getLocation());
			while(b.getType()!=Material.AIR)
				b=player.getWorld().getBlockAt(b.getLocation().add(0,1,0));
			b.setTypeIdAndData(pInfo.flagBlockType, pInfo.flagBlockData, false);
			///final TeamInfo tInfo=plugin.getTeam(pInfo.flagTeam);
			pInfo.flagAlt=null;
			pInfo.flagBlockType=Material.AIR.getId();
			aInfo.setCurrentFlagLocation(b.getLocation());
			aInfo.graceTime=System.currentTimeMillis()+5*1000;
			aInfo.returnTime=System.currentTimeMillis()+20*1000;
			Bukkit.getScheduler().cancelTask(pInfo.taskID);
			pInfo.taskID=-1;
			if(aInfo.getCurrentTeam()!=null)
			{
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
					new Runnable()
					{
					  	public void run()
					  	{
					  		if(System.currentTimeMillis()>=aInfo.graceTime)
							{
					  			if(aInfo.getCurrentFlagLocation()!=null)
					  			{
					  				aInfo.graceTime=-1;
					  				plugin.getServer().broadcastMessage(aInfo.getCurrentTeam()+"' "+aInfo.getName()+" flag may be returned!");
					  			}
							}
					  	}
					}, 6*TICK);
			}
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
				new Runnable()
				{
				  	public void run()
				  	{
				  		if(System.currentTimeMillis()>=aInfo.returnTime)
						{
				  			if(aInfo.getCurrentFlagLocation()!=null&&!aInfo.getFlag().equals(aInfo.getCurrentFlagLocation()))
				  			{
				  				Block flag=aInfo.getCurrentFlagLocation().getBlock();
				  				aInfo.getFlag().getBlock().setTypeIdAndData(flag.getType().getId(), flag.getData(), false);
				  				flag.setType(Material.AIR);
				  				aInfo.setCurrentFlagLocation(aInfo.getFlag());
				  				if(aInfo.getCurrentTeam()!=null)
				  					plugin.getServer().broadcastMessage(aInfo.getCurrentTeam()+" team's "+aInfo.getName()+" flag was returned to it's post!");
				  				else
				  					plugin.getServer().broadcastMessage("The "+aInfo.getName()+" flag was returned to it's post!");
				  			}
						}
				  	}
				}, 21*TICK);
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		final Player player=event.getPlayer();
		PlayerInfo pInfo=plugin.getPlayer(player.getName());
		if(!player.isDead())
		{
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
				new Runnable()
				{
				  	public void run()
				  	{
				  		PlayerInfo.clearPotions(player);
				  		if(plugin.settings!=null&&plugin.settings.spawnLocation!=null)
				  			player.teleport(plugin.settings.spawnLocation);
				  	}
				}, 	TICK);
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		final Player player=event.getPlayer();
		
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
			new Runnable()
			{
			  	public void run()
			  	{
			  		PlayerInfo.clearPotions(player);

			  		PlayerInfo pInfo;
					TeamInfo tInfo;
					if((tInfo=plugin.getTeam((pInfo=plugin.getPlayer(player.getName())).getTeam()))!=null)
					{
						pInfo.setLoadout(pInfo.getLoadout());
						pInfo.setTeam(tInfo.getName());
						player.teleport(tInfo.getSpawn());
						pInfo.inSpawn=true;
					}
					else
					{
						if(plugin.settings!=null&&plugin.settings.spawnLocation!=null)
				  			player.teleport(plugin.settings.spawnLocation);
					}
			  	}
			}, TICK);
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onFoodLevelChange(FoodLevelChangeEvent event)
	{
		event.setCancelled(true);
	}
}
