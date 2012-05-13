package com.mtihc.minecraft.hungergame.configuration;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class DoubleChestSerializable implements InventoryHolderSerializable {

	private final ChestSerializable leftSide;
	private final ChestSerializable rightSide;
	private final InventorySerializable inventory;

	public DoubleChestSerializable(DoubleChest doubleChest) {
		DoubleChestInventory inv = (DoubleChestInventory) doubleChest
		.getInventory();
		Location leftLoc = ((Chest) doubleChest.getLeftSide()).getLocation();
		Location rightLoc = ((Chest) doubleChest.getRightSide()).getLocation();
		this.leftSide = createSide(leftLoc, inv.getLeftSide());
		this.rightSide = createSide(rightLoc, inv.getRightSide());
		this.inventory = getInventory();
	}
	
	protected DoubleChestSerializable(Map<String, Object> values) {
		this.leftSide = (ChestSerializable) values.get("leftSide");
		this.rightSide = (ChestSerializable) values.get("rightSide");
		this.inventory = getInventory();
	}

	protected ChestSerializable createSide(Location location,
			Inventory inventory) {
		return new ChestSerializable(location, inventory);
	}

	public DoubleChestSerializable(ChestSerializable leftSide,
			ChestSerializable rightSide) {
		this.leftSide = leftSide;
		this.rightSide = rightSide;
		this.inventory = getInventory();
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("leftSide", leftSide);
		values.put("rightSide", rightSide);
		return values;
	}

	public static DoubleChestSerializable deserialize(Map<String, Object> values) {
		return new DoubleChestSerializable(values);
	}

	@Override
	public Location getLocation() {
		return leftSide.getLocation();
	}

	@Override
	public InventoryHolder getHolder() {
		Block block = leftSide.getLocation().getBlock();
		if (!(block.getState() instanceof InventoryHolder)) {
			block.setType(Material.CHEST);
		}
		block = rightSide.getLocation().getBlock();
		if (!(block.getState() instanceof InventoryHolder)) {
			block.setType(Material.CHEST);
		}
		return ((InventoryHolder) block.getState()).getInventory().getHolder();
	}

	private InventorySerializable getInventory() {
		int leftSize = leftSide.getSize();
		int rightSize = rightSide.getSize();
		ItemStack[] left = leftSide.getContents();
		ItemStack[] right = rightSide.getContents();

		int size = leftSize + rightSize;
		ItemStack[] contents = new ItemStack[size];

		for (int i = 0; i < leftSize && i < left.length; i++) {
			contents[i] = left[i];
		}
		for (int i = 0; i < rightSize && i < right.length; i++) {
			contents[i + leftSize] = right[i];
		}

		return new InventorySerializable(size, contents);
	}

	@Override
	public int getSize() {
		return inventory.getSize();
	}

	@Override
	public ItemStack[] getContents() {
		return inventory.getContents();
	}

}
