package com.mtihc.minecraft.hungergame.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.entity.Player;

public class PlayerState {

	private final Map<PlayerField<?>, Object> fields;

	public PlayerState() {
		this.fields = new HashMap<PlayerField<?>, Object>();
	}

	public <T> void setFieldValue(PlayerField<T> field, T value) {
		fields.put(field, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T getFieldValue(PlayerField<T> field) {
		return (T) fields.get(field);
	}

	public boolean hasFieldValue(PlayerField<?> field) {
		return fields.containsKey(field);
	}

	public static PlayerState fromPlayer(Player player, PlayerField<?>[] fields) {
		PlayerState result = new PlayerState();

		for (PlayerField<?> field : fields) {
			result.fields.put(field, field.fromPlayer(player));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static void toPlayer(PlayerState state, Player player) {
		if(state == null || state.fields == null || state.fields.isEmpty()) {
			return;
		}
		Set<Entry<PlayerField<?>, Object>> entries = state.fields.entrySet();
		for (Map.Entry<PlayerField<?>, Object> entry : entries) {
			((PlayerField<Object>) entry.getKey()).toPlayer(player,
					entry.getValue());
		}
		player.saveData();
	}

}
