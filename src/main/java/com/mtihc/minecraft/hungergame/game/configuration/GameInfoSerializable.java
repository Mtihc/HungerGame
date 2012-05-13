package com.mtihc.minecraft.hungergame.game.configuration;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.mtihc.minecraft.hungergame.game.GameFeast;
import com.mtihc.minecraft.hungergame.game.GameInfo;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.snapshots.InvalidSnapshotException;
import com.sk89q.worldedit.snapshots.Snapshot;
import com.sk89q.worldedit.snapshots.SnapshotRepository;

public class GameInfoSerializable implements ConfigurationSerializable, GameInfo {

	private final String id;
	private Location location;
	private String region;
	private int restoreStepSize;
	private long restoreStepTime;
	private List<Location> spawnLocations;
	private int invulnerabilitySeconds;
	private int feastDelaySeconds;
	private int reconnectSeconds;

	private GameFeast startFeast;
	private GameFeast delayedFeast;
	
	private File directory;
	private SnapshotRepository snapshotRepo;
	private File schematics;

	
	public GameInfoSerializable(File dataFolder, String id, Location location, String region) {
		this(dataFolder, id, location, region, 120, 300);
	}

	public GameInfoSerializable(File dataFolder, String id, Location location, String region,
			int invulnerabilitySeconds, int feastDelaySeconds) {
		this(dataFolder, id, location, region, null, null, null, invulnerabilitySeconds,
				feastDelaySeconds);
	}

	public GameInfoSerializable(File gameFolder, String id, Location location, String region,
			GameFeast startFeast, GameFeast delayedFeast,
			List<Location> spawnLocations, int invulnerabilitySeconds,
			int feastDelaySeconds) {
		this.id = id;
		this.location = location;
		this.region = region;
		this.startFeast = startFeast;
		this.delayedFeast = delayedFeast;
		if (spawnLocations != null && !spawnLocations.isEmpty()) {
			this.spawnLocations = spawnLocations;
		} else {
			this.spawnLocations = new ArrayList<Location>();
		}
		this.invulnerabilitySeconds = invulnerabilitySeconds;
		this.feastDelaySeconds = feastDelaySeconds;
		this.reconnectSeconds = 30;
		this.restoreStepSize = 50;
		this.restoreStepTime = 10;
		
		this.directory = new File(gameFolder, id);
		initialize();
	}
	
