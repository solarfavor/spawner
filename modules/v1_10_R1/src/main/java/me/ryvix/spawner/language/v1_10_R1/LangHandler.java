/**
 * Spawner - Gather mob spawners with silk touch enchanted tools and the
 * ability to change mob types.
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2016 Ryan Rhode
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package me.ryvix.spawner.language.v1_10_R1;

import me.ryvix.spawner.Main;
import me.ryvix.spawner.api.Language;
import me.ryvix.spawner.EntityMap;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class LangHandler implements Language {

	private FileConfiguration config;
	private File configFile;
	private String name;
	private boolean updates = false;

	/**
	 * Set language file name
	 */
	public void setFileName(String fileName) {
		name = fileName;
	}

	/**
	 * Load language text
	 */
	public void loadText() {
		saveDefaultConfig();
	}

	/**
	 * Get the configuration file
	 *
	 * @return
	 */
	public FileConfiguration getConfig() {
		if (config == null) {
			loadConfig();
		}
		return config;
	}

	/**
	 * Get text
	 *
	 * @param key
	 * @param vars
	 * @return
	 */
	public String getText(String key, String... vars) {
		String text;
		try {
			text = config.getString("Language." + key);
		} catch (Exception e) {
			Main.instance.getLogger().log(Level.WARNING, e.getLocalizedMessage());
			Main.instance.getLogger().log(Level.WARNING, e.toString());
			Main.instance.getLogger().log(Level.WARNING, "Missing language at: Language.{0}", key);
			Main.instance.getLogger().log(Level.WARNING, "Add it or regenerate your language.yml by deleting it and then run /spawner reload");

			// return nothing
			return "";
		}

		// check for empty text
		if (text == null || text.isEmpty()) {
			return "";
		}

		for (int x = 0; x < vars.length; x++) {
			text = text.replace("{" + x + "}", vars[x]);
		}

		return text;
	}

	/**
	 * Get entity
	 *
	 * @param entity
	 * @return
	 */
	public String getEntity(String entity) {
		String mapped = EntityMap.getKey(entity);
		if(mapped != null) {
			entity = mapped;
		}

		String text;
		try {
			text = config.getString("Entities." + entity);
		} catch (Exception e) {
			Main.instance.getLogger().log(Level.WARNING, e.getLocalizedMessage());
			Main.instance.getLogger().log(Level.WARNING, e.toString());
			Main.instance.getLogger().log(Level.WARNING, "Missing language at: Entities.{0}", entity);
			Main.instance.getLogger().log(Level.WARNING, "Add it or regenerate your language.yml by deleting it and then run /spawner reload");

			// return default spawner name
			return "Monster";
		}

		// check for empty text
		if (text.isEmpty()) {
			return "";
		}

		return text;
	}

	/**
	 * Save default configuration file
	 */
	public void saveDefaultConfig() {
		// create file if it doesn't exist
		createConfig();

		// load the file
		getConfig();

		// add any missing default values
		setValues();

		// save file
		saveConfig();

		// make sure to reload the config if there were new additions
		if (updates == true) {
			loadConfig();
		}
	}

	/**
	 * Add default values
	 */
	public void setValues() {
		if (!config.contains("Language")) {
			updates = true;
			config.createSection("Language");
		}
		if (!config.isSet("Language.ConsoleUsageGive")) {
			updates = true;
			config.set("Language.ConsoleUsageGive", "Usage: spawner give <entity> <player>");
		}
		if (!config.isSet("Language.LookAtASpawner")) {
			updates = true;
			config.set("Language.LookAtASpawner", "&4Look at a spawner to see what kind it is!");
		}
		if (!config.isSet("Language.NoPermission")) {
			updates = true;
			config.set("Language.NoPermission", "&4You don't have permission to do that!");
		}
		if (!config.isSet("Language.SpawnerChangedTo")) {
			updates = true;
			config.set("Language.SpawnerChangedTo", "&aSpawner type changed to {0}");
		}
		if (!config.isSet("Language.InvalidSpawner")) {
			updates = true;
			config.set("Language.InvalidSpawner", "&4Invalid spawner type!");
		}
		if (!config.isSet("Language.GivenSpawner")) {
			updates = true;
			config.set("Language.GivenSpawner", "&aYou were given a {0} &aspawner.");
		}
		if (!config.isSet("Language.SpawnerDropped")) {
			updates = true;
			config.set("Language.SpawnerDropped", "&aA {0} spawner was dropped at your feet because your inventory is full.");
		}
		if (!config.isSet("Language.YouGaveSpawner")) {
			updates = true;
			config.set("Language.YouGaveSpawner", "&aYou gave a {0} &aspawner to {1}.");
		}
		if (!config.isSet("Language.NotDeliveredOffline")) {
			updates = true;
			config.set("Language.NotDeliveredOffline", "&4The spawner was not delivered because &e{0} &4is offline.");
		}
		if (!config.isSet("Language.YouPickedUp")) {
			updates = true;
			config.set("Language.YouPickedUp", "&aYou picked up a {0} &aspawner.");
		}
		if (!config.isSet("Language.HoldingSpawner")) {
			updates = true;
			config.set("Language.HoldingSpawner", "&aYou are holding a {0} &aspawner.");
		}
		if (!config.isSet("Language.SpawnerType")) {
			updates = true;
			config.set("Language.SpawnerType", "&aSpawner type: {0}");
		}
		if (!config.isSet("Language.PlacedSpawner")) {
			updates = true;
			config.set("Language.PlacedSpawner", "&aPlaced {0} &aspawner");
		}
		if (!config.isSet("Language.NotPossible")) {
			updates = true;
			config.set("Language.NotPossible", "&4It was not possible to change that spawner.");
		}
		if (!config.isSet("Language.InvalidRadius")) {
			updates = true;
			config.set("Language.InvalidRadius", "&4You entered an invalid radius.");
		}
		if (!config.isSet("Language.InvalidEntity")) {
			updates = true;
			config.set("Language.InvalidEntity", "&4You entered an invalid entity.");
		}
		if (!config.isSet("Language.ErrorRemovingEntities")) {
			updates = true;
			config.set("Language.ErrorRemovingEntities", "&4There was an error removing entities. Some may not have been removed.");
		}
		if (!config.isSet("Language.EntitiesRemoved")) {
			updates = true;
			config.set("Language.EntitiesRemoved", "&a{0} {1} &aremoved.");
		}
		if (!config.isSet("Language.Spawner")) {
			updates = true;
			config.set("Language.Spawner", "&aspawner");
		}
		if (!config.isSet("Language.NoCreative")) {
			updates = true;
			config.set("Language.NoCreative", "&4Sorry but you can't pick that up in creative mode.");
		}
		if (!config.isSet("Language.InventoryFull")) {
			updates = true;
			config.set("Language.InventoryFull", "&4Your inventory is full.");
		}

		if (!config.contains("Entities")) {
			updates = true;
			config.createSection("Entities");
		}
		if (!config.isSet("Entities.Bat")) {
			updates = true;
			config.set("Entities.Bat", "&eBat");
		}
		if (!config.isSet("Entities.Blaze")) {
			updates = true;
			config.set("Entities.Blaze", "&eBlaze");
		}
		if (!config.isSet("Entities.CaveSpider")) {
			updates = true;
			config.set("Entities.CaveSpider", "&eCaveSpider");
		}
		if (!config.isSet("Entities.Chicken")) {
			updates = true;
			config.set("Entities.Chicken", "&eChicken");
		}
		if (!config.isSet("Entities.Cow")) {
			updates = true;
			config.set("Entities.Cow", "&eCow");
		}
		if (!config.isSet("Entities.Creeper")) {
			updates = true;
			config.set("Entities.Creeper", "&eCreeper");
		}
		if (!config.isSet("Entities.EnderDragon")) {
			updates = true;
			config.set("Entities.EnderDragon", "&eEnderDragon");
		}
		if (!config.isSet("Entities.Enderman")) {
			updates = true;
			config.set("Entities.Enderman", "&eEnderman");
		}
		if (!config.isSet("Entities.Endermite")) {
			updates = true;
			config.set("Entities.Endermite", "&eEndermite");
		}
		if (!config.isSet("Entities.FireworksRocketEntity")) {
			updates = true;
			config.set("Entities.FireworksRocketEntity", "&eFireworksRocketEntity");
		}
		if (!config.isSet("Entities.Ghast")) {
			updates = true;
			config.set("Entities.Ghast", "&eGhast");
		}
		if (!config.isSet("Entities.Giant")) {
			updates = true;
			config.set("Entities.Giant", "&eGiant");
		}
		if (!config.isSet("Entities.Guardian")) {
			updates = true;
			config.set("Entities.Guardian", "&eGuardian");
		}
		if (!config.isSet("Entities.EntityHorse")) {
			updates = true;
			config.set("Entities.EntityHorse", "&eEntityHorse");
		}
		if (!config.isSet("Entities.LavaSlime")) {
			updates = true;
			config.set("Entities.LavaSlime", "&eLavaSlime");
		}
		if (!config.isSet("Entities.MushroomCow")) {
			updates = true;
			config.set("Entities.MushroomCow", "&eMushroomCow");
		}
		if (!config.isSet("Entities.Ozelot")) {
			updates = true;
			config.set("Entities.Ozelot", "&eOzelot");
		}
		if (!config.isSet("Entities.Pig")) {
			updates = true;
			config.set("Entities.Pig", "&ePig");
		}
		if (!config.isSet("Entities.PigZombie")) {
			updates = true;
			config.set("Entities.PigZombie", "&ePigZombie");
		}
		if (!config.isSet("Entities.PolarBear")) {
			updates = true;
			config.set("Entities.PolarBear", "&ePolarBear");
		}
		if (!config.isSet("Entities.Rabbit")) {
			updates = true;
			config.set("Entities.Rabbit", "&eRabbit");
		}
		if (!config.isSet("Entities.Sheep")) {
			updates = true;
			config.set("Entities.Sheep", "&eSheep");
		}
		if (!config.isSet("Entities.Shulker")) {
			updates = true;
			config.set("Entities.Shulker", "&eShulker");
		}
		if (!config.isSet("Entities.Silverfish")) {
			updates = true;
			config.set("Entities.Silverfish", "&eSilverfish");
		}
		if (!config.isSet("Entities.Skeleton")) {
			updates = true;
			config.set("Entities.Skeleton", "&eSkeleton");
		}
		if (!config.isSet("Entities.Slime")) {
			updates = true;
			config.set("Entities.Slime", "&eSlime");
		}
		if (!config.isSet("Entities.SnowMan")) {
			updates = true;
			config.set("Entities.SnowMan", "&eSnowMan");
		}
		if (!config.isSet("Entities.Spider")) {
			updates = true;
			config.set("Entities.Spider", "&eSpider");
		}
		if (!config.isSet("Entities.Squid")) {
			updates = true;
			config.set("Entities.Squid", "&eSquid");
		}
		if (!config.isSet("Entities.Villager")) {
			updates = true;
			config.set("Entities.Villager", "&eVillager");
		}
		if (!config.isSet("Entities.VillagerGolem")) {
			updates = true;
			config.set("Entities.VillagerGolem", "&eVillagerGolem");
		}
		if (!config.isSet("Entities.Witch")) {
			updates = true;
			config.set("Entities.Witch", "&eWitch");
		}
		if (!config.isSet("Entities.WitherBoss")) {
			updates = true;
			config.set("Entities.WitherBoss", "&eWitherBoss");
		}
		if (!config.isSet("Entities.Wolf")) {
			updates = true;
			config.set("Entities.Wolf", "&eWolf");
		}
		if (!config.isSet("Entities.XPOrb")) {
			updates = true;
			config.set("Entities.XPOrb", "&eXPOrb");
		}
		if (!config.isSet("Entities.Zombie")) {
			updates = true;
			config.set("Entities.Zombie", "&eZombie");
		}
	}

	/**
	 * Make folder if it doesn't already exist
	 */
	public void makeFolder() {
		Main.instance.getDataFolder().mkdir();
	}

	/**
	 * Create configuration file.
	 */
	public void createConfig() {
		if (configFile == null) {
			makeFolder();
			configFile = new File(Main.instance.getDataFolder(), name);
		}
		if (!configFile.exists()) {
			Main.instance.saveResource(name, false);
		}
	}

	/**
	 * Save configuration file.
	 */
	public void saveConfig() {
		try {
			getConfig().save(configFile);
		} catch (IOException ex) {
			Main.instance.getLogger().log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Load configuration file
	 */
	public void loadConfig() {

		config = YamlConfiguration.loadConfiguration(configFile);
		config.options().copyDefaults(true);

		Reader configStream = null;
		try {
			configStream = new InputStreamReader(Main.instance.getResource(Main.instance.getVersion() + "/language.yml"), "UTF8");
			YamlConfiguration defaults = YamlConfiguration.loadConfiguration(configStream);
			config.setDefaults(defaults);
		} catch (UnsupportedEncodingException ex) {
			Main.instance.getLogger().log(Level.SEVERE, null, ex);
		} finally {
			if (configStream != null) {
				try {
					configStream.close();
				} catch (IOException ex) {
					Main.instance.getLogger().log(Level.SEVERE, null, ex);
				}
			}
		}
	}

	/**
	 * Send a message to a Player if it isn't empty.
	 * Supports color codes, i.e. &4Red text
	 *
	 * @param playerUuid UUID
	 * @param text String
	 */
	public void sendMessage(UUID playerUuid, String text) {
		if (!text.isEmpty()) {
			Player player = Main.instance.getServer().getPlayer(playerUuid);
			if(player != null) {
				player.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), text));
			}
		}
	}

	/**
	 * Send a message to a CommandSender if it isn't empty.
	 * Supports color codes, i.e. &4Red text
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
	 * Translate entity names to their keys or values.
	 *
	 * @param inputName
	 * @param type      returns either the key or value
	 * @return
	 */
	public String translateEntity(String inputName, String type) {
		// Translate spawner language keys
		inputName = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', "" + inputName));
		String returnName = "default";
		ConfigurationSection csEntities;
		Map<String, Object> entityValues;
		Iterator it;
		try {
			csEntities = Main.instance.getLangHandler().getConfig().getConfigurationSection("Entities");
			entityValues = csEntities.getValues(false);
			it = entityValues.entrySet().iterator();
		} catch (Exception e) {
			Main.instance.getLogger().severe("Your Spawner language.yml is missing entities in the Entities section. This is probably because it's outdated. You can update it manually or to install a new one you can rename or delete the old one. Once finished run the command /spawner reload");
			return null;
		}

		if (type != null) {
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				String testKey = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', "" + pairs.getKey()));
				String testValue = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', "" + pairs.getValue()));
				if (testValue.equalsIgnoreCase(inputName) || testKey.equalsIgnoreCase(inputName)) {
					switch (type) {
						case "value":
							returnName = "" + pairs.getValue();
							break;
						case "key":
							returnName = testKey;
							break;
					}
					it.remove();
					break;
				}

				it.remove();
			}
		}

		return returnName;
	}
}
