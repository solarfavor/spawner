/**
 * Spawner - Gather mob spawners with silk touch enchanted tools and the
 * ability to change mob types.
 *
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Ryan Rhode
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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

		// metrics
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			// Failed to submit the stats :-(
		}

		// load files
		loadFiles();

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
	 * Reload Spawner's files from disk.
	 */
	public void reloadFiles() {
		reloadConfig();
		loadFiles();
	}

	/**
	 * Load Spawner's files from disk.
	 */
	public void loadFiles() {

		// load config file
		config = null;
		loadConfig();

		// load language file
		language = null;
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
	}

	/**
	 * Saves the config file.
	 */
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

		boolean updates = false;
		
		List<String> validEntities = Arrays.asList("Creeper", "Skeleton", "Spider", "Giant", "Zombie", "Slime", "Ghast", "PigZombie", "Enderman", "CaveSpider", "Silverfish", "Blaze", "LavaSlime", "EnderDragon", "WitherBoss", "Bat", "Witch", "Pig", "Sheep", "Cow", "Chicken", "Squid", "Wolf", "MushroomCow", "SnowMan", "Ozelot", "VillagerGolem", "EntityHorse", "Villager", "FireworksRocketEntity", "Guardian", "Endermite", "Rabbit");

		// add defaults
		if (!config.contains("valid_entities")) {
			updates = true;
			getConfig().addDefault("valid_entities", validEntities);
		}
		if (config.contains("bad_entities")) {
			updates = true;
			getConfig().set("bad_entities", null);
		}
		if (!config.contains("protect_from_explosions")) {
			updates = true;
			getConfig().addDefault("protect_from_explosions", "true");
		}
		if (!config.contains("drop_from_explosions")) {
			updates = true;
			getConfig().addDefault("drop_from_explosions", "false");
		}
		if (!config.contains("remove_radius")) {
			updates = true;
			getConfig().addDefault("remove_radius", 10);
		}
		if (!config.contains("luck")) {
			updates = true;
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

			List ozelot = new ArrayList();
			ozelot.add("ocelot");
			ozelot.add("cat");
			getConfig().addDefault("aliases.Ozelot", ozelot);
		}

		ConfigurationSection frequency = getConfig().getConfigurationSection("frequency");
		if (frequency == null) {
			updates = true;
			frequency = getConfig().createSection("frequency");
		}

		ConfigurationSection drops = getConfig().getConfigurationSection("drops");
		if (drops == null) {
			updates = true;
			drops = getConfig().createSection("drops");
		}

		Iterator<String> iterator = validEntities.iterator();
		while (iterator.hasNext()) {
			String entity = iterator.next();
			ConfigurationSection frequencyEntity = frequency.getConfigurationSection(entity.toLowerCase());
			if (frequencyEntity == null) {
				updates = true;
				frequency.addDefault(entity.toLowerCase(), 100);
			}
			ConfigurationSection dropsEntity = drops.getConfigurationSection(entity);
			if (dropsEntity == null) {
				updates = true;
				drops.addDefault(entity, new ArrayList());
			}

		}

		updateConfig();
		
		if(updates == true){
			config = null;
			config = YamlConfiguration.loadConfiguration(configFile);
		}
	}
}
