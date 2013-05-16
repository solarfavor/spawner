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

import java.util.Iterator;

import me.ryvix.spawner.language.Keys;

import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class SpawnerEvents implements Listener {

	/**
	 * When a spawner is broken set the Lore to the spawner type.
	 *
	 * @param event Block break event
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void onBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().getType() == Material.MOB_SPAWNER) {

			Player player = event.getPlayer();

			// get spawner block
			CreatureSpawner csBlock = (CreatureSpawner) event.getBlock().getState();
			String spawnerName = csBlock.getSpawnedType().getName().toLowerCase();

			// if they can't mine it just let them break it normally
			boolean canMine = false;
			if (player.hasPermission("spawner.mine.*") || player.hasPermission("spawner.mine." + spawnerName)) {
				canMine = true;
			}

			// if they can't break then cancel the event
			boolean canBreak = false;
			if (player.hasPermission("spawner.break.*") || player.hasPermission("spawner.break." + spawnerName)) {
				canBreak = true;
			}

			// check if they can mine
			if (!canMine) {

				// check if they can break
				if (!canBreak) {
					// don't let them break it
					event.setCancelled(true);
				}

				// just let them break it
				return;
			}

			// if they are in creative or have silk touch and not holding a spawner and not holding a spawner
			if ((player.getGameMode() == GameMode.CREATIVE || player.getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH)) && player.getItemInHand().getTypeId() != 52) {

				// cancel event
				event.setExpToDrop(0);

				// set item damage in players hand
				player.getItemInHand().setDurability((short) (player.getItemInHand().getDurability() + 1));

				// durability for safe keeping
				short durability = csBlock.getSpawnedType().getTypeId();
				if (durability < 1) {
					durability = 90;
				}

				// make an ItemStack
				ItemStack dropSpawner = new ItemStack(Material.MOB_SPAWNER, 1, durability);

				// formatted name
				String name = SpawnerFunctions.formatName(spawnerName);

				// set lore
				ItemStack newSpawner = SpawnerFunctions.setSpawnerName(dropSpawner, name);

				// set durability
				newSpawner.setDurability(durability);

				// drop item
				csBlock.getWorld().dropItemNaturally(csBlock.getLocation(), newSpawner);
			}
		}
	}

	/**
	 * When a spawner is placed set the type to the lore.
	 *
	 * @param event Block place event
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();

		if (event.getBlock().getType() == Material.MOB_SPAWNER) {

			int itemId = player.getInventory().getHeldItemSlot();
			ItemStack iStack = player.getInventory().getItem(itemId);
			short durability = iStack.getDurability();
			if (durability < 1) {
				durability = 90;
			}

			EntityType spawnerType = EntityType.fromId(durability);

			short spawnerId = 90;
			String name;
			if (spawnerType == null) {
				name = "Pig";
				durability = 90;
			} else {
				name = spawnerType.getName();
				spawnerId = spawnerType.getTypeId();
			}

			if (name.isEmpty()) {
				durability = 90;
			}

			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new SpawnerTask(spawnerId, durability, event.getBlock(), player), 0);
		}
	}

	/**
	 * When a spawner is exploded set the Lore to the spawner type.
	 *
	 * @param event Entity explode event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	private void onEntityExplode(EntityExplodeEvent event) {
		if (!Main.instance.config.getBoolean("protect_from_explosions")) {
			return;
		}

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

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	private void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (event.getItem().getItemStack().getType() == Material.MOB_SPAWNER) {

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
			iStack = SpawnerFunctions.setSpawnerName(iStack, spawnerName);

			// set durability
			iStack.setDurability(durability);

			Main.language.sendMessage(event.getPlayer(), Main.language.getText(Keys.YouPickedUp, spawnerName));

			// event.getPlayer().updateInventory();
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
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
		SpawnerFunctions.setSpawnerName(iStack, spawnerName);

		// event.getPlayer().updateInventory();

		Main.language.sendMessage(event.getPlayer(), Main.language.getText(Keys.HoldingSpawner, spawnerName));
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void OnPlayerInteract(PlayerInteractEvent event) {
		if (!event.hasBlock() || event.isCancelled()) {
			return;
		}

		Block clicked = event.getClickedBlock();
		if (clicked.getType().equals(Material.MOB_SPAWNER)) {
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (event.getPlayer().hasPermission("spawner.get")) {

					CreatureSpawner csBlock = (CreatureSpawner) clicked.getState();

					// formatted name
					String spawnerName = SpawnerFunctions.nameFromDurability(csBlock.getSpawnedType().getTypeId());

					Main.language.sendMessage(event.getPlayer(), Main.language.getText(Keys.SpawnerType, spawnerName));
				}
			}
		}
	}
}