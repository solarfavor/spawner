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
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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

		// get the spawner name with no default value
		String entity = getSpawnerName(arg, "key", 3);

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

		// TODO: test if necessary anymore, (test 2 or 3 for options arg for getSpawnerName above)
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
	 * Get the name from the durability/typeid value
	 *
	 * @param durability
	 * @param noDefault
	 * @return Formated name
	 */
	public static String nameFromDurability(short durability, boolean... noDefault) {

		SpawnerType spawnerType = getSpawnerType(durability);
		String spawnerName = "Pig";
		if (spawnerType != null) {
			spawnerName = SpawnerType.getTextFromType(spawnerType);
		} else {
			if (noDefault.length > 0 && noDefault[0] == true) {
				return "";
			}
		}

		return spawnerName;
	}

	/**
	 * Get the durability/typeid value from the EntityType
	 *
	 * @param entityType
	 * @return short
	 */
	public static short durabilityFromEntityType(EntityType entityType) {
		SpawnerType spawnerType = SpawnerType.fromEntityType(entityType);
		return spawnerType.getTypeId();
	}

	/**
	 * Return the SpawnerType of the given id/durability.
	 *
	 * @param d
	 * @return
	 */
	static SpawnerType getSpawnerType(short d) {
		SpawnerType type = SpawnerType.fromId(d);
		return type;
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
		String name = getCorrectName(new Spawner(itemStack));
		return SpawnerType.fromName(name);
	}

	/**
	 * Check if the display name is null and if so try to get the spawned type
	 * from the given itemStack.
	 *
	 * @param itemStack
	 * @param options
	 * 0 = default, no convert
	 * unset, 1 = default, convert
	 * 2 = no default, convert
	 * 3 = no default, no convert
	 * @return
	 */
	private static String getCorrectName(Spawner spawner, int... options) {
		String inputName = spawner.getItemMeta().getDisplayName();
		String outName = inputName;
		if (inputName == null) {
			outName = inputName;
		}

		if (outName == null) {

			// optionally provide default
			if (options.length > 0 && (options[0] > 1)) {
				outName = "";
			} else {
				outName = "Pig";
			}
		}
		return outName;
	}

	/**
	 * Get a spawner name.
	 *
	 * @param inputName
	 * @param type
	 * @param options
	 * 0 = default, no convert
	 * unset, 1 = default, convert
	 * 2 = no default, convert
	 * 3 = no default, no convert
	 * @return
	 */
	public static String getSpawnerName(String inputName, String type, int... options) {
		Spawner spawner = new Spawner(Material.MOB_SPAWNER, 1);
		spawner.setName(inputName);

		String outputName = getSpawnerName(spawner, type, options);
		return outputName;
	}

	/**
	 * Get a spawner name.
	 *
	 * @param spawner
	 * @param type
	 * @param options
	 * 0 = default, no convert
	 * unset, 1 = default, convert
	 * 2 = no default, convert
	 * 3 = no default, no convert
	 * @return
	 */
	public static String getSpawnerName(Spawner spawner, String type, int... options) {
		String inputName = getCorrectName(spawner, options);

		String cleanName = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', inputName));
		String testName;
		String returnName = "default";

		if (options.length > 0 && (options[0] == 0 || options[0] == 3)) {
			if (cleanName.contains(" ") == false) {
				testName = cleanName;
			} else {
				testName = cleanName.split(" ")[0];
			}
		} else {
			if (cleanName.contains(" ") == false) {
				testName = convertAlias(cleanName);
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

		// if still default spawner name try getting value from durability
		if (returnName.equals("default")) {
			if (options.length > 0 && (options[0] > 1)) {
				returnName = nameFromDurability(spawner.getDurability(), true);
				if (returnName.isEmpty()) {
					returnName = testName;
				}
			} else {
				returnName = nameFromDurability(spawner.getDurability());
			}
		}

		return returnName;
	}

	/**
	 * Reset spawner name.
	 *
	 * @param spawner
	 * @return
	 */
	public static String resetSpawnerName(Spawner spawner) {
		Spawner result = setSpawnerName(spawner, "");
		return getSpawnerName(result, "value");
	}

	/**
	 * Set the name to the spawner type
	 *
	 * @param spawner
	 * @param name
	 * @return
	 */
	public static Spawner setSpawnerName(Spawner spawner, String name) {
		String spawnerName;
		if (name.isEmpty()) {
			spawnerName = getSpawnerName(spawner, "value");
		} else {
			spawnerName = getSpawnerName(name, "value");
		}
		spawnerName += " " + Main.language.getText(Keys.Spawner);
		spawner.setName(ChatColor.translateAlternateColorCodes('&', spawnerName));

		// currently just to remove the old lore line
		if (spawner.getLore() != null) {
			spawner.setLore("");
		}

		// also set durability
		SpawnerType spawnerType = getSpawnerType(spawner);
		spawner.setDurability(spawnerType.getTypeId());

		return spawner;
	}

	/**
	 * Get spawner EntityType
	 *
	 * @param target
	 * @return
	 */
	public static SpawnerType getSpawner(Block target) {
		CreatureSpawner testSpawner = (CreatureSpawner) target.getState();
		return SpawnerType.fromEntityType(testSpawner.getSpawnedType());
	}

	/**
	 * Set spawner type
	 *
	 * @param target
	 * @param arg
	 * @return
	 */
	public static boolean setSpawner(Block target, String arg) {
		if (!SpawnerFunctions.isValidEntity(arg)) {
			return false;
		}
		SpawnerType type = SpawnerFunctions.getSpawnerType(arg);
		if (type == null) {
			return false;
		}
		CreatureSpawner testSpawner;
		try {
			testSpawner = (CreatureSpawner) target.getState();
		} catch (Exception e) {
			return false;
		}
		if (testSpawner != null) {
			testSpawner.setSpawnedType(type.getEntityType());
			target.getState().update();
			return true;
		}
		return false;
	}

}
