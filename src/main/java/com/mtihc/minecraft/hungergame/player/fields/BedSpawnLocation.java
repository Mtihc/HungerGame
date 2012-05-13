package com.mtihc.minecraft.hungergame.player.fields;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.hungergame.player.PlayerField;

public class BedSpawnLocation extends PlayerField<Location> {

	@Override
	public String getName() {
		return "bedSpawnLocation";
	}

	@Override
	public void defaultToPlayer(Player player) {
		// stay the same
	}

	@Override
	public Location fromPlayer(Player player) {
		return player.getLocation().clone();
	}

	@Override
	public void toPlayer(Player player, Location value) {
		player.setBedSpawnLocation(value);
	}

}
