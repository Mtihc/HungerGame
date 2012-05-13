package com.mtihc.minecraft.hungergame.tasks;

import java.io.File;
import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.data.DataException;

public class RestoreSchematicTask implements Runnable {

	
	
	private JavaPlugin plugin;
	private RestoreSchematicCallback callback;
	private File root;
	private String[] paths;
	private LocalWorld localWorld;
	private int maxBlocks;
	private int taskId;
	private int index;

	public RestoreSchematicTask(JavaPlugin plugin, RestoreSchematicCallback callback, File schematicsRoot, String[] schematicsPaths, LocalWorld localWorld, int maxBlocks) {
		this.plugin = plugin;
		this.callback = callback;
		this.root = schematicsRoot;
		this.paths = schematicsPaths;
		this.localWorld = localWorld;
		this.maxBlocks = maxBlocks;
		this.taskId = -1;
		this.index = 0;
	}
	
	public RestoreSchematicCallback getCallback() {
		return callback;
	}
	
	public int getSchematicsTotal() {
		return paths.length;
	}
	
	public int getSchematicsRemaining() {
		return getSchematicsTotal() - index;
	}
	
	public boolean isRunning() {
		return taskId != -1;
	}
	
	public void schedule(long timePerSchematic) {
		if(!isRunning()) {
			if(callback != null) callback.onRestoreSchematicStart(this);
			taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, timePerSchematic);
		}
	}
	
	public void cancel() {
		if(isRunning()) {
			plugin.getServer().getScheduler().cancelTask(taskId);
			taskId = -1;
			if(callback != null) {
				if(getSchematicsRemaining() == 0) {
					callback.onRestoreSchematicFinish(this);
				}
				else {
					callback.onRestoreSchematicCancel(this);
				}
			}
		}
	}

	@Override
	public void run() {
		if(getSchematicsRemaining() == 0) {
			cancel();
		}
		else {
			File file = new File(root, paths[index]);
			try {
				CuboidClipboard cc = CuboidClipboard.loadSchematic(file);
				cc.place(new EditSession(localWorld, maxBlocks), cc.getOrigin(), false);
				
			} catch (DataException e) {
				plugin.getLogger().warning("Failed to load schematic \"" + file.getPath() + "\": " + e.getMessage());
			} catch (IOException e) {
				plugin.getLogger().warning("Failed to load schematic \"" + file.getPath() + "\": " + e.getMessage());
			} catch (MaxChangedBlocksException e) {
				plugin.getLogger().warning("Failed to place schematic \"" + file.getPath() + "\": " + e.getMessage());
			}
			index++;
		}
	}

}
