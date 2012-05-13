package com.mtihc.minecraft.hungergame.player.fields;

import org.bukkit.entity.Player;

import com.mtihc.minecraft.hungergame.player.PlayerField;

public class AllowFlight extends PlayerField<Boolean> {

	@Override
	public String getName() {
		return "allowFlight";
	}

	@Override
	public void defaultToPlayer(Player player) {
		player.setAllowFlight(false);
	}

	@Override
	public Boolean fromPlayer(Player player) {
		return player.getAllowFlight();
	}

	@Override
	public void toPlayer(Player player, Boolean value) {
		player.setAllowFlight(value);
	}

}
