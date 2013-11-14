package com.sgtcaze.Leaper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

@SuppressWarnings("unused")
public class Leaper extends JavaPlugin {

	private Config config;
	private EventListener listener;
	private Commands commands;

	@Override
	public void onEnable() {
		config = new Config(this);
		config.loadConfig();
		commands = new Commands(config);
		getCommand("leaper").setExecutor(commands);
		listener = new EventListener(this, config);
		getServer().getPluginManager().registerEvents(listener, this);
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		listener.clearData();
		listener = null;
		commands = null;
		config = null;
	}

}