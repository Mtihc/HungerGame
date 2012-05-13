package com.mtihc.minecraft.hungergame.plugin.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.hungergame.game.configuration.GameInfoSerializable;
import com.mtihc.minecraft.hungergame.plugin.HungerGamePlugin;

public class EditFeastDelay extends SimpleCommand {

	private final String permission;

	public EditFeastDelay(SimpleCommand parent, String label,
			List<String> aliases, String permission) {
		super(parent, label, aliases, "<id> <seconds>",
		"Set a game's feast delay time.");
		this.permission = permission;
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {
		if (!sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.RED
					+ "You don't have permission to set a game's feast delay time.");
			sender.sendMessage(getUsage());
			return false;
		}

		if (args != null && args.length > 2) {
			sender.sendMessage(ChatColor.RED
					+ "Expected only a game id and amount of seconds.");
			sender.sendMessage(getUsage());
			return false;
		}

		String id;
		int sec;
		try {
			id = args[0];
			sec = Integer.parseInt(args[1]);
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED
					+ "Expected a game id and amount of seconds.");
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

		info.setFeastDelaySeconds(sec);
		plugin.saveGameInfo(info);

		int minutes = sec / 60;
		int seconds = sec % 60;

		sender.sendMessage("Feast delay of \"" + id + "\" set to " + minutes
				+ " minutes and " + seconds + " sec.");

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
