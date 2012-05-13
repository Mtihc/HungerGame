package com.mtihc.minecraft.hungergame.plugin;

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.minecraft.hungergame.game.Game;
import com.mtihc.minecraft.hungergame.game.Game.LeaveReason;
import com.mtihc.minecraft.hungergame.game.GameObserver;
import com.mtihc.minecraft.hungergame.game.GamePlayer;

public class HungerGameObserver implements GameObserver {

	private final JavaPlugin plugin;

	public HungerGameObserver(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	protected final String getPrefix() {
		return "[" + plugin.getName() + "] ";
	}

	protected void broadcastServer(String msg) {
		plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + getPrefix() + ChatColor.GRAY
				 + msg);
	}

	protected void broadcastGame(Game game, String msg) {
		Iterator<GamePlayer> players = game.getPlayers().iterator();
		while (players.hasNext()) {
			players.next().getPlayer().sendMessage(ChatColor.GREEN + getPrefix() + ChatColor.WHITE
					 + msg);
		}
	}

	@Override
	public void onJoinEnable(Game game) {
		broadcastServer("Joining enabled for game \""
				+ game.getInfo().getId() + "\".");
		broadcastServer("To join, execute: " + ChatColor.WHITE
				+ "/hunger join " + game.getInfo().getId());
	}

	@Override
	public void onJoinDisableWarning(Game game, int totalSeconds, int remaining) {
		String time = getTimeString(remaining / 60, remaining % 60);
		broadcastServer("Joining will be disabled for game \""
				+ game.getInfo().getId() + "\" in " + time);
	}

	@Override
	public void onJoinDisableCountdown(Game game, int totalSeconds,
			int remaining) {
		// no messages
	}

	@Override
	public void onJoinDisable(Game game) {
		broadcastServer("Joining disabled for game \""
				+ game.getInfo().getId() + "\".");
	}

	@Override
	public void onGameStart(Game game) {
		broadcastServer("Game \"" + game.getInfo().getId()
				+ "\" started.");
	}

	@Override
	public void onGameStop(Game game) {
		broadcastServer("Game \"" + game.getInfo().getId()
				+ "\" ended.");
	}

	@Override
	public void onInvulnerabilityStart(Game game, int secondsLeft) {
		int min = secondsLeft / 60;
		int sec = secondsLeft % 60;

		String timeString = getTimeString(min, sec);

		broadcastGame(game, "You're invulnerable for " + timeString + ".");
	}

	@Override
	public void onInvulnerabilityWarning(Game game, int total, int secondsLeft) {
		broadcastGame(game, "Invulnerability will be disabled after "
				+ secondsLeft + " seconds");
	}

	@Override
	public void onInvulnerabilityCountdown(Game game, int total, int secondsLeft) {
		broadcastGame(game, String.valueOf(secondsLeft));
	}

	@Override
	public void onInvulnerabilityStop(Game game, int total, int secondsLeft) {
		broadcastGame(game, "You are no longer invulnerable.");
	}

	//
	// @Override
	// public void onDie(Player player) {
	//
	// }
	//
	// @Override
	// public void onKill(Player killer, Player killed) {
	// killer.sendMessage(getPrefix() + "You killed " +
	// killed.getDisplayName());
	// }

	@Override
	public void onPlayerJoin(Game game, Player player) {
		broadcastServer(ChatColor.GREEN + "Player " + ChatColor.WHITE + player.getDisplayName()
				+ ChatColor.GREEN + " joined \"" + game.getInfo().getId() + "\".");
	}

	@Override
	public void onPlayerLeave(Game game, Player player, LeaveReason reason) {
		if (!reason.equals(LeaveReason.WIN)) {
			broadcastServer(ChatColor.RED + "Player " + ChatColor.WHITE
					+ player.getDisplayName() + ChatColor.RED + " left \""
					+ game.getInfo().getId() + "\".");
		} else {
			broadcastServer(ChatColor.GOLD + "Player " + ChatColor.WHITE
					+ player.getDisplayName() + ChatColor.GOLD + " won game \""
					+ game.getInfo().getId() + "\"!");
		}
	}

	@Override
	public void onFeastWarning(Game game, int total, int secondsLeft) {
		int min = secondsLeft / 60;
		int sec = secondsLeft % 60;

		String timeString = getTimeString(min, sec);

		broadcastGame(game, "The feast will start in " + timeString + ".");
	}

	private String getTimeString(int min, int sec) {
		if (min > 0) {
			String timeString = min + " minutes";
			if (sec > 0) {
				timeString += " and " + sec + " seconds";
			}
			return timeString;
		} else {
			if (sec > 0) {
				return sec + " seconds";
			} else {
				return "less than a second";
			}
		}
	}

	@Override
	public void onFeastSchedule(Game game, int secondsLeft) {
		broadcastGame(game, "The feast will start in " + secondsLeft
				+ " seconds.");
	}

	@Override
	public void onFeastCountdown(Game game, int total, int secondsLeft) {
		broadcastGame(game, String.valueOf(secondsLeft));

	}

	@Override
	public void onFeast(Game game) {
		broadcastGame(game, "The feast has begun!");
	}

	@Override
	public void onRestoreStart(Game game) {
		broadcastServer("Game \"" + game.getInfo().getId()
				+ "\" can be joined when it's region is restored.");
	}

	@Override
	public void onRestoreFinish(Game game) {
		broadcastServer(ChatColor.GRAY + "Region of game \""
				+ game.getInfo().getId() + "\" restored.");
	}

	@Override
	public void onPlayerSpawn(Game game, Player player, Location location) {
		player.setAllowFlight(false);
		player.setCompassTarget(location);
		player.setExhaustion(0);
		player.setExp(0);
		player.setFoodLevel(20);
		player.setGameMode(GameMode.SURVIVAL);
		player.setHealth(player.getMaxHealth());
		player.setLevel(0);
		player.setRemainingAir(player.getMaximumAir());
		player.setSaturation(0);

		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);

		player.saveData();
		player.sendMessage(getPrefix() + ChatColor.GRAY
				+ "You will get your stuff back when the game ends.");

	}

}
