package com.mtihc.minecraft.hungergame.plugin.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.hungergame.game.GameFeast;
import com.mtihc.minecraft.hungergame.game.configuration.GameDoubleFeastSerializable;
import com.mtihc.minecraft.hungergame.game.configuration.GameFeastSerializable;
import com.mtihc.minecraft.hungergame.game.configuration.GameInfoSerializable;
import com.mtihc.minecraft.hungergame.plugin.HungerGamePlugin;

public class EditFeastSet extends SimpleCommand {

	private final String permission;
	private final boolean startFeast;

	public EditFeastSet(SimpleCommand parent, String label,
			List<String> aliases, String permission, boolean startFeast) {
		super(parent, label, aliases, "<id>",
		"Set a chest to be the feast (look at a chest with items)");
		this.permission = permission;
		this.startFeast = startFeast;
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED
					+ "Command must be executed by a player, in game.");
			sender.sendMessage(getUsage());
			return false;
		}
		if (!sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.RED
					+ "You don't have permission to set the feast chest.");
			return false;
		}

		if (args != null && args.length > 1) {
			sender.sendMessage(ChatColor.RED
					+ "Expected only the game id, while looking at a chest.");
			sender.sendMessage(getUsage());
			return false;
		}

		String id;
		try {
			id = args[0];
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "Expected the game id.");
			sender.sendMessage(getUsage());
			return false;
		}

		HungerGamePlugin plugin = HungerGamePlugin.getPlugin();
		GameInfoSerializable info = plugin.getGameInfo(id);
		if (info == null) {
			sender.sendMessage(ChatColor.RED + "Game \"" + id
					+ "\" doesn't exist.");
			sender.sendMessage(getUsage());
			return false;
		}

		Player p = (Player) sender;
		Block block = p.getTargetBlock(null, 5);
		GameFeast feast;
		if (block.getState() instanceof Chest) {
			Chest chest = (Chest) block.getState();
			if (chest.getInventory().getHolder() instanceof DoubleChest) {
				DoubleChest doubleChest = (DoubleChest) chest.getInventory()
				.getHolder();
				feast = new GameDoubleFeastSerializable(doubleChest);
			} else {
				feast = new GameFeastSerializable(chest);
			}
		} else {
			sender.sendMessage(ChatColor.RED
					+ "You must be looking at a chest, while executing this command.");
			sender.sendMessage(getUsage());
			return false;
		}

		if (startFeast) {
			info.setStartFeast(feast);
		} else {
			info.setDelayedFeast(feast);
		}
		plugin.saveGameInfo(info);

		if (startFeast) {
			sender.sendMessage("Chest set to spawn when game \"" + id
					+ "\" begins.");
		} else {
			sender.sendMessage("Chest set to spawn when the feast begins.");
		}
		return true;
	}

	@Override
	public boolean hasNested() {
		return false;
	}

	@Override
	public SimpleCommand getNested(String labelOrAlias) {
		return null;
	}

	@Override
	public String[] getNestedCommandLabels() {
		return null;
	}

}
