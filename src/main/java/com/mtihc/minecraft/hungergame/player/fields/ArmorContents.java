package com.mtihc.minecraft.hungergame.player.fields;

import java.util.Arrays;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mtihc.minecraft.hungergame.player.PlayerField;

public class ArmorContents extends PlayerField<ItemStack[]> {

	@Override
	public String getName() {
		return "armor";
	}

	@Override
	public void defaultToPlayer(Player player) {
		player.getInventory().setArmorContents(new ItemStack[4]);
	}

	@Override
	public ItemStack[] fromPlayer(Player player) {
		ItemStack[] array = player.getInventory().getArmorContents();
		return Arrays.copyOf(array, array.length);
	}

	@Override
	public void toPlayer(Player player, ItemStack[] value) {
		player.getInventory().setArmorContents(Arrays.copyOf(value, 4));
	}

}
