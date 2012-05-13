package com.mtihc.minecraft.hungergame.game.exceptions;

public class GameJoinedException extends GameException {

	private static final long serialVersionUID = 4244977265664305535L;

	public GameJoinedException(String msg) {
		super(msg);
	}

	public GameJoinedException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
