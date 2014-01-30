package CaptureTheCloth;

import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

public class SupportListener implements Listener
{
	private CaptureTheCloth plugin;
	private static final int TICK=20;
	
	public SupportListener(CaptureTheCloth plug)
	{
		plugin=plug;
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onProjectileLaunch(ProjectileLaunchEvent event)
	{
		if(event.getEntity().getShooter() instanceof Player)
		{
			PlayerInfo pInfo=plugin.getPlayer(((Player)event.getEntity().getShooter()).getName());
			
			if(pInfo.inSpawn)
			{
				event.setCancelled(true);
				((Player)event.getEntity().getShooter()).updateInventory();
				return;
			}
		}
		
		if(event.getEntity() instanceof ThrownPotion)
		{
			final ThrownPotion projectile=(ThrownPotion)event.getEntity();
			ItemStack item=projectile.getItem();
			if(projectile.getShooter() instanceof Player)
			{
				final Player shooter=(Player)projectile.getShooter();
				if(plugin.getPlayer(shooter.getName()).getLoadout().equals("Support"))
				{
					Potion p=Potion.fromItemStack(projectile.getItem());
					int time=1;
					int slot=0;
					if(p.getType()==PotionType.STRENGTH)
					{
						time=20;
						slot=3;
					}
					if(p.getType()==PotionType.REGEN)
					{
						time=12;
						slot=2;
					}
					if(p.getType()==PotionType.INSTANT_HEAL)
					{
						time=8;
						slot=1;
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
							  		if(plugin.getPlayer(shooter.getName()).getLoadout().equals("Support"))
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
	}
}
