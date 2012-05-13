package com.mtihc.minecraft.hungergame.player.fields;

import org.bukkit.entity.Player;

import com.mtihc.minecraft.hungergame.player.PlayerField;

public class Exhaustion extends PlayerField<Float> {

	@Override
	public String getName() {
		return "exhaustion";
	}

	@Override
	public void defaultToPlayer(Player player) {
		player.setExhaustion(0);
	}

	@Override
	public Float fromPlayer(Player player) {
		return player.getExhaustion();
	}

	@Override
	public void toPlayer(Player player, Float value) {
		player.setExhaustion(value);
	}

}
