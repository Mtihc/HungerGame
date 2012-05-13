package com.mtihc.minecraft.hungergame.player.fields;

import org.bukkit.entity.Player;

import com.mtihc.minecraft.hungergame.player.PlayerField;

public class Health extends PlayerField<Integer> {

	@Override
	public String getName() {
		return "health";
	}

	@Override
	public void defaultToPlayer(Player player) {
		player.setHealth(player.getMaxHealth());
	}

	@Override
	public Integer fromPlayer(Player player) {
		return player.getHealth();
	}

	@Override
	public void toPlayer(Player player, Integer value) {
		player.setHealth(value);
	}

}
