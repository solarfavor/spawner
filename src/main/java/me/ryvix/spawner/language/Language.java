/**
 * Spawner - Gather mob spawners with silk touch enchanted tools and the
 * ability to change mob types.
 *
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 Ryan Rhode
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
package me.ryvix.spawner.language;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.ryvix.spawner.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Language {

	private FileConfiguration config;
	private File configFile;
	private final String name;
	private boolean updates = false;

	/**
	 * Constructor
	 *
	 * @param fileName
	 */
	public Language(String fileName) {
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
	public String getText(Keys key, String... vars) {
		String text;
		try {
			text = config.getString("Language." + key.toString());
		} catch (Exception e) {
			Main.instance.getLogger().log(Level.WARNING, e.getLocalizedMessage());
			Main.instance.getLogger().log(Level.WARNING, e.toString());
			Main.instance.getLogger().log(Level.WARNING, "Missing language at: Language.{0}", key.name());
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
	public String getEntity(Entities entity) {
		String text;
		try {
			text = config.getString("Entities." + entity.toString());
		} catch (Exception e) {
			Main.instance.getLogger().log(Level.WARNING, e.getLocalizedMessage());
			Main.instance.getLogger().log(Level.WARNING, e.toString());
			Main.instance.getLogger().log(Level.WARNING, "Missing language at: Entities.{0}", entity.name());
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
	private void saveDefaultConfig() {
		// create file if it doesn't exist
		createConfig();

		// load the file
		getConfig();

		// add any missing default values
		setValues();

		// save file
		saveConfig();

		// make sure to reload the config if there were new additions
		if(updates == true) {
			loadConfig();
		}
	}

	/**
	 * Add default values
	 */
	private void setValues() {
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

		if (!config.contains("Entities")) {
			updates = true;
			config.createSection("Entities");
		}
		if (!config.isSet("Entities.XPOrb")) {
			updates = true;
			config.set("Entities.XPOrb", "&eXPOrb");
		}
		if (!config.isSet("Entities.LeashKnot")) {
			updates = true;
			config.set("Entities.LeashKnot", "&eLeashKnot");
		}
		if (!config.isSet("Entities.Painting")) {
			updates = true;
			config.set("Entities.Painting", "&ePainting");
		}
		if (!config.isSet("Entities.Arrow")) {
			updates = true;
			config.set("Entities.Arrow", "&eArrow");
		}
		if (!config.isSet("Entities.Snowball")) {
			updates = true;
			config.set("Entities.Snowball", "&eSnowball");
		}
		if (!config.isSet("Entities.Fireball")) {
			updates = true;
			config.set("Entities.Fireball", "&eFireball");
		}
		if (!config.isSet("Entities.SmallFireball")) {
			updates = true;
			config.set("Entities.SmallFireball", "&eSmallFireball");
		}
		if (!config.isSet("Entities.ThrownEnderpearl")) {
			updates = true;
			config.set("Entities.ThrownEnderpearl", "&eThrownEnderpearl");
		}
		if (!config.isSet("Entities.EyeOfEnderSignal")) {
			updates = true;
			config.set("Entities.EyeOfEnderSignal", "&eEyeOfEnderSignal");
		}
		if (!config.isSet("Entities.ThrownExpBottle")) {
			updates = true;
			config.set("Entities.ThrownExpBottle", "&eThrownExpBottle");
		}
		if (!config.isSet("Entities.ItemFrame")) {
			updates = true;
			config.set("Entities.ItemFrame", "&eItemFrame");
		}
		if (!config.isSet("Entities.WitherSkull")) {
			updates = true;
			config.set("Entities.WitherSkull", "&eWitherSkull");
		}
		if (!config.isSet("Entities.PrimedTnt")) {
			updates = true;
			config.set("Entities.PrimedTnt", "&ePrimedTnt");
		}
		if (!config.isSet("Entities.MinecartCommandBlock")) {
			updates = true;
			config.set("Entities.MinecartCommandBlock", "&eMinecartCommandBlock");
		}
		if (!config.isSet("Entities.Boat")) {
			updates = true;
			config.set("Entities.Boat", "&eBoat");
		}
		if (!config.isSet("Entities.MinecartRideable")) {
			updates = true;
			config.set("Entities.MinecartRideable", "&eMinecartRideable");
		}
		if (!config.isSet("Entities.MinecartChest")) {
			updates = true;
			config.set("Entities.MinecartChest", "&eMinecartChest");
		}
		if (!config.isSet("Entities.MinecartFurnace")) {
			updates = true;
			config.set("Entities.MinecartFurnace", "&eMinecartFurnace");
		}
		if (!config.isSet("Entities.MinecartTNT")) {
			updates = true;
			config.set("Entities.MinecartTNT", "&eMinecartTNT");
		}
		if (!config.isSet("Entities.MinecartHopper")) {
			updates = true;
			config.set("Entities.MinecartHopper", "&eMinecartHopper");
		}
		if (!config.isSet("Entities.MinecartMobSpawner")) {
			updates = true;
			config.set("Entities.MinecartMobSpawner", "&eMinecartMobSpawner");
		}
		if (!config.isSet("Entities.Creeper")) {
			updates = true;
			config.set("Entities.Creeper", "&eCreeper");
		}
		if (!config.isSet("Entities.Skeleton")) {
			updates = true;
			config.set("Entities.Skeleton", "&eSkeleton");
		}
		if (!config.isSet("Entities.Spider")) {
			updates = true;
			config.set("Entities.Spider", "&eSpider");
		}
		if (!config.isSet("Entities.Giant")) {
			updates = true;
			config.set("Entities.Giant", "&eGiant");
		}
		if (!config.isSet("Entities.Zombie")) {
			updates = true;
			config.set("Entities.Zombie", "&eZombie");
		}
		if (!config.isSet("Entities.Slime")) {
			updates = true;
			config.set("Entities.Slime", "&eSlime");
		}
		if (!config.isSet("Entities.Ghast")) {
			updates = true;
			config.set("Entities.Ghast", "&eGhast");
		}
		if (!config.isSet("Entities.PigZombie")) {
			updates = true;
			config.set("Entities.PigZombie", "&ePigZombie");
		}
		if (!config.isSet("Entities.Enderman")) {
			updates = true;
			config.set("Entities.Enderman", "&eEnderman");
		}
		if (!config.isSet("Entities.CaveSpider")) {
			updates = true;
			config.set("Entities.CaveSpider", "&eCaveSpider");
		}
		if (!config.isSet("Entities.Silverfish")) {
			updates = true;
			config.set("Entities.Silverfish", "&eSilverfish");
		}
		if (!config.isSet("Entities.Blaze")) {
			updates = true;
			config.set("Entities.Blaze", "&eBlaze");
		}
		if (!config.isSet("Entities.LavaSlime")) {
			updates = true;
			config.set("Entities.LavaSlime", "&eLavaSlime");
		}
		if (!config.isSet("Entities.EnderDragon")) {
			updates = true;
			config.set("Entities.EnderDragon", "&eEnderDragon");
		}
		if (!config.isSet("Entities.WitherBoss")) {
			updates = true;
			config.set("Entities.WitherBoss", "&eWitherBoss");
		}
		if (!config.isSet("Entities.Bat")) {
			updates = true;
			config.set("Entities.Bat", "&eBat");
		}
		if (!config.isSet("Entities.Witch")) {
			updates = true;
			config.set("Entities.Witch", "&eWitch");
		}
		if (!config.isSet("Entities.Pig")) {
			updates = true;
			config.set("Entities.Pig", "&ePig");
		}
		if (!config.isSet("Entities.Sheep")) {
			updates = true;
			config.set("Entities.Sheep", "&eSheep");
		}
		if (!config.isSet("Entities.Cow")) {
			updates = true;
			config.set("Entities.Cow", "&eCow");
		}
		if (!config.isSet("Entities.Chicken")) {
			updates = true;
			config.set("Entities.Chicken", "&eChicken");
		}
		if (!config.isSet("Entities.Squid")) {
			updates = true;
			config.set("Entities.Squid", "&eSquid");
		}
		if (!config.isSet("Entities.Wolf")) {
			updates = true;
			config.set("Entities.Wolf", "&eWolf");
		}
		if (!config.isSet("Entities.MushroomCow")) {
			updates = true;
			config.set("Entities.MushroomCow", "&eMushroomCow");
		}
		if (!config.isSet("Entities.SnowMan")) {
			updates = true;
			config.set("Entities.SnowMan", "&eSnowMan");
		}
		if (!config.isSet("Entities.Ozelot")) {
			updates = true;
			config.set("Entities.Ozelot", "&eOzelot");
		}
		if (!config.isSet("Entities.VillagerGolem")) {
			updates = true;
			config.set("Entities.VillagerGolem", "&eVillagerGolem");
		}
		if (!config.isSet("Entities.EntityHorse")) {
			updates = true;
			config.set("Entities.EntityHorse", "&eEntityHorse");
		}
		if (!config.isSet("Entities.Villager")) {
			updates = true;
			config.set("Entities.Villager", "&eVillager");
		}
		if (!config.isSet("Entities.EnderCrystal")) {
			updates = true;
			config.set("Entities.EnderCrystal", "&eEnderCrystal");
		}
		if (!config.isSet("Entities.FireworksRocketEntity")) {
			updates = true;
			config.set("Entities.FireworksRocketEntity", "&eFireworksRocketEntity");
		}
		if (!config.isSet("Entities.Guardian")) {
			updates = true;
			config.set("Entities.Guardian", "&eGuardian");
		}
		if (!config.isSet("Entities.Endermite")) {
			updates = true;
			config.set("Entities.Endermite", "&eEndermite");
		}
		if (!config.isSet("Entities.Rabbit")) {
			updates = true;
			config.set("Entities.Rabbit", "&eRabbit");
		}
	}

	/**
	 * Make folder if it doesn't already exist
	 */
	private void makeFolder() {
		Main.instance.getDataFolder().mkdir();
	}

	/**
	 * Create configuration file.
	 */
	private void createConfig() {
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
	private void saveConfig() {
		try {
			getConfig().save(configFile);
		} catch (IOException ex) {
			Logger.getLogger(Language.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Load configuration file
	 */
	private void loadConfig() {
		config = YamlConfiguration.loadConfiguration(configFile);
		config.options().copyDefaults(true);

		Reader configStream = null;
		try {
			configStream = new InputStreamReader(Main.instance.getResource(name), "UTF8");
			YamlConfiguration defaults = YamlConfiguration.loadConfiguration(configStream);
			config.setDefaults(defaults);
		} catch (UnsupportedEncodingException ex) {
			Logger.getLogger(Language.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			if (configStream != null) {
				try {
					configStream.close();
				} catch (IOException ex) {
					Logger.getLogger(Language.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
	}

	/**
	 * Send a message to a Player if it isn't empty.
	 * Supports color codes, i.e. &4Red text
	 *
	 * @param player
	 * @param text
	 */
	public void sendMessage(String player, String text) {
		if (!text.isEmpty()) {
			Main.instance.getServer().getPlayer(player).sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), text));
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
}
