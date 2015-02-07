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
package me.ryvix.spawner;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.ryvix.spawner.language.Keys;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

public class SpawnerFunctions {

	/**
	 * Check config for alias and return the entity type.
	 * Returns input string if no aliases are found.
	 *
	 * @param entity
	 * @return String
	 */
	public static String convertAlias(String entity) {

		ConfigurationSection aliases = Main.instance.config.getConfigurationSection("aliases");

		if (aliases == null) {
			return entity;
		}

		for (String key : aliases.getKeys(false)) {
			List<String> aliasList = aliases.getStringList(key);
			for (String alias : aliasList) {
				if (alias.equalsIgnoreCase(entity)) {
					return key;
				}
			}
		}

		return entity;
	}

	/**
	 * Checks if entity is valid.
	 *
	 * @param arg
	 * @return
	 */
	public static boolean isValidEntity(String arg) {

		String entity = getSpawnerName(arg, "key");

		// allow only valid entity types matched against aliases
		List<String> validEntities = Main.instance.config.getStringList("valid_entities");

		if (validEntities == null) {
			return false;
		}

		// check valid_entities first
		for (String entry : validEntities) {
			if (entry.equalsIgnoreCase(entity)) {
				return true;
			}
		}

		// no valid_entities found so check for aliases
		String aliasCheck = convertAlias(entity);
		return !aliasCheck.isEmpty();
	}

	/**
	 * A chance to return true or false based on the given config key integer.
	 * A configKey with a value less than 100 will have a chance to return false.
	 *
	 * @param configKey
	 * @return
	 */
	public static boolean chance(String configKey) {
		int chance = Main.instance.config.getInt(configKey);
		if (chance < 100) {
			if ((int) (Math.random() * 100) < chance) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Set the name to the spawner type
	 *
	 * @param spawner
	 * @param name
	 * @return
	 */
	public static ItemStack setSpawnerName(ItemStack spawner, String name) {
		Spawner newSpawnerStack = new Spawner();
		ItemStack newSpawner = newSpawnerStack.setName(spawner, ChatColor.translateAlternateColorCodes('&', name + " " + Main.language.getText(Keys.Spawner)));

		// currently just to remove the old lore line
		if (newSpawnerStack.getLore(newSpawner) != null) {
			newSpawner = newSpawnerStack.setLore(newSpawner, "");
		}

		return newSpawner;
	}

	/**
	 * Reads a file to a string Lines starting with # will be ignored
	 *
	 * @param fileName
	 * @return String
	 */
	public static String readFile(String fileName) {
		String contents = null;
		try {
			FileInputStream fstream = new FileInputStream(fileName);

			try (DataInputStream in = new DataInputStream(fstream)) {
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				StringBuilder sb = new StringBuilder();
				String line;

				while ((line = br.readLine()) != null) {
					char check = line.charAt(0);
					if (check == "#".charAt(0)) {
						continue;
					}
					sb.append(line);
					sb.append("\n");
				}
				contents = sb.toString();
			}
		} catch (IOException e) {
			Logger.getLogger(SpawnerFunctions.class.getName()).log(Level.SEVERE, null, e);
		}

		return contents;
	}

	/**
	 * Remove entities around the player
	 *
	 * @param player
	 * @param entityArg
	 * @param radius
	 * @return
	 */
	public static boolean removeEntities(Player player, String entityArg, int radius) {
		int count = 0;
		SpawnerType type = getSpawnerType(entityArg);
		if (type == null) {
			Main.language.sendMessage(player, Main.language.getText(Keys.InvalidEntity));
			return false;
		}

		List<Entity> entities = player.getNearbyEntities(radius, radius, radius);

		try {
			for (Entity entity : entities) {
				if (entity.getType() == type.getEntityType()) {
					entity.remove();
					count++;
				}
			}
		} catch (Exception e) {
			Main.language.sendMessage(player, Main.language.getText(Keys.ErrorRemovingEntities));
		}

		String[] vars = new String[2];
		vars[0] = "" + count;
		vars[1] = SpawnerType.getTextFromName(entityArg);
		Main.language.sendMessage(player, Main.language.getText(Keys.EntitiesRemoved, vars));

		return true;
	}

	/**
	 * Try to find a spawner block targeted by the player.
	 *
	 * @param player
	 * @param distance
	 * @return
	 */
	public static Block findSpawnerBlock(Player player, int distance) {
		BlockIterator bit = new BlockIterator(player, distance);
		Block blockToCheck = null;
		while (bit.hasNext()) {
			blockToCheck = bit.next();
			if (blockToCheck.getType() == Material.MOB_SPAWNER && blockToCheck.getType() != Material.AIR) {
				return blockToCheck;
			}
		}

		return blockToCheck;
	}

	/**
	 * Return the SpawnerType of the given string.
	 *
	 * @param arg
	 * @return
	 */
	public static SpawnerType getSpawnerType(String arg) {
		return SpawnerType.fromName(arg);
	}

	/**
	 * Return the SpawnerType of the given ItemStack.
	 *
	 * @param itemStack
	 * @return SpawnerType
	 */
	public static SpawnerType getSpawnerType(ItemStack itemStack) {
		return SpawnerType.fromName(itemStack.getItemMeta().getDisplayName());
	}

	/**
	 * Get a spawner name.
	 *
	 * @param itemStack
	 * @param type
	 * @return
	 */
	public static String getSpawnerName(ItemStack itemStack, String type) {
		return getSpawnerName(itemStack.getItemMeta().getDisplayName(), type);
	}

	/**
	 * Get a spawner name.
	 *
	 * @param inputName
	 * @param type
	 * @param convert
	 * @return
	 */
	public static String getSpawnerName(String inputName, String type, boolean... convert) {

		String cleanName = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', inputName));
		String testName;
		String returnName = cleanName;
		if (!cleanName.contains(" ")) {
			if (convert.length > 0 && convert[0] == false) {
				testName = cleanName;
			} else {
				testName = convertAlias(cleanName);
			}
		} else {
			if (convert.length > 0 && convert[0] == false) {
				testName = cleanName.split(" ")[0];
			} else {
				testName = convertAlias(cleanName.split(" ")[0]);
			}
		}

		// Translate spawner language keys
		ConfigurationSection csEntities = Main.language.getConfig().getConfigurationSection("Entities");
		Map<String, Object> entityValues = csEntities.getValues(false);
		Iterator it = entityValues.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			String testKey = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', "" + pairs.getKey()));
			String testValue = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', "" + pairs.getValue()));
			if (type != null && (testValue.equalsIgnoreCase(testName) || testKey.equalsIgnoreCase(testName))) {
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

		return returnName;
	}

	/**
	 * Reset spawner name.
	 *
	 * @param itemStack
	 * @return
	 */
	public static String resetSpawnerName(ItemStack itemStack) {
		return setSpawnerName(itemStack);
	}

	/**
	 * Set spawner name.
	 *
	 * @param itemStack
	 * @return
	 */
	private static String setSpawnerName(ItemStack itemStack) {
		String name = getSpawnerName(itemStack, "value");
		Spawner newSpawnerStack = new Spawner();
		ItemStack newSpawner = newSpawnerStack.setName(itemStack, ChatColor.translateAlternateColorCodes('&', name + " " + Main.language.getText(Keys.Spawner)));

		// currently just to remove the old lore line
		if (newSpawnerStack.getLore(newSpawner) != null) {
			newSpawnerStack.setLore(newSpawner, "");
		}

		return name;
	}
}
