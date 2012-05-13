package com.mtihc.minecraft.hungergame.plugin.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.hungergame.game.exceptions.GameException;
import com.mtihc.minecraft.hungergame.plugin.HungerGamePlugin;

public class GameStop extends SimpleCommand {

	private final String permission;

	public GameStop(SimpleCommand parent, String label, List<String> aliases,
			String permission) {
		super(parent, label, aliases, "<id>", "End a game.");
		this.permission = permission;
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {

		if (!sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.RED
					+ "You don't have permission to end games.");
			sender.sendMessage(getUsage());
			return false;
		}

		if (args != null && args.length > 1) {
			sender.sendMessage(ChatColor.RED + "Expected a game id.");
			sender.sendMessage(getUsage());
			return false;
		}

		String id = "";
		for (int i = 0; i < args.length; i++) {
			if (args[i] == null || args[i].isEmpty()) {
				continue;
			}
			id += " " + args[i];
		}
		if (id.isEmpty()) {
			sender.sendMessage(ChatColor.RED + "Expected a game id.");
			sender.sendMessage(getUsage());
			return false;
		} else {
			id = id.substring(1);
		}

		HungerGamePlugin plugin = HungerGamePlugin.getPlugin();
		try {
			plugin.gameStop(id);
		} catch (GameException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
			return false;
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
