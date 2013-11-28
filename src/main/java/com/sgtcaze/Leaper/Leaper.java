package com.sgtcaze.Leaper;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

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