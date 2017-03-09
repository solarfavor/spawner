package me.ryvix.spawner.config.v1_10_R1;

import me.ryvix.spawner.Main;
import me.ryvix.spawner.api.Config;
import me.ryvix.spawner.api.Language;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

// https://www.spigotmc.org/threads/making-a-subfolders-to-a-plugin.93062/
public class ConfigHandler implements Config {

	private String folder;
	private File configFile;

	/**
	 * Load Spawner's files from disk.
	 */
	@Override
	public void loadFiles() {
		folder = Main.instance.getDataFolder() + File.separator + Main.instance.getVersion() + File.separator;

		// load config file
		Main.setSpawnerConfig(null);
		Main.instance.getConfigHandler().loadConfig();

		// load language file
		Main.instance.getLangHandler().setFileName(Main.instance.getVersion() + File.separator + "language.yml");
		Main.instance.getLangHandler().loadText();

		// create help and list files
		try {
			File file = new File(Main.instance.getDataFolder(), Main.instance.getVersion() + File.separator + "help.txt");
			if (!file.exists()) {
				Main.instance.saveResource(Main.instance.getVersion() + "/help.txt", false);
			}
			file = new File(Main.instance.getDataFolder(), Main.instance.getVersion() + File.separator + "list.txt");
			if (!file.exists()) {
				Main.instance.saveResource(Main.instance.getVersion() + "/list.txt", false);
			}
		} catch (Exception ex) {
			Main.instance.getLogger().log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Saves the config file.
	 */
	@Override
	public void updateConfig() {

		Main.instance.getConfig().options().copyDefaults(true);

		// add header
		Main.instance.getConfig().options().header("Spawner config file\n\n");
		Main.instance.getConfig().options().copyHeader(true);

		// save file
		try {
			Main.instance.getConfig().save(folder + "config.yml");

		} catch (IOException ex) {
			Main.instance.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
		}

	}

	/**
	 * Load config values
	 */
	@Override
	public void loadConfig() {

		// create config file
		configFile = new File(Main.instance.getDataFolder(), Main.instance.getVersion() + File.separator + "config.yml");
		try {
			if (!configFile.exists()) {
				Main.instance.getDataFolder().mkdir();

				updateConfig();
			}
		} catch (Exception e) {
			Main.instance.getLogger().log(Level.WARNING, "Cannot make folder: {0}", Main.instance.getDataFolder());
		}

		// set config file
		Main.instance.setSpawnerConfig(YamlConfiguration.loadConfiguration(configFile));

		boolean updates = false;

		List<String> validEntities = Arrays.asList(
				"Bat",
				"Blaze",
				"CaveSpider",
				"Chicken",
				"Cow",
				"Creeper",
				"EnderDragon",
				"Enderman",
				"Endermite",
				"FireworksRocketEntity",
				"Ghast",
				"Giant",
				"Guardian",
				"EntityHorse",
				"LavaSlime",
				"MushroomCow",
				"Ozelot",
				"Pig",
				"PigZombie",
				"PolarBear",
				"Rabbit",
				"Sheep",
				"Shulker",
				"Silverfish",
				"Skeleton",
				"Slime",
				"SnowMan",
				"Spider",
				"Squid",
				"Villager",
				"VillagerGolem",
				"Witch",
				"WitherBoss",
				"Wolf",
				"XPOrb",
				"Zombie"
		);

		// add defaults
		if (!Main.instance.getSpawnerConfig().contains("valid_entities")) {
			updates = true;
			Main.instance.getConfig().addDefault("valid_entities", validEntities);
		}
		if (Main.instance.getSpawnerConfig().contains("bad_entities")) {
			updates = true;
			Main.instance.getConfig().set("bad_entities", null);
		}
		if (!Main.getSpawnerConfig().contains("protect_from_explosions")) {
			updates = true;
			Main.instance.getConfig().addDefault("protect_from_explosions", true);
		}
		if (!Main.instance.getSpawnerConfig().contains("drop_from_explosions")) {
			updates = true;
			Main.instance.getConfig().addDefault("drop_from_explosions", false);
		}
		if (!Main.instance.getSpawnerConfig().contains("remove_radius")) {
			updates = true;
			Main.instance.getConfig().addDefault("remove_radius", 10);
		}
		if (!Main.instance.getSpawnerConfig().contains("luck")) {
			updates = true;
			Main.instance.getConfig().addDefault("luck", 100);
		}
		if (!Main.instance.getSpawnerConfig().contains("aliases")) {
			updates = true;

			List<String> wither = new ArrayList<>();
			wither.add("wither");
			Main.instance.getConfig().addDefault("aliases.WitherBoss", wither);

			List<String> golem = new ArrayList<>();
			golem.add("golem");
			golem.add("irongolem");
			Main.instance.getConfig().addDefault("aliases.VillagerGolem", golem);

			List<String> horse = new ArrayList<>();
			horse.add("horse");
			Main.instance.getConfig().addDefault("aliases.EntityHorse", horse);

			List<String> ocelot = new ArrayList<>();
			ocelot.add("ocelot");
			ocelot.add("cat");
			Main.instance.getConfig().addDefault("aliases.Ozelot", ocelot);

			List<String> rabbit = new ArrayList<>();
			rabbit.add("bunny");
			Main.instance.getConfig().addDefault("aliases.Rabbit", rabbit);

			List<String> fireworks = new ArrayList<>();
			fireworks.add("fireworks");
			Main.instance.getConfig().addDefault("aliases.FireworksRocketEntity", fireworks);

			List<String> xp = new ArrayList<>();
			xp.add("xp");
			Main.instance.getConfig().addDefault("aliases.XPOrb", xp);
		}

		ConfigurationSection frequency = Main.instance.getConfig().getConfigurationSection("frequency");
		if (frequency == null) {
			updates = true;
			frequency = Main.instance.getConfig().createSection("frequency");
		}

		ConfigurationSection drops = Main.instance.getConfig().getConfigurationSection("drops");
		if (drops == null) {
			updates = true;
			drops = Main.instance.getConfig().createSection("drops");
		}

		for (String entity : validEntities) {
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

		if (!Main.getSpawnerConfig().contains("break_into_inventory")) {
			updates = true;
			Main.instance.getConfig().addDefault("break_into_inventory", false);
		}
		if (!Main.getSpawnerConfig().contains("prevent_break_if_inventory_full")) {
			updates = true;
			Main.instance.getConfig().addDefault("prevent_break_if_inventory_full", false);
		}

		updateConfig();

		if (updates) {
			Main.setSpawnerConfig(null);
			Main.setSpawnerConfig(YamlConfiguration.loadConfiguration(configFile));
		}
	}

}
