/**
 *   Spawner - Gather mob spawners with silk touch enchanted tools and the
 *   ability to change mob types.
 *
 *   Copyright (C) 2012 Ryan Rhode - rrhode@gmail.com
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package me.ryvix.spawner;

import me.ryvix.spawner.Main;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;


public class SpawnerFunctions {

	private static Main plugin;

	public SpawnerFunctions(Main plugin) {
		SpawnerFunctions.plugin = plugin;
	}

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
	 * Set the Lore to the spawner name
	 * 
	 * @param spawner
	 * @param name
	 * @return
	 */
	public static ItemStack setSpawnerLore(ItemStack spawner, String name) {
		Spawner newSpawnerStack = new Spawner(plugin);
		ItemStack newSpawner = newSpawnerStack.setName(spawner, "Spawner");
		newSpawner = newSpawnerStack.setLore(newSpawner, name);

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
}