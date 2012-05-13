package com.mtihc.minecraft.hungergame.plugin.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

public class HungerGameCommand extends SimpleCommand {

	public HungerGameCommand(SimpleCommand parent, String label,
			List<String> aliases) {
		super(parent, label, aliases, "help", "Command help");

	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {

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
		if (lbl.equals("join") || lbl.equals("j")) {
			List<String> joinAliases = new ArrayList<String>();
			joinAliases.add("j");
			return new GameJoin(this, "join", joinAliases, "hungergame.join");
		} else if (lbl.equals("leave") || lbl.equals("l")) {
			List<String> leaveAliases = new ArrayList<String>();
			leaveAliases.add("l");
			return new GameLeave(this, "leave", leaveAliases,
			"hungergame.leave");

		} else if (lbl.equals("start")) {

			return new GameStart(this, "start", null, "hungergame.start", false);

		} else if (lbl.equals("stop")) {
			return new GameStop(this, "stop", null, "hungergame.stop");

		} else if (lbl.equals("create")) {
			return new GameCreate(this, "create", null, "hungergame.create");

		} else if (lbl.equals("info")) {
			return new InfoCommand(this, "info", null, "hungergame.create");
		} else if (lbl.equals("list")) {
			return new ListCommand(this, "list", null, "hungergame.list");
		} else if (lbl.equals("edit") || lbl.equals("e")) {

			List<String> editAliases = new ArrayList<String>();
			editAliases.add("e");
			return new EditCommand(this, "edit", editAliases,
			"hungergame.create");

		} else if (lbl.equals("delete") || lbl.equals("del")) {
			List<String> deleteAliases = new ArrayList<String>();
			deleteAliases.add("del");
			return new GameDelete(this, "delete", deleteAliases,
			"hungergame.delete");

		} else if (lbl.equals("reload")) {
			return new ReloadCommand(this, "reload", null, "hungergame.reload");
		} else {
			return null;
		}

	}

	@Override
	public String[] getNestedCommandLabels() {
		return new String[] { "join", "leave", "start", "stop", "create",
				"info", "list", "edit", "delete", "reload" };
	}

}
