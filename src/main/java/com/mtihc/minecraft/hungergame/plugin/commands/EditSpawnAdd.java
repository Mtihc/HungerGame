package com.mtihc.minecraft.hungergame.plugin.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.hungergame.game.configuration.GameInfoSerializable;
import com.mtihc.minecraft.hungergame.plugin.HungerGamePlugin;

public class EditSpawnAdd extends SimpleCommand {

	private final String permission;

	public EditSpawnAdd(SimpleCommand parent, String label,
			List<String> aliases, String permission) {
		super(parent, label, aliases, "<id>",
		"Add spawn at your current location.");
		this.permission = permission;
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED
					+ "Command must be executed by a player, in game.");
			return false;
		}

		Player p = (Player) sender;

		if (!sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.RED
					+ "You don't have permission to add/remove spawn locations.");
			sender.sendMessage(getUsage());
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
		GameInfoSerializable info = plugin.getGameInfo(id);
		if (info == null) {
			sender.sendMessage(ChatColor.RED + "Game \"" + id
					+ "\" doesn't exist.");
			sender.sendMessage(getUsage());
			return false;
		}

		List<Location> spawns = info.getSpawnLocations();
		if (spawns == null) {
			spawns = new ArrayList<Location>();
		}
		spawns.add(p.getLocation());
		info.setSpawnLocations(spawns);

		plugin.saveGameInfo(info);

		sender.sendMessage("Spawn location added to \"" + id + "\".");

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
