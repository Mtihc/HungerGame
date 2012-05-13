package com.mtihc.minecraft.hungergame.tasks;

public interface RestoreSchematicCallback {
	void onRestoreSchematicStart(RestoreSchematicTask task);

	void onRestoreSchematicCancel(RestoreSchematicTask task);

	void onRestoreSchematicFinish(RestoreSchematicTask task);
}