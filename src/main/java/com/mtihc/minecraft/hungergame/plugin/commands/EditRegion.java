package com.mtihc.minecraft.hungergame.plugin.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.hungergame.game.configuration.GameInfoSerializable;
import com.mtihc.minecraft.hungergame.plugin.HungerGamePlugin;

public class EditRegion extends SimpleCommand {

	private final String permission;

	public EditRegion(SimpleCommand parent, String label, List<String> aliases,
			String permission) {
		super(parent, label, aliases, "<id> <region>",
		"Change the WorldGuard region in which the game takes place.");
		this.permission = permission;
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {
		if (!sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.RED
					+ "You don't have permission to change the region.");
			return false;
		}

		if (args != null && args.length > 2) {
			sender.sendMessage(ChatColor.RED
					+ "Expected only the game id and region id.");
			sender.sendMessage(getUsage());
			return false;
		}

		String id;
		String region;
		try {
			id = args[0];
			region = args[1];
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED
					+ "Expected the region name and game id.");
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

		World world = info.getLocation().getWorld();

		if (!plugin.hasRegion(world, region)) {
			sender.sendMessage(ChatColor.RED + "Region \"" + region
					+ "\" does not exist in the game's world \""
					+ world.getName() + "\".");
			sender.sendMessage(getUsage());
			return false;
		}

		info.setRegion(region);
		plugin.saveGameInfo(info);
		sender.sendMessage("Region \"" + region + "\" set for game \""
				+ info.getId() + "\".");
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
