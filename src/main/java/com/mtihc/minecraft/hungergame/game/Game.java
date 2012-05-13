package com.mtihc.minecraft.hungergame.game;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.mtihc.minecraft.hungergame.game.exceptions.GameException;
import com.mtihc.minecraft.hungergame.game.exceptions.GameJoinDisabledException;
import com.mtihc.minecraft.hungergame.game.exceptions.GameJoinedException;
import com.mtihc.minecraft.hungergame.game.exceptions.GameRunningException;
import com.mtihc.minecraft.hungergame.player.PlayerState;
import com.mtihc.minecraft.hungergame.player.fields.DefaultPlayerField;
import com.mtihc.minecraft.hungergame.tasks.CountdownCallback;
import com.mtihc.minecraft.hungergame.tasks.CountdownTask;
import com.mtihc.minecraft.hungergame.tasks.RestoreRegionCallback;
import com.mtihc.minecraft.hungergame.tasks.RestoreRegionTask;
import com.mtihc.minecraft.hungergame.tasks.RestoreSchematicCallback;
import com.mtihc.minecraft.hungergame.tasks.RestoreSchematicTask;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.data.ChunkStore;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.snapshots.Snapshot;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Game implements CountdownCallback, RestoreRegionCallback, RestoreSchematicCallback {

	public enum LeaveReason {
		KICK, QUIT, DEATH, GAME_STOP, WIN, LEAVE;
	}

	private final GameControl control;
	private final GameInfo info;
	private final LocalWorld localWorld;
	private final ProtectedRegion region;

	private boolean running;

	private final File directory;

	private boolean joiningEnabled;
	private boolean invulnerabilityEnabled;

	private final Map<String, GamePlayer> players;
	private final Map<String, GamePlayer> playersDead;
	private final Map<String, ReconnectTimer> playersOffline;

	private GameFeast feast;

	private RestoreRegionTask taskRestore;
	private RestoreSchematicTask taskRestoreSchematics;
	private CountdownTask taskDisableJoin;
	private FeastTask taskFeast;
	private CountdownTask taskInvul;

	public Game(String id, GameControl control) throws GameException {
		this.control = control;
		info = control.getGameInfoCollection().getInfo(id);
		if(info == null) {
			throw new GameException("The game yml file does not exist.");
		}

		directory = info.getDirectory();
		
		if(!directory.exists()) {
			throw new GameException("The game folder does not exist: " + directory.getPath());
		}
		
		World world = info.getLocation().getWorld();
		
		// find local world
		Iterator<LocalWorld> worlds = control.getWorldEdit().getWorldEdit().getServer().getWorlds().iterator();
		LocalWorld w = null;
		while(worlds.hasNext()) {
			LocalWorld next = worlds.next();
			if(next.getName().equals(world.getName())) {
				w = next;
				break;
			}
		}
		
		if(w == null) {
			throw new GameException("World \"" + world.getName() + "\" not found at WorldEdit.");
		}
		else {
			this.localWorld = w;
		}


		RegionManager mgr = control.getWorldGuard().getRegionManager(world);
		this.region = mgr.getRegion(info.getRegion());

		
		if (region == null) {
			throw new GameException("Region \"" + info.getRegion()
					+ "\" doesn't exist in world \"" + world.getName() + "\".");
		}
		
		// players can't enter or leave the area
		region.setFlag(DefaultFlag.ENTRY, State.DENY);
		region.setFlag(DefaultFlag.EXIT, State.DENY);
		

		this.joiningEnabled = false;
		this.invulnerabilityEnabled = false;

		this.players = new HashMap<String, GamePlayer>();
		this.playersDead = new LinkedHashMap<String, GamePlayer>();
		this.playersOffline = new HashMap<String, ReconnectTimer>();

		this.control.setGame(info.getId(), this);
	}
	
	/**
	 * Creates a player state object that specified which player informations is
	 * to be saved.
	 * 
	 * @param player
	 *            The player to save the state of
	 * @return The player state object
	 */
	protected PlayerState createPlayerState(Player player) {
		return PlayerState.fromPlayer(player, DefaultPlayerField.values());
	}

	/**
	 * Whether the game is currently running
	 * 
	 * @return true if the game is running, false otherwise
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * The game id
	 * 
	 * @return Game id
	 */
	public String getId() {
		return info.getId();
	}

	/**
	 * Single instance that serves as observer for this game. But is also
	 * observed by the game for bukkit events.
	 * 
	 * @return Game game control
	 */
	public GameControl getControl() {
		return control;
	}

	/**
	 * The game info
	 * 
	 * @return Game info
	 */
	public GameInfo getInfo() {
		return info;
	}

	/**
	 * The game's spawn locations. If there are no spawn locations, a list
	 * containing the main location is returned.
	 * 
	 * @return List containing spawn locations
	 */
	public List<Location> getSpawnLocations() {
		List<Location> result = info.getSpawnLocations();
		if (result == null || result.isEmpty()) {
			result = new ArrayList<Location>();
			result.add(info.getLocation());
			return result;
		} else {
			return result;
		}
	}

	// -----------------------------
	//
	// Observers
	//
	// -----------------------------

	// public boolean hasObserver(Observer observer) {
	// return observers.contains(observer);
	// }
	//
	// public boolean addObserver(Observer observer) {
	// return observers.add(observer);
	// }
	//
	// public boolean removeObserver(Observer observer) {
	// return observers.remove(observer);
	// }
	//
	// public Iterator<Observer> getObservers() {
	// return observers.iterator();
	// }

	// -----------------------------
	//
	// Region / Snapshot
	//
	// -----------------------------

	public boolean containsLocation(Location location) {
		return location.getWorld().getName().equalsIgnoreCase(localWorld.getName())
		&& containsLocation(location.getBlockX(), location.getBlockY(),
				location.getBlockZ());
	}

	public boolean containsLocation(int x, int y, int z) {
		return region.contains(x, y, z);
	}

	/**
	 * The game region
	 * 
	 * @return Game region
	 */
	public ProtectedRegion getRegion() {
		return region;
	}
	
	/**
	 * Whether the region is currently being restored
	 * 
	 * @return true if the restore task is running, false otherwise
	 */
	public boolean isRegionRestoring() {
		return taskRestore != null && taskRestore.isRunning();
	}

	protected void scheduleRestoreRegion(Snapshot snapshot,
			RestoreRegionCallback restoreRegionCallback)
	throws IOException, GameException {
		if (taskRestore != null && taskRestore.isRunning()) {
			throw new GameException("Region is already being restored.");
		}

		// get chunk store from snapshot
		ChunkStore chunkStore;
		try {
			chunkStore = snapshot.getChunkStore();
		} catch (DataException e) {
			throw new GameException(e);
		}
		
		// create sub regions of the game region
		
		CuboidRegion cr = new CuboidRegion(localWorld, region.getMinimumPoint(),
				region.getMaximumPoint());
		List<CuboidRegion> subRegions = RestoreRegionTask.getSubRegions(cr,
				info.getRestoreStepSize());

		// create and return restore task
		taskRestore = new RestoreRegionTask(control.getPlugin(), chunkStore,
				subRegions, info.getRestoreStepTime(), restoreRegionCallback);
		taskRestore.schedule();
	}
	
	
	
	

	// -----------------------------
	//
	// Invulnerability
	//
	// -----------------------------

	public boolean isInvulnerabilityEnabled() {
		return invulnerabilityEnabled;
	}

	public void setInvulnerabilityEnabled(boolean value) {
		invulnerabilityEnabled = value;
	}

	protected void scheduleInvulnerabilityDisable() {
		taskInvul = new CountdownTask(control.getPlugin(), info.getInvulnerabilitySeconds(), this);
		taskInvul.schedule();
	}

	// -----------------------------
	//
	// Joining
	//
	// -----------------------------

	public boolean isJoiningEnabled() {
		return joiningEnabled;
	}

	protected void setJoiningEnabled(boolean value) {
		joiningEnabled = value;
	}

	protected void scheduleJoinDisable(int disableAfterSeconds) {
		taskDisableJoin = new CountdownTask(control.getPlugin(), disableAfterSeconds,
				this);
		taskDisableJoin.schedule();
	}
	
	// -----------------------------
	//
	// Feast
	//
	// -----------------------------

	public GameFeast getFeast() {
		return feast;
	}

	public boolean hasFeast() {
		return feast != null;
	}
	
	public boolean isFeastScheduled() {
		return taskFeast != null && taskFeast.isRunning();
	}
	
	public int getFeastRemainingSeconds() {
		if(!hasFeast()) {
			return 0;
		}
		else {
			if(isFeastScheduled()) {
				return taskFeast.getSecondsRemaining();
			}
			else {
				return 0;
			}
		}
	}

	protected void setFeast(GameFeast value) {
		if (this.feast != value) {
			clearFeast();
			this.feast = value;
		}
	}

	private void clearFeast() {
		Iterator<GamePlayer> values = players.values().iterator();
		while (values.hasNext()) {
			GamePlayer p = values.next();
			p.setFeastInventory(null);
		}
		this.feast = null;
	}

	protected void scheduleFeast(GameFeast feast)
	throws GameException {
		if (isFeastScheduled()) {
			throw new GameException("Feast is already scheduled.");
		}
		taskFeast = new FeastTask(feast);
		taskFeast.schedule();
	}
	
	private class FeastTask extends CountdownTask {

		private GameFeast feast;

		private FeastTask(GameFeast feast) {
			super(control.getPlugin(), info.getFeastDelaySeconds(), Game.this);
			this.feast = feast;
		}
	}

	// -----------------------------
	// on Feast click
	// -----------------------------

	protected Inventory createFeastInventory(GameFeast feast) {
		Inventory result = control.getPlugin().getServer()
		.createInventory(feast.getHolder(), feast.getSize(), "Feast");
		result.setContents(feast.getContents());
		return result;
	}

	protected boolean isFeastAt(GameFeast feast, Location location) {
		InventoryHolder holder = feast.getHolder();
		if (holder instanceof DoubleChest) {
			DoubleChest dchest = (DoubleChest) holder;
			Location leftLoc = ((Chest) dchest.getLeftSide()).getLocation();
			Location rightLoc = ((Chest) dchest.getRightSide()).getLocation();
			return locationsAreEqual(leftLoc, location)
			|| locationsAreEqual(rightLoc, location);

		} else {
			return locationsAreEqual(feast.getLocation(), location);
		}
	}

	protected boolean locationsAreEqual(Location a, Location b) {
		return a.getWorld() == b.getWorld() && a.getBlockX() == b.getBlockX()
		&& a.getBlockY() == b.getBlockY()
		&& a.getBlockZ() == b.getBlockZ();
	}

	// -----------------------------
	//
	// Players / Dead Players
	//
	// -----------------------------

	public int getTotalPlayers() {
		return players.size() + playersDead.size();
	}

	public int getTotalAlivePlayers() {
		return players.size();
	}

	public int getTotalDeadPlayers() {
		return playersDead.size();
	}

	public boolean hasPlayer(String name) {
		return players.containsKey(name);
	}

	public boolean hasDeadPlayer(String name) {
		return playersDead.containsKey(name);
	}

	public GamePlayer getPlayer(String name) {
		return players.get(name);
	}

	public GamePlayer getDeadPlayer(String name) {
		return playersDead.get(name);
	}

	public Collection<GamePlayer> getPlayers() {
		return players.values();
	}

	public Collection<GamePlayer> getDeadPlayers() {
		return playersDead.values();
	}
	
	private void safeTeleport(GamePlayer player, Location spawn) {
		//control.getWorldEdit().wrapPlayer(player.getPlayer()).findFreePosition(new WorldVector(localWorld, spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ()));
		player.teleportSafe(spawn);
	}

	// -----------------------------
	//
	// start / stop
	//
	// -----------------------------

	private int startAfterSeconds;
	private boolean started;

	public void openAfterRestore(int startAfterSeconds, Snapshot snapshot)
	throws IOException, GameException {
		if (running) {
			throw new GameRunningException("Game \"" + getId()
					+ "\" is already running.");
		}
		running = true;
		this.startAfterSeconds = startAfterSeconds;
		scheduleRestoreRegion(snapshot, this);
	}

	public void open(int startAfterSeconds) throws GameRunningException {
		if (running) {
			throw new GameRunningException("Game \"" + getId()
					+ "\" is already running.");
		}
		running = true;
		this.startAfterSeconds = startAfterSeconds;
		doOpen();

	}

	public void start() throws GameRunningException {
		if (running) {
			throw new GameRunningException("Game \"" + getId()
					+ "\" is already running.");
		}
		running = true;
		this.startAfterSeconds = 0;
		doStart();
	}

	private void doOpen() {

		scheduleJoinDisable(this.startAfterSeconds);

	}
	
	public boolean isStarted() {
		return started;
	}

	private void doStart() {

		if (getTotalAlivePlayers() == 0) {
			try {
				stop();
			} catch (GameRunningException e) {
			}
			return;
		}

		// players that went offline are kicked
		Iterator<ReconnectTimer> offlines = playersOffline.values().iterator();
		while (offlines.hasNext()) {
			ReconnectTimer timer = offlines.next();
			timer.run();
		}

		// joining is disabled if it wasn't already
		if (taskDisableJoin != null && taskDisableJoin.isRunning()) {
			taskDisableJoin.cancel();
		}
		setJoiningEnabled(false);

		// restoring of the region is cancelled if it's still running
		if (taskRestore != null && taskRestore.isRunning()) {
			taskRestore.cancel();
		}

		List<Location> locations = getSpawnLocations();
		int index = 0;

		Iterator<GamePlayer> iterator = getPlayers().iterator();
		while (iterator.hasNext()) {
			GamePlayer p = iterator.next();

			// player get sperate spawn locations as much as possible (using
			// modulo operator)
			Location spawn = locations.get(index % locations.size());
			index++;

			// save the state that the player is currently in 
			// (like health, inventory etc)
			// so that it can be loaded again, when the player leaves the game
			p.setOriginalState(createPlayerState(p.getPlayer()));


			// add players as member to region...
			// player can't leave region anymore
			region.getMembers().addPlayer(p.getName());
			
			// teleport the player to his spawn location
			safeTeleport(p, spawn);
			

			// TODO set player's values, possibly 
			// according to player's class/job
			control.getObserver().onPlayerSpawn(this, p.getPlayer(), spawn);
		}

		// set the start feast
		setFeast(info.getStartFeast());

		started = true;
		
		// notify observer
		control.getObserver().onGameStart(this);

		// schedule the big feast
		try {
			scheduleFeast(info.getDelayedFeast());
		} catch (GameException e) {
			control.getPlugin().getLogger().warning(e.getMessage());
		}

		// players are invulnerable for some amount of time
		scheduleInvulnerabilityDisable();
	}

	public void stop() throws GameRunningException {
		if (!running) {
			throw new GameRunningException("Game \"" + getId()
					+ "\" is already running.");
		}

		// cancel all running tasks
		if (taskDisableJoin != null) {
			taskDisableJoin.cancel();
		}
		if (taskFeast != null) {
			taskFeast.cancel();
		}
		if (taskInvul != null) {
			taskInvul.cancel();
		}
		if (taskRestore != null) {
			taskRestore.cancel();
		}

		// kick offline players
		Iterator<ReconnectTimer> offlines = playersOffline.values().iterator();
		while (offlines.hasNext()) {
			ReconnectTimer timer = offlines.next();
			timer.run();
		}

		// kick alive players (move to dead players)
		Iterator<GamePlayer> iterator = players.values().iterator();
		while (iterator.hasNext()) {
			GamePlayer p = iterator.next();
			try {
				leavePlayer(p.getName(), LeaveReason.GAME_STOP);
			} catch (GameJoinedException e) {
				control.getPlugin().getLogger().warning("Failed to properly kick player " + p.getName() + " from game \"" + getId() + "\".");
			}

		}

		running = false;
		

		// notify observer
		control.getObserver().onGameStop(this);

		
		control.removeGame(getId());
	}

	private void onPlayerRemove(GamePlayer p, LeaveReason leaveReason) {

		// remove player as member from region...
		// player can't enter region anymore
		region.getMembers().removePlayer(p.getName());
		
		PlayerState state = p.getOriginalState();
		if(state != null) {
			PlayerState.toPlayer(state, p.getPlayer());
		}

		control.getObserver().onPlayerLeave(this, p.getPlayer(), leaveReason);
	}

	// -----------------------------
	//
	// join / leave
	//
	// -----------------------------

	/**
	 * 
	 * @param player
	 * @throws GameJoinDisabledException
	 *             When joining is disabled
	 * @throws GameJoinedException
	 *             When already joined
	 */
	public void joinPlayer(Player player) throws GameJoinDisabledException,
	GameJoinedException {
		if (!joiningEnabled) {
			throw new GameJoinDisabledException(
					"Joining is disabled for game \"" + getId() + "\".");
		}
		if (hasPlayer(player.getName())) {
			throw new GameJoinedException("Already joined to game \"" + getId()
					+ "\".");
		}
		GamePlayer p = new GamePlayer(player);
		players.put(player.getName(), p);

		control.getObserver().onPlayerJoin(this, player);
	}

	/**
	 * 
	 * @param name
	 * @param leaveReason
	 * @throws GameJoinedException
	 *             When not joined
	 */
	public void leavePlayer(String name, LeaveReason leaveReason)
	throws GameJoinedException {
		GamePlayer p = players.remove(name);
		if (p == null) {
			throw new GameJoinedException("Not joined to game \"" + getId()
					+ "\".");
		}

		playersDead.put(name, p);

		onPlayerRemove(p, leaveReason);

		// check win and stop
		if(!leaveReason.equals(LeaveReason.GAME_STOP)) {
			int alive = getTotalAlivePlayers();
			if (alive == 1) {

				Iterator<GamePlayer> ps = players.values().iterator();
				GamePlayer winner = ps.next();

				leavePlayer(winner.getName(), LeaveReason.WIN);
			} else if (alive == 0) {
				try {
					stop();
				} catch (GameRunningException e) {
					e.printStackTrace();
				}
			}
		}
		

	}

	// -----------------------------
	//
	// Event handlers
	//
	// -----------------------------

	protected void onPlayerDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();
		if (hasPlayer(p.getName())) {
			
			try {
				leavePlayer(p.getName(), LeaveReason.DEATH);
				p.kickPlayer(event.getDeathMessage());
			} catch (GameJoinedException e) {

			}
		}
	}

	protected void onPlayerTeleport(PlayerTeleportEvent event) {
		if (isStarted() && hasPlayer(event.getPlayer().getName())
				&& !containsLocation(event.getTo())) {
			event.setCancelled(true);
		}
	}

	protected void onPlayerJoin(PlayerJoinEvent event) {
		// if player disconnected a minute ago, add back to game
		ReconnectTimer task = playersOffline
		.remove(event.getPlayer().getName());
		if (task != null) {
			task.cancel();
		}
	}

	protected void onPlayerQuit(PlayerQuitEvent event) {
		GamePlayer p = players.remove(event.getPlayer().getName());
		if (p == null) {
			return;
		}
		ReconnectTimer task = new ReconnectTimer(p);
		task.schedule();
	}

	protected void onPlayerKick(PlayerKickEvent event) {
		try {
			leavePlayer(event.getPlayer().getName(), LeaveReason.KICK);
		} catch (GameJoinedException e) {

		}
	}

	protected void onPlayerRespawn(PlayerRespawnEvent event) {
		// not used
	}

	protected void onPlayerDamage(EntityDamageByEntityEvent event) {
		if (invulnerabilityEnabled && event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (players.containsKey(p.getName())) {
				event.setDamage(0);
			}
		}
	}

	protected void onPlayerInteract(PlayerInteractEvent event) {
		if(event.isCancelled() || event.useInteractedBlock().equals(Result.DENY)) {
			return;
		}
		GamePlayer p = getPlayer(event.getPlayer().getName());
		if (p == null) {
			return;
		}

		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && hasFeast()
				&& isFeastAt(getFeast(), event.getClickedBlock().getLocation())) {
			if (!p.hasFeastInventory()) {
				p.setFeastInventory(createFeastInventory(getFeast()));
			}
			p.getPlayer().openInventory(p.getFeastInventory());
			
			event.setUseInteractedBlock(Result.DENY);
			event.setCancelled(true);
		}

	}

	public boolean isBlockProtected(Block block) {
		return hasFeast() && isFeastAt(feast, block.getLocation());
	}

	protected void onBlockBreak(BlockBreakEvent event) {
		if (isBlockProtected(event.getBlock())) {
			event.setCancelled(true);
		}
	}

	protected void onBlockIgnite(BlockIgniteEvent event) {
		if (isBlockProtected(event.getBlock())) {
			event.setCancelled(true);
		}
	}

	protected void onEntityExplode(EntityExplodeEvent event) {
		Iterator<Block> values = event.blockList().iterator();
		while (values.hasNext()) {
			Block block = values.next();
			if (isBlockProtected(block)) {
				values.remove();
			}
		}
	}

	// -----------------------------
	//
	// Reconnect timer
	//
	// -----------------------------

	private class ReconnectTimer implements Runnable {

		private final GamePlayer player;
		private int taskId;

		private ReconnectTimer(GamePlayer player) {
			this.player = player;
			taskId = -1;
		}

		private void cancel() {
			if (taskId != -1) {
				control.getPlugin().getServer().getScheduler()
				.cancelTask(taskId);
				taskId = -1;
				if (playersOffline.remove(player.getName()) != null) {
					players.put(player.getName(), player);
				}
			}
		}

		private void schedule() {
			if (taskId == -1) {
				playersOffline.put(player.getName(), this);
				taskId = control
				.getPlugin()
				.getServer()
				.getScheduler()
				.scheduleAsyncDelayedTask(control.getPlugin(), this,
						info.getReconnectSeconds() * 20L);
			}
		}

		@Override
		public void run() {
			cancel();
			try {
				leavePlayer(player.getName(), LeaveReason.QUIT);
			} catch (GameJoinedException e) {
			}
		}

	}

	@Override
	public void onRestoreStart(RestoreRegionTask task) {
		control.getObserver().onRestoreStart(this);
	}

	@Override
	public void onRestoreCancel(RestoreRegionTask task) {
		control.getPlugin().getLogger()
		.warning("Restoring of region \"" + getId() + "\" cancelled.");
		onRestoreFinish(task);
	}

	@Override
	public void onRestoreFinish(RestoreRegionTask task) {
		String[] folders = info.getSchematicFolders();
		List<String> paths = new ArrayList<String>();
		Random r = new Random();
		for (int i = 0; i < folders.length; i++) {
			String[] names = info.getSchematicNames(folders[i]);
			if(names == null || names.length == 0) {
				continue;
			}
			paths.add(folders[i] + "/" + names[r.nextInt(names.length)]);
		}
		if(paths.isEmpty()) {
			onRestoreFinish();
		}
		else {
			taskRestoreSchematics = new RestoreSchematicTask(control.getPlugin(), this, info.getSchematicsRoot(), paths.toArray(new String[paths.size()]), localWorld, -1);
			taskRestoreSchematics.schedule(info.getRestoreStepTime());
		}
	}

	@Override
	public void onRestoreSchematicStart(RestoreSchematicTask task) {
		
	}

	@Override
	public void onRestoreSchematicCancel(RestoreSchematicTask task) {
		onRestoreSchematicFinish(task);
	}

	@Override
	public void onRestoreSchematicFinish(RestoreSchematicTask task) {
		onRestoreFinish();
	}
	
	private void onRestoreFinish() {
		control.getObserver().onRestoreFinish(this);
		doOpen();
	}

	private void placeChest(Block block) {
		if(block.getState() instanceof InventoryHolder) {
			((InventoryHolder)block.getState()).getInventory().clear();
		}
		else {
			block.setType(Material.CHEST);
		}
	}

	@Override
	public void onCountdownStart(CountdownTask task, int totalSeconds) {
		if(task == taskDisableJoin) {
			setJoiningEnabled(true);
			control.getObserver().onJoinEnable(this);
		}
		else if(task == taskFeast) {
			control.getObserver().onFeastSchedule(this, totalSeconds);
		}
		else if(task == taskInvul) {
			setInvulnerabilityEnabled(true);
			control.getObserver().onInvulnerabilityStart(this, totalSeconds);
		}
	}

	@Override
	public void onCountdownWarning(CountdownTask task, int totalSeconds,
			int remaining) {
		if(task == taskDisableJoin) {
			control.getObserver().onJoinDisableWarning(this, totalSeconds, remaining);
		}
		else if(task == taskFeast) {
			control.getObserver().onFeastWarning(this, totalSeconds, remaining);
		}
		else if(task == taskInvul) {
			control.getObserver().onInvulnerabilityWarning(this, totalSeconds, remaining);
		}
	}

	@Override
	public void onCountdown(CountdownTask task, int totalSeconds, int remaining) {
		if(task == taskDisableJoin) {
			control.getObserver().onJoinDisableCountdown(this, totalSeconds, remaining);
		}
		else if(task == taskFeast) {
			control.getObserver().onFeastCountdown(this, totalSeconds, remaining);
		}
		else if(task == taskInvul) {
			control.getObserver()
			.onInvulnerabilityCountdown(this, totalSeconds, remaining);
		}
	}

	@Override
	public void onCountdownCancel(CountdownTask task, int totalSeconds,
			int remaining) {
		if(task == taskDisableJoin) {
			setJoiningEnabled(false);
			control.getObserver().onJoinDisable(this);
		}
		else if(task == taskFeast) {
			
		}
		else if(task == taskInvul) {
			setInvulnerabilityEnabled(false);
			control.getObserver().onInvulnerabilityStop(this, totalSeconds, remaining);
		}
	}

	@Override
	public void onCountdownFinish(CountdownTask task, int totalSeconds) {
		if(task == taskDisableJoin) {
			setJoiningEnabled(false);
			doStart();
			control.getObserver().onJoinDisable(this);
		}
		else if(task == taskFeast) {
			setFeast(taskFeast.feast);
			control.getObserver().onFeast(this);
			InventoryHolder holder = taskFeast.feast.getHolder();
			if(holder instanceof DoubleChest) {
				Block leftBlock = ((BlockState)((DoubleChest)holder).getLeftSide()).getBlock();
				Block rightBlock = ((BlockState)((DoubleChest)holder).getRightSide()).getBlock();
				placeChest(leftBlock);
				placeChest(rightBlock);
			}
			else {
				Block block = ((BlockState)holder).getBlock();
				placeChest(block);
			}
		}
		else if(task == taskInvul) {
			setInvulnerabilityEnabled(false);
			control.getObserver().onInvulnerabilityStop(this, totalSeconds, 0);
		}
		
	}
}
