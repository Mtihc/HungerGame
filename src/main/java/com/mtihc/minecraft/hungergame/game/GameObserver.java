package com.mtihc.minecraft.hungergame.game;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.hungergame.game.Game.LeaveReason;

public interface GameObserver {

	void onRestoreStart(Game game);

	void onRestoreFinish(Game game);

	void onJoinEnable(Game game);

	void onJoinDisableWarning(Game game, int totalSeconds, int remaining);

	void onJoinDisableCountdown(Game game, int totalSeconds, int remaining);

	void onJoinDisable(Game game);

	void onGameStart(Game game);

	void onGameStop(Game game);

	void onPlayerJoin(Game game, Player player);

	void onPlayerLeave(Game game, Player player, LeaveReason reason);

	void onPlayerSpawn(Game game, Player player, Location location);

	void onInvulnerabilityStart(Game game, int totalSeconds);

	void onInvulnerabilityWarning(Game game, int totalSeconds, int remaining);

	void onInvulnerabilityCountdown(Game game, int totalSeconds, int remaining);

	void onInvulnerabilityStop(Game game, int totalSeconds, int remaining);

	void onFeastSchedule(Game game, int totalSeconds);

	void onFeastWarning(Game game, int totalSeconds, int remaining);

	void onFeastCountdown(Game game, int totalSeconds, int remaining);

	void onFeast(Game game);
}
