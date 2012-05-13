package com.mtihc.minecraft.hungergame.player.fields;

import org.bukkit.entity.Player;

import com.mtihc.minecraft.hungergame.player.PlayerField;

public class Saturation extends PlayerField<Float> {

	@Override
	public String getName() {
		return "saturation";
	}

	@Override
	public void defaultToPlayer(Player player) {
		player.setSaturation(0);
	}

	@Override
	public Float fromPlayer(Player player) {
		return player.getSaturation();
	}

	@Override
	public void toPlayer(Player player, Float value) {
		player.setSaturation(value);
	}

}
