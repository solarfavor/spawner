/**
 * Spawner - Gather mob spawners with silk touch enchanted tools and the
 * ability to change mob types.
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Ryan Rhode
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

import java.util.Iterator;
import me.ryvix.spawner.nbt.NBTItem;
import me.ryvix.spawner.nbt.NBTReflectionUtil;
import me.ryvix.spawner.language.Keys;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.ConfigurationSection;
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
import org.bukkit.inventory.PlayerInventory;

public class SpawnerEvents implements Listener {

	/**
	 * When a spawner is broken.
	 *
	 * @param event Block break event
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void onBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().getType() == Material.MOB_SPAWNER) {

			Player player = event.getPlayer();

			// get spawner block
			CreatureSpawner csBlock = (CreatureSpawner) event.getBlock().getState();
			SpawnerType spawnerType = SpawnerType.fromEntityType(csBlock.getSpawnedType());
			String spawnerName = spawnerType.getName();
			if (spawnerName == null) {
				// prevent from breaking invalid spawners
				Main.language.sendMessage(player, Main.language.getText(Keys.InvalidSpawner));
				event.setCancelled(true);
				return;
			}

			// if they can't mine it just let them break it normally
			boolean canMine = false;
			if (player.hasPermission("spawner.mine.all") || player.hasPermission("spawner.mine." + spawnerName)) {
				canMine = true;
			}

			// if they can't break then cancel the event
			boolean canBreak = false;
			if (player.hasPermission("spawner.break.all") || player.hasPermission("spawner.break." + spawnerName)) {
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
			if (!SpawnerFunctions.chance("luck")) {
				// don't drop anything at all
				return;
			}

			// check for drops
			int amount;
			short damage;
			Byte data;
			Material type;
			boolean silk;
			boolean doDrop = true;
			ConfigurationSection drops = Main.instance.config.getConfigurationSection("drops");
			for (String key : drops.getKeys(false)) {

				if (key.equalsIgnoreCase(spawnerName)
					&& drops.contains(key + ".type")
					&& drops.contains(key + ".amount")
					&& drops.contains(key + ".damage")
					&& drops.contains(key + ".data")
					&& drops.contains(key + ".silk")) {

					type = Material.matchMaterial(drops.getString(key + ".type"));
					amount = drops.getInt(key + ".amount");
					damage = Short.parseShort(drops.getString(key + ".damage"));
					data = Byte.parseByte(drops.getString(key + ".data"));
					silk = drops.getBoolean(key + ".silk");

					PlayerInventory playerInv = player.getInventory();
					if ((silk && playerInv.getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH))
						|| (silk && playerInv.getItemInOffHand().containsEnchantment(Enchantment.SILK_TOUCH))
						|| (!silk && !playerInv.getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH))
						|| (!silk && !playerInv.getItemInOffHand().containsEnchantment(Enchantment.SILK_TOUCH))) {

						// don't drop spawner anymore
						doDrop = false;

						// stop exp
						event.setExpToDrop(0);

						// make an ItemStack
						ItemStack dropItem = new ItemStack(type);
						dropItem.setAmount(amount);
						dropItem.setDurability(data);
						dropItem.getData().setData(data);

						// drop item
						csBlock.getWorld().dropItemNaturally(csBlock.getLocation(), dropItem);
					}
				}
			}

			// if they are in creative or have silk touch and not holding a spawner and not holding a spawner
			if ((player.getGameMode() == GameMode.CREATIVE || (player.hasPermission("spawner.nosilk.all") || player.hasPermission("spawner.nosilk." + spawnerName) || player.getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH))) && player.getItemInHand().getType() != Material.MOB_SPAWNER) {

				// drop spawner
				if (doDrop) {

					// stop exp
					event.setExpToDrop(0);

					// make an ItemStack
					Spawner dropSpawner = new Spawner(Material.MOB_SPAWNER, 1);

					// set name
					ItemStack newSpawner = SpawnerFunctions.setSpawnerName(dropSpawner, spawnerName);

					// drop item
					csBlock.getWorld().dropItemNaturally(csBlock.getLocation(), newSpawner);

				}
			}
		}
	}

	/**
	 * When a spawner is placed.
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
			if (!player.hasPermission("spawner.place.all") && !player.hasPermission("spawner.place." + spawnerName.toLowerCase())) {
				event.setCancelled(true);
				String spawnerText = SpawnerType.getTextFromType(SpawnerType.fromEntityType(csBlock.getSpawnedType()));
				Main.language.sendMessage(event.getPlayer(), Main.language.getText(Keys.NoPermission, spawnerText));
				return;
			}

			SpawnerType spawnerType = null;
			PlayerInventory playerInv = player.getInventory();
			if (playerInv.getItemInMainHand().getType().equals(Material.MOB_SPAWNER)) {
				spawnerType = SpawnerFunctions.getSpawnerType(playerInv.getItemInMainHand());
			} else if (playerInv.getItemInOffHand().getType().equals(Material.MOB_SPAWNER)) {
				spawnerType = SpawnerFunctions.getSpawnerType(playerInv.getItemInOffHand());
			}

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
	 * When a spawner is exploded.
	 *
	 * @param event Entity explode event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	private void onEntityExplode(EntityExplodeEvent event) {

		if (Main.instance.config.getBoolean("protect_from_explosions")) {
			// protect_from_explosions

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

		} else if (Main.instance.config.getBoolean("drop_from_explosions")) {
			// drop_from_explosions

			if (event.blockList().isEmpty()) {
				return;
			}

			// check if explosion causes any spawners to drop
			Iterator<Block> iterator = event.blockList().iterator();
			while (iterator.hasNext()) {

				// if block was a spawner drop a spawner
				Block block = iterator.next();
				if (block.getType().equals(Material.MOB_SPAWNER)) {

					SpawnerType spawnerType = SpawnerFunctions.getSpawner(block);
					String spawnerName = spawnerType.getName();
					if (spawnerName != null) {

						// make an ItemStack
						Spawner dropSpawner = new Spawner(Material.MOB_SPAWNER, 1);

						// set name
						ItemStack newSpawner = SpawnerFunctions.setSpawnerName(dropSpawner, spawnerName);

						// drop item
						block.getWorld().dropItemNaturally(block.getLocation(), newSpawner);
					}
				}
			}
		}

	}

	/**
	 * When a spawner is picked up.
	 *
	 * @param event
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (event.getItem().getItemStack().getType() == Material.MOB_SPAWNER) {

			Spawner spawner = new Spawner(event.getItem().getItemStack());
			spawner.removeEnchantment(Enchantment.SILK_TOUCH);

			String spawnerName = SpawnerFunctions.resetSpawnerName(spawner);
			spawner.updateDurability();

			Main.language.sendMessage(event.getPlayer(), Main.language.getText(Keys.YouPickedUp, spawnerName));

			// event.getPlayer().updateInventory();
		}
	}

	/**
	 * When a spawner is held.
	 *
	 * @param event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerItemHeld(PlayerItemHeldEvent event) {

		if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
			return;
		}
		ItemStack itemStack = event.getPlayer().getInventory().getItem(event.getNewSlot());

		if (itemStack != null && itemStack.getType().equals(Material.MOB_SPAWNER)) {
			Spawner spawner = new Spawner(itemStack);
			String spawnerName = SpawnerFunctions.resetSpawnerName(spawner);
			spawner.updateDurability();

			spawner.removeEnchantment(Enchantment.SILK_TOUCH);

			Main.language.sendMessage(event.getPlayer(), Main.language.getText(Keys.HoldingSpawner, spawnerName));
		}
	}

	/**
	 * When a player interacts with a spawner.
	 *
	 * @param event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!event.hasBlock()) {
			return;
		}

		Block clicked = event.getClickedBlock();
		Player player = event.getPlayer();

		// if a mob spawner was clicked
		if (clicked.getType().equals(Material.MOB_SPAWNER)) {

			// check permission for spawner eggs
			ItemStack itemInHand = null;
			PlayerInventory playerInv = player.getInventory();
			if (playerInv.getItemInMainHand().getType().equals(Material.MONSTER_EGG)) {

				itemInHand = playerInv.getItemInMainHand();

			} else if (playerInv.getItemInOffHand().getType().equals(Material.MONSTER_EGG)) {

				itemInHand = playerInv.getItemInOffHand();

			} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

				// allow right-clicking a spawner to get the name of it
				if (player.hasPermission("spawner.get")) {

					CreatureSpawner csBlock = (CreatureSpawner) clicked.getState();

					// formatted name
					String spawnerName = SpawnerType.getTextFromType(SpawnerType.fromEntityType(csBlock.getSpawnedType()));

					Main.language.sendMessage(player, Main.language.getText(Keys.SpawnerType, spawnerName));
				}
			}

			if (itemInHand != null) {

				String eggId = "";

				// get spawner name
				try {

					NBTItem nbtItem = new NBTItem(itemInHand);

					if (!nbtItem.hasKey("EntityTag")) {
						Main.language.sendMessage(player, Main.language.getText(Keys.NotPossible));
						event.setCancelled(true);
						return;
					}
					
					eggId = NBTReflectionUtil.fromCompound(itemInHand, "EntityTag", "String", "id");

				} catch (Exception ex) {
					Main.language.sendMessage(player, Main.language.getText(Keys.NotPossible));
					event.setCancelled(true);
					return;
				}

				String spawnerName = SpawnerFunctions.getSpawnerName(eggId, "key");

				if (!player.hasPermission("spawner.eggs.all") && !player.hasPermission("spawner.eggs." + spawnerName.toLowerCase())) {
					Main.language.sendMessage(player, Main.language.getText(Keys.NoPermission));
					event.setCancelled(true);

				} else {
					// formatted name
					spawnerName = SpawnerType.getTextFromName(eggId);
					Main.language.sendMessage(player, Main.language.getText(Keys.SpawnerChangedTo, spawnerName));
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
		String spawnerText = SpawnerType.getUnformattedTextFromName(event.getEntityType().name());

		// apply spawn frequency chance
		if (!SpawnerFunctions.chance("frequency." + spawnerText)) {

			// stop spawner event
			event.setCancelled(true);
		}
	}

	/**
	 * Prevent anvil renaming of spawners.
	 *
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
		Player player = (Player) event.getWhoClicked();
		if (event.getCurrentItem().getType() == Material.MOB_SPAWNER && !player.hasPermission("spawner.anvil.spawners")) {
			Main.language.sendMessage(player, Main.language.getText(Keys.NoPermission));
			event.setCancelled(true);
			return;
		}
		if (event.getCurrentItem().getType() == Material.MONSTER_EGG && !player.hasPermission("spawner.anvil.eggs")) {
			Main.language.sendMessage(player, Main.language.getText(Keys.NoPermission));
			event.setCancelled(true);
		}
	}

}
