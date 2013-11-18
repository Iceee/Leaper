package com.sgtcaze.Leaper;

import java.util.HashMap;

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
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class EventListener implements Listener {

	private Leaper plugin;
	private Config config;

	public EventListener(Leaper plugin, Config config) {
		this.plugin = plugin;
		this.config = config;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		if (config.SmootherWalk) {
			p.setWalkSpeed(0.25F);
		}

	}

	// this hashmap is used to disable flying for player after 3 seconds
	private HashMap<String, Integer> unflytask = new HashMap<String, Integer>();
	// this hashmap is used to ensure that player will take fall damage
	// the only exclusion - if player falls on location where
	// fall_location_height < start_location_height - 2
	private HashMap<String, Double> maxdowny = new HashMap<String, Double>();
	// this hashmap is used to not allow player to move up more that 2 blocks
	// yes, you can do this with a cheat, because for some reason NCP doesn't
	// check player for flying if he has flight allowed
	private HashMap<String, Double> maxupy = new HashMap<String, Double>();

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		World world = player.getWorld();
		// allow player to fly
		final String playername = player.getName();
		if (config.enabledWorlds.contains(world.getName())) {
			if (maxdowny.containsKey(playername)) {
				if (event.getTo().getY() <= maxdowny.get(playername)) {
					player.setAllowFlight(false);
					removeFromHashMaps(playername);
				}
			}
			if ((player.getGameMode() != GameMode.CREATIVE)
					&& isOnGround(player)
					&& event.getTo().getY() > event.getFrom().getY()) {
				player.setAllowFlight(true);
				//maxdowny.put(player.getName(), event.getFrom().getY() - 2);
				if (maxupy.containsKey(playername)) {
					if (event.getTo().getY() > maxupy.get(playername)) {
						player.setAllowFlight(false);
						removeFromHashMaps(playername);
					}
				}
				if ((player.getGameMode() != GameMode.CREATIVE)
						&& isOnGround(player)) {
					player.setAllowFlight(true);
					//maxdowny.put(playername, event.getFrom().getY() - 2);
					//maxupy.put(playername, event.getFrom().getY() + 2);
					int unfly = Bukkit.getScheduler().scheduleSyncDelayedTask(
							plugin, new Runnable() {
								public void run() {
									Player player = Bukkit
											.getPlayerExact(playername);
									player.setAllowFlight(false);
									removeFromHashMaps(player.getName());
								}
							}, 60);
					unflytask.put(playername, unfly);
				}
			}
		}
	}

	private boolean isOnGround(Player player) {
		return ((Entity) player).isOnGround();
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onFly(PlayerToggleFlightEvent event) {
		Player player = event.getPlayer();
		String worldname = player.getWorld().getName();
		if (config.enabledWorlds.contains(worldname)) {
			if (player.getGameMode() != GameMode.CREATIVE) {
				event.setCancelled(true);
				player.setAllowFlight(false);
				player.setFlying(false);
				player.setVelocity(player.getLocation().getDirection()
						.multiply(1.6D * config.multiply)
						.setY(1.0 * config.height));
				player.setFallDistance(0);
				playEffects(player.getLocation());
				maxupy.remove(player.getName());
				if (unflytask.containsKey(player.getName())) {
					Bukkit.getScheduler().cancelTask(
							unflytask.get(player.getName()));
					unflytask.remove(player.getName());
				}
				player.setVelocity(player.getLocation().getDirection()
						.multiply(1.0D * config.multiply)
						.setY(1.0 * config.height));
				player.setFallDistance(0);
				playEffects(player.getLocation());
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

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void FallDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		String worldname = entity.getWorld().getName();
		if (config.enabledWorlds.contains(worldname)) {
			if (entity instanceof Player) {
				Player player = (Player) entity;
				if (event.getCause() == DamageCause.FALL) {
					if (maxdowny.containsKey(player.getName())) {
						event.setCancelled(true);
						removeFromHashMaps(player.getName());
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
		String playername = event.getPlayer().getName();
		if (event.getNewGameMode() == GameMode.CREATIVE) {
			removeFromHashMaps(playername);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		removeFromHashMaps(event.getPlayer().getName());
	}

	private void removeFromHashMaps(String playername) {
		maxdowny.remove(playername);
		maxupy.remove(playername);
		if (unflytask.containsKey(playername)) {
			Bukkit.getScheduler().cancelTask(unflytask.get(playername));
			unflytask.remove(playername);
		}
	}
}
