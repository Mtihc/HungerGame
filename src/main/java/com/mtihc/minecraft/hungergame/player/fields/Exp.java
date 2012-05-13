package com.mtihc.minecraft.hungergame.player.fields;

import org.bukkit.entity.Player;

import com.mtihc.minecraft.hungergame.player.PlayerField;

public class Exp extends PlayerField<Float> {

	@Override
	public String getName() {
		return "exp";
	}

	@Override
	public void defaultToPlayer(Player player) {
		player.setExp(0);
	}

	@Override
	public Float fromPlayer(Player player) {
		return player.getExp();
	}

	@Override
	public void toPlayer(Player player, Float value) {
		player.setExp(value);
	}

}
