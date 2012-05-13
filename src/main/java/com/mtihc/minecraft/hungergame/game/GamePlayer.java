package com.mtihc.minecraft.hungergame.game;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.mtihc.minecraft.hungergame.player.PlayerState;

public class GamePlayer {

	private final Player player;
	private PlayerState originalState;
	private Inventory feastInventory;

	protected GamePlayer(Player player) {
		this.player = player;
		this.originalState = null;
		this.feastInventory = null;
	}

	public String getName() {
		return player.getName();
	}

	public Player getPlayer() {
		return player;
	}

	protected PlayerState getOriginalState() {
		return originalState;
	}

	protected void setOriginalState(PlayerState state) {
		this.originalState = state;
	}

	protected boolean hasFeastInventory() {
		return feastInventory != null;
	}

	protected Inventory getFeastInventory() {
		return feastInventory;
	}

	protected void setFeastInventory(Inventory value) {
		this.feastInventory = value;
	}

	public void teleportSafe(Location spawn) {
		Block block = spawn.getBlock();
		Block above = block.getRelative(0, 1, 0);
		while (!block.isEmpty() || !above.isEmpty()) {
			block = above;
			above = block.getRelative(0, 1, 0);
		}
		player.teleport(block.getLocation());
		
	}

}
