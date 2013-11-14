package com.sgtcaze.Leaper;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class EventListener implements Listener {

	private Leaper plugin;
	private Config config;

	public EventListener(Leaper plugin, Config config)
	{
		this.plugin = plugin;
		this.config = config;
		wghook = new WGIntegration(config);
	}
	
	private WGIntegration wghook;

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		if (config.SmootherWalk) {
			p.setWalkSpeed(0.25F);
		}

	}
	
	//used to disable flying for player after 3 seconds
	private HashMap<String,Integer> unflytask = new HashMap<String,Integer>();
	//used to ensure that player will take fall damage
	//the only exclusion - if player falls on location where fall_location_height < start_location_height - 2
	private HashMap<String,Double> maxdowny = new HashMap<String,Double>();
	//used to not allow player to move up more that 2 blocks 
	//yes, you can do this with a cheat, because for some reason NCP doesn't check player for flying if he has flight allowed
	private HashMap<String,Double> maxupy = new HashMap<String,Double>();
	//used to cancel player fly when he starts to move down
	private HashMap<String,Integer> ongroundtask = new HashMap<String,Integer>();

	//... the most hard checks are in player move event k_k
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		World world = player.getWorld();
		//allow player to fly
		final String playername = player.getName();
		if (config.enabledWorlds.contains(world.getName()))  {
			//check if player reached maximum down location and remove it's data
			if (maxdowny.containsKey(playername)) {
				if (event.getTo().getY() <= maxdowny.get(playername)) {
					player.setAllowFlight(false);
					removeFromHashMaps(playername);
					return;
				}
			}
			//check if player reached maximum up location and remove it's data (ignore this check if player used double jump)
			if (maxupy.containsKey(playername) && player.getAllowFlight()) {
				if (event.getTo().getY() > maxupy.get(playername)) {
					player.setAllowFlight(false);
					removeFromHashMaps(playername);
					return;
				}
			}
			//if player moved up that it means that he probably jumped
			if ((player.getGameMode() != GameMode.CREATIVE) && isOnGround(player) && event.getTo().getY() > event.getFrom().getY())  {
				//ignore player if he has allowfly
				if (!player.getAllowFlight()) {
					//allow fly so player can trigger fly enable
					player.setAllowFlight(true);
					//add player max down position data
					maxdowny.put(playername, event.getFrom().getY()-2);
					//add player max up position data
					maxupy.put(playername, event.getFrom().getY()+2);
					//add player disable allowfly data
					int unfly = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							Player player = Bukkit.getPlayerExact(playername);
							player.setAllowFlight(false);
							removeFromHashMaps(player.getName());
						}
					}, 60);
					unflytask.put(playername, unfly);
					//add on ground check task
					int taskid = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
						public void run() {
							Player player = Bukkit.getPlayerExact(playername);
							if (isOnGround(player) && player.getAllowFlight()) {
								player.setAllowFlight(false);
								removeFromHashMaps(playername);
							}
						}
					},0,1);
					ongroundtask.put(player.getName(), taskid);
				}
			}
		}
	}

	private boolean isOnGround(Player player) {
		return ((Entity) player).isOnGround();
	}

	//when player toggles fly cancel fly and increase player velocity
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onFly(PlayerToggleFlightEvent event) {
		Player player = event.getPlayer();
		String worldname = player.getWorld().getName();
		if (config.enabledWorlds.contains(worldname)) {
			if (player.getGameMode() != GameMode.CREATIVE) {
				event.setCancelled(true);
				player.setAllowFlight(false);
				//remove max up data
				maxupy.remove(player.getName());
				//remove unfly task data
				if (unflytask.containsKey(player.getName())) {
					Bukkit.getScheduler().cancelTask(unflytask.get(player.getName()));
					unflytask.remove(player.getName());
				}
				//remove onground task data
				if (ongroundtask.containsKey(player.getName())) {
					Bukkit.getScheduler().cancelTask(ongroundtask.get(player.getName()));
					ongroundtask.remove(player.getName());
				}
				if (wghook.isAllowedInRegion(player.getLocation())) {
					//do double jump
					player.setVelocity(player.getLocation().getDirection()
							.multiply(1.0D * config.multiply).setY(1.0 * config.height));
					player.setFallDistance(0);
					playEffects(player.getLocation());
				} else {
					maxdowny.remove(player.getName());
				}
			}
		}
	}


	private void playEffects(Location location) {
		if (config.pSmoke) {
			location.getWorld().playEffect(location, Effect.SMOKE, 2000);
		}
		if (config.pMobSpawnerFlames) {
			location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES,
					2004);
		}
		if (config.pEnderSignal) {
			location.getWorld().playEffect(location, Effect.ENDER_SIGNAL, 2003);
		}
		if (config.pPotionBreak) {
			location.getWorld().playEffect(location, Effect.POTION_BREAK, 2002);
		}

		if (config.sBatTakeOff) {
			location.getWorld().playSound(location, Sound.BAT_TAKEOFF, 1.0F,
					-5.0F);
		}
		if (config.sEnderDragonWings) {
			location.getWorld().playSound(location, Sound.ENDERDRAGON_WINGS,
					1.0F, -5.0F);
		}
		if (config.sShootArrow) {
			location.getWorld().playSound(location, Sound.SHOOT_ARROW, 1.0F,
					-5.0F);
		}
	}

	//cancel fall damage for player if we have data about maximum player down location
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void FallDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		String worldname = entity.getWorld().getName();
		if (config.enabledWorlds.contains(worldname)) {
			if (entity instanceof Player) {
				Player player = (Player) entity;
				if (event.getCause() == DamageCause.FALL) {
					if (maxdowny.containsKey(player.getName()) || config.disableFallDamage) {
						event.setCancelled(true);
						removeFromHashMaps(player.getName());
					}
				}
			}
		}
	}

	//remove player jump data on gamemode change to creative
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
		String playername = event.getPlayer().getName();
		if (event.getNewGameMode() == GameMode.CREATIVE) {
			removeFromHashMaps(playername);
		}
	}
	
	//remove player jump data on teleport
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			event.getPlayer().setAllowFlight(false);
			removeFromHashMaps(event.getPlayer().getName());
		}
	}
	
	//remove player jump data on quit
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.getPlayer().setAllowFlight(false);
		removeFromHashMaps(event.getPlayer().getName());
	}
	
	private void removeFromHashMaps(String playername) {
		maxdowny.remove(playername);
		maxupy.remove(playername);
		if (unflytask.containsKey(playername)) {
			Bukkit.getScheduler().cancelTask(unflytask.get(playername));
			unflytask.remove(playername);
	 	}
		if (ongroundtask.containsKey(playername)) {
			Bukkit.getScheduler().cancelTask(ongroundtask.get(playername));
			ongroundtask.remove(playername);
		}
	}

	//this will clear everything on disable
	public void clearData()
	{
		HashSet<String> playernames = new HashSet<String>(maxdowny.keySet());
		for (String playername : playernames)
		{
			try {
				Bukkit.getPlayerExact(playername).setAllowFlight(false);
				removeFromHashMaps(playername);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
