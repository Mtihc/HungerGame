package com.mtihc.minecraft.hungergame.plugin.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.hungergame.game.exceptions.GameException;
import com.mtihc.minecraft.hungergame.game.exceptions.GameJoinedException;
import com.mtihc.minecraft.hungergame.plugin.HungerGamePlugin;

public class GameJoin extends SimpleCommand {

	private final String permission;

	public GameJoin(SimpleCommand parent, String label, List<String> aliases,
			String permission) {
		super(parent, label, aliases, "<id>", "Join the game.");
		this.permission = permission;
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED
					+ "Command must be executed by a player, in game.");
			sender.sendMessage(getUsage());
			return false;
		}

		Player player = (Player) sender;

		if (!sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.RED
					+ "You don't have permission to join games.");
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
			plugin.playerJoin(player, id);
		} catch (GameJoinedException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
			return false;
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
