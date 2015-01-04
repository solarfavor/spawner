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
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

// Build against Spigot not Bukkit or you will get an error here.
import org.bukkit.event.entity.SpawnerSpawnEvent;

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
			String spawnerName = SpawnerType.getTextFromType(SpawnerType.fromEntityType(csBlock.getSpawnedType()));
			if (spawnerName == null) {
				// prevent from breaking invalid spawners
				Main.language.sendMessage(player, Main.language.getText(Keys.InvalidSpawner));
				event.setCancelled(true);
				return;
			}

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

			// apply luck
			if(SpawnerFunctions.chance("luck")) {
				return;
			}

			// if they are in creative or have silk touch and not holding a spawner and not holding a spawner
			if ((player.getGameMode() == GameMode.CREATIVE || (player.hasPermission("spawner.mine.nosilk." + spawnerName) || player.getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH))) && player.getItemInHand().getType() != Material.MOB_SPAWNER) {

				// cancel event
				event.setExpToDrop(0);

				// make an ItemStack
				ItemStack dropSpawner = new ItemStack(Material.MOB_SPAWNER, 1);

				// formatted name
				String name = SpawnerFunctions.formatName(spawnerName);

				// set lore
				ItemStack newSpawner = SpawnerFunctions.setSpawnerName(dropSpawner, name);

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

			// get spawner block
			CreatureSpawner csBlock = (CreatureSpawner) event.getBlock().getState();
			String spawnerName = SpawnerType.fromEntityType(csBlock.getSpawnedType()).getName();

			// if they can't place it cancel event
			if (!player.hasPermission("spawner.place.*") && !player.hasPermission("spawner.place." + spawnerName.toLowerCase())) {
				event.setCancelled(true);
				String spawnerText = SpawnerType.getTextFromType(SpawnerType.fromEntityType(csBlock.getSpawnedType()));
				Main.language.sendMessage(event.getPlayer(), Main.language.getText(Keys.NoPermission, spawnerText));
				return;
			}

			int itemId = player.getInventory().getHeldItemSlot();
			ItemStack itemStack = player.getInventory().getItem(itemId);

			SpawnerType spawnerType = SpawnerFunctions.getSpawnerType(itemStack);

			short spawnerId = 90;
			String name;
			if (spawnerType == null) {
				name = "Pig";
			} else {
				name = spawnerType.getName();
				spawnerId = spawnerType.getTypeId();
			}

			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new SpawnerTask(spawnerId, name, event.getBlock(), player), 0);
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

			ItemStack itemStack = event.getItem().getItemStack();
			itemStack.removeEnchantment(Enchantment.SILK_TOUCH);

			String spawnerName = SpawnerFunctions.resetSpawnerName(itemStack);

			Main.language.sendMessage(event.getPlayer(), Main.language.getText(Keys.YouPickedUp, spawnerName));

			// event.getPlayer().updateInventory();
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerItemHeld(PlayerItemHeldEvent event) {

		if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
			return;
		}

		ItemStack itemStack = event.getPlayer().getInventory().getItem(event.getNewSlot());
		if (itemStack == null) {
			return;
		}

		if (itemStack.getType().equals(Material.MOB_SPAWNER)) {
			itemStack.removeEnchantment(Enchantment.SILK_TOUCH);
		} else {
			return;
		}

		String spawnerName = SpawnerFunctions.resetSpawnerName(itemStack);

		// event.getPlayer().updateInventory();
		Main.language.sendMessage(event.getPlayer(), Main.language.getText(Keys.HoldingSpawner, spawnerName));
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!event.hasBlock()) {
			return;
		}

		Block clicked = event.getClickedBlock();
		if (clicked.getType().equals(Material.MOB_SPAWNER)) {
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (event.getPlayer().hasPermission("spawner.get")) {

					CreatureSpawner csBlock = (CreatureSpawner) clicked.getState();

					// formatted name
					String spawnerName = SpawnerType.getTextFromType(SpawnerType.fromEntityType(csBlock.getSpawnedType()));

					Main.language.sendMessage(event.getPlayer(), Main.language.getText(Keys.SpawnerType, spawnerName));
				}
			}
		}
	}

	/**
	 * Spigot SpawnerSpawnEvent for spawn frequency chance
	 * https://hub.spigotmc.org/stash/projects/SPIGOT/repos/spigot/browse/Bukkit-Patches/0007-Define-EntitySpawnEvent-and-SpawnerSpawnEvent.patch
	 * 
	 * @param event 
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onSpawnerSpawn(SpawnerSpawnEvent event) {

		// apply spawn frequency chance
		if(!SpawnerFunctions.chance("frequency." + event.getEntityType().name().toLowerCase())) {

			// stop spawner event
			event.setCancelled(true);
		}
	}	


	/**
	 * Prevent anvil renaming of spawners.
	 * @param event 
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onInventoryClickEvent(InventoryClickEvent event) {
		if (!(event.getInventory() instanceof AnvilInventory)) {
			return;
		}
		if (event.getSlotType() != SlotType.RESULT) {
			return;
		}
		if (event.getCurrentItem().getType() == Material.MOB_SPAWNER) {
			event.setCancelled(true);
		}
	}

}
