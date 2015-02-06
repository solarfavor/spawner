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

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.ryvix.spawner.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Language {

	protected FileConfiguration config;
	protected File configFile;
	protected String name;
	public List<Map<?, ?>> language;
	public List<Map<?, ?>> entities;

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
		Main.instance.getLogger().log(Level.INFO, "Loading {0}", name);

		saveDefaultConfig();
		loadConfig();

		language = getConfig().getMapList("Language");
		entities = getConfig().getMapList("Entities");
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

	/**
	 * Get text
	 *
	 * @param key
	 * @param vars
	 * @return
	 */
	public String getText(Keys key, String... vars) {
		String text = "";
		try {
			Map<?, ?> entry = language.get(key.ordinal());
			text = (String) entry.get(key.toString());
		} catch (Exception e) {
			Main.instance.getLogger().log(Level.WARNING, e.getLocalizedMessage());
			Main.instance.getLogger().log(Level.WARNING, e.toString());
			Main.instance.getLogger().log(Level.WARNING, "Missing language at: Language.{0}", key.name());
			Main.instance.getLogger().log(Level.WARNING, "Add it or regenerate your language.yml by deleting it and then run /spawner reload");

			// return default spawner name
			return "";
		}

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
	 * Get entity
	 *
	 * @param entity
	 * @return
	 */
	public String getEntity(Entities entity) {
		String text = "";
		try {
			Map<?, ?> entry = entities.get(entity.ordinal());
			text = (String) entry.get(entity.toString());

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
	 * Make folder if it doesn't already exist
	 */
	private void makeFolder() {
		Main.instance.getDataFolder().mkdir();
	}

	/**
	 * Save default configuration file
	 */
	public void saveDefaultConfig() {
		createConfig();

		// add defaults
		ConfigurationSection csLanguage = getConfig().getConfigurationSection("Language");
		if (csLanguage == null) {
			csLanguage = getConfig().createSection("Language");
		}

		if (!csLanguage.contains("ConsoleUsageGive")) {
			csLanguage.addDefault("ConsoleUsageGive", "Usage: spawner give <entity> <player>");
		}
		if (!csLanguage.contains("LookAtASpawner")) {
			csLanguage.addDefault("LookAtASpawner", "&4Look at a spawner to see what kind it is!");
		}
		if (!csLanguage.contains("NoPermission")) {
			csLanguage.addDefault("NoPermission", "&4You don''t have permission to do that!");
		}
		if (!csLanguage.contains("SpawnerChangedTo")) {
			csLanguage.addDefault("SpawnerChangedTo", "&aSpawner type changed to {0}");
		}
		if (!csLanguage.contains("InvalidSpawner")) {
			csLanguage.addDefault("InvalidSpawner", "&4Invalid spawner type!");
		}
		if (!csLanguage.contains("GivenSpawner")) {
			csLanguage.addDefault("GivenSpawner", "&aYou were given a {0} &aspawner.");
		}
		if (!csLanguage.contains("SpawnerDropped")) {
			csLanguage.addDefault("SpawnerDropped", "&aA {0} spawner was dropped at your feet because your inventory is full.");
		}
		if (!csLanguage.contains("YouGaveSpawner")) {
			csLanguage.addDefault("YouGaveSpawner", "&aYou gave a {0} &aspawner to {1}.");
		}
		if (!csLanguage.contains("NotDeliveredOffline")) {
			csLanguage.addDefault("NotDeliveredOffline", "&4The spawner was not delivered because &e{0} &4is offline.");
		}
		if (!csLanguage.contains("YouPickedUp")) {
			csLanguage.addDefault("YouPickedUp", "&aYou picked up a {0} &aspawner.");
		}
		if (!csLanguage.contains("HoldingSpawner")) {
			csLanguage.addDefault("HoldingSpawner", "&aYou are holding a {0} &aspawner.");
		}
		if (!csLanguage.contains("SpawnerType")) {
			csLanguage.addDefault("SpawnerType", "&aSpawner type: {0}");
		}
		if (!csLanguage.contains("PlacedSpawner")) {
			csLanguage.addDefault("PlacedSpawner", "&aPlaced {0} &aspawner");
		}
		if (!csLanguage.contains("NotPossible")) {
			csLanguage.addDefault("NotPossible", "&4It was not possible to change that spawner.");
		}
		if (!csLanguage.contains("InvalidRadius")) {
			csLanguage.addDefault("InvalidRadius", "&4You entered an invalid radius.");
		}
		if (!csLanguage.contains("InvalidEntity")) {
			csLanguage.addDefault("InvalidEntity", "&4You entered an invalid entity.");
		}
		if (!csLanguage.contains("ErrorRemovingEntities")) {
			csLanguage.addDefault("ErrorRemovingEntities", "&4There was an error removing entities. Some may not have been removed.");
		}
		if (!csLanguage.contains("EntitiesRemoved")) {
			csLanguage.addDefault("EntitiesRemoved", "&a{0} {1} &aremoved.");
		}
		if (!csLanguage.contains("Spawner")) {
			csLanguage.addDefault("Spawner", "&aspawner");
		}

		ConfigurationSection csEntities = getConfig().getConfigurationSection("Entities");
		if (csEntities == null) {
			csEntities = getConfig().createSection("Entities");
		}

		if (!csEntities.contains("XPOrb")) {
			csEntities.addDefault("XPOrb", "&eXPOrb");
		}
		if (!csEntities.contains("LeashKnot")) {
			csEntities.addDefault("LeashKnot", "&eLeashKnot");
		}
		if (!csEntities.contains("Painting")) {
			csEntities.addDefault("Painting", "&ePainting");
		}
		if (!csEntities.contains("Arrow")) {
			csEntities.addDefault("Arrow", "&eArrow");
		}
		if (!csEntities.contains("Snowball")) {
			csEntities.addDefault("Snowball", "&eSnowball");
		}
		if (!csEntities.contains("Fireball")) {
			csEntities.addDefault("Fireball", "&eFireball");
		}
		if (!csEntities.contains("SmallFireball")) {
			csEntities.addDefault("SmallFireball", "&eSmallFireball");
		}
		if (!csEntities.contains("ThrownEnderpearl")) {
			csEntities.addDefault("ThrownEnderpearl", "&eThrownEnderpearl");
		}
		if (!csEntities.contains("EyeOfEnderSignal")) {
			csEntities.addDefault("EyeOfEnderSignal", "&eEyeOfEnderSignal");
		}
		if (!csEntities.contains("ThrownExpBottle")) {
			csEntities.addDefault("ThrownExpBottle", "&eThrownExpBottle");
		}
		if (!csEntities.contains("ItemFrame")) {
			csEntities.addDefault("ItemFrame", "&eItemFrame");
		}
		if (!csEntities.contains("WitherSkull")) {
			csEntities.addDefault("WitherSkull", "&eWitherSkull");
		}
		if (!csEntities.contains("PrimedTnt")) {
			csEntities.addDefault("PrimedTnt", "&ePrimedTnt");
		}
		if (!csEntities.contains("MinecartCommandBlock")) {
			csEntities.addDefault("MinecartCommandBlock", "&eMinecartCommandBlock");
		}
		if (!csEntities.contains("Boat")) {
			csEntities.addDefault("Boat", "&eBoat");
		}
		if (!csEntities.contains("MinecartRideable")) {
			csEntities.addDefault("MinecartRideable", "&eMinecartRideable");
		}
		if (!csEntities.contains("MinecartChest")) {
			csEntities.addDefault("MinecartChest", "&eMinecartChest");
		}
		if (!csEntities.contains("MinecartFurnace")) {
			csEntities.addDefault("MinecartFurnace", "&eMinecartFurnace");
		}
		if (!csEntities.contains("MinecartTNT")) {
			csEntities.addDefault("MinecartTNT", "&eMinecartTNT");
		}
		if (!csEntities.contains("MinecartHopper")) {
			csEntities.addDefault("MinecartHopper", "&eMinecartHopper");
		}
		if (!csEntities.contains("MinecartMobSpawner")) {
			csEntities.addDefault("MinecartMobSpawner", "&eMinecartMobSpawner");
		}
		if (!csEntities.contains("Creeper")) {
			csEntities.addDefault("Creeper", "&eCreeper");
		}
		if (!csEntities.contains("Skeleton")) {
			csEntities.addDefault("Skeleton", "&eSkeleton");
		}
		if (!csEntities.contains("Spider")) {
			csEntities.addDefault("Spider", "&eSpider");
		}
		if (!csEntities.contains("Giant")) {
			csEntities.addDefault("Giant", "&eGiant");
		}
		if (!csEntities.contains("Zombie")) {
			csEntities.addDefault("Zombie", "&eZombie");
		}
		if (!csEntities.contains("Slime")) {
			csEntities.addDefault("Slime", "&eSlime");
		}
		if (!csEntities.contains("Ghast")) {
			csEntities.addDefault("Ghast", "&eGhast");
		}
		if (!csEntities.contains("PigZombie")) {
			csEntities.addDefault("PigZombie", "&ePigZombie");
		}
		if (!csEntities.contains("Enderman")) {
			csEntities.addDefault("Enderman", "&eEnderman");
		}
		if (!csEntities.contains("CaveSpider")) {
			csEntities.addDefault("CaveSpider", "&eCaveSpider");
		}
		if (!csEntities.contains("Silverfish")) {
			csEntities.addDefault("Silverfish", "&eSilverfish");
		}
		if (!csEntities.contains("Blaze")) {
			csEntities.addDefault("Blaze", "&eBlaze");
		}
		if (!csEntities.contains("LavaSlime")) {
			csEntities.addDefault("LavaSlime", "&eLavaSlime");
		}
		if (!csEntities.contains("EnderDragon")) {
			csEntities.addDefault("EnderDragon", "&eEnderDragon");
		}
		if (!csEntities.contains("WitherBoss")) {
			csEntities.addDefault("WitherBoss", "&eWitherBoss");
		}
		if (!csEntities.contains("Bat")) {
			csEntities.addDefault("Bat", "&eBat");
		}
		if (!csEntities.contains("Witch")) {
			csEntities.addDefault("Witch", "&eWitch");
		}
		if (!csEntities.contains("Pig")) {
			csEntities.addDefault("Pig", "&ePig");
		}
		if (!csEntities.contains("Sheep")) {
			csEntities.addDefault("Sheep", "&eSheep");
		}
		if (!csEntities.contains("Cow")) {
			csEntities.addDefault("Cow", "&eCow");
		}
		if (!csEntities.contains("Chicken")) {
			csEntities.addDefault("Chicken", "&eChicken");
		}
		if (!csEntities.contains("Squid")) {
			csEntities.addDefault("Squid", "&eSquid");
		}
		if (!csEntities.contains("Wolf")) {
			csEntities.addDefault("Wolf", "&eWolf");
		}
		if (!csEntities.contains("MushroomCow")) {
			csEntities.addDefault("MushroomCow", "&eMushroomCow");
		}
		if (!csEntities.contains("SnowMan")) {
			csEntities.addDefault("SnowMan", "&eSnowMan");
		}
		if (!csEntities.contains("Ozelot")) {
			csEntities.addDefault("Ozelot", "&eOzelot");
		}
		if (!csEntities.contains("VillagerGolem")) {
			csEntities.addDefault("VillagerGolem", "&eVillagerGolem");
		}
		if (!csEntities.contains("EntityHorse")) {
			csEntities.addDefault("EntityHorse", "&eEntityHorse");
		}
		if (!csEntities.contains("Villager")) {
			csEntities.addDefault("Villager", "&eVillager");
		}
		if (!csEntities.contains("EnderCrystal")) {
			csEntities.addDefault("EnderCrystal", "&eEnderCrystal");
		}
		if (!csEntities.contains("FireworksRocketEntity")) {
			csEntities.addDefault("FireworksRocketEntity", "&eFireworksRocketEntity");
		}
		if (!csEntities.contains("Guardian")) {
			csEntities.addDefault("Guardian", "&eGuardian");
		}
		if (!csEntities.contains("Endermite")) {
			csEntities.addDefault("Endermite", "&eEndermite");
		}
		if (!csEntities.contains("Rabbit")) {
			csEntities.addDefault("Rabbit", "&eRabbit");
		}

		getConfig().options().copyDefaults(true);

		// add header
		getConfig().options().header("Spawner Language\n\n");
		getConfig().options().copyHeader(true);

		saveConfig();
	}

	/**
	 * Load configuration file
	 */
	public void loadConfig() {
		config = YamlConfiguration.loadConfiguration(configFile);

		final InputStream configStream = Main.instance.getResource(name);
		if (configStream == null) {
			return;
		}

		final byte[] bytes;
		final YamlConfiguration defaults = new YamlConfiguration();
		try {
			bytes = ByteStreams.toByteArray(configStream);
		} catch (final IOException e) {
			return;
		}

		final String text = new String(bytes, Charset.defaultCharset());
		if (!text.equals(new String(bytes, Charsets.UTF_8))) {
			Main.instance.getLogger().log(Level.WARNING, "{0} may not have been loaded properly due to the default character set encoding on the system.", name);
		}

		try {
			defaults.loadFromString(text);
		} catch (final InvalidConfigurationException e) {
		}

		config.setDefaults(defaults);
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
	 * Create config file.
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
	 * Save config file.
	 */
	public void saveConfig() {
		try {
			getConfig().save(configFile);
		} catch (IOException ex) {
			Logger.getLogger(Language.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
