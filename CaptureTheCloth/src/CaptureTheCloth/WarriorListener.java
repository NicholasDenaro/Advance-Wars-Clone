package CaptureTheCloth;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class WarriorListener implements Listener
{
	private CaptureTheCloth plugin;
	private static final int TICK=20;
	
	public WarriorListener(CaptureTheCloth plug)
	{
		plugin=plug;
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onPlayerItemConsume(PlayerItemConsumeEvent event)
	{
		final Player player=event.getPlayer();
		
		PlayerInfo pInfo=plugin.getPlayer(event.getPlayer().getName());
		
		if(pInfo.inSpawn)
		{
			event.setCancelled(true);
			player.updateInventory();
			return;
		}
		
		if(pInfo.getLoadout().equals("Warrior"))
		{
			int time=1;
			int slot=1;
			if(event.getItem().getType()==Material.POTION)
			{
				Potion potion=Potion.fromItemStack(event.getItem());
				if(potion.getType()==PotionType.INSTANT_HEAL)
				{
					time=15;
					slot=1;
				}
				if(potion.getType()==PotionType.FIRE_RESISTANCE)
				{
					//System.out.println("YO");
					time=30;
					slot=2;
					player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,20*TICK,1),true);
				}
			}
			final int fslot=slot;
			final ItemStack item=event.getItem();
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
				new Runnable()
				{
				  	public void run()
				  	{
				  		if(plugin.getPlayer(player.getName()).getLoadout().equals("Warrior"))
						{
				  			player.getInventory().setItem(fslot, item);
						}
				  	}
				}, time*TICK);
		}
	}
}
