package com.mtihc.minecraft.hungergame.game;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class GameControl implements Listener {

	private final JavaPlugin plugin;
	private final GameInfoCollection infos;
	private final WorldGuardPlugin worldGuard;
	private final WorldEditPlugin worldEdit;

	private final Map<String, Game> games;

	private final GameObserver observer;

	public GameControl(JavaPlugin plugin, GameInfoCollection infos,
			GameObserver observer, WorldGuardPlugin worldGuard,
			WorldEditPlugin worldEdit) {
		this.plugin = plugin;
		this.infos = infos;
		this.worldGuard = worldGuard;
		this.worldEdit = worldEdit;
		this.games = new HashMap<String, Game>();

		this.observer = observer;
	}

	public JavaPlugin getPlugin() {
		return plugin;
	}

	public WorldGuardPlugin getWorldGuard() {
		return worldGuard;
	}

	public WorldEditPlugin getWorldEdit() {
		return worldEdit;
	}

	public GameInfoCollection getGameInfoCollection() {
		return infos;
	}

	public GameObserver getObserver() {
		return observer;
	}

	public boolean hasGame(String id) {
		return games.containsKey(id);
	}

	public Game getGame(String id) {
		return games.get(id);
	}

	protected Game removeGame(String id) {
		return games.remove(id);
	}

	protected void setGame(String id, Game game) {
		games.put(game.getId(), game);
	}

	public Collection<Game> getGames() {
		return games.values();
	}

	public Game getGameOf(String playerName) {
		Iterator<Game> iterator = games.values().iterator();
		while (iterator.hasNext()) {
			Game game = iterator.next();
			if (game.hasPlayer(playerName) || game.hasDeadPlayer(playerName)) {
				return game;
			}
		}
		return null;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Iterator<Game> it = getGames().iterator();
		while (it.hasNext()) {
			Game game = it.next();
			game.onBlockBreak(event);
		}
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		Iterator<Game> it = getGames().iterator();
		while (it.hasNext()) {
			Game game = it.next();
			game.onEntityExplode(event);
		}
	}

	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {
		Iterator<Game> it = getGames().iterator();
		while (it.hasNext()) {
			Game game = it.next();
			game.onBlockIgnite(event);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Iterator<Game> it = getGames().iterator();
		while (it.hasNext()) {
			Game game = it.next();
			game.onPlayerInteract(event);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Iterator<Game> it = getGames().iterator();
		while (it.hasNext()) {
			Game game = it.next();
			game.onPlayerDeath(event);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Iterator<Game> it = getGames().iterator();
		while (it.hasNext()) {
			Game game = it.next();
			game.onPlayerQuit(event);
		}
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		Iterator<Game> it = getGames().iterator();
		while (it.hasNext()) {
			Game game = it.next();
			game.onPlayerKick(event);
		}
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Iterator<Game> it = getGames().iterator();
		while (it.hasNext()) {
			Game game = it.next();
			game.onPlayerRespawn(event);
		}
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent event) {
		Iterator<Game> it = getGames().iterator();
		while (it.hasNext()) {
			Game game = it.next();
			game.onPlayerDamage(event);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Iterator<Game> it = getGames().iterator();
		while (it.hasNext()) {
			Game game = it.next();
			game.onPlayerJoin(event);
		}
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Iterator<Game> it = getGames().iterator();
		while (it.hasNext()) {
			Game game = it.next();
			game.onPlayerTeleport(event);
		}
	}

}
