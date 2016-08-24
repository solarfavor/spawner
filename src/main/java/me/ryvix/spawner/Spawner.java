/**
 * Spawner - Gather mob spawners with silk touch enchanted tools and the
 * ability to change mob types.
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2016 Ryan Rhode
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

import me.ryvix.spawner.language.Keys;
import net.minecraft.server.v1_10_R1.NBTTagCompound;
import net.minecraft.server.v1_10_R1.NBTTagList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class Spawner extends ItemStack {

	private String entityName;
	private SpawnerType spawnerType;

	Spawner(Material material, int amount, String name) {
		super(material, amount);
		initSpawner(name);
	}

	Spawner(ItemStack item) {
		super(item);
		initSpawner("");
	}

	Spawner(String name) {
		super(Material.MOB_SPAWNER);
		initSpawner(name);
	}

	Spawner(String name, int amount) {
		super(Material.MOB_SPAWNER, amount);
		initSpawner(name);
	}

	Spawner(ItemStack item, int amount) {
		super(item);
		item.setAmount(amount);
		initSpawner("");
	}

	private void initSpawner(String name) {
		if (name == null || name.isEmpty()) {
			name = getEntityName();
		}
		setEntityName(name);
		setFormattedName(name);
		updateDurability();
		removeEnchantment(Enchantment.SILK_TOUCH);
	}

	public String getEntityNameFromDisplay() {
		if (!(getType() == Material.MOB_SPAWNER)) {
			return null;
		}

		String displayName = getName();
		String returnName;
		if (!displayName.contains(" ")) {
			returnName = displayName;
		} else {
			returnName = displayName.split(" ")[0];
		}
		returnName = SpawnerFunctions.convertAlias(returnName);
		returnName = Main.language.translateEntity(returnName, "key");

		return returnName;
	}

	/**
	 * Set display name
	 *
	 * @param name
	 * @return
	 */
	public Spawner setName(String name) {

		ItemMeta im = getItemMeta();
		im.setDisplayName(name);
		setItemMeta(im);

		return this;
	}

	/**
	 * Set formatted display name
	 *
	 * @param name
	 * @return
	 */
	public Spawner setFormattedName(String name) {

		ItemMeta im = getItemMeta();
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', Main.language.translateEntity(name, "value") + " " + Main.language.getText(Keys.Spawner)));
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
		EntityType entityType = getSpawnerType().getEntityType();
		short durability = SpawnerFunctions.durabilityFromEntityType(entityType);
		this.setDurability(durability);
		return this.getDurability();
	}

	public String getEntityName() {
		if (entityName == null) {
			entityName = SpawnerFunctions.getEntityNameFromSpawnerNBT(this);
			if (entityName == null || entityName.isEmpty()) {
				entityName = getEntityNameFromDisplay();
				if (entityName == null || entityName.isEmpty()) {
					entityName = "";
				}
			}
		}
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', entityName));
	}

	public String getFormattedEntityName() {
		String formattedEntityName = Main.language.translateEntity(getEntityName(), "value");
		return formattedEntityName;
	}

	public SpawnerType getSpawnerType() {
		// if spawner type is null try setting it
		if (this.spawnerType == null) {
			String name = getEntityName();
			setSpawnerType(name);
		}
		return this.spawnerType;
	}

	public void setSpawnerType(String entityName) {
		this.spawnerType = SpawnerType.fromCleanName(SpawnerFunctions.convertAlias(entityName));
	}
}
