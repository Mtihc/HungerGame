package com.mtihc.minecraft.hungergame.player.fields;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.hungergame.player.PlayerField;

public class GameModeField extends PlayerField<GameMode> {

	@Override
	public String getName() {
		return "gameMode";
	}

	@Override
	public void defaultToPlayer(Player player) {
		player.setGameMode(GameMode.SURVIVAL);
	}

	@Override
	public GameMode fromPlayer(Player player) {
		return player.getGameMode();
	}

	@Override
	public void toPlayer(Player player, GameMode value) {
		player.setGameMode(value);
	}

}
