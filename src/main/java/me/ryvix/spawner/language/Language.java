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
		Map<?, ?> entry = language.get(key.ordinal());
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
	 * Get entity
	 *
	 * @param entity
	 * @return
	 */
	public String getEntity(Entities entity) {
		
		Map<?, ?> entry = entities.get(entity.ordinal());
		String text = (String) entry.get(entity.toString());

		// check for empty text
		if (text.isEmpty()) {
			return "";
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
			getConfig().addDefault("Language.GivenSpawner", "&aYou were given a {0} &aspawner.");
		}
		if (!getConfig().contains("Language.SpawnerDropped")) {
			getConfig().addDefault("Language.SpawnerDropped", "&aA {0} spawner was dropped at your feet because your inventory is full.");
		}
		if (!getConfig().contains("Language.YouGaveSpawner")) {
			getConfig().addDefault("Language.YouGaveSpawner", "&aYou gave a {0} &aspawner to {1}.");
		}
		if (!getConfig().contains("Language.NotDeliveredOffline")) {
			getConfig().addDefault("Language.NotDeliveredOffline", "&4The spawner was not delivered because &e{0} &ais offline.");
		}
		if (!getConfig().contains("Language.YouPickedUp")) {
			getConfig().addDefault("Language.YouPickedUp", "&aYou picked up a {0} &aspawner.");
		}
		if (!getConfig().contains("Language.HoldingSpawner")) {
			getConfig().addDefault("Language.HoldingSpawner", "&aYou are holding a {0} &aspawner.");
		}
		if (!getConfig().contains("Language.SpawnerType")) {
			getConfig().addDefault("Language.SpawnerType", "&aSpawner type: {0}");
		}
		if (!getConfig().contains("Language.PlacedSpawner")) {
			getConfig().addDefault("Language.PlacedSpawner", "&aPlaced {0} &aspawner");
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
			getConfig().addDefault("Language.EntitiesRemoved", "&a{0} {1} &aremoved.");
		}
		if (!getConfig().contains("Language.Spawner")) {
			getConfig().addDefault("Language.Spawner", "spawner");
		}

		if (!getConfig().contains("Entities.XPOrb")) {
			getConfig().addDefault("Entities.XPOrb", "&eXPOrb");
		}
		if (!getConfig().contains("Entities.LeashKnot")) {
			getConfig().addDefault("Entities.LeashKnot", "&eLeashKnot");
		}
		if (!getConfig().contains("Entities.Painting")) {
			getConfig().addDefault("Entities.Painting", "&ePainting");
		}
		if (!getConfig().contains("Entities.Arrow")) {
			getConfig().addDefault("Entities.Arrow", "&eArrow");
		}
		if (!getConfig().contains("Entities.Snowball")) {
			getConfig().addDefault("Entities.Snowball", "&eSnowball");
		}
		if (!getConfig().contains("Entities.Fireball")) {
			getConfig().addDefault("Entities.Fireball", "&eFireball");
		}
		if (!getConfig().contains("Entities.SmallFireball")) {
			getConfig().addDefault("Entities.SmallFireball", "&eSmallFireball");
		}
		if (!getConfig().contains("Entities.ThrownEnderpearl")) {
			getConfig().addDefault("Entities.ThrownEnderpearl", "&eThrownEnderpearl");
		}
		if (!getConfig().contains("Entities.EyeOfEnderSignal")) {
			getConfig().addDefault("Entities.EyeOfEnderSignal", "&eEyeOfEnderSignal");
		}
		if (!getConfig().contains("Entities.ThrownExpBottle")) {
			getConfig().addDefault("Entities.ThrownExpBottle", "&eThrownExpBottle");
		}
		if (!getConfig().contains("Entities.ItemFrame")) {
			getConfig().addDefault("Entities.ItemFrame", "&eItemFrame");
		}
		if (!getConfig().contains("Entities.WitherSkull")) {
			getConfig().addDefault("Entities.WitherSkull", "&eWitherSkull");
		}
		if (!getConfig().contains("Entities.PrimedTnt")) {
			getConfig().addDefault("Entities.PrimedTnt", "&ePrimedTnt");
		}
		if (!getConfig().contains("Entities.MinecartCommandBlock")) {
			getConfig().addDefault("Entities.MinecartCommandBlock", "&eMinecartCommandBlock");
		}
		if (!getConfig().contains("Entities.Boat")) {
			getConfig().addDefault("Entities.Boat", "&eBoat");
		}
		if (!getConfig().contains("Entities.MinecartRideable")) {
			getConfig().addDefault("Entities.MinecartRideable", "&eMinecartRideable");
		}
		if (!getConfig().contains("Entities.MinecartChest")) {
			getConfig().addDefault("Entities.MinecartChest", "&eMinecartChest");
		}
		if (!getConfig().contains("Entities.MinecartFurnace")) {
			getConfig().addDefault("Entities.MinecartFurnace", "&eMinecartFurnace");
		}
		if (!getConfig().contains("Entities.MinecartTNT")) {
			getConfig().addDefault("Entities.MinecartTNT", "&eMinecartTNT");
		}
		if (!getConfig().contains("Entities.MinecartHopper")) {
			getConfig().addDefault("Entities.MinecartHopper", "&eMinecartHopper");
		}
		if (!getConfig().contains("Entities.MinecartMobSpawner")) {
			getConfig().addDefault("Entities.MinecartMobSpawner", "&eMinecartMobSpawner");
		}
		if (!getConfig().contains("Entities.Creeper")) {
			getConfig().addDefault("Entities.Creeper", "&eCreeper");
		}
		if (!getConfig().contains("Entities.Skeleton")) {
			getConfig().addDefault("Entities.Skeleton", "&eSkeleton");
		}
		if (!getConfig().contains("Entities.Spider")) {
			getConfig().addDefault("Entities.Spider", "&eSpider");
		}
		if (!getConfig().contains("Entities.Giant")) {
			getConfig().addDefault("Entities.Giant", "&eGiant");
		}
		if (!getConfig().contains("Entities.Zombie")) {
			getConfig().addDefault("Entities.Zombie", "&eZombie");
		}
		if (!getConfig().contains("Entities.Slime")) {
			getConfig().addDefault("Entities.Slime", "&eSlime");
		}
		if (!getConfig().contains("Entities.Ghast")) {
			getConfig().addDefault("Entities.Ghast", "&eGhast");
		}
		if (!getConfig().contains("Entities.PigZombie")) {
			getConfig().addDefault("Entities.PigZombie", "&ePigZombie");
		}
		if (!getConfig().contains("Entities.Enderman")) {
			getConfig().addDefault("Entities.Enderman", "&eEnderman");
		}
		if (!getConfig().contains("Entities.CaveSpider")) {
			getConfig().addDefault("Entities.CaveSpider", "&eCaveSpider");
		}
		if (!getConfig().contains("Entities.Silverfish")) {
			getConfig().addDefault("Entities.Silverfish", "&eSilverfish");
		}
		if (!getConfig().contains("Entities.Blaze")) {
			getConfig().addDefault("Entities.Blaze", "&eBlaze");
		}
		if (!getConfig().contains("Entities.LavaSlime")) {
			getConfig().addDefault("Entities.LavaSlime", "&eLavaSlime");
		}
		if (!getConfig().contains("Entities.EnderDragon")) {
			getConfig().addDefault("Entities.EnderDragon", "&eEnderDragon");
		}
		if (!getConfig().contains("Entities.WitherBoss")) {
			getConfig().addDefault("Entities.WitherBoss", "&eWitherBoss");
		}
		if (!getConfig().contains("Entities.Bat")) {
			getConfig().addDefault("Entities.Bat", "&eBat");
		}
		if (!getConfig().contains("Entities.Witch")) {
			getConfig().addDefault("Entities.Witch", "&eWitch");
		}
		if (!getConfig().contains("Entities.Pig")) {
			getConfig().addDefault("Entities.Pig", "&ePig");
		}
		if (!getConfig().contains("Entities.Sheep")) {
			getConfig().addDefault("Entities.Sheep", "&eSheep");
		}
		if (!getConfig().contains("Entities.Cow")) {
			getConfig().addDefault("Entities.Cow", "&eCow");
		}
		if (!getConfig().contains("Entities.Chicken")) {
			getConfig().addDefault("Entities.Chicken", "&eChicken");
		}
		if (!getConfig().contains("Entities.Squid")) {
			getConfig().addDefault("Entities.Squid", "&eSquid");
		}
		if (!getConfig().contains("Entities.Wolf")) {
			getConfig().addDefault("Entities.Wolf", "&eWolf");
		}
		if (!getConfig().contains("Entities.MushroomCow")) {
			getConfig().addDefault("Entities.MushroomCow", "&eMushroomCow");
		}
		if (!getConfig().contains("Entities.SnowMan")) {
			getConfig().addDefault("Entities.SnowMan", "&eSnowMan");
		}
		if (!getConfig().contains("Entities.Ozelot")) {
			getConfig().addDefault("Entities.Ozelot", "&eOzelot");
		}
		if (!getConfig().contains("Entities.VillagerGolem")) {
			getConfig().addDefault("Entities.VillagerGolem", "&eVillagerGolem");
		}
		if (!getConfig().contains("Entities.EntityHorse")) {
			getConfig().addDefault("Entities.EntityHorse", "&eEntityHorse");
		}
		if (!getConfig().contains("Entities.Villager")) {
			getConfig().addDefault("Entities.Villager", "&eVillager");
		}
		if (!getConfig().contains("Entities.EnderCrystal")) {
			getConfig().addDefault("Entities.EnderCrystal", "&eEnderCrystal");
		}

		try {
			// try to save the file
			getConfig().save(configFile);
		} catch (IOException ex) {
			Logger.getLogger(Language.class.getName()).log(Level.SEVERE, null, ex);
		}

		language = getConfig().getMapList("Language");
		entities = getConfig().getMapList("Entities");
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
		getConfig().options().copyDefaults(true);
		try {
			getConfig().save(configFile);
		} catch (IOException ex) {
			Logger.getLogger(Language.class.getName()).log(Level.SEVERE, null, ex);
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
