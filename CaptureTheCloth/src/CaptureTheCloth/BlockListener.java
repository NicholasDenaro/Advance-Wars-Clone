package CaptureTheCloth;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.material.Wool;
import org.bukkit.util.Vector;

public class BlockListener implements Listener
{
	private CaptureTheCloth plugin;
	private static final int TICK=20;
	
	public BlockListener(CaptureTheCloth plug)
	{
		plugin=plug;
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event)
	{
		PlayerInfo pInfo;
		
		if((pInfo=plugin.getPlayer(event.getPlayer().getName())).creatingTeam)
		{
			if(pInfo.creatingTeamFlag==null)
			{
				pInfo.creatingTeamFlag=event.getBlock().getLocation();
				pInfo.creatingTeamBlockType=event.getBlock().getDrops().iterator().next();
				event.getPlayer().sendMessage("Flag has been set, as well as block.");
				event.getPlayer().sendMessage("Click on the block to spawn people on.");
			}
			else if(pInfo.creatingTeamSpawn==null)
			{
				pInfo.creatingTeamSpawn=event.getBlock().getLocation().add(0, 1, 0);
				plugin.createTeam(pInfo.creatingTeamName, pInfo.creatingTeamFlag, pInfo.creatingTeamBlockType, pInfo.creatingTeamSpawn);
				event.getPlayer().sendMessage("Team "+pInfo.creatingTeamName+" has been created.");
				pInfo.creatingTeam=false;
				pInfo.creatingTeamName=null;
				pInfo.creatingTeamFlag=null;
				pInfo.creatingTeamBlockType=null;
				pInfo.creatingTeamSpawn=null;
			}
		}
		
		if((pInfo=plugin.getPlayer(event.getPlayer().getName())).creatingAltSpawn)
		{
			if(pInfo.creatingAltSpawnFlag==null)
			{
				pInfo.creatingAltSpawnFlag=event.getBlock().getLocation();
				pInfo.creatingAltSpawnBlockType=event.getBlock().getDrops().iterator().next();
				event.getPlayer().sendMessage("Flag has been set, as well as block.");
				event.getPlayer().sendMessage("Click on the block to spawn people on.");
			}
			else if(pInfo.creatingAltSpawnSpawn==null)
			{
				pInfo.creatingAltSpawnSpawn=event.getBlock().getLocation().add(0, 1, 0);
				plugin.createAltSpawn(pInfo.creatingAltSpawnName, pInfo.creatingAltSpawnFlag, pInfo.creatingAltSpawnBlockType, pInfo.creatingAltSpawnSpawn);
				event.getPlayer().sendMessage("AltSpawn "+pInfo.creatingAltSpawnName+" has been created.");
				pInfo.creatingAltSpawn=false;
				pInfo.creatingAltSpawnName=null;
				pInfo.creatingAltSpawnFlag=null;
				pInfo.creatingAltSpawnBlockType=null;
				pInfo.creatingAltSpawnSpawn=null;
			}
		}
		
		if((pInfo=plugin.getPlayer(event.getPlayer().getName())).restrictingRegion)
		{
			if(pInfo.restrictingLoc1==null)
			{
				pInfo.restrictingLoc1=event.getBlock().getLocation();
				event.getPlayer().sendMessage("Destroy the block for location 2.");
				
			}
			else
			{
				
				pInfo.restrictingLoc2=event.getBlock().getLocation();
				plugin.addRestrictedLocation(pInfo.restrictingLoc1,pInfo.restrictingLoc2);
				event.getPlayer().sendMessage("Restricted region has been created.");
				pInfo.restrictingLoc1=null;
				pInfo.restrictingLoc2=null;
				pInfo.restrictingRegion=false;
			}
		}
		
		if(plugin.building.equals("on")||(plugin.building.equals("op")&&event.getPlayer().isOp()))
			return;
		//if(plugin.getPlayer(event.getPlayer().getName()).getLoadout().equals("Builder"))
		{
			if(!plugin.breakableBlock(event.getBlock().getLocation()))
			{
				if(event.getBlock().getType()!=Material.LEAVES)
				{
					event.setCancelled(true);
					System.out.println(event.getBlock().getLocation());
				}
			}
			else
			{
				plugin.removeBreakableBlock(event.getBlock().getLocation());
				if(plugin.getPlayer(event.getPlayer().getName()).getLoadout().equals("Builder"))
				{
					ItemStack item=event.getPlayer().getInventory().getItem(1);
					if(item.getAmount()==0)
					{
						item.setType(Material.COBBLESTONE);
						item.setAmount(1);
					}
					else if(item.getAmount()<32)
					{
						item.setAmount(item.getAmount()+1);
						event.getPlayer().updateInventory();
					}
				}
			}
		}
		/*else
		{
			event.setCancelled(true);
		}*/
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if(plugin.building.equals("on")||(plugin.building.equals("op")&&event.getPlayer().isOp()))
			return;
		
		PlayerInfo pInfo=plugin.getPlayer(event.getPlayer().getName());
		
		if(pInfo.inSpawn)
		{
			event.setCancelled(true);
			event.getPlayer().updateInventory();
			return;
		}
		
		final Player player=event.getPlayer();
		
		if(plugin.blockIsInRestrictedArea(event.getBlock().getLocation()))
		{
			event.setCancelled(true);
			player.updateInventory();
			return;
		}
		
		if(!pInfo.getLoadout().equals("Builder"))
		{
			event.setCancelled(true);
			player.updateInventory();
		}
		else	
		{
			if(event.getBlock().getType()!=Material.TNT)
			{
				if(event.getBlockAgainst().getType()!=Material.SOUL_SAND&&event.getBlockAgainst().getType()!=Material.SUGAR_CANE&&event.getBlockAgainst().getType()!=Material.SUGAR_CANE_BLOCK)
					plugin.addBreakableBlock(event.getBlock().getLocation(),player.getName());
				else
				{
					event.setCancelled(true);
					player.updateInventory();
				}
			}
			else
			{
				//event.setCancelled(true);
				//event.getPlayer().getInventory().setItemInHand(new ItemStack(Material.AIR));
				//player.updateInventory();
				//TNTPrimed tnt=event.getBlock().getWorld().spawn(event.getBlock().getLocation().add(0.5, 0.5, 0.5), TNTPrimed.class);
				//tnt.setFuseTicks(5*TICK/2);
			}
			final int fslot=event.getPlayer().getInventory().getHeldItemSlot();
			int time=1;
			final ItemStack item=event.getItemInHand();
			if(event.getItemInHand().getType()==Material.COBBLESTONE)
			{
				time=3;
			}
			if(event.getItemInHand().getType()==Material.LADDER)
			{
				time=5;
			}
			if(event.getItemInHand().getType()==Material.TNT)
			{
				time=15;
			}
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
				new Runnable()
				{
				  	public void run()
				  	{
				  		if(plugin.getPlayer(player.getName()).getLoadout().equals("Builder"))
						{
				  			if(fslot==3)
				  			{
						  		item.setAmount(1);
						  		/*if(player.getInventory().getItem(fslot)!=null)
						  			item.setAmount(player.getInventory().getItem(fslot).getAmount()+1);*/
						  		player.getInventory().setItem(fslot, item);
				  			}
						}
				  	}
				}, time*TICK);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Block block;
		if((block=event.getClickedBlock())!=null)
		{
			if(block.getType()!=Material.WALL_SIGN)
			{
				//System.out.println("Block was clicked!");
				final PlayerInfo pInfo=plugin.getPlayer(event.getPlayer().getName());
				TeamInfo tInfo;
				AltSpawnInfo aInfo;
				if((tInfo=plugin.blockIsFlag(block.getLocation()))!=null)
				{
					if(!pInfo.getTeam().equals(tInfo.getName()))
					{
						if(pInfo.flagTeam==null&&pInfo.flagAlt==null)
						{
							event.getPlayer().setFoodLevel(10);
							plugin.getServer().broadcastMessage(pInfo.getName()+" has captured "+tInfo.getName()+" team's flag.");
							pInfo.captureFlag(tInfo.getName(), block);
							tInfo.setFlagCaptured(pInfo.getName());
							System.out.println("Block: "+block);
							event.getClickedBlock().setType(Material.AIR);
							final Player player=plugin.getServer().getPlayer(pInfo.getName());
							pInfo.taskID=plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,
								new Runnable()
								{
								  	public void run()
								  	{
								  		Location loc = player.getLocation();
								        Firework firework = player.getWorld().spawn(loc, Firework.class);
								        FireworkMeta data = (FireworkMeta) firework.getFireworkMeta();
								        if(pInfo.flagTeam.equals("Blue"))
								        	data.addEffects(FireworkEffect.builder().withColor(Color.BLUE).with(Type.BALL_LARGE).build());
								        if(pInfo.flagTeam.equals("White"))
								        	data.addEffects(FireworkEffect.builder().withColor(Color.WHITE).with(Type.BALL_LARGE).build());
								        if(pInfo.flagTeam.equals("Gold"))
								        	data.addEffects(FireworkEffect.builder().withColor(Color.YELLOW).with(Type.BALL_LARGE).build());
								        if(pInfo.flagTeam.equals("Diamond"))
								        	data.addEffects(FireworkEffect.builder().withColor(Color.TEAL).with(Type.BALL_LARGE).build());
								        data.setPower(2);
								        firework.setFireworkMeta(data);
								  	}
								}, 1*TICK,1*TICK);
						}
					}
					else
					{
						if(pInfo.flagTeam!=null)
						{
							if(tInfo.getFlag().equals(tInfo.getCurrentFlagLocation()))
							{
								event.getPlayer().setFoodLevel(20);
								plugin.getServer().broadcastMessage(tInfo.getName()+" has scored a point!");
								tInfo.points++;
								TeamInfo opponentTeam=plugin.getTeam(pInfo.flagTeam);
								plugin.getServer().broadcastMessage("The score is: "+tInfo.getName()+": "+tInfo.points+" "+opponentTeam.getName()+": "+opponentTeam.points);
								opponentTeam.setCurrentFlagLocation(opponentTeam.getFlag());
								opponentTeam.getFlag().getBlock().setTypeIdAndData(pInfo.flagBlockType, pInfo.flagBlockData, false);
								pInfo.flagTeam=null;
								pInfo.flagBlockType=Material.AIR.getId();
								pInfo.flagBlockData=0;
								Bukkit.getScheduler().cancelTask(pInfo.taskID);
								pInfo.taskID=-1;
							}
						}
						if(pInfo.flagAlt!=null)
						{
							aInfo=plugin.getAltSpawn(pInfo.flagAlt);
							if(tInfo.getFlag().equals(tInfo.getCurrentFlagLocation()))
							{
								event.getPlayer().setFoodLevel(20);
								plugin.getServer().broadcastMessage(tInfo.getName()+" has captured the "+aInfo.getName()+" spawn point.");
								aInfo.setCurrentFlagLocation(aInfo.getFlag());
								aInfo.getFlag().getBlock().setTypeIdAndData(pInfo.flagBlockType, pInfo.flagBlockData, false);
								aInfo.setCurrentTeam(pInfo.getTeam());
								pInfo.flagAlt=null;
								pInfo.flagBlockType=Material.AIR.getId();
								pInfo.flagBlockData=0;
								Bukkit.getScheduler().cancelTask(pInfo.taskID);
								pInfo.taskID=-1;
							}
						}
						if(!tInfo.getFlag().equals(tInfo.getCurrentFlagLocation()))
						{
							if(tInfo.graceTime==-1)
							{
								Block flag=tInfo.getCurrentFlagLocation().getBlock();
								tInfo.getFlag().getBlock().setTypeIdAndData(flag.getType().getId(), flag.getData(), false);
								flag.setType(Material.AIR);
								tInfo.setCurrentFlagLocation(tInfo.getFlag());
								plugin.getServer().broadcastMessage(tInfo.getName()+"'s flag was returned to it's post!");
							}
							else
							{
								event.getPlayer().sendMessage("You may not return the flag yet.");
							}
						}
					}
				}
				if((aInfo=plugin.blockIsAltFlag(block.getLocation()))!=null)
				{
					if(aInfo.getCurrentTeam()==null||!pInfo.getTeam().equals(aInfo.getCurrentTeam()))
					{
						if(pInfo.flagTeam==null&&pInfo.flagAlt==null)
						{
							event.getPlayer().setFoodLevel(10);
							if(aInfo.getCurrentTeam()!=null)
								plugin.getServer().broadcastMessage(pInfo.getName()+" has captured "+aInfo.getCurrentTeam()+" team's "+aInfo.getName()+" flag!");
							else
								plugin.getServer().broadcastMessage(pInfo.getName()+" has captured the "+aInfo.getName()+" flag!");
							pInfo.captureAltFlag(aInfo.getName(), block);
							aInfo.setFlagCaptured(pInfo.getName());
							System.out.println("Block: "+block);
							event.getClickedBlock().setType(Material.AIR);
							final Player player=plugin.getServer().getPlayer(pInfo.getName());
							pInfo.taskID=plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,
								new Runnable()
								{
								  	public void run()
								  	{
								  		Location loc = player.getLocation();
								        Firework firework = player.getWorld().spawn(loc, Firework.class);
								        FireworkMeta data = (FireworkMeta) firework.getFireworkMeta();
								        if(pInfo.flagAlt.equals("Red"))
								        	data.addEffects(FireworkEffect.builder().withColor(Color.RED).with(Type.BALL_LARGE).build());
								        if(pInfo.flagAlt.equals("Yellow"))
								        	data.addEffects(FireworkEffect.builder().withColor(Color.YELLOW).with(Type.BALL_LARGE).build());
								        data.setPower(2);
								        firework.setFireworkMeta(data);
								  	}
								}, 1*TICK,1*TICK);
						}
					}
					else
					{
						if(!aInfo.getFlag().equals(aInfo.getCurrentFlagLocation()))
						{
							if(aInfo.graceTime==-1)
							{
								tInfo=plugin.getTeam(aInfo.getCurrentTeam());
								Block flag=aInfo.getCurrentFlagLocation().getBlock();
								aInfo.getFlag().getBlock().setTypeIdAndData(flag.getType().getId(), flag.getData(), false);
								flag.setType(Material.AIR);
								aInfo.setCurrentFlagLocation(aInfo.getFlag());
								if(aInfo.getCurrentTeam()!=null)
									plugin.getServer().broadcastMessage("The "+aInfo.getName()+" flag was returned to it's post!");
								else
									plugin.getServer().broadcastMessage(tInfo.getName()+"'s "+aInfo.getName()+" flag was returned to it's post!");
							}
							else
							{
								event.getPlayer().sendMessage("You may not return the flag yet.");
							}
						}
					}
				}
			}
			else//it's a wall sign
			{
				if(event.getAction()==Action.LEFT_CLICK_BLOCK)
				{
					Sign sign=(Sign)event.getClickedBlock().getState();
					String[] lines=sign.getLines();
					if(lines[0].equals("Class"))
					{
						if(lines[1].contains("[")&&lines[1].contains("]"))
						{
							lines[1]=lines[1].replace("[", "");
							lines[1]=lines[1].replace("]", "");
							PlayerInfo pInfo=plugin.getPlayer(event.getPlayer().getName());
							pInfo.setLoadout(lines[1]);
							pInfo.setTeam(plugin.getPlayer(event.getPlayer().getName()).getTeam());
							pInfo.inSpawn=true;
						}
					}
					else if(lines[0].equals("Team"))
					{
						if(lines[1].contains("[")&&lines[1].contains("]"))
						{
							lines[1]=lines[1].replace("[", "");
							lines[1]=lines[1].replace("]", "");
							PlayerInfo pInfo=plugin.getPlayer(event.getPlayer().getName());
							pInfo.setLoadout("Warrior");
							if(!lines[1].equals("Random"))
							{
								pInfo.setTeam(lines[1]);
								TeamInfo tInfo=plugin.getTeam(pInfo.getTeam());
								tInfo.players++;
								event.getPlayer().teleport(tInfo.getSpawn());
								pInfo.inSpawn=true;
							}
							else
							{
								TeamInfo tInfo=plugin.getRandomTeam();
								pInfo.setTeam(tInfo.getName());
								tInfo.players++;
								event.getPlayer().teleport(tInfo.getSpawn());
								pInfo.inSpawn=true;
							}
						}
					}
					else if(lines[0].equals("Warp"))
					{
						if(lines[1].contains("[")&&lines[1].contains("]"))
						{
							lines[1]=lines[1].replace("[", "");
							lines[1]=lines[1].replace("]", "");
							PlayerInfo pInfo=plugin.getPlayer(event.getPlayer().getName());
							AltSpawnInfo aInfo=plugin.getAltSpawn(lines[1]);
							TeamInfo tInfo=plugin.getTeam(lines[1]);
							if(aInfo!=null)
							{
								if(pInfo.getTeam().equals(aInfo.getCurrentTeam()))
								{
									event.getPlayer().teleport(aInfo.getSpawn());
									pInfo.inSpawn=true;
								}
								else
								{
									event.getPlayer().sendMessage("You can't go to this spawn.");
								}
							}
							else if(tInfo!=null)
							{
								if(pInfo.getTeam().equals(tInfo.getName()))
								{
									event.getPlayer().teleport(tInfo.getSpawn());
									pInfo.inSpawn=true;
								}
								else
								{
									event.getPlayer().sendMessage("You can't go to this spawn.");
								}
							}
						}
					}
					else if(lines[0].equals("Spawn"))
					{
						Player player=event.getPlayer();
						PlayerInfo pInfo=plugin.getPlayer(event.getPlayer().getName());
						
						if(pInfo.canSpawn)
						{
							pInfo.inSpawn=false;
							
							org.bukkit.material.Sign s=(org.bukkit.material.Sign)block.getState().getData();
							
							Block rel=block.getRelative(s.getAttachedFace());
							rel=rel.getRelative(s.getAttachedFace());
							Location loc=rel.getLocation().add(0.5,-0.5,0.5);
							loc.setPitch(player.getLocation().getPitch());
							loc.setYaw(player.getLocation().getYaw());
							player.teleport(loc);
						}
						else
						{
							player.sendMessage(ChatColor.RED+"You can't spawn yet");
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onEntityExplode(EntityExplodeEvent event)
	{
		event.setCancelled(true);
		
		for(Block block:event.blockList())
		{
			if(block.getType()==Material.TNT)
			{
				block.setType(Material.AIR);
				TNTPrimed tnt=block.getWorld().spawn(block.getLocation().add(0.5, 0.5, 0.5), TNTPrimed.class);
				tnt.setFuseTicks(1*TICK/2);
			}
			else
			{
				if(plugin.breakableBlock(block.getLocation()))
					block.setType(Material.AIR);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockBurn(BlockBurnEvent event)
	{
		Block block=event.getBlock();
		if(block.getType()!=Material.LOG&&block.getType()!=Material.LEAVES)
		{
			event.setCancelled(true);
			if(block.getRelative(BlockFace.UP).getType()==Material.FIRE)
				block.getRelative(BlockFace.UP).setType(Material.AIR);
			if(block.getRelative(BlockFace.DOWN).getType()==Material.FIRE)
				block.getRelative(BlockFace.DOWN).setType(Material.AIR);
			if(block.getRelative(BlockFace.NORTH).getType()==Material.FIRE)
				block.getRelative(BlockFace.NORTH).setType(Material.AIR);
			if(block.getRelative(BlockFace.SOUTH).getType()==Material.FIRE)
				block.getRelative(BlockFace.SOUTH).setType(Material.AIR);
			if(block.getRelative(BlockFace.EAST).getType()==Material.FIRE)
				block.getRelative(BlockFace.EAST).setType(Material.AIR);
			if(block.getRelative(BlockFace.WEST).getType()==Material.FIRE)
				block.getRelative(BlockFace.WEST).setType(Material.AIR);
		}
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerMove(PlayerMoveEvent event)
	{
		Player player=event.getPlayer();
		if(plugin.settings!=null&&player.getLocation().getY()>plugin.settings.mapTop)
		{
			player.teleport(player.getLocation().add(0, -1, 0));
		}
	}
}
