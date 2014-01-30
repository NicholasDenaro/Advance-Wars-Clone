package CaptureTheCloth;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

public class MagicianListener implements Listener
{
	private CaptureTheCloth plugin;
	private static final int TICK=20;
	
	public MagicianListener(CaptureTheCloth plug)
	{
		plugin=plug;
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		PlayerInfo pInfo=plugin.getPlayer(event.getPlayer().getName());
		
		if(pInfo.inSpawn)
		{
			event.setCancelled(true);
			event.getPlayer().updateInventory();
			return;
		}
		
		if((pInfo.getLoadout()!=null)&&pInfo.getLoadout().equals("Magician"))
		{
			final Player p=event.getPlayer();
			if(event.getPlayer().getItemInHand().getType()==Material.BLAZE_ROD)
			{
				if(event.getAction()==Action.RIGHT_CLICK_AIR||event.getAction()==Action.RIGHT_CLICK_BLOCK)
				{
					if(p.getItemInHand().getType()==Material.BLAZE_ROD)
					{
						int maxDistance=30;
						HashSet<Byte> ignoreBlocks=new HashSet<Byte>();
						ignoreBlocks.add((byte)0);
						ignoreBlocks.add((byte)Material.SNOW.getId());
						ignoreBlocks.add((byte)Material.LONG_GRASS.getId());
						ignoreBlocks.add((byte)Material.YELLOW_FLOWER.getId());
						ignoreBlocks.add((byte)Material.RED_ROSE.getId());
						List<Block> blocks=p.getLastTwoTargetBlocks(ignoreBlocks, maxDistance);
						if(blocks.get(0).getType()==Material.AIR
								||blocks.get(0).getType()==Material.SNOW
								||blocks.get(0).getType()==Material.LONG_GRASS
								||blocks.get(0).getType()==Material.YELLOW_FLOWER
								||blocks.get(0).getType()==Material.RED_ROSE)
						{
							blocks.get(0).setType(Material.FIRE);
							if(blocks.get(0).getType()==Material.FIRE)
							{
								p.getInventory().setItem(1, new ItemStack(Material.STICK,1));
								plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
									new Runnable()
									{
									  	public void run()
									  	{
									  		//System.out.println("YUMPCHEVRONDIAMOND");
									  		if(plugin.getPlayer(p.getName()).getLoadout().equals("Magician"))
											{
									  			p.getInventory().setItem(1, new ItemStack(Material.BLAZE_ROD,1));
											}
									  	}
									}, 5*TICK);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
	{
		if(event.getDamager() instanceof Player)
		{
			final Player p=(Player)event.getDamager();
			
			PlayerInfo pInfo=plugin.getPlayer(p.getName());
			
			if(pInfo.inSpawn)
			{
				event.setCancelled(true);
				return;
			}
			
			String team=plugin.getPlayer(p.getName()).getTeam();
			if(event.getEntity() instanceof Player)
			{
				Player defender=(Player)event.getEntity();
				if(!plugin.getPlayer(defender.getName()).getTeam().equals(team))
				{
					if(plugin.getPlayer(p.getName()).getLoadout().equals("Magician"))
					{
						if(p.getItemInHand().getType()==Material.BLAZE_ROD)
						{
							defender.setFireTicks(5*TICK);
							p.getInventory().setItem(1, new ItemStack(Material.STICK,1));
							plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
								new Runnable()
								{
								  	public void run()
								  	{
								  		if(plugin.getPlayer(p.getName()).getLoadout().equals("Magician"))
										{
								  			p.getInventory().setItem(1, new ItemStack(Material.BLAZE_ROD,1));
										}
								  	}
								}, 5*TICK);
						}
					}
				}
			}
			
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		if(event.getCause()==TeleportCause.ENDER_PEARL)
		{
			event.setCancelled(true);
			/*if(event.getTo().getY()<16||event.getTo().getY()>60)
				event.setCancelled(true);
			PlayerInfo pInfo=plugin.getPlayer(event.getPlayer().getName());*/
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onProjectileHit(ProjectileHitEvent event)
	{
		if(event.getEntity() instanceof EnderPearl)
		{
			final EnderPearl pearl=(EnderPearl)event.getEntity();
			Location loc=pearl.getWorld().getBlockAt(pearl.getLocation()).getLocation().add(0.5,0.5,0.5);
			if(loc.getY()>=plugin.settings.mapBottom&&loc.getY()<plugin.settings.mapTop)
			{
		  		loc.setPitch(pearl.getShooter().getLocation().getPitch());
		  		loc.setYaw(pearl.getShooter().getLocation().getYaw());
		  		pearl.getShooter().damage(5);
		  		pearl.getShooter().teleport(loc);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onProjectileLaunch(ProjectileLaunchEvent event)
	{
		if(event.getEntity().getShooter() instanceof Player)
		{
			Player p=(Player)event.getEntity().getShooter();
			
			PlayerInfo pInfo=plugin.getPlayer(p.getName());
			
			if(pInfo.inSpawn)
			{
				event.setCancelled(true);
				p.updateInventory();
				return;
			}
		}
			
		//System.out.println("Throw Event!");
		if(event.getEntity() instanceof ThrownPotion)
		{
			//System.out.println("  Thrown potion!");
			final ThrownPotion projectile=(ThrownPotion)event.getEntity();
			ItemStack item=projectile.getItem();
			//System.out.println("Item display name: "+item.getType().toString());
			if(projectile.getShooter() instanceof Player)
			{
				final Player shooter=(Player)projectile.getShooter();
				if(plugin.getPlayer(shooter.getName()).getLoadout().equals("Magician"))
				{
					Potion p=Potion.fromItemStack(projectile.getItem());
					int time=1;
					int slot=0;
					if(p.getType()==PotionType.POISON)
					{
						time=10;
						slot=3;
					}
					if(p.getType()==PotionType.SLOWNESS)
					{
						time=8;
						slot=2;
					}
					if(p.getType()==PotionType.INSTANT_DAMAGE)
					{
						time=2;
						slot=0;
					}
					
					final int fslot=slot;
					
					projectile.setVelocity(projectile.getVelocity().multiply(new Vector(1.75,1.75,1.75)));
					if(projectile.getShooter() instanceof Player)
					{
						plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
							new Runnable()
							{
							  	public void run()
							  	{
							  		if(plugin.getPlayer(shooter.getName()).getLoadout().equals("Magician"))
									{
								  		PlayerInventory inv=shooter.getInventory();
								  		ItemStack item=new ItemStack(projectile.getItem());
								  		item.setAmount(1);
								  		inv.setItem(fslot, item);
									}
							  	}
							}, time*TICK);
					}
				}
			}
		}
		if(event.getEntity() instanceof EnderPearl)
		{
			if(!plugin.settings.enderPearlEnabled)
			{
				event.setCancelled(true);
				return;
			}
			final EnderPearl projectile=(EnderPearl)event.getEntity();
			if(projectile.getShooter() instanceof Player)
			{
				final Player shooter=(Player)projectile.getShooter();
				if(plugin.getPlayer(shooter.getName()).flagTeam!=null)
				{
					projectile.setVelocity(projectile.getVelocity().multiply(new Vector(0.75,0.75,0.75)));
				}
				if(plugin.getPlayer(shooter.getName()).getLoadout().equals("Magician"))
				{
					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
						new Runnable()
						{
						  	public void run()
						  	{
						  		if(plugin.getPlayer(shooter.getName()).getLoadout().equals("Magician"))
								{
						  			PlayerInventory inv=shooter.getInventory();
						  			inv.setItem(4, new ItemStack(Material.ENDER_PEARL,1));
								}
						  	}
						}, 20*TICK);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPotionSplash(PotionSplashEvent event)
	{
		ThrownPotion potion=event.getEntity();
		if(potion.getShooter() instanceof Player)
		{
			Player p=(Player)potion.getShooter();
			//System.out.println("shooter: "+p.getName());
			String team=plugin.getPlayer(p.getName()).getTeam();
			Potion potionItem=Potion.fromItemStack(potion.getItem());
			for(LivingEntity e:event.getAffectedEntities())
			{
				if(e instanceof Player)
				{
					final Player pe=(Player)e;
					if(plugin.getPlayer(pe.getName()).getTeam().equals(team))
					{
						if(potionItem.getType()==PotionType.POISON||potionItem.getType()==PotionType.SLOWNESS||potionItem.getType()==PotionType.INSTANT_DAMAGE)
							event.setIntensity(e, 0);
						else
						{
							if(potionItem.getType()==PotionType.INSTANT_HEAL)
							{
								event.setIntensity(e, 0);
								pe.setHealth(Math.min(20,pe.getHealth()+2));
							}
							if(potionItem.getType()==PotionType.REGEN)
							{
								event.setIntensity(e, 0);
								pe.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,8*TICK,0));
							}
							if(potionItem.getType()==PotionType.STRENGTH)
							{
								event.setIntensity(e, 0);
								PotionEffect effect=new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 8*TICK, 0);
								pe.addPotionEffect(effect);
							}
						}
					}
					else
					{
						if(potionItem.getType()==PotionType.INSTANT_HEAL||potionItem.getType()==PotionType.STRENGTH||potionItem.getType()==PotionType.REGEN)
							event.setIntensity(e, 0);
						else
						{
							if(potionItem.getType()==PotionType.POISON)
							{
								event.setIntensity(e, 0);
								pe.addPotionEffect(new PotionEffect(PotionEffectType.POISON,5*TICK,1));
							}
							if(potionItem.getType()==PotionType.SLOWNESS)
							{
								event.setIntensity(e, 0);
								pe.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,4*TICK,1),true);
								if(plugin.getPlayer(pe.getName()).getLoadout().equals("Warrior"))
								{
									plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
											new Runnable()
											{
											  	public void run()
											  	{
											  		if(plugin.getPlayer(pe.getName()).getLoadout().equals("Warrior"))
													{
											  			pe.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,9999999,0),true);
													}
											  	}
											}, 4*TICK);
								}
							}
							if(potionItem.getType()==PotionType.INSTANT_DAMAGE)
							{
								event.setIntensity(e, 0);
								pe.damage(6);
							}
						}
					}
				}
			}
		}
	}
}
