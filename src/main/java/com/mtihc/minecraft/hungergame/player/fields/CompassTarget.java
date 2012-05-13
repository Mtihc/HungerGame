package com.mtihc.minecraft.hungergame.player.fields;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.hungergame.player.PlayerField;

public class CompassTarget extends PlayerField<Location> {

	public CompassTarget() {

	}

	@Override
	public String getName() {
		return "compassTarget";
	}

	@Override
	public void defaultToPlayer(Player player) {
		player.setCompassTarget(player.getBedSpawnLocation());
	}

	@Override
	public Location fromPlayer(Player player) {
		return player.getCompassTarget().clone();
	}

	@Override
	public void toPlayer(Player player, Location value) {
		player.setCompassTarget(value);
	}

}
