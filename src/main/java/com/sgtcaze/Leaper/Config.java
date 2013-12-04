package com.sgtcaze.Leaper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {

	public Config(Leaper plugin) {
		configpath = new File(plugin.getDataFolder(), "config.yml");
	}

	public double height = 1.0D;
	public double multiply = 1.0D;

	public boolean SmootherWalk = true;

	public boolean disableFallDamage = false;

	public HashSet<String> enabledWorlds = new HashSet<String>();

	public HashSet<String> disabledRegions = new HashSet<String>();

	public boolean pSmoke = false;
	public boolean pMobSpawnerFlames = false;
	public boolean pEnderSignal = false;
	public boolean pPotionBreak = false;

	public boolean sBatTakeOff = false;
	public boolean sEnderDragonWings = false;
	public boolean sShootArrow = false;

	private File configpath;

	public void loadConfig() {
		FileConfiguration config = YamlConfiguration
				.loadConfiguration(configpath);

		height = config.getDouble("height", height);
		multiply = config.getDouble("multiply", multiply);

		disableFallDamage = config.getBoolean("disableAllFallDamage",
				disableFallDamage);

		SmootherWalk = config.getBoolean("SmootherWalk", SmootherWalk);

		enabledWorlds = new HashSet<String>(
				config.getStringList("Worlds.EnabledWorlds"));
		if (enabledWorlds.isEmpty()) {
			enabledWorlds.add(Bukkit.getWorlds().get(0).getName());
		}
		disabledRegions = new HashSet<String>(
				config.getStringList("WorldGuard.DisabledRegions"));

		pSmoke = config.getBoolean("Particles.Smoke", pSmoke);
		pMobSpawnerFlames = config.getBoolean("Particles.MobSpawnerFlames",
				pMobSpawnerFlames);
		pEnderSignal = config.getBoolean("Particles.EnderSignal", pEnderSignal);
		pPotionBreak = config.getBoolean("Particles.PotionBreak", pPotionBreak);

		sBatTakeOff = config.getBoolean("Sounds.BatTakeOff", sBatTakeOff);
		sEnderDragonWings = config.getBoolean("Sounds.EnderDragonWings",
				sEnderDragonWings);
		sShootArrow = config.getBoolean("Sounds.ShootArrow", sShootArrow);

		saveConfig();
	}

	private void saveConfig() {
		FileConfiguration config = new YamlConfiguration();

		config.set("height", height);
		config.set("multiply", multiply);

		config.set("SmootherWalk", SmootherWalk);

		config.set("disableAllFallDamage", disableFallDamage);

		config.set("Worlds.EnabledWorlds", new ArrayList<String>(enabledWorlds));
		config.set("WorldGuard.DisabledRegions", new ArrayList<String>(
				disabledRegions));

		config.set("Particles.Smoke", pSmoke);
		config.set("Particles.MobSpawnerFlames", pMobSpawnerFlames);
		config.set("Particles.EnderSignal", pEnderSignal);
		config.set("Particles.PotionBreak", pPotionBreak);

		config.set("Sounds.BatTakeOff", sBatTakeOff);
		config.set("Sounds.EnderDragonWings", sEnderDragonWings);
		config.set("Sounds.ShootArrow", sShootArrow);

		try {
			config.save(configpath);
		} catch (IOException e) {
		}
	}

}
