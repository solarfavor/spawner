/**
 * Spawner - Gather mob spawners with silk touch enchanted tools and the ability
 * to change mob types.
 *
 * Copyright (C) 2012-2015 Ryan Rhode - rrhode@gmail.com
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
	}

	/**
	 * Add default values
	 */
	private void setValues() {

		if (!config.isSet("Language")) {
			config.createSection("Language");
		}
		if (!config.isSet("Language.ConsoleUsageGive")) {
			config.set("Language.ConsoleUsageGive", "Usage: spawner give <entity> <player>");
		}
		if (!config.isSet("Language.LookAtASpawner")) {
			config.set("Language.LookAtASpawner", "&4Look at a spawner to see what kind it is!");
		}
		if (!config.isSet("Language.NoPermission")) {
			config.set("Language.NoPermission", "&4You don't have permission to do that!");
		}
		if (!config.isSet("Language.SpawnerChangedTo")) {
			config.set("Language.SpawnerChangedTo", "&aSpawner type changed to {0}");
		}
		if (!config.isSet("Language.InvalidSpawner")) {
			config.set("Language.InvalidSpawner", "&4Invalid spawner type!");
		}
		if (!config.isSet("Language.GivenSpawner")) {
			config.set("Language.GivenSpawner", "&aYou were given a {0} &aspawner.");
		}
		if (!config.isSet("Language.SpawnerDropped")) {
			config.set("Language.SpawnerDropped", "&aA {0} spawner was dropped at your feet because your inventory is full.");
		}
		if (!config.isSet("Language.YouGaveSpawner")) {
			config.set("Language.YouGaveSpawner", "&aYou gave a {0} &aspawner to {1}.");
		}
		if (!config.isSet("Language.NotDeliveredOffline")) {
			config.set("Language.NotDeliveredOffline", "&4The spawner was not delivered because &e{0} &4is offline.");
		}
		if (!config.isSet("Language.YouPickedUp")) {
			config.set("Language.YouPickedUp", "&aYou picked up a {0} &aspawner.");
		}
		if (!config.isSet("Language.HoldingSpawner")) {
			config.set("Language.HoldingSpawner", "&aYou are holding a {0} &aspawner.");
		}
		if (!config.isSet("Language.SpawnerType")) {
			config.set("Language.SpawnerType", "&aSpawner type: {0}");
		}
		if (!config.isSet("Language.PlacedSpawner")) {
			config.set("Language.PlacedSpawner", "&aPlaced {0} &aspawner");
		}
		if (!config.isSet("Language.NotPossible")) {
			config.set("Language.NotPossible", "&4It was not possible to change that spawner.");
		}
		if (!config.isSet("Language.InvalidRadius")) {
			config.set("Language.InvalidRadius", "&4You entered an invalid radius.");
		}
		if (!config.isSet("Language.InvalidEntity")) {
			config.set("Language.InvalidEntity", "&4You entered an invalid entity.");
		}
		if (!config.isSet("Language.ErrorRemovingEntities")) {
			config.set("Language.ErrorRemovingEntities", "&4There was an error removing entities. Some may not have been removed.");
		}
		if (!config.isSet("Language.EntitiesRemoved")) {
			config.set("Language.EntitiesRemoved", "&a{0} {1} &aremoved.");
		}
		if (!config.isSet("Language.Spawner")) {
			config.set("Language.Spawner", "&aspawner");
		}

		if (!config.isSet("Entities")) {
			config.createSection("Entities");
		}
		if (!config.isSet("Entities.XPOrb")) {
			config.set("Entities.XPOrb", "&eXPOrb");
		}
		if (!config.isSet("Entities.LeashKnot")) {
			config.set("Entities.LeashKnot", "&eLeashKnot");
		}
		if (!config.isSet("Entities.Painting")) {
			config.set("Entities.Painting", "&ePainting");
		}
		if (!config.isSet("Entities.Arrow")) {
			config.set("Entities.Arrow", "&eArrow");
		}
		if (!config.isSet("Entities.Snowball")) {
			config.set("Entities.Snowball", "&eSnowball");
		}
		if (!config.isSet("Entities.Fireball")) {
			config.set("Entities.Fireball", "&eFireball");
		}
		if (!config.isSet("Entities.SmallFireball")) {
			config.set("Entities.SmallFireball", "&eSmallFireball");
		}
		if (!config.isSet("Entities.ThrownEnderpearl")) {
			config.set("Entities.ThrownEnderpearl", "&eThrownEnderpearl");
		}
		if (!config.isSet("Entities.EyeOfEnderSignal")) {
			config.set("Entities.EyeOfEnderSignal", "&eEyeOfEnderSignal");
		}
		if (!config.isSet("Entities.ThrownExpBottle")) {
			config.set("Entities.ThrownExpBottle", "&eThrownExpBottle");
		}
		if (!config.isSet("Entities.ItemFrame")) {
			config.set("Entities.ItemFrame", "&eItemFrame");
		}
		if (!config.isSet("Entities.WitherSkull")) {
			config.set("Entities.WitherSkull", "&eWitherSkull");
		}
		if (!config.isSet("Entities.PrimedTnt")) {
			config.set("Entities.PrimedTnt", "&ePrimedTnt");
		}
		if (!config.isSet("Entities.MinecartCommandBlock")) {
			config.set("Entities.MinecartCommandBlock", "&eMinecartCommandBlock");
		}
		if (!config.isSet("Entities.Boat")) {
			config.set("Entities.Boat", "&eBoat");
		}
		if (!config.isSet("Entities.MinecartRideable")) {
			config.set("Entities.MinecartRideable", "&eMinecartRideable");
		}
		if (!config.isSet("Entities.MinecartChest")) {
			config.set("Entities.MinecartChest", "&eMinecartChest");
		}
		if (!config.isSet("Entities.MinecartFurnace")) {
			config.set("Entities.MinecartFurnace", "&eMinecartFurnace");
		}
		if (!config.isSet("Entities.MinecartTNT")) {
			config.set("Entities.MinecartTNT", "&eMinecartTNT");
		}
		if (!config.isSet("Entities.MinecartHopper")) {
			config.set("Entities.MinecartHopper", "&eMinecartHopper");
		}
		if (!config.isSet("Entities.MinecartMobSpawner")) {
			config.set("Entities.MinecartMobSpawner", "&eMinecartMobSpawner");
		}
		if (!config.isSet("Entities.Creeper")) {
			config.set("Entities.Creeper", "&eCreeper");
		}
		if (!config.isSet("Entities.Skeleton")) {
			config.set("Entities.Skeleton", "&eSkeleton");
		}
		if (!config.isSet("Entities.Spider")) {
			config.set("Entities.Spider", "&eSpider");
		}
		if (!config.isSet("Entities.Giant")) {
			config.set("Entities.Giant", "&eGiant");
		}
		if (!config.isSet("Entities.Zombie")) {
			config.set("Entities.Zombie", "&eZombie");
		}
		if (!config.isSet("Entities.Slime")) {
			config.set("Entities.Slime", "&eSlime");
		}
		if (!config.isSet("Entities.Ghast")) {
			config.set("Entities.Ghast", "&eGhast");
		}
		if (!config.isSet("Entities.PigZombie")) {
			config.set("Entities.PigZombie", "&ePigZombie");
		}
		if (!config.isSet("Entities.Enderman")) {
			config.set("Entities.Enderman", "&eEnderman");
		}
		if (!config.isSet("Entities.CaveSpider")) {
			config.set("Entities.CaveSpider", "&eCaveSpider");
		}
		if (!config.isSet("Entities.Silverfish")) {
			config.set("Entities.Silverfish", "&eSilverfish");
		}
		if (!config.isSet("Entities.Blaze")) {
			config.set("Entities.Blaze", "&eBlaze");
		}
		if (!config.isSet("Entities.LavaSlime")) {
			config.set("Entities.LavaSlime", "&eLavaSlime");
		}
		if (!config.isSet("Entities.EnderDragon")) {
			config.set("Entities.EnderDragon", "&eEnderDragon");
		}
		if (!config.isSet("Entities.WitherBoss")) {
			config.set("Entities.WitherBoss", "&eWitherBoss");
		}
		if (!config.isSet("Entities.Bat")) {
			config.set("Entities.Bat", "&eBat");
		}
		if (!config.isSet("Entities.Witch")) {
			config.set("Entities.Witch", "&eWitch");
		}
		if (!config.isSet("Entities.Pig")) {
			config.set("Entities.Pig", "&ePig");
		}
		if (!config.isSet("Entities.Sheep")) {
			config.set("Entities.Sheep", "&eSheep");
		}
		if (!config.isSet("Entities.Cow")) {
			config.set("Entities.Cow", "&eCow");
		}
		if (!config.isSet("Entities.Chicken")) {
			config.set("Entities.Chicken", "&eChicken");
		}
		if (!config.isSet("Entities.Squid")) {
			config.set("Entities.Squid", "&eSquid");
		}
		if (!config.isSet("Entities.Wolf")) {
			config.set("Entities.Wolf", "&eWolf");
		}
		if (!config.isSet("Entities.MushroomCow")) {
			config.set("Entities.MushroomCow", "&eMushroomCow");
		}
		if (!config.isSet("Entities.SnowMan")) {
			config.set("Entities.SnowMan", "&eSnowMan");
		}
		if (!config.isSet("Entities.Ozelot")) {
			config.set("Entities.Ozelot", "&eOzelot");
		}
		if (!config.isSet("Entities.VillagerGolem")) {
			config.set("Entities.VillagerGolem", "&eVillagerGolem");
		}
		if (!config.isSet("Entities.EntityHorse")) {
			config.set("Entities.EntityHorse", "&eEntityHorse");
		}
		if (!config.isSet("Entities.Villager")) {
			config.set("Entities.Villager", "&eVillager");
		}
		if (!config.isSet("Entities.EnderCrystal")) {
			config.set("Entities.EnderCrystal", "&eEnderCrystal");
		}
		if (!config.isSet("Entities.FireworksRocketEntity")) {
			config.set("Entities.FireworksRocketEntity", "&eFireworksRocketEntity");
		}
		if (!config.isSet("Entities.Guardian")) {
			config.set("Entities.Guardian", "&eGuardian");
		}
		if (!config.isSet("Entities.Endermite")) {
			config.set("Entities.Endermite", "&eEndermite");
		}
		if (!config.isSet("Entities.Rabbit")) {
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
