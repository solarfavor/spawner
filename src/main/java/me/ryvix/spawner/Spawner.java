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

import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
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

	public short updateDurability() {
		// set correct durability
		SpawnerType spawnerType = SpawnerFunctions.getSpawnerType(this);
		EntityType entityType = spawnerType.getEntityType();
		short durability = SpawnerFunctions.durabilityFromEntityType(entityType);
		this.setDurability(durability);
		return this.getDurability();
	}
}
