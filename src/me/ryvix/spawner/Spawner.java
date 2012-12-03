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

import java.util.ArrayList;

import me.ryvix.spawner.Main;
import net.minecraft.server.EntityItem;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftCreatureSpawner;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;


public class Spawner {

	private static CraftItemStack craftStack;
	private static net.minecraft.server.ItemStack itemStack;

	private Main plugin;

	public Spawner(Main plugin) {
		this.plugin = plugin;
	}

	public ItemStack setName(ItemStack item, String name) {
		if (item instanceof CraftItemStack) {
			craftStack = (CraftItemStack) item;
			Spawner.itemStack = craftStack.getHandle();
		} else if (item instanceof ItemStack) {
			craftStack = new CraftItemStack(item);
			Spawner.itemStack = craftStack.getHandle();
		}

		NBTTagCompound tag = itemStack.tag;

		if (tag == null) {
			tag = new NBTTagCompound();
			tag.setCompound("display", new NBTTagCompound());
			itemStack.tag = tag;
		}

		tag = itemStack.tag.getCompound("display");
		tag.setString("Name", name);
		itemStack.tag.setCompound("display", tag);

		return craftStack;
	}

	public String getName(ItemStack item) {
		if (item instanceof CraftItemStack) {
			craftStack = (CraftItemStack) item;
			Spawner.itemStack = craftStack.getHandle();
		} else if (item instanceof ItemStack) {
			craftStack = new CraftItemStack(item);
			Spawner.itemStack = craftStack.getHandle();
		}

		NBTTagCompound tag = itemStack.tag;

		if (tag == null) {
			return null;
		}

		tag = itemStack.tag.getCompound("display");

		return tag.getString("Name");
	}

	public ItemStack setLore(ItemStack item, String... lore) {
		if (item instanceof CraftItemStack) {
			craftStack = (CraftItemStack) item;
			Spawner.itemStack = craftStack.getHandle();
		} else if (item instanceof ItemStack) {
			craftStack = new CraftItemStack(item);
			Spawner.itemStack = craftStack.getHandle();
		}

		NBTTagCompound tag = itemStack.tag;

		if (tag == null) {
			tag = new NBTTagCompound();
			tag.setCompound("display", new NBTTagCompound());
			itemStack.tag = tag;
		}

		tag = itemStack.tag.getCompound("display");
		NBTTagList list = new NBTTagList();

		for (String l : lore) {
			list.add(new NBTTagString("", l));
		}

		tag.set("Lore", list);
		itemStack.tag.setCompound("display", tag);

		return craftStack;
	}

	public ItemStack addLore(ItemStack item, String lore) {
		if (item instanceof CraftItemStack) {
			craftStack = (CraftItemStack) item;
			Spawner.itemStack = craftStack.getHandle();
		} else if (item instanceof ItemStack) {
			craftStack = new CraftItemStack(item);
			Spawner.itemStack = craftStack.getHandle();
		}

		NBTTagCompound tag = itemStack.tag;
		if (tag == null) {
			tag = new NBTTagCompound();
			tag.setCompound("display", new NBTTagCompound());
			tag.getCompound("display").set("Lore", new NBTTagList());
			itemStack.tag = tag;
		}

		tag = itemStack.tag.getCompound("display");
		NBTTagList list = tag.getList("Lore");
		list.add(new NBTTagString("", lore));
		tag.set("Lore", list);
		itemStack.tag.setCompound("display", tag);

		return craftStack;
	}

	public String[] getLore(ItemStack item) {
		if (item instanceof CraftItemStack) {
			craftStack = (CraftItemStack) item;
			Spawner.itemStack = craftStack.getHandle();
		}

		NBTTagCompound tag = itemStack.tag;
		if (tag == null) {
			tag = new NBTTagCompound();
			tag.setCompound("display", new NBTTagCompound());
			tag.getCompound("display").set("Lore", new NBTTagList());
			itemStack.tag = tag;
		}

		tag = itemStack.tag;
		NBTTagList list = tag.getCompound("display").getList("Lore");
		ArrayList<String> strings = new ArrayList<String>();
		String[] lores = new String[] {};

		for (int i = 0; i < list.size(); i++) {
			strings.add(((NBTTagString) list.get(i)).data);
		}

		strings.toArray(lores);

		return lores;
	}

	public void dropItem(net.minecraft.server.ItemStack mItem, Location loc) {

		double xs = plugin.gen.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
		double ys = plugin.gen.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
		double zs = plugin.gen.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
		EntityItem entity = new EntityItem(((CraftWorld) loc.getWorld()).getHandle(), loc.getX() + xs, loc.getY() + ys, loc.getZ() + zs, mItem);
		((CraftWorld) loc.getWorld()).getHandle().addEntity(entity);
	}

	public void dropItem(ItemStack setLore, Location location) {

		// dropItem();
	}

	public EntityType getSpawner(Block target) {
		CraftCreatureSpawner testSpawner = new CraftCreatureSpawner(target);
		return testSpawner.getSpawnedType();
	}

	public boolean setSpawner(Block target, String arg) {
		EntityType type = EntityType.fromName(arg);
		if (type == null) {
			return false;
		}

		CraftCreatureSpawner testSpawner = new CraftCreatureSpawner(target);
		testSpawner.setSpawnedType(type);
		target.getState().update();

		return true;
	}

}