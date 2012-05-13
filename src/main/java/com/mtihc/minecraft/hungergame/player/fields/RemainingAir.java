package com.mtihc.minecraft.hungergame.player.fields;

import org.bukkit.entity.Player;

import com.mtihc.minecraft.hungergame.player.PlayerField;

public class RemainingAir extends PlayerField<Integer> {

	@Override
	public String getName() {
		return "remainingAir";
	}

	@Override
	public void defaultToPlayer(Player player) {
		player.setRemainingAir(player.getMaximumAir());
	}

	@Override
	public Integer fromPlayer(Player player) {
		return player.getRemainingAir();
	}

	@Override
	public void toPlayer(Player player, Integer value) {
		player.setRemainingAir(value);
	}

}
