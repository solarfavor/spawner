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

import java.util.Iterator;

import me.ryvix.spawner.Main;

import org.bukkit.block.Block;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_4_5.block.CraftCreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class SpawnerEvents implements Listener {

	private Main plugin;

	public SpawnerEvents(Main plugin) {
		this.plugin = plugin;
	}

	/**
	 * When a spawner is broken set the Lore to the spawner type.
	 * 
	 * @param event Block break event
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	private void onBlockBreak(BlockBreakEvent event) {
		if (!event.isCancelled() && event.getBlock().getType() == Material.MOB_SPAWNER) {

			Player player = event.getPlayer();

			if (player.getGameMode() == GameMode.CREATIVE || player.getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH)) {

				// cancel event
				event.setCancelled(true);

				// set item damage in players hand
				player.getItemInHand().setDurability((short) (player.getItemInHand().getDurability() + 1));

				// get spawner block
				CraftCreatureSpawner csBlock = new CraftCreatureSpawner(event.getBlock());

				// durability for safe keeping
				short durability = csBlock.getSpawnedType().getTypeId();
				if (durability < 1) {
					durability = 90;
				}

				// make an ItemStack
				ItemStack dropSpawner = new ItemStack(Material.MOB_SPAWNER, 1, durability);

				// formatted name
				String name = SpawnerFunctions.formatName(csBlock.getSpawnedType().getName().toLowerCase());

				// set lore
				ItemStack newSpawner = SpawnerFunctions.setSpawnerLore(dropSpawner, name);

				// set durability
				newSpawner.setDurability(durability);

				// drop item
				csBlock.getWorld().dropItemNaturally(csBlock.getLocation(), newSpawner);

				// set block to air
				event.getBlock().setType(Material.AIR);
			}
		}
	}

	/**
	 * When a spawner is placed set the type to the lore.
	 * 
	 * @param event Block place event
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	private void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();

		// onPlayerHoldItem will remove silk touch
		// some mob spawner plugins add silk touch to mob spawners
		// so we dont let them break blocks when holding a spawner
		// ItemStack heldItem = player.getItemInHand();
		// if (heldItem != null && heldItem.getType() == Material.MOB_SPAWNER) {
		// event.setCancelled(true);
		// return;
		// }

		if (!event.isCancelled() && event.getBlock().getType() == Material.MOB_SPAWNER) {

			int itemId = player.getInventory().getHeldItemSlot();
			ItemStack iStack = player.getInventory().getItem(itemId);
			short durability = iStack.getDurability();
			if (durability < 1) {
				durability = 90;
			}

			EntityType spawnerType = EntityType.fromId(durability);

			short spawnerId = 90;
			String name = "";
			if (spawnerType == null) {
				name = "Pig";
				durability = 90;
			} else {
				name = spawnerType.getName();
				spawnerId = spawnerType.getTypeId();
			}

			if (name.isEmpty()) {
				name = "Pig";
				durability = 90;
			} else {
				String f = name.substring(0, 1);
				name = name.toLowerCase().replaceFirst(f, f.toUpperCase());
			}

			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new SpawnerTask(spawnerId, durability, event.getBlock(), plugin, player), 0);
		}
	}

	/**
	 * When a spawner is exploded set the Lore to the spawner type.
	 * 
	 * @param event Entity explode event
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	private void onEntityExplode(EntityExplodeEvent event) {
		if (!event.isCancelled()) {
			if (event.blockList().isEmpty()) {
				return;
			}

			// check if explosion causes a spawner to be broken
			Iterator<Block> iterator = event.blockList().iterator();
			while (iterator.hasNext()) {

				// if block was a spawner cancel explosion event
				// this gives a natural protection around spawners
				if (iterator.next().getType() == Material.MOB_SPAWNER) {
					event.setCancelled(true);

					// return since we already found a spawner
					return;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (!event.isCancelled() && event.getItem().getItemStack().getType() == Material.MOB_SPAWNER) {

			ItemStack iStack = event.getItem().getItemStack();
			iStack.removeEnchantment(Enchantment.SILK_TOUCH);

			// durability for safe keeping
			short durability = iStack.getDurability();
			if (durability < 1) {
				durability = 90;
			}

			// formatted name
			String spawnerName = SpawnerFunctions.nameFromDurability(durability);

			// set lore
			iStack = SpawnerFunctions.setSpawnerLore(iStack, spawnerName);

			// set durability
			iStack.setDurability(durability);

			event.getPlayer().sendMessage(ChatColor.GREEN + "You picked up a " + spawnerName + " spawner.");

			// event.getPlayer().updateInventory();
		}
	}

	/**
	 * When a spawner is taken from the inventory and dropped on the ground make it drop. If we don't do this it will just disappear.
	 * 
	 * @param event Block place event
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	private void onPlayerDropItem(PlayerDropItemEvent event) {
		if (!event.isCancelled() && event.getItemDrop().getItemStack().getType() == Material.MOB_SPAWNER) {

			// cancel event
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void OnPlayerItemHeld(PlayerItemHeldEvent event) {

		if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
			return;
		}

		ItemStack iStack = event.getPlayer().getInventory().getItem(event.getNewSlot());
		if (iStack == null) {
			return;
		}

		if (iStack.getType().equals(Material.MOB_SPAWNER)) {
			iStack.removeEnchantment(Enchantment.SILK_TOUCH);
		} else {
			return;
		}

		// formatted name
		String spawnerName = SpawnerFunctions.nameFromDurability(iStack.getDurability());

		// set lore
		SpawnerFunctions.setSpawnerLore(iStack, spawnerName);

		// event.getPlayer().updateInventory();

		event.getPlayer().sendMessage(ChatColor.GREEN + "You are holding a " + spawnerName + " spawner.");
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void OnPlayerInteract(PlayerInteractEvent event) {
		if (!event.hasBlock() || event.isCancelled()) {
			return;
		}

		Block clicked = event.getClickedBlock();
		if (clicked.getType().equals(Material.MOB_SPAWNER)) {
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

				CraftCreatureSpawner csBlock = new CraftCreatureSpawner(clicked);

				// formatted name
				String spawnerName = SpawnerFunctions.nameFromDurability(csBlock.getSpawnedType().getTypeId());

				event.getPlayer().sendMessage(ChatColor.GREEN + "Spawner type: " + spawnerName);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void InventoryClick(InventoryClickEvent event) {

		Player player = (Player) event.getWhoClicked();

		// event is cancelled or in creative return
		if (event.isCancelled() || player.getGameMode() == GameMode.CREATIVE) {
			return;
		}

		// the slot they clicked
		int slot = event.getSlot();
		int rawSlot = event.getRawSlot();

		// inventory slot sizes
		int topSlots = 0;
		int botSlots = 0;

		// check for top inventory
		Inventory topInv = event.getView().getTopInventory();
		if (topInv != null) {
			topSlots = topInv.getSize();
		}

		// check for bottom inventory
		Inventory botInv = event.getView().getBottomInventory();
		if (botInv != null) {
			botSlots = botInv.getSize();
		}

		// clicked outside the inventory window or a bad slot #
		if (slot < 0 /* || slot > (topSlots + botSlots) */) {
			event.setCancelled(true);
			return;
		}

		// ItemStack in clicked slot when picked up
		ItemStack pickup = null;
		if (event.isShiftClick()) {

			// which inv was clicked
			if (rawSlot <= topSlots) {
				pickup = topInv.getItem(slot);

			} else if (rawSlot > botSlots) {
				pickup = botInv.getItem(slot);

			}
		}

		// ItemStack in clicked slot now in players hand on putdown
		ItemStack putdown = player.getItemOnCursor();

		// check action
		if (pickup != null && pickup.getType() == Material.MOB_SPAWNER) {
			// took an item from the inventory

			// check for shift click on spawner
			if (event.isShiftClick()) {
				event.setCancelled(true);
				return;
			}

		} else if (putdown != null && putdown.getType() == Material.MOB_SPAWNER) {
			// put an item into the inventory

			// check for shift click on spawner
			if (event.isShiftClick()) {
				event.setCancelled(true);
				return;
			}

			// if put on another spawner
			if (pickup != null && pickup.getType() == Material.MOB_SPAWNER) {
				player.getInventory().setItem(slot, pickup);
				player.setItemOnCursor(putdown);
			}

		} else {
			// something else so return
			return;
		}
	}
}