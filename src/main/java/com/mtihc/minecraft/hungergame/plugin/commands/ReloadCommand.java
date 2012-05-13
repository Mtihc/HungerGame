package com.mtihc.minecraft.hungergame.plugin.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.hungergame.plugin.HungerGamePlugin;

public class ReloadCommand extends SimpleCommand {

	private final String permission;

	public ReloadCommand(SimpleCommand parent, String label,
			List<String> aliases, String permission) {
		super(parent, label, aliases, "", "Reload the config");
		this.permission = permission;
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {
		if (!sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.RED
					+ "You don't have permission to reload the config.");
			return false;
		}

		if (args != null && args.length > 0) {
			sender.sendMessage(ChatColor.RED + "Expected no arguments.");
			sender.sendMessage(getUsage());
			return false;
		}

		HungerGamePlugin.getPlugin().reloadConfig();
		sender.sendMessage(HungerGamePlugin.getPlugin().getName()
				+ " configuration reloaded.");
		return true;
	}

	@Override
	public boolean hasNested() {
		return false;
	}

	@Override
	public SimpleCommand getNested(String labelOrAlias) {
		return null;
	}

	@Override
	public String[] getNestedCommandLabels() {
		return null;
	}

}
