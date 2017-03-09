/**
 * Spawner - Gather mob spawners with silk touch enchanted tools and the
 * ability to change mob types.
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2017 Ryan Rhode
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
package me.ryvix.spawner;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.BlockIterator;

import java.io.*;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Useful Info:
 * http://minecraft.gamepedia.com/Tutorials/Command_NBT_tags
 * http://minecraft.gamepedia.com/Monster_Spawner
 */
public class SpawnerFunctions {

	public static Spawner makeSpawner(Material material, int amount, String name) {
		Spawner spawner = new Spawner(material, amount, name);
		return new Spawner(Main.instance.getNmsHandler().setSpawnerNBT(spawner), amount);
	}

	public static Spawner makeSpawner(ItemStack item) {
		Spawner spawner = new Spawner(item);
		return new Spawner(Main.instance.getNmsHandler().setSpawnerNBT(spawner), 1);
	}

	public static Spawner makeSpawner(String name) {
		Spawner spawner = new Spawner(name);
		return new Spawner(Main.instance.getNmsHandler().setSpawnerNBT(spawner), 1);
	}

	public static Spawner makeSpawner(String name, int amount) {
		Spawner spawner = new Spawner(name, amount);
		return new Spawner(Main.instance.getNmsHandler().setSpawnerNBT(spawner), amount);
	}

