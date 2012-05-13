package com.mtihc.minecraft.hungergame.plugin.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.hungergame.plugin.HungerGamePlugin;

public class ListCommand extends SimpleCommand {

	private final String permission;

	public ListCommand(SimpleCommand parent, String label,
			List<String> aliases, String permission) {
		super(parent, label, aliases, "[page]", "List of created games.");
		this.permission = permission;
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {
		if (!sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.RED
					+ "You don't have permission to list games.");
			return false;
		}

		if (args != null && args.length > 1) {
			sender.sendMessage(ChatColor.RED + "Expected only a page number.");
			sender.sendMessage(getUsage());
			return false;
		}

		int page;
		try {
			page = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + "Expected a page number.");
			sender.sendMessage(getUsage());
			return false;
		} catch (Exception e) {
			page = 1;
		}

		String[] idArray = HungerGamePlugin.getPlugin().gameIds();

		int total = idArray.length;
		int totalPerPage = 10;
		int totalPages = (total - 1) / totalPerPage + 1;

		int startIndex = (page - 1) * totalPerPage;
		int endIndex = startIndex + totalPerPage;

		sender.sendMessage(ChatColor.GREEN + "["
				+ HungerGamePlugin.getPlugin().getName() + "] "
				+ ChatColor.WHITE + "List of game ids (" + page + "/"
				+ totalPages + "):");
		if (total == 0) {
			sender.sendMessage(ChatColor.RED + "  (none)");
		} else {
			for (int i = startIndex; i < total && i < endIndex; i++) {
				sender.sendMessage(ChatColor.GREEN + "  " + (i + 1) + ". "
						+ ChatColor.WHITE + idArray[i]);
			}
			if (totalPages > 1) {
				int nextPage = page + 1;
				if (nextPage > totalPages) {
					nextPage = 1;
				}
				sender.sendMessage(ChatColor.GREEN + "Next page: "
						+ ChatColor.WHITE
						+ getUsage().replace("[page]", String.valueOf(page)));
			}

		}

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
