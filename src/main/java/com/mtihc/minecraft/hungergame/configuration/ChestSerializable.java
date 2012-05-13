package com.mtihc.minecraft.hungergame.configuration;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class ChestSerializable implements InventoryHolderSerializable {

	private final Location location;
	private final InventorySerializable inventory;

	public ChestSerializable(InventoryHolder holder) {
		if (!(holder instanceof BlockState)) {
			throw new IllegalArgumentException(
					"Parameter holder must be a BlockState object, instead of "
					+ holder.getClass().getSimpleName() + ".");
		}
		BlockState blockState = (BlockState) holder;

		this.location = blockState.getLocation().clone();
		this.inventory = new InventorySerializable(holder.getInventory());
	}
	
	protected ChestSerializable(Map<String, Object> values) {
		this.location = ChestSerializable.convertMapToLocation((Map<?, ?>) values
				.get("location"));
		this.inventory = (InventorySerializable) values
		.get("inventory");
	}

	public ChestSerializable(Location location, Inventory inventory) {
		this(location, new InventorySerializable(inventory));
	}

	public ChestSerializable(Location location, int size, ItemStack[] contents) {
		this(location, new InventorySerializable(size, contents));
	}

	public ChestSerializable(Location location, InventorySerializable inventory) {
		this.location = location.clone();
		this.inventory = inventory;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("location", ChestSerializable.convertLocationToMap(location));
		values.put("inventory", inventory);
		return values;
	}
	

	public static ChestSerializable deserialize(Map<String, Object> values) {
		
		return new ChestSerializable(values);
	}

	private static Map<String, Object> convertLocationToMap(Location location) {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		result.put("world", location.getWorld().getName());
		result.put("yaw", location.getYaw());
		result.put("pitch", location.getPitch());
		result.put("x", location.getBlockX());
		result.put("y", location.getBlockY());
		result.put("z", location.getBlockZ());
		return result;
	}

	private static Location convertMapToLocation(Map<?, ?> map) {
		String worldName = map.get("world").toString();
		World world = Bukkit.getWorld(worldName);

		float yaw = Float.parseFloat(map.get("yaw").toString());
		float pitch = Float.parseFloat(map.get("pitch").toString());
		int x = Integer.parseInt(map.get("x").toString());
		int y = Integer.parseInt(map.get("y").toString());
		int z = Integer.parseInt(map.get("z").toString());

		return new Location(world, x, y, z, yaw, pitch);
	}
	
	@Override
	public Location getLocation() {
		return location.clone();
	}

	@Override
	public InventoryHolder getHolder() {
		Block block = location.getBlock();
		if (!(block.getState() instanceof InventoryHolder)) {
			block.setType(Material.CHEST);
		}
		return (InventoryHolder) block.getState();
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
