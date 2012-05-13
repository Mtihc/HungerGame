package com.mtihc.minecraft.hungergame.player;

import org.bukkit.entity.Player;

public abstract class PlayerField<T> {

	public abstract String getName();

	public abstract void defaultToPlayer(Player player);

	public abstract T fromPlayer(Player player);

	public abstract void toPlayer(Player player, T value);

}