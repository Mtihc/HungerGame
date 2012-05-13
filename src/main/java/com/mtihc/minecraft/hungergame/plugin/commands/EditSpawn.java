package com.mtihc.minecraft.hungergame.plugin.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class EditSpawn extends SimpleCommand {

	private final String permission;

	public EditSpawn(SimpleCommand parent, String label, List<String> aliases,
			String permission) {
		super(parent, label, aliases, "help [page]", "Command help");
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

		sendHelp(sender, args);

		return true;
	}

	@Override
	public boolean hasNested() {
		return true;
	}

	@Override
	public SimpleCommand getNested(String labelOrAlias) {
		String lbl = labelOrAlias.toLowerCase();
		if (lbl.equals("add")) {
			return new EditSpawnAdd(this, "add", null, permission);
		} else if (lbl.equals("clear")) {
			return new EditSpawnClear(this, "clear", null, permission);
		} else {
			return null;
		}
	}

	@Override
	public String[] getNestedCommandLabels() {
		return new String[] { "add", "clear" };
	}

}
