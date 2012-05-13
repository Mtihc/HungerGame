package com.mtihc.minecraft.hungergame.plugin.commands;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.hungergame.game.Game;
import com.mtihc.minecraft.hungergame.game.GameInfo;
import com.mtihc.minecraft.hungergame.game.GamePlayer;
import com.mtihc.minecraft.hungergame.plugin.HungerGamePlugin;

public class InfoCommand extends SimpleCommand {

	private final String permission;

	public InfoCommand(SimpleCommand parent, String label,
			List<String> aliases, String permission) {
		super(parent, label, aliases, "[id]", "Get details about a game");
		this.permission = permission;
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {
		if (!sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.RED
					+ "You don't have permission to get details about games.");
			return false;
		}

		HungerGamePlugin plugin = HungerGamePlugin.getPlugin();
		
		
		String id;
		try {
			id = args[0];
		} catch (Exception e) {
			Game game = plugin.getGameOf(sender.getName());
			if(game == null) {
				sender.sendMessage(ChatColor.RED + "Expected game id. You're not in a game.");
				sender.sendMessage(getUsage());
				return false;
			}
			InfoCommand.sendInfo(sender, game);
			return true;
		}

		GameInfo info = plugin.getGameInfo(id);
		if (info == null) {
			sender.sendMessage(ChatColor.RED + "Game \"" + id
					+ "\" doesn't exist.");
			sender.sendMessage(getUsage());
			return false;
		}

		InfoCommand.sendInfo(sender, info);

		return true;
	}
	
	public static void sendInfo(CommandSender sender, Game game) {
		ChatColor green = ChatColor.GREEN;
		ChatColor white = ChatColor.WHITE;
		sender.sendMessage(green + "Game \"" + white + game.getId() + green
				+ "\" info:");
		
		if(game.isFeastScheduled()) {
			sender.sendMessage(green + "  Next Feast: " + white + "starts after " + getTimeString(game.getFeastRemainingSeconds()));
		}
		if(game.hasFeast()) {
			sender.sendMessage(green + "  Feast: " + white + getLocationString(game.getFeast().getLocation()));
		}
		else {
			sender.sendMessage(green + "  Feast: " + white + "(no current feast)");
		}
		
		sender.sendMessage(green + "  Alive players (" + game.getTotalAlivePlayers() + "): " + white + getPlayersString(game.getPlayers()));
		
		sender.sendMessage(green + "  Dead players (" + game.getTotalDeadPlayers() + "): " + white + getPlayersString(game.getDeadPlayers()));
	}

	public static void sendInfo(CommandSender sender, GameInfo info) {
		ChatColor green = ChatColor.GREEN;
		ChatColor white = ChatColor.WHITE;
		Location loc = info.getLocation();
		sender.sendMessage(green + "Game \"" + white + info.getId() + green
				+ "\" info:");
		sender.sendMessage(green + "  Region: " + white + info.getRegion());
		sender.sendMessage(green + "  Location: " + white
				+ InfoCommand.getLocationString(loc));
		sender.sendMessage(green + "  Spawn locations: " + white
				+ info.getSpawnLocations().size());
		sender.sendMessage(green + "  Invulnerability: " + white
				+ InfoCommand.getTimeString(info.getInvulnerabilitySeconds()));
		
		sender.sendMessage(green
				+ "  Startfeast location: "
				+ white
				+ (info.getStartFeast() == null ? "(none)"
						: InfoCommand.getLocationString(info.getStartFeast()
								.getLocation())));
		
		sender.sendMessage(green
				+ "  Feast location: "
				+ white
				+ (info.getDelayedFeast() == null ? "(none)"
						: InfoCommand.getLocationString(info.getDelayedFeast().getLocation())));
		
		sender.sendMessage(green + "  Feast spawns after " + white
				+ InfoCommand.getTimeString(info.getFeastDelaySeconds()));
		sender.sendMessage(green + "  Kick player when offline for " + white
				+ InfoCommand.getTimeString(info.getReconnectSeconds()));

	}
	
	private static String getPlayersString(Collection<GamePlayer> players) {
		String result = "";
		
		Iterator<GamePlayer> it = players.iterator();
		while(it.hasNext()) {
			GamePlayer p = it.next();
			result += ", " + p.getPlayer().getName();
		}
		if(result.isEmpty()) {
			result = "(none)";
		}
		else {
			result = result.substring(2);
		}
		return result;
	}

	private static String getLocationString(Location loc) {
		return loc.getWorld().getName() + " " + loc.getBlockX() + ","
		+ loc.getBlockY() + "," + loc.getBlockZ();
	}

	private static String getTimeString(int seconds) {
		int min = seconds / 60;

		if (min > 0) {
			int sec = seconds % 60;

			String timeString;

			if (min == 1) {
				timeString = min + " minute";
			} else {
				timeString = min + " minutes";
			}
			if (sec > 0) {
				timeString += " and " + sec + "sec";
			}
			return timeString;
		} else {
			return seconds + " sec";
		}
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
