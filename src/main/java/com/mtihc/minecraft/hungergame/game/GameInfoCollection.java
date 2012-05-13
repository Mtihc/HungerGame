package com.mtihc.minecraft.hungergame.game;

public interface GameInfoCollection {

	GameInfo getInfo(String id);

	void setInfo(String id, GameInfo info);

	void removeInfo(String id);

	boolean hasInfo(String id);

	String[] getInfoIds();
}
