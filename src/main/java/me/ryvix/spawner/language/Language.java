/**
 * Spawner - Gather mob spawners with silk touch enchanted tools and the ability
 * to change mob types.
 *
 * Copyright (C) 2012-2014 Ryan Rhode - rrhode@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package me.ryvix.spawner.language;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.ryvix.spawner.Main;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Language {

	protected FileConfiguration config;
	protected File configFile;
	protected String name;
	public List<Map<?, ?>> entries;

	/**
	 * Constructor
	 *
	 * @param fileName
	 */
	public Language(String fileName) {
		name = fileName;
	}

	/**
	 * Send a message to a Player if it isn't empty Supports color codes, i.e.
	 * &4Red text
	 *
	 * @param player
	 * @param text
	 */
	public void sendMessage(Player player, String text) {
		if (!text.isEmpty()) {
			player.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), text));
		}
	}

	/**
	 * Send a message to a CommandSender if it isn't empty Supports color codes,
	 * i.e. &4Red text
	 *
	 * @param sender
	 * @param text
	 */
	public void sendMessage(CommandSender sender, String text) {
		if (!text.isEmpty()) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), text));
		}
	}

	/**
	 * Get text
	 *
	 * @param key
	 * @param vars
	 * @return
	 */
	public String getText(Keys key, String... vars) {
		Map<?, ?> entry = entries.get(key.ordinal());
		String text = (String) entry.get(key.toString());

		// check for empty text
		if (text.isEmpty()) {
			return "";
		}

		for (int x = 0; x < vars.length; x++) {
			text = text.replace("{" + x + "}", vars[x]);
		}

		return text;
	}

	/**
	 * Load language text
	 */
	public void loadText() {
		Main.instance.getLogger().log(Level.INFO, "Loading {0}", name);
		saveDefaultConfig();

		// add defaults
		if (!getConfig().contains("Language.ConsoleUsageGive")) {
			getConfig().addDefault("Language.ConsoleUsageGive", "Usage: spawner give <entity> <player>");
		}
		if (!getConfig().contains("Language.LookAtASpawner")) {
			getConfig().addDefault("Language.LookAtASpawner", "&4Look at a spawner to see what kind it is!");
		}
		if (!getConfig().contains("Language.NoPermission")) {
			getConfig().addDefault("Language.NoPermission", "&4You don''t have permission to do that!");
		}
		if (!getConfig().contains("Language.SpawnerChangedTo")) {
			getConfig().addDefault("Language.SpawnerChangedTo", "&aSpawner type changed to {0}");
		}
		if (!getConfig().contains("Language.InvalidSpawner")) {
			getConfig().addDefault("Language.InvalidSpawner", "&4Invalid spawner type!");
		}
		if (!getConfig().contains("Language.GivenSpawner")) {
			getConfig().addDefault("Language.GivenSpawner", "&aYou were given a {0} spawner.");
		}
		if (!getConfig().contains("Language.SpawnerDropped")) {
			getConfig().addDefault("Language.SpawnerDropped", "&aA {0} spawner was dropped at your feet because your inventory is full.");
		}
		if (!getConfig().contains("Language.YouGaveSpawner")) {
			getConfig().addDefault("Language.YouGaveSpawner", "&aYou gave a {0} spawner to {1}.");
		}
		if (!getConfig().contains("Language.NotDeliveredOffline")) {
			getConfig().addDefault("Language.NotDeliveredOffline", "&4The spawner was not delivered because {0} is offline.");
		}
		if (!getConfig().contains("Language.YouPickedUp")) {
			getConfig().addDefault("Language.YouPickedUp", "&aYou picked up a {0} spawner.");
		}
		if (!getConfig().contains("Language.HoldingSpawner")) {
			getConfig().addDefault("Language.HoldingSpawner", "&aYou are holding a {0} spawner.");
		}
		if (!getConfig().contains("Language.SpawnerType")) {
			getConfig().addDefault("Language.SpawnerType", "&aSpawner type: {0}");
		}
		if (!getConfig().contains("Language.PlacedSpawner")) {
			getConfig().addDefault("Language.PlacedSpawner", "&aPlaced {0} spawner");
		}
		if (!getConfig().contains("Language.NotPossible")) {
			getConfig().addDefault("Language.NotPossible", "&4It was not possible to change that spawner.");
		}
		if (!getConfig().contains("Language.InvalidRadius")) {
			getConfig().addDefault("Language.InvalidRadius", "&4You entered an invalid radius.");
		}
		if (!getConfig().contains("Language.InvalidEntity")) {
			getConfig().addDefault("Language.InvalidEntity", "&4You entered an invalid entity.");
		}
		if (!getConfig().contains("Language.ErrorRemovingEntities")) {
			getConfig().addDefault("Language.ErrorRemovingEntities", "&4There was an error removing entities. Some may not have been removed.");
		}
		if (!getConfig().contains("Language.EntitiesRemoved")) {
			getConfig().addDefault("Language.EntitiesRemoved", "&a{0} {1} removed.");
		}

		try {
			// try to save the file
			getConfig().save(configFile);
		} catch (IOException ex) {
			Logger.getLogger(Language.class.getName()).log(Level.SEVERE, null, ex);
		}

		entries = getConfig().getMapList("Language");
	}

	/**
	 * Make folder if it doesn't already exist
	 */
	private void makeFolder() {
		Main.instance.getDataFolder().mkdir();
	}

	/**
	 * Save default configuration file
	 */
	public void saveDefaultConfig() {
		if (configFile == null) {
			makeFolder();
			configFile = new File(Main.instance.getDataFolder(), name);
		}
		if (!configFile.exists()) {
			Main.instance.saveResource(name, false);
		}
	}

	/**
	 * Reload configuration file
	 */
	public void reloadConfig() {
		if (configFile == null) {
			makeFolder();
			configFile = new File(Main.instance.getDataFolder(), name);
		}
		config = YamlConfiguration.loadConfiguration(configFile);

		// load default config if it's there
		InputStream defaultStream = Main.instance.getResource(name);
		if (defaultStream != null) {
			YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(defaultStream);
			config.setDefaults(defaultConfig);
		}
	}

	/**
	 * Get the configuration file
	 *
	 * @return
	 */
	public FileConfiguration getConfig() {
		if (config == null) {
			reloadConfig();
		}
		return config;
	}
}