package com.mtihc.minecraft.hungergame.tasks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.data.ChunkStore;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.snapshots.SnapshotRestore;

public class RestoreRegionTask implements Runnable {

	private long MAX_TICKS = 60L;
	private long MIN_TICKS = 2L;
	
	private final JavaPlugin plugin;
	private final ChunkStore chunkStore;
	private final List<CuboidRegion> regions;
	private final long ticksPerRegion;
	private final long estimatedTicks;
	private final RestoreRegionCallback callback;
	private int taskId;
	private Iterator<CuboidRegion> iterator;

	/**
	 * Constructor
	 * 
	 * @param plugin
	 *            The plugin that is used for scheduling the task
	 * @param chunkStore
	 *            The <code>ChunkStore</code>, usually retrieved from a
	 *            <code>Snapshot</code>
	 * @param regions
	 *            The regions to restore using the chunkStore
	 * @param ticksPerRegion
	 *            How many ticks until the next region is restored
	 * @param restoreRegionCallback
	 *            Will be notified about task progress
	 */
	public RestoreRegionTask(JavaPlugin plugin, ChunkStore chunkStore,
			List<CuboidRegion> regions, long ticksPerRegion,
			RestoreRegionCallback restoreRegionCallback) {
		this.plugin = plugin;
		this.chunkStore = chunkStore;
		this.regions = regions;
		
		if(ticksPerRegion < MIN_TICKS) {
			this.ticksPerRegion = MIN_TICKS;
		}
		else if(ticksPerRegion > MAX_TICKS) {
			this.ticksPerRegion = MAX_TICKS;
		}
		else {
			this.ticksPerRegion = ticksPerRegion;
		}
		this.estimatedTicks = this.ticksPerRegion * regions.size();
		this.callback = restoreRegionCallback;
		this.taskId = -1;
	}

	/**
	 * Whether the task is currently running
	 * 
	 * @return true if it's running, false otherwise
	 */
	public boolean isRunning() {
		return taskId != -1;
	}

	public long getEstimatedTicks() {
		return estimatedTicks;
	}

	public String getEstimatedTimeString() {
		int seconds = (int) (estimatedTicks / 20);
		int sec = seconds % 60;
		int min = seconds / 60;

		if (min > 0) {
			return min + "min " + sec + "sec";
		} else {
			return sec + "sec";
		}

	}

	/**
	 * Start restoring the regions, at a certain speed
	 * 
	 * 
	 */
	public void schedule() {
		if (taskId == -1) {
			if (callback != null) {
				callback.onRestoreStart(this);
			}
			iterator = regions.iterator();
			taskId = plugin.getServer().getScheduler()
			.scheduleSyncRepeatingTask(plugin, this, 0, ticksPerRegion);
		}
	}

	/**
	 * Stop restoring the regions
	 */
	public void cancel() {
		if (cancelTask()) {
			if (callback != null) {
				callback.onRestoreCancel(this);
			}
		}
	}

	private boolean cancelTask() {
		if (taskId != -1) {
			plugin.getServer().getScheduler().cancelTask(taskId);
			taskId = -1;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void run() {
		if (!iterator.hasNext()) {
			// no more regions in the list

			// stop the task at scheduler
			cancelTask();

			// callback
			if (callback != null) {
				callback.onRestoreFinish(this);
			}

		} else {
			// get next region from the list
			CuboidRegion region = iterator.next();

			// restore the region
			RestoreRegionTask.restoreRegionInstantly(chunkStore, region);
		}
	}

	/**
	 * Restore a region
	 * 
	 * @param chunkStore
	 *            The <code>ChunkStore</code>, usually retrieved from a
	 *            <code>Snapshot</code>
	 * @param region
	 *            The region to restore
	 */
	public static void restoreRegionInstantly(ChunkStore chunkStore,
			Region region) {
		SnapshotRestore restore = new SnapshotRestore(chunkStore, region);
		try {
			restore.restore(new EditSession(region.getWorld(), -1));
		} catch (MaxChangedBlocksException e) {
		}
	}

	/**
	 * Divide a region in sub-regions of the specified size.
	 * 
	 * @param region
	 *            The region to divide
	 * @param subRegionSize
	 *            The size of the sub-regions
	 * @return List of sub-regions
	 */
	public static List<CuboidRegion> getSubRegions(CuboidRegion region,
			int subRegionSize) {
		// create result list
		List<CuboidRegion> regions = new ArrayList<CuboidRegion>();

		// get min/max points
		Vector min = region.getMinimumPoint();
		Vector max = region.getMaximumPoint();

		// starting coordinates
		int x = min.getBlockX();
		int y = min.getBlockY();
		int z = min.getBlockZ();

		// loop over coordinates with big steps, to
		// create subregions
		for (int i = x; i < max.getBlockX(); i += subRegionSize) {
			for (int j = y; j < max.getBlockY(); j += subRegionSize) {
				for (int k = z; k < max.getBlockZ(); k += subRegionSize) {
					regions.add(new CuboidRegion(region.getWorld(), new Vector(
							i, j, k), new Vector(i + subRegionSize, j
									+ subRegionSize, k + subRegionSize)));
				}
			}
		}

		return regions;
	}
}