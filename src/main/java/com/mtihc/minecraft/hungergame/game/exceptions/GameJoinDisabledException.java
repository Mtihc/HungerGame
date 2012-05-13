package com.mtihc.minecraft.hungergame.game.exceptions;

public class GameJoinDisabledException extends GameException {

	private static final long serialVersionUID = 4244977265664305534L;

	public GameJoinDisabledException(String msg) {
		super(msg);
	}

	public GameJoinDisabledException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