	/**
	 * Check config for alias and return the entity type.
	 * Returns input string if no aliases are found.
	 *
	 * @param entity String
	 * @return String
	 */
	public static String convertAlias(String entity) {

		ConfigurationSection aliases = Main.getSpawnerConfig().getConfigurationSection("aliases");

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
	 * @param arg String
	 * @return boolean
	 */
	public static boolean isValidEntity(String arg) {

		// get the spawner name with no default value
		String entity = getSpawnerName(arg, "key", 3);

		// allow only valid entity types matched against aliases
		List<String> validEntities = Main.getSpawnerConfig().getStringList("valid_entities");

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
		return !(!aliasCheck.isEmpty() && aliasCheck.equalsIgnoreCase(arg));
	}

	/**
	 * A chance to return true or false based on the given config key integer.
	 * A configKey with a value less than 100 will have a chance to return false.
	 *
	 * @param configKey String
	 * @return boolean
	 */
	public static boolean chance(String configKey) {
		int chance = Main.getSpawnerConfig().getInt(configKey.toLowerCase());
		if (chance == 0) {
			return false;
		}

		if (chance < 100) {
			int randomChance = (int) (Math.random() * 100);
			if (chance < randomChance) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Reads a file to a string Lines starting with # will be ignored
	 *
	 * @param fileName String
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
	 * @param playerUuid String
	 * @param entityArg String
	 * @param radius int
	 * @return boolean
	 */
	public static boolean removeEntities(UUID playerUuid, String entityArg, int radius) {
		Player player = Main.instance.getServer().getPlayer(playerUuid);
		int count = 0;
		EntityType type = getSpawnerType(entityArg);
		if (type == null) {
			Main.instance.getLangHandler().sendMessage(player, Main.instance.getLangHandler().getText("InvalidEntity"));
			return false;
		}

		List<Entity> entities = player.getNearbyEntities(radius, radius, radius);

		try {
			for (Entity entity : entities) {
				if (entity.getType() == type) {
					entity.remove();
					count++;
				}
			}
		} catch (Exception e) {
			Main.instance.getLangHandler().sendMessage(player, Main.instance.getLangHandler().getText("ErrorRemovingEntities"));
		}

		String[] vars = new String[2];
		vars[0] = "" + count;
		vars[1] = getTextFromName(entityArg);
		Main.instance.getLangHandler().sendMessage(player, Main.instance.getLangHandler().getText("EntitiesRemoved", vars));

		return true;
	}

	/**
	 * Try to find a spawner block targeted by the player.
	 *
	 * @param playerUuid Player
	 * @param distance int
	 * @return Block
	 */
	public static Block findSpawnerBlock(UUID playerUuid, int distance) {
		Player player = Main.instance.getServer().getPlayer(playerUuid);
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
	 * @param durability short
	 * @param noDefault boolean
	 * @return String
	 */
	public static String nameFromDurability(short durability, boolean... noDefault) {

		EntityType spawnerType = getSpawnerType(durability);
		String spawnerName = "default";
		if (spawnerType != null) {
			spawnerName = getTextFromType(spawnerType);
		} else if (noDefault.length > 0 && noDefault[0]) {
			return "";
		}

		if (spawnerName.equals("default")) {
			try {
				throw new Pigception();
			} catch (Pigception p) {
				p.printStackTrace();
			}
			spawnerName = "Pig";
		}

		return spawnerName;
	}

	/**
	 * Get the durability/typeid value from the EntityType
	 *
	 * @param entityType EntityType
	 * @return short
	 */
	public static short durabilityFromEntityType(EntityType entityType) {
		EntityType spawnerType = fromEntityType(entityType);
		return spawnerType.getTypeId();
	}

	/**
	 * Return the EntityType of the given id/durability.
	 *
	 * @param d short
	 * @return EntityType
	 */
	public static EntityType getSpawnerType(short d) {
		EntityType type = EntityType.fromId(d);
		return type;
	}

	/**
	 * Return the EntityType of the given string.
	 *
	 * @param arg String
	 * @return EntityType
	 */
	public static EntityType getSpawnerType(String arg) {
		return EntityType.fromName(arg);
	}

	/**
	 * Return the EntityType of the given ItemStack.
	 *
	 * @param spawner Spawner
	 * @return EntityType
	 */
	public static EntityType getSpawnerType(Spawner spawner) {
		String name = Main.instance.getNmsHandler().getEntityNameFromSpawnerNBT(spawner);
		return EntityType.fromName(name);
	}

	/**
	 * Return the EntityType of the given ItemStack.
	 *
	 * @param is ItemStack
	 * @return EntityType
	 */
	public static EntityType getSpawnerType(ItemStack is) {
		Spawner spawner = SpawnerFunctions.makeSpawner(is);
		return spawner.getSpawnerType();
	}

	/**
	 * Get a spawner name.
	 *
	 * @param inputName String
	 * @param type String
	 * @param options   0 = default, no convert
	 *                  unset, 1 = default, convert
	 *                  2 = no default, convert
	 *                  3 = no default, no convert
	 * @return String
	 */
	public static String getSpawnerName(String inputName, String type, int... options) {
		Spawner spawner = makeSpawner(Material.MOB_SPAWNER, 1, inputName);

		return getSpawnerName(spawner, type, options);
	}

	/**
	 * Get a spawner name.
	 *
	 * @param spawner Spawner
	 * @param type String
	 * @param options 0 = default, no convert
	 *                unset, 1 = default, convert
	 *                2 = no default, convert
	 *                3 = no default, no convert
	 * @return String
	 */
	public static String getSpawnerName(Spawner spawner, String type, int... options) {
		String cleanName = spawner.getEntityName();
		String testName;
		if (options.length > 0 && (options[0] == 0 || options[0] == 3)) {
			if (!cleanName.contains(" ")) {
				testName = cleanName;
			} else {
				testName = cleanName.split(" ")[0];
			}
		} else if (!cleanName.contains(" ")) {
			testName = convertAlias(cleanName);
		} else {
			testName = convertAlias(cleanName.split(" ")[0]);
		}
		String returnName = Main.instance.getLangHandler().translateEntity(testName, type);

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
	 * Get spawner EntityType
	 *
	 * @param target Block
	 * @return EntityType
	 */
	public static EntityType getSpawner(Block target) {
		CreatureSpawner testSpawner;
		try {
			testSpawner = (CreatureSpawner) target.getState();
		} catch (Exception e) {
			try {
				throw new Pigception();
			} catch (Pigception p) {
				p.printStackTrace();
			}
			return EntityType.PIG;
		}
		return fromEntityType(testSpawner.getSpawnedType());
	}

	/**
	 * Set spawner type
	 *
	 * @param target Block
	 * @param arg String
	 * @return boolean
	 */
	public static boolean setSpawner(Block target, String arg) {
		if (!SpawnerFunctions.isValidEntity(arg)) {
			return false;
		}
		EntityType type = SpawnerFunctions.getSpawnerType(arg);
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
			testSpawner.setSpawnedType(type);
			target.getState().update();
			return true;
		}
		return false;
	}

	/**
	 * Spawn a random firework at the given location.
	 *
	 * @param loc
	 */
	public static void spawnRandomFirework(Location loc) {
		Firework firework = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
		FireworkMeta fireworkMeta = firework.getFireworkMeta();

		Random random = new Random();
		int randomInt = random.nextInt(5) + 1;

		Type type = null;
		switch (randomInt) {
			case 1:
				type = Type.BALL;
				break;
			case 2:
				type = Type.BALL_LARGE;
				break;
			case 3:
				type = Type.BURST;
				break;
			case 4:
				type = Type.CREEPER;
				break;
			case 5:
				type = Type.STAR;
				break;
			default:
				type = Type.BALL;
		}

		FireworkEffect effect = FireworkEffect.builder()
				.flicker(random.nextBoolean())
				.with(type)
				.withColor(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
				.withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
				.trail(random.nextBoolean())
				.build();
		fireworkMeta.addEffect(effect);

		fireworkMeta.setPower(randomInt);

		firework.setFireworkMeta(fireworkMeta);
	}

	/**
	 * Get EntityType from EntityType
	 *
	 * @param entityType EntityType
	 * @return EntityType
	 */
	public static EntityType fromEntityType(EntityType entityType) {
		if (entityType == null) {
			return null;
		}

		try {
			return EntityType.valueOf(entityType.name());
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get text from name
	 *
	 * @param arg String
	 * @return String
	 */
	public static String getTextFromName(String arg) {
		String name = getSpawnerName(convertAlias(arg), "key");

		if (name == null) {
			return null;
		}

		return getSpawnerName(name, "value");
	}

	/**
	 * Get text from type
	 *
	 * @param type EntityType
	 * @return String
	 */
	public static String getTextFromType(EntityType type) {
		if (type == null) {
			return null;
		}

		return getSpawnerName(type.name(), "value");
	}

	/**
	 * Get type from clean name
	 *
	 * @param name String
	 * @return EntityType
	 */
	public static EntityType fromCleanName(String name) {
		if (name == null) {
			return null;
		}
		return EntityType.fromName(name);
	}

}
