package CaptureTheCloth;

import java.lang.reflect.Field;

import net.minecraft.server.v1_6_R2.EntityArrow;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;

public class ArcherListener implements Listener
{
	private CaptureTheCloth plugin;
	private static final int TICK=20;
	
	public ArcherListener(CaptureTheCloth plug)
	{
		plugin=plug;
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onEntityShootBow(EntityShootBowEvent event)
	{
		if(event.getEntity() instanceof Player)
		{
			Player p=(Player)event.getEntity();
			PlayerInfo pInfo=plugin.getPlayer(p.getName());
			
			if(pInfo.inSpawn)
			{
				event.setCancelled(true);
				p.updateInventory();
				return;
			}
		}
		
		/*if(event.getEntity() instanceof Player)
		{
			final Player player=(Player)event.getEntity();
			if(plugin.getPlayer(player.getName()).getLoadout().equals("Archer"))
			{
				player.getInventory().setItem(1, new ItemStack(Material.ARROW,1));
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
					new Runnable()
					{
					  	public void run()
					  	{
					  		if(plugin.getPlayer(player.getName()).getLoadout().equals("Archer"))
					  		{
					  			player.getInventory().setItem(1, new ItemStack(Material.ARROW,1));
					  		}
					  	}
					}, 1*TICK);
			}
		}*/
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onProjectileHit(ProjectileHitEvent event)
	{
		if(event.getEntity() instanceof Arrow)
		{
			//System.out.println("ARROW!");
			final Arrow arrow=(Arrow)event.getEntity();
			
			final World world = arrow.getWorld();

            
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
				new Runnable()
				{
				  	public void run()
				  	{
				  		try
			            {
				  			EntityArrow entityArrow = ((CraftArrow)arrow).getHandle();
				            Field fieldX = EntityArrow.class.getDeclaredField("d");
				            Field fieldY = EntityArrow.class.getDeclaredField("e");
				            Field fieldZ = EntityArrow.class.getDeclaredField("f");
				
				            fieldX.setAccessible(true);
				            fieldY.setAccessible(true);
				            fieldZ.setAccessible(true);
				
				            int x = fieldX.getInt(entityArrow);
				            int y = fieldY.getInt(entityArrow);
				            int z = fieldZ.getInt(entityArrow);
				            //System.out.println("x: "+x+" y: "+y+" z: "+z);
				            
				            Block block = world.getBlockAt(x, y, z);
				            if(block.getType()==Material.TNT)
				            {
				            	block.setType(Material.AIR);
				            	TNTPrimed tnt=block.getWorld().spawn(block.getLocation().add(0.5, 0.5, 0.5), TNTPrimed.class);
								tnt.setFuseTicks(3*TICK/2);
				            }
			            }
			            catch(Exception ex)
			            {
			            	ex.printStackTrace();
			            }
				  	}
				}, TICK/5);

			//if(arrow.isOnGround())
			{
				/*Location loc=arrow.getLocation();
				Block block=loc.getWorld().getBlockAt(loc);
				boolean shouldremove=false;
				if(block.getType()==Material.TNT)
				{
					
					System.out.println("GO BOOM!");
					block.setType(Material.AIR);
					TNTPrimed tnt=block.getWorld().spawn(block.getLocation().add(0.5, 0.5, 0.5), TNTPrimed.class);
					tnt.setFuseTicks(3*TICK/2);
					shouldremove=true;
				}
				else
				{
					BlockIterator b = new BlockIterator(loc.getWorld(), loc.toVector(), arrow.getVelocity().normalize(), 0, 1);
					while(b.hasNext())
					{
						block=b.next();
						if(block.getType()==Material.TNT)
						{
							System.out.println("GO BOOM!");
							block.setType(Material.AIR);
							TNTPrimed tnt=block.getWorld().spawn(block.getLocation().add(0.5, 0.5, 0.5), TNTPrimed.class);
							tnt.setFuseTicks(3*TICK/2);
							shouldremove=true;
						}
					}
				}
				if(shouldremove)
					event.getEntity().remove();*/
			}
		}
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
		
		if(pInfo.getLoadout()!=null&&pInfo.getLoadout().equals("Archer"))
		{
			final Player player=event.getPlayer();
			//if(event.getAction()==Action.LEFT_CLICK_BLOCK||event.getAction()==Action.LEFT_CLICK_AIR)
			{
				if(event.getPlayer().getItemInHand().getType()==Material.POTION)
				{
					event.setCancelled(true);
					PotionEffect effect=new PotionEffect(PotionEffectType.INVISIBILITY,15*TICK,1);
					player.addPotionEffect(effect, true);
					final ItemStack pot=event.getPlayer().getItemInHand();
					player.getInventory().setItem(2, new ItemStack(Material.AIR,0));
					final ItemStack helmet=player.getInventory().getHelmet();
					player.getInventory().setHelmet(new ItemStack(Material.AIR));
					player.updateInventory();
					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
						new Runnable()
						{
						  	public void run()
						  	{
						  		if(plugin.getPlayer(player.getName()).getLoadout().equals("Archer"))
						  			player.getInventory().setItem(2, pot);
						  	}
						}, 30*TICK);
					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
							new Runnable()
							{
							  	public void run()
							  	{
							  		if(plugin.getPlayer(player.getName()).getLoadout().equals("Archer"))
							  		{
							  			player.getInventory().setHelmet(helmet);
							  			player.updateInventory();
							  		}
							  	}
							}, 15*TICK);
				}
			}
		}
	}
}
