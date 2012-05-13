package com.mtihc.minecraft.hungergame.game;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Location;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.snapshots.InvalidSnapshotException;
import com.sk89q.worldedit.snapshots.Snapshot;

public interface GameInfo {

	/**
	 * 
	 * @return The game's id
	 */
	String getId();

	/**
	 * 
	 * @return The game's main locatin
	 */
	Location getLocation();

	File getDirectory();
	
	Snapshot getSnapshot(String name) throws InvalidSnapshotException;
	Snapshot getRandomSnapshot() throws InvalidSnapshotException;
	String[] getSnapshotNames();
	
	CuboidClipboard getSchematic(String folder, String name) throws DataException, IOException;
	File getSchematicsRoot();
	String[] getSchematicNames(String folder);
	String[] getSchematicFolders();
	
	/**
	 * The game's region id
	 * 
	 * @return The game's region id
	 */
	String getRegion();

	/**
	 * 
	 * @return The size of the subregions when a large region is restored using
	 *         a snapshot.
	 */
	int getRestoreStepSize();

	/**
	 * 
	 * @return The delay before the next subregion is restored, when restoring a
	 *         large region.
	 */
	long getRestoreStepTime();

	boolean hasSpawnLocations();

	/**
	 * Returns the game's spawn locations. Or the game's main location, if there
	 * are none.
	 * 
	 * @return The game's spawn locations
	 */
	List<Location> getSpawnLocations();

	/**
	 * The amount of seconds that players are invulnerable at the beginning of
	 * the game.
	 * 
	 * @return The amount of seconds that players are invulnerable.
	 */
	int getInvulnerabilitySeconds();

	/**
	 * 
	 * @return The amount of seconds before the feast starts.
	 */
	int getFeastDelaySeconds();

	int getReconnectSeconds();

	GameFeast getStartFeast();

	GameFeast getDelayedFeast();

}