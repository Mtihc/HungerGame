package com.mtihc.minecraft.hungergame.tasks;

import org.bukkit.plugin.java.JavaPlugin;

public class CountdownTask implements Runnable {

	private static final long TICKS_IN_SECOND = 20L;
	
	private final JavaPlugin plugin;
	private int totalSeconds;
	private CountdownCallback callback;

	private int taskId;
	private int step;

	public CountdownTask(JavaPlugin plugin, int totalSeconds, CountdownCallback callback) {
		this.plugin = plugin;
		this.totalSeconds = totalSeconds;
		this.callback = callback;
		this.taskId = -1;
		this.step = 0;
	}

	public JavaPlugin getPlugin() {
		return plugin;
	}

	public int getSecondsTotal() {
		return totalSeconds;
	}

	public void setSecondsTotal(int value) {
		this.totalSeconds = value;
	}
	
	public int getSecondsPassed() {
		return step;
	}

	public int getSecondsRemaining() {
		return totalSeconds - step;
	}

	public boolean isRunning() {
		return taskId != -1;
	}

	public boolean schedule() {
		if (taskId == -1) {
			step = 0;
			// on countdown start
			callback.onCountdownStart(this, totalSeconds);
			
			taskId = plugin
			.getServer()
			.getScheduler()
			.scheduleSyncRepeatingTask(plugin, this, 
					CountdownTask.TICKS_IN_SECOND,
					CountdownTask.TICKS_IN_SECOND);
			
			return true;
		}
		else {
			return false;
		}
	}

	public boolean cancel() {
		if (taskId != -1) {
			plugin.getServer().getScheduler().cancelTask(taskId);
			taskId = -1;

			if(step == totalSeconds) {
				// on countdown finish
				callback.onCountdownFinish(this, totalSeconds);
			}
			else {
				// on countdown cancel
				callback.onCountdownCancel(this, totalSeconds, getSecondsRemaining());
			}
			step = 0;
			return true;
		}
		else {
			return false;
		}
	}
	

	@Override
	public void run() {
		if(!isRunning()) {
			callback.onCountdownStart(this, 0);
			callback.onCountdownFinish(this, 0);
			return;
		}
		step++;
		int remaining = getSecondsRemaining();
		if (remaining <= 0) {
			cancel();
		} else if(remaining < 10) {
			// on countdown second
			callback.onCountdown(this, totalSeconds, remaining);
		} else if(isWarnTime(remaining)) {
			callback.onCountdownWarning(this, totalSeconds, remaining);
		}
		
		
	}
	
	protected boolean isWarnTime(int remaining) {
		if(remaining > 0) {
			if(remaining == 10) {
				return true;
			}
			else if(remaining > 10) {
				if(remaining == 20) {
					return true;
				}
				else if(remaining > 20) {
					if(remaining == 30) {
						return true;
					}
					else if(remaining > 30) {
						if(remaining == 60) {
							return true;
						}
						else if(remaining > 60) {
							if(remaining == 120) {
								return true;
							}
							else if(remaining > 120) {
								if(remaining == 300) {
									return true;
								}
								else if(remaining > 300) {
									if(remaining == 600) {
										return true;
									}
									else if(remaining > 600) {
										if(remaining == 1800) {
											return true;
										}
										else if(remaining % 3600 == 0) {
											return true;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

}
