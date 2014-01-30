package CaptureTheCloth;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;

public class BuilderListener implements Listener
{
	private CaptureTheCloth plugin;
	private static final int TICK=20;
	
	public BuilderListener(CaptureTheCloth plug)
	{
		plugin=plug;
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player player=event.getPlayer();
		PlayerInfo pInfo=plugin.getPlayer(player.getName());
		
		if(pInfo.inSpawn)
		{
			event.setCancelled(true);
			event.getPlayer().updateInventory();
			return;
		}
		
		if((pInfo.getLoadout()!=null)&&pInfo.getLoadout().equals("Builder"))
		{
			Block block=event.getClickedBlock();
			if(block!=null&&block.getType()==Material.TNT&&event.getAction()==Action.LEFT_CLICK_BLOCK)
			{
				event.setCancelled(true);
				block.setType(Material.AIR);
				TNTPrimed tnt=block.getWorld().spawn(block.getLocation().add(0.5, 0.5, 0.5), TNTPrimed.class);
				tnt.setFuseTicks(5*TICK/2);
			}
		}
	}
}
