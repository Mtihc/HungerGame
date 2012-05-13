package com.mtihc.minecraft.hungergame.tasks;

public interface CountdownCallback {
	void onCountdownStart(CountdownTask task, int totalSeconds);

	void onCountdownWarning(CountdownTask task, int totalSeconds, int remaining);

	void onCountdown(CountdownTask task, int totalSeconds, int remaining);

	void onCountdownCancel(CountdownTask task, int totalSeconds, int remaining);

	void onCountdownFinish(CountdownTask task, int totalSeconds);
}