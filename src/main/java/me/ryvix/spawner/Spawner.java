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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
		returnName = Main.instance.getLangHandler().translateEntity(returnName, "key");

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
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', Main.instance.getLangHandler().translateEntity(name, "value") + " " + Main.instance.getLangHandler().getText("Spawner")));
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
		String name = im.getDisplayName();
		if(name == null) {
			name = "";
		}

		return name;
	}

	public short updateDurability() {
		// set correct durability
		SpawnerType type = getSpawnerType();
		if(type == null) {
			return 0;
		}
		EntityType entityType = type.getEntityType();
		short durability = SpawnerFunctions.durabilityFromEntityType(entityType);
		this.setDurability(durability);
		return this.getDurability();
	}

	public String getEntityName() {
		if (entityName == null) {
			entityName = Main.instance.getNmsHandler().getEntityNameFromSpawnerNBT(this);
			if (entityName == null || entityName.isEmpty()) {
				entityName = getEntityNameFromDisplay();
				if (entityName == null || entityName.isEmpty()) {
					entityName = "";
				}
			} else {
				entityName = getEntityNameFromNbtName(entityName);
			}
		}
		return SpawnerFunctions.convertAlias(entityName);
	}

	public String getEntityNameFromNbtName(String nbtName) {

		// Split string if it contains a semicolon i.e. minecraft:horse
		if(nbtName.contains(":")) {
			String[] parts = nbtName.split(":");
			return parts[1];
		}

		return nbtName;
	}

	public void setEntityName(String entityName) {
		this.entityName = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', entityName));
	}

	public String getFormattedEntityName() {
		return Main.instance.getLangHandler().translateEntity(getEntityName(), "value");
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
