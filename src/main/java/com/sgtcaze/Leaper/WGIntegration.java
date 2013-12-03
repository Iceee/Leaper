package com.sgtcaze.Leaper;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Bukkit;

import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WGBukkit;

public class WGIntegration {

	private Config config;

	public WGIntegration(Config config) {
		this.config = config;
	}

	public boolean isAllowedInRegion(Location location) {
		if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
			return true;
		}
		try {
			List<String> aregions = WGBukkit.getRegionManager(
					location.getWorld()).getApplicableRegionsIDs(
					BukkitUtil.toVector(location));
			for (String disabledregion : config.disabledRegions) {
				if (aregions.contains(disabledregion)) {
					return false;
				}
			}
		} catch (Exception e) {
		}
		return true;
	}

}
