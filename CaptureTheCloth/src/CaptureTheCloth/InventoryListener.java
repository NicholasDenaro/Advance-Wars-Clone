package CaptureTheCloth;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener
{
	private CaptureTheCloth plugin;
	
	public InventoryListener(CaptureTheCloth ctc)
	{
		plugin=ctc;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent event)
	{
		if(plugin.building.equals("on")||(plugin.building.equals("op")&&event.getWhoClicked().isOp()))
			return;
		event.setCancelled(true);
		/*if(!event.getWhoClicked().isOp())
			event.setCancelled(true);*/
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		if(plugin.building.equals("on")||(plugin.building.equals("op")&&event.getPlayer().isOp()))
			return;
		event.setCancelled(true);
		event.getPlayer().updateInventory();
		event.getPlayer().setSprinting(!event.getPlayer().isSprinting());
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		event.getDrops().clear();
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerPickupItem(PlayerPickupItemEvent event)
	{
		if(event.getItem().getItemStack().getType()==Material.ARROW)
		{
			event.setCancelled(true);
			if(plugin.getPlayer(event.getPlayer().getName()).getLoadout().equals("Archer"))
			{
				event.getPlayer().getInventory().setItem(1, new ItemStack(Material.ARROW,1));
				event.getItem().remove();
			}
		}
		else
			event.setCancelled(true);
	}
}
