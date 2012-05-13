package com.mtihc.minecraft.hungergame.game.configuration;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.mtihc.minecraft.hungergame.configuration.ChestSerializable;
import com.mtihc.minecraft.hungergame.configuration.InventorySerializable;
import com.mtihc.minecraft.hungergame.game.GameFeast;

public class GameFeastSerializable extends ChestSerializable implements
GameFeast {

	public GameFeastSerializable(InventoryHolder holder) {
		super(holder);
	}

	public GameFeastSerializable(Location location, Inventory inventory) {
		super(location, inventory);
	}

	public GameFeastSerializable(Location location, int size,
			ItemStack[] contents) {
		super(location, size, contents);
	}
	
	protected GameFeastSerializable(Map<String, Object> values) {
		super(values);
	}

	public GameFeastSerializable(Location location,
			InventorySerializable inventory) {
		super(location, inventory);
	}

	public static GameFeastSerializable deserialize(Map<String, Object> values) {
		return new GameFeastSerializable(values);
	}
}
