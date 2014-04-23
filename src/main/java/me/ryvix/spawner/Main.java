/**
 * Spawner - Gather mob spawners with silk touch enchanted tools and the
 * ability to change mob types.
 *
 * Copyright (C) 2012-2014 Ryan Rhode - rrhode@gmail.com
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
package me.ryvix.spawner;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import me.ryvix.spawner.language.Language;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	public Configuration config;
	public static Language language;
	public static Main instance;

	@Override
	public void onEnable() {
		instance = this;
		language = new Language("language.yml");
		language.loadText();

		// create config file
		try {
			File configFile = new File(getDataFolder(), "config.yml");
			if (!configFile.exists()) {
				getDataFolder().mkdir();
				this.getConfig().options().copyDefaults(true);
				this.getConfig().options().header("Spawner config file\n");
				this.getConfig().options().copyHeader(true);
				this.saveConfig();
			}

		} catch (Exception e) {
		}

		// load config file
		loadConfig();

		// create help and list files
		try {
			File file = new File(getDataFolder(), "help.txt");
			if (!file.exists()) {
				saveResource("help.txt", false);
			}
			file = new File(getDataFolder(), "list.txt");
			if (!file.exists()) {
				saveResource("list.txt", false);
			}
		} catch (Exception e) {
		}

		// register events
		getServer().getPluginManager().registerEvents(new SpawnerEvents(), this);

		// spawner
		getCommand("spawner").setExecutor(new SpawnerCommands());
	}

	@Override
	public void onDisable() {
		config = null;
		language = null;
	}

	/**
	 * Load config values
	 *
	 */
	public void loadConfig() {

		// get config file
		FileConfiguration getConfig = getConfig();
		File configFile = new File(this.getDataFolder() + "/config.yml");
		config = YamlConfiguration.loadConfiguration(configFile);

		// add defaults
		if (!config.contains("valid_entities")) {
			List<String> validEntities = Arrays.asList("Creeper", "Skeleton", "Spider", "Giant", "Zombie", "Slime", "Ghast", "PigZombie", "Enderman", "CaveSpider", "Silverfish", "Blaze", "LavaSlime", "EnderDragon", "WitherBoss", "Bat", "Witch", "Pig", "Sheep", "Cow", "Chicken", "Squid", "Wolf", "MushroomCow", "SnowMan", "Ozelot", "VillagerGolem", "EntityHorse", "Villager");
			getConfig().addDefault("valid_entities", validEntities);
		}
		if (config.contains("bad_entities")) {
			getConfig().set("bad_entities", null);
		}
		if (!config.contains("protect_from_explosions")) {
			getConfig().addDefault("protect_from_explosions", "true");
		}
		if (!config.contains("remove_radius")) {
			getConfig().addDefault("remove_radius", 10);
		}
		/*if (!config.contains("limit")) {
		 getConfig().addDefault("limit.members", "members");
		 getConfig().addDefault("limit.vip", "vip");
		 getConfig().addDefault("limit.elite", "elite");
		 getConfig().addDefault("limit.godlike", "godlike");
		 }
		 if (!config.contains("allow_baby")) {
		 getConfig().addDefault("allow_baby", "false");
		 }
		 if (!config.contains("allow_armour")) {
		 getConfig().addDefault("allow_armour", "true");
		 }*/
		getConfig.options().copyDefaults(true);

		// add header
		getConfig().options().header("Spawner config file\n\n");
		getConfig().options().copyHeader(true);

		// save file
		saveConfig();
	}
}
