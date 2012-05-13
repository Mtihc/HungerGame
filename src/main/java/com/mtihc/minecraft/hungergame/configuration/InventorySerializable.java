package com.mtihc.minecraft.hungergame.configuration;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventorySerializable implements ConfigurationSerializable {

	private final int size;
	private final ItemStack[] contents;

	public InventorySerializable(Inventory inventory) {
		this.size = inventory.getSize();
		this.contents = Arrays.copyOf(inventory.getContents(), this.size);
	}

	public InventorySerializable(int size, ItemStack[] contents) {
		this.size = size;
		this.contents = Arrays.copyOf(contents, size);
	}

	public InventorySerializable(int size) {
		this.size = size;
		this.contents = new ItemStack[size];
	}

	public int getSize() {
		return size;
	}

	public ItemStack[] getContents() {
		return contents;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("size", size);

		Map<String, Object> contentsSection = new LinkedHashMap<String, Object>();
		for (int i = 0; i < contents.length; i++) {
			if (contents[i] == null || contents[i].getTypeId() == 0) {
				continue;
			}
			contentsSection.put("item" + i, contents[i]);
		}
		values.put("contents", contentsSection);

		return values;
	}

	public static InventorySerializable deserialize(Map<String, Object> values) {
		int size = Integer.parseInt(values.get("size").toString());
		InventorySerializable result = new InventorySerializable(size);

		Map<?, ?> contentsSection = (Map<?, ?>) values.get("contents");
		Set<?> entries = contentsSection.entrySet();
		for (Object object : entries) {
			Map.Entry<?, ?> entry = (Map.Entry<?, ?>) object;
			int index = Integer.parseInt(entry.getKey().toString()
					.substring("item".length()));
			result.contents[index] = (ItemStack) entry.getValue();
		}

		return result;
	}

}
