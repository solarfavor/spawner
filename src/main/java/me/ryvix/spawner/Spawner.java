/**
 * Spawner - Gather mob spawners with silk touch enchanted tools and the
 * ability to change mob types.
 *
 * Copyright (C) 2012-2015 Ryan Rhode - rrhode@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package me.ryvix.spawner;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Spawner extends ItemStack {

	Spawner(Material material) {
		super(material);
	}

	Spawner(Material material, int amount) {
		super(material, amount);
	}

	Spawner(ItemStack item) {
		super(item);
	}

	/**
	 * Set display name
	 *
	 * @param name
	 * @return
	 */
	public ItemStack setName(String name) {

		ItemMeta im = getItemMeta();
		im.setDisplayName(name);
		setItemMeta(im);

		return this;
	}

	/**
	 * Get display name
	 *
	 * @return
	 */
	public String getName() {

		ItemMeta im = getItemMeta();

		return im.getDisplayName();
	}

	/**
	 * Set lore
	 *
	 * @param lore
	 * @return
	 */
	public Spawner setLore(String lore) {

		ItemMeta im = getItemMeta();
		if (lore.isEmpty() && im.hasLore()) {
			im.setLore(null);
		} else {
			im.setLore(Arrays.asList(lore));
		}
		setItemMeta(im);

		return this;
	}

	/**
	 * Add lore
	 *
	 * @param lore
	 * @return
	 */
	public Spawner addLore(String lore) {

		ItemMeta im = getItemMeta();
		List<String> newLore = im.getLore();
		newLore.add(lore);
		im.setLore(newLore);
		setItemMeta(im);

		return this;
	}

	/**
	 * Get lore
	 *
	 * @return
	 */
	public List<String> getLore() {

		ItemMeta im = getItemMeta();
		if (im.hasLore()) {
			return im.getLore();
		}
		return null;
	}
}
