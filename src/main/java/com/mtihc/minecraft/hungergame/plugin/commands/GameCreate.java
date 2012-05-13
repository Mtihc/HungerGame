package com.mtihc.minecraft.hungergame.plugin.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.hungergame.game.configuration.GameInfoSerializable;
import com.mtihc.minecraft.hungergame.plugin.HungerGamePlugin;

public class GameCreate extends SimpleCommand {

	private final String permission;

	public GameCreate(SimpleCommand parent, String label, List<String> aliases,
			String permission) {
		super(parent, label, aliases,
				"<id> <region> [invul] [feast delay]",
		"Create game at your current location.");
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

		Player p = (Player) sender;

		if (!sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.RED
					+ "You don't have permission to create games.");
			sender.sendMessage(getUsage());
			return false;
		}

		if (args != null && args.length > 4) {
			sender.sendMessage(ChatColor.RED + "Expected less arguments.");
			sender.sendMessage(getUsage());
			return false;
		}

		String id;
		String region;
		try {
			id = args[0];
			region = args[1];
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "Invalid use of command.");
			sender.sendMessage(getUsage());
			return false;
		}

		int index = 2;

		int inv;
		int feast;
		try {
			inv = Integer.parseInt(args[index]);
			index++;
		} catch (Exception e) {
			inv = 60;
		}

		try {
			feast = Integer.parseInt(args[index]);
			index++;
		} catch (Exception e) {
			feast = 600;
		}

		HungerGamePlugin plugin = HungerGamePlugin.getPlugin();
		if (plugin.hasGameInfo(id)) {
			sender.sendMessage(ChatColor.RED + "Game \"" + id
					+ "\" already exists.");
			sender.sendMessage(getUsage());
			return false;
		}

		if (!plugin.hasRegion(p.getWorld(), region)) {
			sender.sendMessage(ChatColor.RED + "Region \"" + region
					+ "\" does not exist in world \"" + p.getWorld().getName()
					+ "\".");
			sender.sendMessage(getUsage());
			return false;
		}

		Location location = p.getLocation().clone();

		GameInfoSerializable info = plugin.createGameInfo(id, location, region, inv, feast);
		plugin.saveGameInfo(info);

		InfoCommand.sendInfo(sender, info);

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
