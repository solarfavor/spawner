/**
 * Spawner - Gather mob spawners with silk touch enchanted tools and the
 * ability to change mob types.
 *
 * Copyright (C) 2012-2015 Ryan Rhode - rrhode@gmail.com
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import me.ryvix.spawner.language.Language;
import me.ryvix.spawner.metrics.Metrics;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	public Configuration config;
	public static Language language;
	public static Main instance;

	@Override
	public void onEnable() {
		instance = this;

		// load config file
		loadConfig();

		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			// Failed to submit the stats :-(
		}

		language = new Language("language.yml");
		language.loadText();

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

	private void updateConfig() {

		getConfig().options().copyDefaults(true);

		// add header
		getConfig().options().header("Spawner config file\n\n");
		getConfig().options().copyHeader(true);

		// save file
		saveConfig();

	}

	/**
	 * Load config values
	 *
	 */
	public void loadConfig() {

		// create config file
		File configFile = new File(this.getDataFolder(), "config.yml");
		try {
			if (!configFile.exists()) {
				getDataFolder().mkdir();

				updateConfig();
			}
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Cannot make folder: {0}", getDataFolder());
		}

		// get config file
		config = YamlConfiguration.loadConfiguration(configFile);

		List<String> validEntities = Arrays.asList("Creeper", "Skeleton", "Spider", "Giant", "Zombie", "Slime", "Ghast", "PigZombie", "Enderman", "CaveSpider", "Silverfish", "Blaze", "LavaSlime", "EnderDragon", "WitherBoss", "Bat", "Witch", "Pig", "Sheep", "Cow", "Chicken", "Squid", "Wolf", "MushroomCow", "SnowMan", "Ozelot", "VillagerGolem", "EntityHorse", "Villager", "FireworksRocketEntity", "Guardian", "Endermite", "Rabbit");

		// add defaults
		if (!config.contains("valid_entities")) {
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
		if (!config.contains("luck")) {
			getConfig().addDefault("luck", 100);
		}
		if (!config.contains("aliases")) {
			List wither = new ArrayList();
			wither.add("wither");
			getConfig().addDefault("aliases.WitherBoss", wither);

			List golems = new ArrayList();
			golems.add("golem");
			golems.add("irongolem");
			getConfig().addDefault("aliases.VillagerGolem", golems);

			List horse = new ArrayList();
			horse.add("horse");
			getConfig().addDefault("aliases.EntityHorse", horse);
		}

		ConfigurationSection frequency = getConfig().getConfigurationSection("frequency");
		if (frequency == null) {
			frequency = getConfig().createSection("frequency");
		}

		ConfigurationSection drops = getConfig().getConfigurationSection("drops");
		if (drops == null) {
			drops = getConfig().createSection("drops");
		}

		Iterator<String> iterator = validEntities.iterator();
		while (iterator.hasNext()) {
			String entity = iterator.next();
			ConfigurationSection frequencyEntity = frequency.getConfigurationSection(entity);
			if (frequencyEntity == null) {
				frequency.addDefault(entity, 100);
			}
			ConfigurationSection dropsEntity = drops.getConfigurationSection(entity);
			if (dropsEntity == null) {
				drops.addDefault(entity, new ArrayList());
			}

		}

		updateConfig();
	}
}
