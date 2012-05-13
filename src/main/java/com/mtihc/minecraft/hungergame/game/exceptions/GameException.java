package com.mtihc.minecraft.hungergame.game.exceptions;

public class GameException extends Exception {

	private static final long serialVersionUID = 4244977265664305532L;

	public GameException(String msg) {
		super(msg);
	}

	public GameException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public GameException(Throwable cause) {
		super(cause);
	}

}
