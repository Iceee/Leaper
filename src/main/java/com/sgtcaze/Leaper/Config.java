package com.sgtcaze.Leaper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

	public List<String> enabledWorlds = new ArrayList<String>();

	public List<String> disabledRegions = new ArrayList<String>();

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

		SmootherWalk = config.getBoolean("SmootherWalk", SmootherWalk);

		enabledWorlds = config.getStringList("Worlds.EnabledWorlds");
		if (enabledWorlds.isEmpty()) {
			enabledWorlds.add(Bukkit.getWorlds().get(0).getName());
		}

		pSmoke = config.getBoolean("Particles.Smoke", pSmoke);
		pMobSpawnerFlames = config.getBoolean("Particles.MobSpawnerFlames",
				pMobSpawnerFlames);
		pEnderSignal = config.getBoolean("Particles.EnderSignal", pEnderSignal);
		pPotionBreak = config.getBoolean("Particles.PotionBreak", pPotionBreak);

		sBatTakeOff = config.getBoolean("Sounds.BatTakeOff", sBatTakeOff);
		sEnderDragonWings = config.getBoolean("Sounds.EnderDragonWings",
				sEnderDragonWings);
		sShootArrow = config.getBoolean("Sounds.ShootArrow", sShootArrow);

		disabledRegions = config.getStringList("WorldGuard.DisabledRegions");

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

		config.set("Worlds.EnabledWorlds", enabledWorlds);

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

		config.set("height", height);
		config.set("multiply", multiply);

		config.set("SmootherWalk", SmootherWalk);

		config.set("Worlds.EnabledWorlds", enabledWorlds);
		config.set("WorldGuard.DisabledRegions", disabledRegions);

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