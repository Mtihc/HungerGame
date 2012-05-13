package com.mtihc.minecraft.hungergame.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.minecraft.hungergame.configuration.InventorySerializable;
import com.mtihc.minecraft.hungergame.game.Game;
import com.mtihc.minecraft.hungergame.game.Game.LeaveReason;
import com.mtihc.minecraft.hungergame.game.GameControl;
import com.mtihc.minecraft.hungergame.game.GameInfoCollection;
import com.mtihc.minecraft.hungergame.game.configuration.GameDoubleFeastSerializable;
import com.mtihc.minecraft.hungergame.game.configuration.GameFeastSerializable;
import com.mtihc.minecraft.hungergame.game.configuration.GameInfoSerializable;
import com.mtihc.minecraft.hungergame.game.exceptions.GameException;
import com.mtihc.minecraft.hungergame.game.exceptions.GameJoinedException;
import com.mtihc.minecraft.hungergame.game.exceptions.GameRunningException;
import com.mtihc.minecraft.hungergame.plugin.commands.HungerGameCommand;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.MissingWorldException;
import com.sk89q.worldedit.snapshots.InvalidSnapshotException;
import com.sk89q.worldedit.snapshots.Snapshot;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class HungerGamePlugin extends JavaPlugin {

	private static HungerGamePlugin plugin;

	public static HungerGamePlugin getPlugin() {
		return HungerGamePlugin.plugin;
	}

	static {
		ConfigurationSerialization.registerClass(InventorySerializable.class);
		
		ConfigurationSerialization.registerClass(GameFeastSerializable.class);
		ConfigurationSerialization.registerClass(GameDoubleFeastSerializable.class);
		ConfigurationSerialization.registerClass(GameInfoSerializable.class);
	}

	private ConfigYaml config;

	private GameInfoCollection games;

	private GameControl control;

	@Override
	public void onDisable() {
		
		// stop all games
		Iterator<Game> iterator = control.getGames().iterator();
		while(iterator.hasNext()) {
			Game game = iterator.next();
			try {
				game.stop();
			} catch (GameRunningException e) {
				
			}
		}
	}
	
	@Override
	public void onEnable() {

		Plugin worldGuard = getServer().getPluginManager().getPlugin(
				"WorldGuard");
		Plugin worldEdit = null;
		if (worldGuard == null || !(worldGuard instanceof WorldGuardPlugin)) {
			getLogger().severe("WorldGuard is not installed!");
			getLogger()
			.severe("This plugin depends on the WorldEdit and WorldGuard plugin.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		} else {
			try {
				worldEdit = ((WorldGuardPlugin) worldGuard).getWorldEdit();
			} catch (CommandException e) {
				getLogger().severe("WorldEdit is not installed!");
				getLogger()
				.severe("This plugin depends on the WorldEdit and WorldGuard plugin.");
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
		}

		// static getPlugin()
		HungerGamePlugin.plugin = this;

		// config.yml file
		config = new ConfigYaml(this, "config");
		config.reload();

		// games/games.yml file
		// games = new GameInfoYaml(this, "games/games");
		// games.reload();
		games = new GameInfoDirectory(this, "games");

		// control
		control = new GameControl(this, games, new HungerGameObserver(this),
				(WorldGuardPlugin) worldGuard, (WorldEditPlugin) worldEdit);
		
		getServer().getPluginManager().registerEvents(control, this);
	}

	public void playerJoin(Player player, String id)
	throws GameJoinedException, GameException {
		Game game = control.getGameOf(player.getName());
		if (game != null) {
			throw new GameJoinedException("Already joined game \""
					+ game.getInfo().getId() + "\".");
		}
		game = control.getGame(id);
		if (game != null) {
			game.joinPlayer(player);
		} else {
			throw new GameException("The game doesn't exist.");
		}
	}

	public void playerLeave(Player player) throws GameJoinedException {
		Game game = control.getGameOf(player.getName());
		if (game != null) {
			game.leavePlayer(player.getName(), LeaveReason.LEAVE);
		}
		else {
			throw new GameJoinedException("Not joined to a game.");
		}
	}

	public void gameStart(String id, int startAfterSeconds,
			boolean resetRegionFirst) throws GameException {
		Game game = new Game(id, control);
		Snapshot snap;
		if (resetRegionFirst) {
			try {
				snap = game.getInfo().getRandomSnapshot();
			} catch (InvalidSnapshotException e) {
				snap = null;
			}
			if(snap == null) {
				getLogger().warning("Failed to load random snapshot for game \"" + game.getId() + "\".");
				getLogger().warning("Loading default snapshot from the server's snapshots folder.");
				try {
					snap = getFallbackSnapshot(game.getInfo().getLocation().getWorld().getName());
				} catch (MissingWorldException e1) {
					snap = null;
				}
				if(snap == null) {
					getLogger().warning("Failed to load default snapshot.");
					throw new GameException("Failed to load random snapshot. And failed to load default snapshot.");
				}
			}
			try {
				game.openAfterRestore(startAfterSeconds, snap);
			} catch (IOException e) {
				throw new GameException("Failed to load snapshot: " + e.getMessage(), e);
			}
		} else {
			game.open(startAfterSeconds);
		}

	}
	
	private Snapshot getFallbackSnapshot(String world) throws MissingWorldException {
		return control.getWorldEdit().getLocalConfiguration().snapshotRepo.getDefaultSnapshot(world);
	}

	public void gameStop(String id) throws GameException {
		Game game = control.getGame(id);
		if (game != null) {
			game.stop();
		} else {
			throw new GameException("The game is not running.");
		}
	}

	public String[] gameIds() {
		return control.getGameInfoCollection().getInfoIds();
	}
	
	public GameInfoSerializable createGameInfo(String id, Location location, String region, int invulnerabilitySeconds, int feastDelaySeconds) {
		return new GameInfoSerializable(((GameInfoDirectory)control.getGameInfoCollection()).getFolder(), id, location,
				region, invulnerabilitySeconds, feastDelaySeconds);
	}

	public boolean hasGameInfo(String id) {
		return control.getGameInfoCollection().hasInfo(id);
	}

	public void saveGameInfo(GameInfoSerializable info) {
		control.getGameInfoCollection().setInfo(info.getId(), info);
	}

	public void deleteGameInfo(String id) {
		control.getGameInfoCollection().removeInfo(id);
	}

	public GameInfoSerializable getGameInfo(String id) {
		try {
			return (GameInfoSerializable) control.getGameInfoCollection()
			.getInfo(id);
		} catch (ClassCastException e) {
			return null;
		}
	}
	
	public Game getGameOf(String playerName) {
		return control.getGameOf(playerName);
	}
	
	public Game getGame(String id) {
		return control.getGame(id);
	}
	
	public Collection<Game> getGames() {
		return control.getGames();
	}

	@Override
	public FileConfiguration getConfig() {
		return config.getConfig();
	}

	@Override
	public void reloadConfig() {
		config.reload();
	}

	@Override
	public void saveConfig() {
		config.save();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		String lbl = label.toLowerCase();
		if (lbl.equals("hunger") || lbl.equals("hungergame")) {
			List<String> aliases = new ArrayList<String>();
			aliases.add("hungergame");
			new HungerGameCommand(null, "hunger", aliases).execute(sender,
					label, args);
			return true;
		} else {
			sender.sendMessage(ChatColor.RED + "Unknown command: /" + label);
			return false;
		}
	}

	public boolean hasRegion(World world, String region) {
		try {
			return control.getWorldGuard().getRegionManager(world)
			.hasRegion(region);
		} catch (Exception e) {
			return false;
		}
	}

}
