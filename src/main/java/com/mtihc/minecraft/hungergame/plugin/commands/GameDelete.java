package com.mtihc.minecraft.hungergame.plugin.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.hungergame.plugin.HungerGamePlugin;

public class GameDelete extends SimpleCommand {

	private final String permission;

	public GameDelete(SimpleCommand parent, String label, List<String> aliases,
			String permission) {
		super(parent, label, aliases, "<id>", "Delete a created game.");
		this.permission = permission;
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {
		if (!sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.RED
					+ "You don't have permission to delete games.");
			return false;
		}

		if (args != null && args.length > 1) {
			sender.sendMessage(ChatColor.RED + "Expected only a game id.");
			sender.sendMessage(getUsage());
			return false;
		}

		String id;
		try {
			id = args[0];
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "Expected a game id.");
			sender.sendMessage(getUsage());
			return false;
		}

		HungerGamePlugin plugin = HungerGamePlugin.getPlugin();
		if (!plugin.hasGameInfo(id)) {
			sender.sendMessage(ChatColor.RED + "Game \"" + id
					+ "\" doesn't exist.");
			sender.sendMessage(getUsage());
			return false;
		} else {
			plugin.deleteGameInfo(id);
			sender.sendMessage("Game \"" + id + "\" deleted.");
		}

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
