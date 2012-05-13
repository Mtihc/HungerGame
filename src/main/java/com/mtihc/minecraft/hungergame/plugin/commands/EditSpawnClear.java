package com.mtihc.minecraft.hungergame.plugin.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.hungergame.game.configuration.GameInfoSerializable;
import com.mtihc.minecraft.hungergame.plugin.HungerGamePlugin;

public class EditSpawnClear extends SimpleCommand {

	private final String permission;

	public EditSpawnClear(SimpleCommand parent, String label,
			List<String> aliases, String permission) {
		super(parent, label, aliases, "<id>", "Clear all spawns of a game.");
		this.permission = permission;
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {

		if (!sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.RED
					+ "You don't have permission to add/remove spawn locations.");
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
		} else {
			spawns.clear();
		}
		info.setSpawnLocations(spawns);
		plugin.saveGameInfo(info);

		sender.sendMessage("Spawn locations of \"" + id + "\" cleared.");

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