	private void initialize() {
		this.snapshotRepo = new SnapshotRepository(new File(directory, "snapshots"));
		try {
			this.snapshotRepo.getDirectory().createNewFile();
		} catch (IOException e) {
			
		}
		
		this.schematics = new File(directory, "schematics");
		try {
			this.schematics.createNewFile();
		} catch (IOException e) {
			
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mtihc.minecraft.hungergame.core.GameInfo#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mtihc.minecraft.hungergame.core.GameInfo#getLocation()
	 */
	@Override
	public Location getLocation() {
		return location.clone();
	}

	public void setLocation(Location value) {
		this.location = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mtihc.minecraft.hungergame.core.GameInfo#getRegion()
	 */
	@Override
	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mtihc.minecraft.hungergame.core.GameInfo#getRestoreStepSize()
	 */
	@Override
	public int getRestoreStepSize() {
		return restoreStepSize;
	}

	public void setRestoreStepSize(int value) {
		this.restoreStepSize = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mtihc.minecraft.hungergame.core.GameInfo#getRestoreStepTime()
	 */
	@Override
	public long getRestoreStepTime() {
		return restoreStepTime;
	}

	public void setRestoreStepTime(long value) {
		this.restoreStepTime = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mtihc.minecraft.hungergame.core.GameInfo#hasSpawnLocations()
	 */
	@Override
	public boolean hasSpawnLocations() {
		return spawnLocations != null && !spawnLocations.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mtihc.minecraft.hungergame.core.GameInfo#getSpawnLocations()
	 */
	@Override
	public List<Location> getSpawnLocations() {
		return spawnLocations;
	}

	public void setSpawnLocations(List<Location> locations) {
		this.spawnLocations = locations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mtihc.minecraft.hungergame.core.GameInfo#getInvulnerabilitySeconds()
	 */
	@Override
	public int getInvulnerabilitySeconds() {
		return invulnerabilitySeconds;
	}

	public void setInvulnerabilitySeconds(int value) {
		this.invulnerabilitySeconds = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mtihc.minecraft.hungergame.core.GameInfo#getFeastDelaySeconds()
	 */
	@Override
	public int getFeastDelaySeconds() {
		return feastDelaySeconds;
	}

	public void setFeastDelaySeconds(int value) {
		this.feastDelaySeconds = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mtihc.minecraft.hungergame.core.GameInfo#getReconnectSeconds()
	 */
	@Override
	public int getReconnectSeconds() {
		return reconnectSeconds;
	}

	public void setReconnectSeconds(int value) {
		this.reconnectSeconds = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mtihc.minecraft.hungergame.core.GameInfo#getStartFeast()
	 */
	@Override
	public GameFeast getStartFeast() {
		return startFeast;
	}

	public void setStartFeast(GameFeast value) {
		this.startFeast = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mtihc.minecraft.hungergame.core.GameInfo#getDelayedFeast()
	 */
	@Override
	public GameFeast getDelayedFeast() {
		return delayedFeast;
	}

	public void setDelayedFeast(GameFeast value) {
		this.delayedFeast = value;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("directory", directory.getPath());
		values.put("id", id);
		values.put("location", GameInfoSerializable.convertLocationToMap(location));
		values.put("region", region);
		values.put("restoreStepSize", restoreStepSize);
		values.put("restoreStepTime", restoreStepTime);
		values.put("invulnerabilitySeconds", invulnerabilitySeconds);
		values.put("feastDelaySeconds", feastDelaySeconds);
		values.put("reconnectSeconds", reconnectSeconds);

		if (spawnLocations != null && !spawnLocations.isEmpty()) {
			Map<String, Object> spawnsSection = new HashMap<String, Object>();
			int index = 0;
			for (Location spawn : spawnLocations) {
				spawnsSection.put("spawn" + index, GameInfoSerializable.convertLocationToMap(spawn));
				index++;
			}
			values.put("spawns", spawnsSection);
		}

		values.put("startFeast", startFeast);
		values.put("delayedFeast", delayedFeast);

		return values;
	}
	
	protected GameInfoSerializable(Map<String, Object> values) {
		
		this.id = values.get("id").toString();
		this.directory = new File(values.get("directory").toString());
		this.location = GameInfoSerializable.convertMapToLocation((Map<?, ?>) values
				.get("location"));
		this.region = values.get("region").toString();
		this.invulnerabilitySeconds = Integer.parseInt(values.get(
		"invulnerabilitySeconds").toString());
		this.feastDelaySeconds = Integer.parseInt(values
				.get("feastDelaySeconds").toString());

		this.spawnLocations = new ArrayList<Location>();

		Map<?, ?> spawnsSection = (Map<?, ?>) values.get("spawns");
		if (spawnsSection != null) {
			Set<?> entries = spawnsSection.entrySet();
			for (Object object : entries) {
				Map.Entry<?, ?> entry = (Map.Entry<?, ?>) object;
				Location spawn = GameInfoSerializable.convertMapToLocation((Map<?, ?>) entry
						.getValue());

				spawnLocations.add(spawn);
			}
		}

		this.startFeast = (GameFeast) values.get("startFeast");
		this.delayedFeast = (GameFeast) values.get("delayedFeast");

		
		this.restoreStepSize = Integer.parseInt(values.get("restoreStepSize")
				.toString());
		this.restoreStepTime = Long.parseLong(values.get("restoreStepTime")
				.toString());
		this.reconnectSeconds = Integer.parseInt(values.get(
		"reconnectSeconds").toString());
		
		initialize();
	}

	public static GameInfoSerializable deserialize(Map<String, Object> values) {
		return new GameInfoSerializable(values);
	}

	private static Map<String, Object> convertLocationToMap(Location location) {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		result.put("world", location.getWorld().getName());
		result.put("yaw", location.getYaw());
		result.put("pitch", location.getPitch());
		result.put("x", location.getBlockX());
		result.put("y", location.getBlockY());
		result.put("z", location.getBlockZ());
		return result;
	}

	private static Location convertMapToLocation(Map<?, ?> map) {
		String worldName = map.get("world").toString();
		World world = Bukkit.getWorld(worldName);

		float yaw = Float.parseFloat(map.get("yaw").toString());
		float pitch = Float.parseFloat(map.get("pitch").toString());
		int x = Integer.parseInt(map.get("x").toString());
		int y = Integer.parseInt(map.get("y").toString());
		int z = Integer.parseInt(map.get("z").toString());

		return new Location(world, x, y, z, yaw, pitch);
	}

	@Override
	public File getDirectory() {
		return directory;
	}

	@Override
	public Snapshot getSnapshot(String name) throws InvalidSnapshotException {
		return snapshotRepo.getSnapshot(name);
	}

	@Override
	public Snapshot getRandomSnapshot() throws InvalidSnapshotException {
		String[] names = getSnapshotNames();
		if(names == null || names.length == 0) {
			return null;
		}
		int r = new Random().nextInt(names.length);
		return getSnapshot(names[r]);
	}

	@Override
	public String[] getSnapshotNames() {
		File dir = snapshotRepo.getDirectory();
		return dir.list();
	}

	@Override
	public CuboidClipboard getSchematic(String folder, String name) throws DataException, IOException {
		return CuboidClipboard.loadSchematic(new File(schematics, folder + "/" + name + ".schematic"));
	}

	@Override
	public File getSchematicsRoot() {
		return schematics;
	}

	@Override
	public String[] getSchematicNames(String folder) {
		
		return new File(schematics, folder).list(new FilenameFilter() {
			
			@Override
			public boolean accept(File file, String name) {
				return name.endsWith(".schematic");
			}
		});
	}

	@Override
	public String[] getSchematicFolders() {
		return schematics.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return !name.endsWith(".schematic");
			}
		});
	}

}
