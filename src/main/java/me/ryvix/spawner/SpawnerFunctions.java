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
package me.ryvix.spawner;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.ryvix.spawner.language.Keys;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

public class SpawnerFunctions {

	/**
	 * Format name
	 *
	 * @param name
	 * @return
	 */
	public static String formatName(String name) {
		String f = name.substring(0, 1);
		return name.replaceFirst(f, f.toUpperCase());
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
		ItemStack newSpawner = newSpawnerStack.setName(spawner, name + " Spawner");

		// currently just to remove the old lore line
		if (newSpawnerStack.getLore(newSpawner) != null) {
			newSpawner = newSpawnerStack.setLore(newSpawner, "");
		}

		return newSpawner;
	}

	/**
	 * Get the name from the durability/itemid value
	 *
	 * @param durability
	 * @return Formated name
	 */
	public static String nameFromDurability(short durability) {

		EntityType spawnerType = getSpawnerType(durability);
		String spawnerName = "Pig";
		if (spawnerType != null) {
			spawnerName = SpawnerType.getTextFromType(spawnerType);
		}

		return formatName(spawnerName);
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

			DataInputStream in = new DataInputStream(fstream);
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
			in.close();
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
		EntityType type = getSpawnerType(entityArg);
		if (type == null) {
			Main.language.sendMessage(player, Main.language.getText(Keys.InvalidEntity));
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
			Main.language.sendMessage(player, Main.language.getText(Keys.ErrorRemovingEntities));
		}

		String[] vars = new String[2];
		vars[0] = "" + count;
		vars[1] = entityArg;
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
	 * Return the EntityType of the given string.
	 *
	 * @param arg
	 * @return
	 */
	public static EntityType getSpawnerType(String arg) {

		/*
		 EntityType[] types = EntityType.values();
		 for (EntityType type : types) {
		 if(type.name().equalsIgnoreCase(arg)) {
		 return type;
		 }
		 }
		 return null;
		 */
		EntityType type = EntityType.fromName(arg);
		return type;
	}

	/**
	 * Return the EntityType of the given id/durability.
	 *
	 * @param d
	 * @return
	 */
	static EntityType getSpawnerType(short d) {
		EntityType type = EntityType.fromId(d);
		return type;
	}
}
