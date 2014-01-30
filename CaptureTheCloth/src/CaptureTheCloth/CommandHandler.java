package CaptureTheCloth;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;


public class CommandHandler  implements CommandExecutor
{
	private CaptureTheCloth plugin;
	
	public CommandHandler(CaptureTheCloth ctc)
	{
		plugin=ctc;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("LoadSettings"))
		{
			if(args.length==0)
			{
				plugin.settings=new GameSettings(plugin,plugin.getServer().getPlayer(sender.getName()).getWorld().getName());
				sender.sendMessage("Settings loaded.");
			}
			else if(args.length==1)
			{
				plugin.settings=new GameSettings(plugin,args[0]);
				sender.sendMessage("Settings loaded.");
			}
			else
				return(false);
			return(true);
		}
		else if(cmd.getName().equalsIgnoreCase("EnableEnderPearls"))
		{
			if(args.length==1)
			{
				if(args[0].equalsIgnoreCase("false")||args[0].equalsIgnoreCase("true"))
				{
					plugin.settings.setEnderPearlEnabled(new Boolean(args[0]));
				}
				else
					return(false);
			}
			else
				return(false);
			return(true);
		}
		else if(cmd.getName().equalsIgnoreCase("RestrictRegion"))
		{
			plugin.getPlayer(sender.getName()).restrictingRegion=true;
			sender.sendMessage("Destroy the block for location 1.");
			return(true);
		}
		else if(cmd.getName().equalsIgnoreCase("SetMapBottom"))
		{
			plugin.settings.setMapBottom(plugin.getServer().getPlayer(sender.getName()).getLocation());
			sender.sendMessage("The bottom of the map has been set.");
			return(true);
		}
		else if(cmd.getName().equalsIgnoreCase("SetMapTop"))
		{
			plugin.settings.setMapTop(plugin.getServer().getPlayer(sender.getName()).getLocation());
			sender.sendMessage("The top of the map has been set.");
			return(true);
		}
		else if(cmd.getName().equalsIgnoreCase("SetSpawnTime"))
		{
			if(args.length==1)
			{
				plugin.settings.spawnTime=new Integer(args[0]);
			}
			else
				return(false);
			return(true);
		}
		else if(cmd.getName().equalsIgnoreCase("SetSpawn"))
		{
			plugin.setSpawnLocation(plugin.getServer().getPlayer(sender.getName()).getLocation().add(0, 1, 0));
			sender.sendMessage("The spawn location has been set");
			return(true);
		}
		else if(cmd.getName().equalsIgnoreCase("NewTeam"))
		{
			if(args.length==1)
			{
				plugin.getPlayer(sender.getName()).creatingTeam=true;
				plugin.getPlayer(sender.getName()).creatingTeamName=args[0];
				sender.sendMessage("Click on the cloth block.");
			}
			else
				return(false);
			return(true);
		}
		if(cmd.getName().equalsIgnoreCase("NewAltSpawn"))
		{
			if(args.length==1)
			{
				plugin.getPlayer(sender.getName()).creatingAltSpawn=true;
				plugin.getPlayer(sender.getName()).creatingAltSpawnName=args[0];
				sender.sendMessage("Click on the cloth block.");
			}
			else
				return(false);
			return(true);
		}
		if(cmd.getName().equalsIgnoreCase("SetTeam"))
		{
			if(args.length==1)
			{
				plugin.getPlayer(sender.getName()).setTeam(args[0]);
				sender.sendMessage("You are now on team "+args[0]);
			}
			else if(args.length==2)
			{
				if(plugin.getServer().getPlayerExact(args[1])!=null)
				{
					plugin.getPlayer(args[1]).setTeam(args[0]);
					sender.sendMessage(args[1]+" is now on team "+args[0]);
					plugin.getServer().getPlayerExact(args[1]).sendMessage("You are now on team "+args[0]);
				}
			}
			else
				return(false);
			return(true);
		}
		else if(cmd.getName().equalsIgnoreCase("SetBuild"))
		{
			if(args.length==1)
			{
				if(args[0].equalsIgnoreCase("on"))
				{
					plugin.building="on";
					plugin.getServer().broadcastMessage("<SERVER>Building is now on.");
				}
				else if(args[0].equalsIgnoreCase("op"))
				{
					plugin.building="op";
					plugin.getServer().broadcastMessage("<SERVER>Building is now set for ops.");
				}
				else if(args[0].equalsIgnoreCase("off"))
				{
					plugin.building="off";
					plugin.getServer().broadcastMessage("<SERVER>Building is now off.");
				}
				else
					return(false);
			}
			else
				return(false);
			return(true);
		}
		else if(cmd.getName().equalsIgnoreCase("KillAll"))
		{
			if(args.length==0)
			{
				for(Player p:plugin.getServer().getOnlinePlayers())
				{
					p.setHealth(0);
				}
			}
			else if(args.length==1)
			{
				if(args[0].equals("1"))
				{
					for(Player p:plugin.getServer().getOnlinePlayers())
					{
						if(!p.isOp())
							p.setHealth(0);
					}
				}
				else
				{
					for(Player p:plugin.getServer().getOnlinePlayers())
					{
						p.setHealth(0);
					}
				}
			}
			else
				return(false);
			plugin.getServer().broadcastMessage("<SERVER>Everyone was killed!");
			return(true);
		}
		else if(cmd.getName().equalsIgnoreCase("SetLoadout"))
		{
			if(args.length==2)
			{
				Player player=plugin.getServer().getPlayerExact(args[0]);
				if(player!=null)
				{
					plugin.getPlayer(args[0]).setLoadout(args[1]);
					sender.sendMessage(args[0]+" is now a "+args[1]);
				}
			}
			else
				return(false);
			return(true);
		}
		else if(cmd.getName().equalsIgnoreCase("ResetMap"))
		{
			plugin.resetBreakableBlocks();
			sender.sendMessage("The breakable blocks were reset.");
			return(true);
		}
		return(false);
	}

}
