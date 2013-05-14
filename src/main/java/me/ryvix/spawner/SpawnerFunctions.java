/**
 * Spawner - Gather mob spawners with silk touch enchanted tools and the
 * ability to change mob types.
 *
 * Copyright (C) 2012-2013 Ryan Rhode - rrhode@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package me.ryvix.spawner;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class SpawnerFunctions {

	/**
	 * Format name
	 *
	 * @param block
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

		EntityType spawnerType = EntityType.fromId(durability);
		String spawnerName = "Pig";
		if (spawnerType != null) {
			spawnerName = spawnerType.getName();
		}

		return formatName(spawnerName);
	}

	/**
	 * Reads a file to a string
	 * Lines starting with # will be ignored
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
		} catch (Exception e) {
			Logger.getLogger(SpawnerFunctions.class.getName()).log(Level.SEVERE, null, e);
		}

		return contents;
	}
}