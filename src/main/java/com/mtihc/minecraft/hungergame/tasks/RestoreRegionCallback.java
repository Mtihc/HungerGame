package com.mtihc.minecraft.hungergame.tasks;

/**
 * Observer for a RestoreTask
 * 
 * @author Mitch
 * 
 */
public interface RestoreRegionCallback {
	void onRestoreStart(RestoreRegionTask task);

	void onRestoreCancel(RestoreRegionTask task);

	void onRestoreFinish(RestoreRegionTask task);
}
