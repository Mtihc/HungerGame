package com.mtihc.minecraft.hungergame.game.configuration;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.Inventory;

import com.mtihc.minecraft.hungergame.configuration.ChestSerializable;
import com.mtihc.minecraft.hungergame.configuration.DoubleChestSerializable;
import com.mtihc.minecraft.hungergame.game.GameFeast;

public class GameDoubleFeastSerializable extends DoubleChestSerializable implements GameFeast {

	public GameDoubleFeastSerializable(DoubleChest doubleChest) {
		super(doubleChest);
	}

	public GameDoubleFeastSerializable(GameFeastSerializable leftSide,
			GameFeastSerializable rightSide) {
		super(leftSide, rightSide);
	}
	
	protected GameDoubleFeastSerializable(Map<String, Object> values) {
		super(values);
	}

	@Override
	protected ChestSerializable createSide(Location location,
			Inventory inventory) {
		return new GameFeastSerializable(location, inventory);
	}

	public static GameDoubleFeastSerializable deserialize(Map<String, Object> values) {
		return new GameDoubleFeastSerializable(values);
	}
}
