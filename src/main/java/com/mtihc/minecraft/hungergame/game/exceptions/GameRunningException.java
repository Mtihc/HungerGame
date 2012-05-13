package com.mtihc.minecraft.hungergame.game.exceptions;

public class GameRunningException extends GameException {

	private static final long serialVersionUID = 4244977265664305536L;

	public GameRunningException(String msg) {
		super(msg);
	}

	public GameRunningException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
