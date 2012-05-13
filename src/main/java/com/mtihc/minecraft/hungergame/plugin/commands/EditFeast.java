package com.mtihc.minecraft.hungergame.plugin.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class EditFeast extends SimpleCommand {

	private final String permission;

	public EditFeast(SimpleCommand parent, String label, List<String> aliases,
			String permission) {
		super(parent, label, aliases, "help [page]", "Command help");
		this.permission = permission;

	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {

		if (!sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.RED
					+ "You don't have permission to change the feast.");
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
		if (lbl.equals("delay")) {
			return new EditFeastDelay(this, "delay", null, permission);

		} else if (lbl.equals("set")) {
			return new EditFeastSet(this, "set", null, permission, false);

		} else if (lbl.equals("first") || lbl.equals("start")
				|| lbl.equals("setfirst") || lbl.equals("setstart")) {
			List<String> setFirstFeastAliases = new ArrayList<String>();
			setFirstFeastAliases.add("first");
			setFirstFeastAliases.add("start");
			setFirstFeastAliases.add("setstart");
			return new EditFeastSet(this, "setfirst", setFirstFeastAliases,
					permission, true);
		} else {
			return null;
		}
	}

	@Override
	public String[] getNestedCommandLabels() {
		return new String[] { "delay", "setfirst", "set" };
	}

}
