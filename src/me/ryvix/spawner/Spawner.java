/**
 *   Spawner - Gather mob spawners with silk touch enchanted tools and the
 *   ability to change mob types.
 *
 *   Copyright (C) 2012-2013 Ryan Rhode - rrhode@gmail.com
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

import java.util.Arrays;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Spawner {

	/**
	 * Set display name
	 * 
	 * @param item
	 * @param name
	 * @return
	 */
	public ItemStack setName(ItemStack item, String name) {

		ItemMeta im = (ItemMeta) item.getItemMeta();
		im.setDisplayName(name);
		item.setItemMeta(im);

		return item;
	}

	/**
	 * Get display name
	 * 
	 * @param item
	 * @return
	 */
	public String getName(ItemStack item) {

		ItemMeta im = (ItemMeta) item.getItemMeta();

		return im.getDisplayName();
	}

	/**
	 * Set lore
	 * 
	 * @param item
	 * @param lore
	 * @return
	 */
	public ItemStack setLore(ItemStack item, String lore) {

		ItemMeta im = (ItemMeta) item.getItemMeta();
		if (lore.isEmpty() && im.hasLore()) {
			im.setLore(null);
		} else {
			im.setLore(Arrays.asList(lore));
		}
		item.setItemMeta(im);

		return item;
	}

	/**
	 * Add lore
	 * 
	 * @param item
	 * @param lore
	 * @return
	 */
	public ItemStack addLore(ItemStack item, String lore) {

		ItemMeta im = (ItemMeta) item.getItemMeta();
		List<String> newLore = im.getLore();
		newLore.add(lore);
		im.setLore(newLore);
		item.setItemMeta(im);

		return item;
	}

	/**
	 * Get lore
	 * 
	 * @param item
	 * @return
	 */
	public List<String> getLore(ItemStack item) {

		ItemMeta im = (ItemMeta) item.getItemMeta();
		if (im.hasLore())
			return im.getLore();
		return null;
	}

	/**
	 * Get spawner type
	 * 
	 * @param target
	 * @return
	 */
	public EntityType getSpawner(Block target) {
		CreatureSpawner testSpawner = (CreatureSpawner) target.getState();
		return testSpawner.getSpawnedType();
	}

	/**
	 * Set spawner type
	 * 
	 * @param target
	 * @param arg
	 * @return
	 */
	public boolean setSpawner(Block target, String arg) {
		EntityType type = EntityType.fromName(arg);
		if (type == null) {
			return false;
		}
		CreatureSpawner testSpawner = null;
		try {
			testSpawner = (CreatureSpawner) target.getState();
		} catch (Exception e) {
			return false;
		}
		testSpawner.setSpawnedType(type);
		target.getState().update();

		return true;
	}

}