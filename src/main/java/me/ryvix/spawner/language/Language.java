/**
 * Spawner - Gather mob spawners with silk touch enchanted tools and the
 * ability to change mob types.
 *
 * Copyright (C) 2012-2013 Ryan Rhode - rrhode@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package me.ryvix.spawner.language;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import me.ryvix.spawner.Main;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Language {

	protected FileConfiguration config;
	protected File configFile;
	protected String name;
	public List<Map<?, ?>> entries;

	/**
	 * Constructor
	 *
	 * @param main
	 * @param fileName
	 */
	public Language(String fileName) {
		name = fileName;
	}

	/**
	 * Get text - supports color codes, i.e. &4Red text
	 *
	 * @param key
	 * @param vars
	 * @return
	 */
	public String getText(Keys key, String... vars) {
		Map<?, ?> entry = entries.get(key.ordinal());
		String text = (String) entry.get(key.toString());
		for (int x = 0; x < vars.length; x++) {
			text = text.replace("{" + x + "}", vars[x]);
		}

		return ChatColor.translateAlternateColorCodes("&".charAt(0), text);
	}

	/**
	 * Load language text
	 */
	public void loadText() {
		Main.instance.getLogger().log(Level.INFO, "Loading {0}", name);
		saveDefaultConfig();
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