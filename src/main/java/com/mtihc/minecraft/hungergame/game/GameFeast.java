package com.mtihc.minecraft.hungergame.game;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public interface GameFeast extends ConfigurationSerializable {

	Location getLocation();

	InventoryHolder getHolder();

	int getSize();

	ItemStack[] getContents();
}
