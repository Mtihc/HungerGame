package com.mtihc.minecraft.hungergame.player.fields;

import java.util.Arrays;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mtihc.minecraft.hungergame.player.PlayerField;

public class InventoryContents extends PlayerField<ItemStack[]> {

	@Override
	public String getName() {
		return "inventory";
	}

	@Override
	public void defaultToPlayer(Player player) {
		player.getInventory().clear();
	}

	@Override
	public ItemStack[] fromPlayer(Player player) {
		ItemStack[] array = player.getInventory().getContents();
		return Arrays.copyOf(array, array.length);
	}

	@Override
	public void toPlayer(Player player, ItemStack[] value) {
		int size = player.getInventory().getSize();
		player.getInventory().setContents(Arrays.copyOf(value, size));
	}

}
