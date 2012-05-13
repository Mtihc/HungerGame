package com.mtihc.minecraft.hungergame.plugin.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.hungergame.game.exceptions.GameException;
import com.mtihc.minecraft.hungergame.plugin.HungerGamePlugin;

public class GameStart extends SimpleCommand {

	private final String permission;
	private final boolean restoreRegion;

	private static String getDescrition(boolean restoreRegion) {
		if (restoreRegion) {
			return "Start a game, after restoring the game region.";
		} else {
			return "Start a game.";
		}
	}

	public GameStart(SimpleCommand parent, String label, List<String> aliases,
			String permission, boolean restoreRegion) {
		super(parent, label, aliases, "<id> <join seconds>",
				GameStart.getDescrition(restoreRegion));
		this.permission = permission;
		this.restoreRegion = restoreRegion;
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {

		if (!sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.RED
					+ "You don't have permission to start games.");
			sender.sendMessage(getUsage());
			return false;
		}

		if (args != null && args.length > 2) {
			sender.sendMessage(ChatColor.RED
					+ "Expected only a game id and the amount of seconds to join.");
			sender.sendMessage(getUsage());
			return false;
		}

		HungerGamePlugin plugin = HungerGamePlugin.getPlugin();

		String id;
		int joinDelaySeconds;
		try {
			id = args[0];
			joinDelaySeconds = Integer.parseInt(args[1]);
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED
					+ "Expected a game id and the amount of seconds to join.");
			sender.sendMessage(getUsage());
			return false;
		}
		

		try {
			plugin.gameStart(id, joinDelaySeconds, this.restoreRegion);
		} catch (GameException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
			return false;
		}

		return true;
	}

	@Override
	public boolean hasNested() {
		return !(getParent() instanceof GameStart);
	}

	@Override
	public SimpleCommand getNested(String labelOrAlias) {
		String lbl = labelOrAlias.toLowerCase();
		if (lbl.equals("-restore") || lbl.equals("-r")) {
			if (!(getParent() instanceof GameStart)) {
				List<String> restoreAliases = new ArrayList<String>();
				restoreAliases.add("-r");
				return new GameStart(this, "-restore", restoreAliases,
						permission, !restoreRegion);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public String[] getNestedCommandLabels() {
		if (!(getParent() instanceof GameStart)) {
			return new String[] { "-restore" };
		} else {
			return null;
		}
	}

}
