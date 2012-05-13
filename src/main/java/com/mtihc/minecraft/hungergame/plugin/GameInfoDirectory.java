package com.mtihc.minecraft.hungergame.plugin;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.minecraft.hungergame.game.GameInfo;
import com.mtihc.minecraft.hungergame.game.GameInfoCollection;

public class GameInfoDirectory implements GameInfoCollection {

	private final JavaPlugin plugin;
	private final File folder;

	public GameInfoDirectory(JavaPlugin plugin, String path) {
		this.plugin = plugin;
		folder = new File(plugin.getDataFolder(), path);
	}

	public File getFolder() {
		return folder;
	}

	private String getValidId(String id) {
		return id.toLowerCase().replace(" ", "_");
	}

	private String getGameFolder(String id) {
		return folder + "/" + getValidId(id);
	}

	private File getGameFile(String id) {
		return new File(getGameFolder(id) + "/game.yml");
	}

	private YamlConfiguration getGameConfig(File gameFile) {
		return YamlConfiguration.loadConfiguration(gameFile);
	}

	private YamlConfiguration getGameConfig(String id) {
		return getGameConfig(getGameFile(id));
	}

	@Override
	public GameInfo getInfo(String id) {
		return (GameInfo) getGameConfig(id).get("game");
	}

	@Override
	public void setInfo(String id, GameInfo info) {
		File gameFile = getGameFile(id);
		YamlConfiguration game = getGameConfig(gameFile);
		game.set("game", info);
		try {
			game.save(gameFile);
		} catch (IOException e) {
			plugin.getLogger().severe(
					"Failed to save game file: " + gameFile.getPath());
			plugin.getLogger().severe(
					e.getClass().getSimpleName() + ": " + e.getMessage());
			return;
		}

	}

	@Override
	public void removeInfo(String id) {
		File gameFolder = getGameFile(id);
		if (gameFolder.delete()) {
			plugin.getLogger()
			.info("Successfully deleted game folder: "
					+ gameFolder.getPath());
		} else {
			plugin.getLogger().severe(
					"Failed to delete game folder: " + gameFolder.getPath());
		}

	}

	@Override
	public boolean hasInfo(String id) {
		File gameFile = getGameFile(id);
		return gameFile.exists();
	}

	@Override
	public String[] getInfoIds() {
		return folder.list();
	}


}
