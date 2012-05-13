package com.mtihc.minecraft.hungergame.plugin.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.hungergame.game.exceptions.GameException;
import com.mtihc.minecraft.hungergame.plugin.HungerGamePlugin;

public class GameLeave extends SimpleCommand {

	private final String permission;

	public GameLeave(SimpleCommand parent, String label, List<String> aliases,
			String permission) {
		super(parent, label, aliases, "", "Leave your current game.");
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
					+ "You don't have permission to leave games.");
			sender.sendMessage(getUsage());
			return false;
		}

		if (args != null && args.length > 0) {
			sender.sendMessage(ChatColor.RED + "Expected no arguments.");
			sender.sendMessage(getUsage());
			return false;
		}

		HungerGamePlugin plugin = HungerGamePlugin.getPlugin();
		try {
			plugin.playerLeave(player);
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
