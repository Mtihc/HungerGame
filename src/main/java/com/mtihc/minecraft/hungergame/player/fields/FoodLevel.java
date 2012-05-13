package com.mtihc.minecraft.hungergame.player.fields;

import org.bukkit.entity.Player;

import com.mtihc.minecraft.hungergame.player.PlayerField;

public class FoodLevel extends PlayerField<Integer> {

	@Override
	public String getName() {
		return "foodLevel";
	}

	@Override
	public void defaultToPlayer(Player player) {
		player.setFoodLevel(20);
	}

	@Override
	public Integer fromPlayer(Player player) {
		return player.getFoodLevel();
	}

	@Override
	public void toPlayer(Player player, Integer value) {
		player.setFoodLevel(value);
	}

}
