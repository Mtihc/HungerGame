package com.mtihc.minecraft.hungergame.player.fields;

import com.mtihc.minecraft.hungergame.player.PlayerField;

public class DefaultPlayerField {

	public static final AllowFlight ALLOW_FLIGHT = new AllowFlight();
	public static final BedSpawnLocation BED_SPAWN_LOCATION = new BedSpawnLocation();
	public static final CompassTarget COMPASS_TARGET = new CompassTarget();
	public static final Exhaustion EXHAUSTION = new Exhaustion();
	public static final Exp EXP = new Exp();
	public static final FoodLevel FOOD_LEVEL = new FoodLevel();
	public static final GameModeField GAME_MODE = new GameModeField();
	public static final Health HEALTH = new Health();
	public static final Level LEVEL = new Level();
	public static final RemainingAir REMAINING_AIR = new RemainingAir();
	public static final Saturation SATURATION = new Saturation();
	public static final LocationField LOCATION = new LocationField();
	public static final InventoryContents INVENTORY = new InventoryContents();
	public static final ArmorContents ARMOR = new ArmorContents();

	private static final PlayerField<?>[] values = new PlayerField<?>[] {
		DefaultPlayerField.ALLOW_FLIGHT, DefaultPlayerField.ARMOR, DefaultPlayerField.BED_SPAWN_LOCATION, DefaultPlayerField.COMPASS_TARGET,
		DefaultPlayerField.EXHAUSTION, DefaultPlayerField.EXP, DefaultPlayerField.FOOD_LEVEL, DefaultPlayerField.GAME_MODE, DefaultPlayerField.HEALTH, DefaultPlayerField.INVENTORY, DefaultPlayerField.LEVEL,
		DefaultPlayerField.LOCATION, DefaultPlayerField.REMAINING_AIR, DefaultPlayerField.SATURATION };

	public static PlayerField<?>[] values() {
		return DefaultPlayerField.values;
	}

	public static PlayerField<?> fromName(String name) {
		for (PlayerField<?> field : DefaultPlayerField.values) {
			if (field.getName().equalsIgnoreCase(name)) {
				return field;
			}
		}
		return null;
	}

	private DefaultPlayerField() {

	}

}
