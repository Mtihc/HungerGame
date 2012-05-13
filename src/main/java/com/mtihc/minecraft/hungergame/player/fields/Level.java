package com.mtihc.minecraft.hungergame.player.fields;

import org.bukkit.entity.Player;

import com.mtihc.minecraft.hungergame.player.PlayerField;

public class Level extends PlayerField<Integer> {

	@Override
	public String getName() {
		return "level";
	}

	@Override
	public void defaultToPlayer(Player player) {
		player.setLevel(0);
	}

	@Override
	public Integer fromPlayer(Player player) {
		return player.getLevel();
	}

	@Override
	public void toPlayer(Player player, Integer value) {
		player.setLevel(value);
	}

}
