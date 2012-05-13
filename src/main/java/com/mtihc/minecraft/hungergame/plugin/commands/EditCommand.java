package com.mtihc.minecraft.hungergame.plugin.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class EditCommand extends SimpleCommand {

	private final String permission;

	public EditCommand(SimpleCommand parent, String label,
			List<String> aliases, String permission) {
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
		if (lbl.equals("region") || lbl.equals("r")) {
			List<String> regionAliases = new ArrayList<String>();
			regionAliases.add("r");
			return new EditRegion(this, "region", regionAliases, permission);
		} else if (lbl.equals("invulnerability") || lbl.equals("i")
				|| lbl.equals("invul")) {
			List<String> invulnerabilityAliases = new ArrayList<String>();
			invulnerabilityAliases.add("invulnerability");
			invulnerabilityAliases.add("i");
			return new EditInvulnerability(this, "invul",
					invulnerabilityAliases, permission);
		} else if (lbl.equals("f") || lbl.equals("feast")) {
			List<String> feastAliases = new ArrayList<String>();
			feastAliases.add("f");
			return new EditFeast(this, "feast", feastAliases, permission);
		} else if (lbl.equals("spawn") || lbl.equals("s")) {
			List<String> spawnAliases = new ArrayList<String>();
			spawnAliases.add("s");
			return new EditSpawn(this, "spawn", spawnAliases, permission);
		} else {
			return null;
		}
	}

	@Override
	public String[] getNestedCommandLabels() {
		return new String[] { "region", "invul",
				"feast", "spawn" };
	}

}
