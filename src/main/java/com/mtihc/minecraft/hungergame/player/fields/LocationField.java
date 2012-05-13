package com.mtihc.minecraft.hungergame.player.fields;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.hungergame.player.PlayerField;

public class LocationField extends PlayerField<Location> {

	@Override
	public String getName() {
		return "location";
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
		Block block = value.getBlock();
		Block above = block.getRelative(0, 1, 0);
		while (!block.isEmpty() || !above.isEmpty()) {
			block = above;
			above = block.getRelative(0, 1, 0);
		}
		player.teleport(block.getLocation());
	}

}
