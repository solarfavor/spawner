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

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Random;

// Build against Spigot not Bukkit or you will get an error here.

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
			EntityType spawnerType = SpawnerFunctions.fromEntityType(csBlock.getSpawnedType());
			String spawnerName = spawnerType.name();
			if (spawnerName == null) {
				// prevent from breaking invalid spawners
				Main.instance.getLangHandler().sendMessage(player, Main.instance.getLangHandler().getText("InvalidSpawner"));
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

			PlayerInventory playerInv = player.getInventory();

			boolean break_into_inventory = Main.getSpawnerConfig().getBoolean("break_into_inventory");
			boolean prevent_break_if_inventory_full = Main.getSpawnerConfig().getBoolean("prevent_break_if_inventory_full");

			// prevent break if inv is full and prevent_break_if_inventory_full option is true
			if (break_into_inventory && playerInv.firstEmpty() == -1 && prevent_break_if_inventory_full) {
				Main.instance.getLangHandler().sendMessage(player, Main.instance.getLangHandler().getText("InventoryFull"));
				event.setCancelled(true);
				return;
			}

			// check for drops
			int amount;
			//short damage;
			Byte data;
			Material type;
			boolean silk;
			boolean doDrop = true;
			ConfigurationSection drops = Main.getSpawnerConfig().getConfigurationSection("drops");
			for (String key : drops.getKeys(false)) {

				if (key.equalsIgnoreCase(spawnerName)
						&& drops.contains(key + ".type")
						&& drops.contains(key + ".amount")
						&& drops.contains(key + ".damage")
						&& drops.contains(key + ".data")
						&& drops.contains(key + ".silk")) {

					type = Material.matchMaterial(drops.getString(key + ".type"));
					amount = drops.getInt(key + ".amount");
					//damage = Short.parseShort(drops.getString(key + ".damage"));
					data = Byte.parseByte(drops.getString(key + ".data"));
					silk = drops.getBoolean(key + ".silk");

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

						if (!break_into_inventory || (playerInv.firstEmpty() == -1 && !prevent_break_if_inventory_full)) {
							// drop item
							csBlock.getWorld().dropItemNaturally(csBlock.getLocation(), dropItem);
						} else {
							// place into free inventory slot
							int invSlot = playerInv.firstEmpty();
							playerInv.setItem(invSlot, dropItem);

							// make sure to show the player the item
							player.updateInventory();
						}
					}
				}
			}

			// if they are in creative or have silk touch and not holding a spawner
			if ((player.getGameMode() == GameMode.CREATIVE
					|| (player.hasPermission("spawner.nosilk.all")
					|| player.hasPermission("spawner.nosilk." + spawnerName)
					|| (playerInv.getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)
					|| playerInv.getItemInOffHand().containsEnchantment(Enchantment.SILK_TOUCH))))
					&& (playerInv.getItemInMainHand().getType() != Material.MOB_SPAWNER
					&& playerInv.getItemInOffHand().getType() != Material.MOB_SPAWNER)) {

				// drop spawner
				if (doDrop) {

					// stop exp
					event.setExpToDrop(0);

					// get a new spawner
					Spawner dropSpawner = SpawnerFunctions.makeSpawner(spawnerName);

					// drop spawner
					if (!break_into_inventory || (playerInv.firstEmpty() == -1 && !prevent_break_if_inventory_full)) {
						// drop spawner
						csBlock.getWorld().dropItemNaturally(csBlock.getLocation(), dropSpawner);
					} else {
						// place into free inventory slot
						int invSlot = playerInv.firstEmpty();
						playerInv.setItem(invSlot, dropSpawner);

						// make sure to show the player the spawner
						player.updateInventory();
					}

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
			EntityType spawnerTypeBlock = SpawnerFunctions.fromEntityType(csBlock.getSpawnedType());
			String spawnerName = spawnerTypeBlock.name();

			EntityType spawnerTypeHand = null;
			PlayerInventory playerInv = player.getInventory();
			if (playerInv.getItemInMainHand().getType() == Material.MOB_SPAWNER) {
				spawnerTypeHand = SpawnerFunctions.getSpawnerType(playerInv.getItemInMainHand());
			} else if (playerInv.getItemInOffHand().getType() == Material.MOB_SPAWNER) {
				spawnerTypeHand = SpawnerFunctions.getSpawnerType(playerInv.getItemInOffHand());
			}

			String name;
			if (spawnerTypeHand == null) {
				name = "Pig";
				try {
					throw new Pigception();
				} catch (Pigception p) {
					p.printStackTrace();
				}
			} else {
				name = spawnerTypeHand.getName();
			}

			// if they can't place it cancel event
			if (!player.hasPermission("spawner.place.all") && !player.hasPermission("spawner.place." + spawnerName.toLowerCase())) {
				event.setCancelled(true);
				String spawnerText = SpawnerFunctions.getTextFromType(spawnerTypeBlock);
				Main.instance.getLangHandler().sendMessage(event.getPlayer(), Main.instance.getLangHandler().getText("NoPermission", spawnerText));
				return;
			}

			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new SpawnerTask(name, event.getBlock(), player), 0);
		}
	}

	/**
	 * When a spawner is exploded.
	 *
	 * @param event Entity explode event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	private void onEntityExplode(EntityExplodeEvent event) {

		if (Main.getSpawnerConfig().getBoolean("protect_from_explosions")) {
			// protect_from_explosions

			if (event.blockList().isEmpty()) {
				return;
			}

			// check if explosion causes a spawner to be broken
			for (Block block : event.blockList()) {

				// if block was a spawner cancel explosion event
				// this gives a natural protection around spawners
				if (block.getType() == Material.MOB_SPAWNER) {
					event.setCancelled(true);

					// return since we already found a spawner
					return;
				}
			}

		} else if (Main.getSpawnerConfig().getBoolean("drop_from_explosions")) {
			// drop_from_explosions

			if (event.blockList().isEmpty()) {
				return;
			}

			// check if explosion causes any spawners to drop
			for (Block block : event.blockList()) {
				// if block was a spawner drop a spawner
				if (block.getType().equals(Material.MOB_SPAWNER)) {

					EntityType spawnerType = SpawnerFunctions.getSpawner(block);
					String spawnerName = spawnerType.name();
					if (spawnerName != null) {

						// get a new spawner
						Spawner dropSpawner = SpawnerFunctions.makeSpawner(spawnerName);

						// drop item
						block.getWorld().dropItemNaturally(block.getLocation(), dropSpawner);
					}
				}
			}
		}

	}

	/**
	 * When a spawner is picked up.
	 *
	 * @param event PlayerPickupItemEvent
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (event.getItem().getItemStack().getType() == Material.MOB_SPAWNER) {

			Spawner spawner = SpawnerFunctions.makeSpawner(event.getItem().getItemStack());

			Main.instance.getLangHandler().sendMessage(event.getPlayer(), Main.instance.getLangHandler().getText("YouPickedUp", spawner.getFormattedEntityName()));

			event.getPlayer().updateInventory();
		}
	}

	/**
	 * When a spawner is held.
	 *
	 * @param event PlayerItemHeldEvent
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	private void onPlayerItemHeld(PlayerItemHeldEvent event) {

		if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
			return;
		}
		ItemStack itemStack = event.getPlayer().getInventory().getItem(event.getNewSlot());

		if (itemStack != null && itemStack.getType().equals(Material.MOB_SPAWNER)) {
			Spawner spawner = SpawnerFunctions.makeSpawner(itemStack);

			event.getPlayer().updateInventory();

			Main.instance.getLangHandler().sendMessage(event.getPlayer(), Main.instance.getLangHandler().getText("HoldingSpawner", spawner.getFormattedEntityName()));
		}
	}

	/**
	 * When a player interacts with a spawner.
	 *
	 * @param event PlayerInteractEvent
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	private void onPlayerInteract(PlayerInteractEvent event) {
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
					String spawnerName = SpawnerFunctions.getTextFromType(SpawnerFunctions.fromEntityType(csBlock.getSpawnedType()));

					Main.instance.getLangHandler().sendMessage(player, Main.instance.getLangHandler().getText("SpawnerType", spawnerName));
				}
			}

			if (itemInHand != null) {

				String eggId = Main.instance.getNmsHandler().getEntityNameFromSpawnEgg(itemInHand);

				Spawner spawner = SpawnerFunctions.makeSpawner(eggId);
				String spawnerName = spawner.getEntityName();

				if (!player.hasPermission("spawner.eggs.all") && !player.hasPermission("spawner.eggs." + spawnerName.toLowerCase())) {
					Main.instance.getLangHandler().sendMessage(player, Main.instance.getLangHandler().getText("NoPermission"));
					event.setCancelled(true);

				} else {
					// formatted name
					spawnerName = SpawnerFunctions.getTextFromName(eggId);
					Main.instance.getLangHandler().sendMessage(player, Main.instance.getLangHandler().getText("SpawnerChangedTo", spawnerName));
				}
			}
		}
	}

	/**
	 * CreatureSpawnEvent for spawn frequency chance
	 *
	 * @param event CreatureSpawnEvent
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	private void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) {

			// get type of entity
			String entityName = event.getEntityType().name();

			// apply spawn frequency chance
			boolean doSpawn = SpawnerFunctions.chance("frequency." + entityName);
			if (!doSpawn) {

				// stop creature from spawning
				event.setCancelled(true);
				return;
			}

			// handle specific spawner types
			switch (entityName) {
				case "FireworksRocketEntity":
					// make fireworks!
					SpawnerFunctions.spawnRandomFirework(event.getLocation());
					break;
				case "XPOrb":
					// make xp orbs drop xp worth something
					ExperienceOrb xpOrb = (ExperienceOrb) event.getEntity();
					Random random = new Random();
					xpOrb.setExperience(random.nextInt(10 - 1) + 1);
					break;
				default:
					break;
			}
		}
	}

	/**
	 * Prevent anvil renaming of spawners.
	 *
	 * @param event InventoryClickEvent
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	private void onInventoryClickEvent(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (player == null) {
			return;
		}

		ItemStack item = event.getCurrentItem();
		if (item == null) {
			return;
		}

		Material type = item.getType();
		if (type == null) {
			return;
		}

		// can we stop creative mode messing spawners up? A: Somewhat
		if (type == Material.MOB_SPAWNER && player.getGameMode() == GameMode.CREATIVE) {
			Main.instance.getLangHandler().sendMessage(player, Main.instance.getLangHandler().getText("NoCreative"));
			event.setCancelled(true);
			player.updateInventory();
			return;
		}

		if (!(event.getInventory() instanceof AnvilInventory)) {
			return;
		}
		if (event.getSlotType() != SlotType.RESULT) {
			return;
		}

		if (type == Material.MOB_SPAWNER && !player.hasPermission("spawner.anvil.spawners")) {
			Main.instance.getLangHandler().sendMessage(player, Main.instance.getLangHandler().getText("NoPermission"));
			event.setCancelled(true);
			return;
		}
		if (type == Material.MONSTER_EGG && !player.hasPermission("spawner.anvil.eggs")) {
			Main.instance.getLangHandler().sendMessage(player, Main.instance.getLangHandler().getText("NoPermission"));
			event.setCancelled(true);
		}
	}
}
