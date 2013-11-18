package com.sgtcaze.Leaper;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

	private Config config;

	public Commands(Config config) {
		this.config = config;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (isAllowed(sender)) {
			if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
				config.loadConfig();
				sender.sendMessage(ChatColor.GREEN + "Configuration reloaded");
				return true;
			}
		}
		return false;
	}

	private boolean isAllowed(CommandSender sender) {
		if (sender instanceof ConsoleCommandSender
				|| sender instanceof RemoteConsoleCommandSender) {
			return true;
		} else if (sender instanceof Player
				&& sender.hasPermission("leaper.reload")) {
			return true;
		}
		return false;
	}

}
