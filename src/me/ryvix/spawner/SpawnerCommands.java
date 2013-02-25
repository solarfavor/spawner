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

import me.ryvix.spawner.Main;
import me.ryvix.spawner.Spawner;
import me.ryvix.spawner.SpawnerFunctions;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SpawnerCommands implements CommandExecutor {

	private Main plugin;

	public SpawnerCommands(Main plugin) {
		this.plugin = plugin;
	}

	/**
	 * '/command' command controller.
	 * 
	 * @param sender Command sender
	 * @param cmd Command
	 * @param label Command alias
	 * @param args Other params
	 * @return boolean
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String cName = cmd.getName();

		if (cName.equalsIgnoreCase("spawner")) {

			if (args.length == 0) {
				// get spawner type

				if (sender.hasPermission("spawner.get")) {
					Player player = (Player) sender;
					Block target = player.getTargetBlock(null, 20);

					if (target.getType() == Material.MOB_SPAWNER) {
						Spawner spawner = new Spawner();
						String type = spawner.getSpawner(target).getName();
						type = type.toLowerCase().replaceFirst(type.substring(0, 1), type.substring(0, 1).toUpperCase());
						sender.sendMessage(ChatColor.GREEN + type + " spawner.");

					} else {
						sender.sendMessage(ChatColor.RED + "Look at a spawner to see what kind it is!");
					}

				} else {
					sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
					return true;
				}

			} else if (args.length == 1) {
				// set spawner type

				if (!(sender instanceof Player)) {
					plugin.getLogger().info("This command can only be run by a player!");
					return true;
				}

				if (sender.hasPermission("spawner.set.*") || sender.hasPermission("spawner.set." + args[0].toLowerCase())) {
					Player player = (Player) sender;
					Block target = player.getTargetBlock(null, 20);

					if (target.getType() == Material.MOB_SPAWNER) {
						// set type of spawner player is targeting

						Spawner spawner = new Spawner();
						if (spawner.setSpawner(target, args[0])) {
							String spawnerName = args[0].toLowerCase().replaceFirst(args[0].substring(0, 1), args[0].substring(0, 1).toUpperCase());

							sender.sendMessage(ChatColor.GREEN + "Spawner type changed to " + spawnerName);

						} else {
							sender.sendMessage(ChatColor.RED + "Invalid spawner type!");
						}

					} else if (player.getItemInHand().getType() == Material.MOB_SPAWNER) {
						// otherwise set type of spawner in players hand

						EntityType spawnerType = EntityType.fromName(args[0]);

						if (spawnerType == null) {
							sender.sendMessage(ChatColor.RED + "Invalid spawner type!");
							return true;
						}

						short durability = spawnerType.getTypeId();
						String spawnerName = spawnerType.getName();

						// make an ItemStack
						ItemStack newSpawner = new ItemStack(Material.MOB_SPAWNER, player.getItemInHand().getAmount(), durability);

						// formatted name
						String name = SpawnerFunctions.formatName(spawnerName);

						// set lore
						newSpawner = SpawnerFunctions.setSpawnerName(newSpawner, name);

						// set durability
						newSpawner.setDurability(durability);

						// set item in players hand
						player.setItemInHand(newSpawner);

						sender.sendMessage(ChatColor.GREEN + "Spawner type changed to " + spawnerName);
					}
				} else {
					sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
				}

				return true;

			} else if (args.length == 2) {
				// give a spawner

				if (!(sender instanceof Player)) {
					plugin.getLogger().info("This command can only be run by a player!");
					return true;
				}

				if (sender.hasPermission("spawner.give.*") || sender.hasPermission("spawner.give." + args[1].toLowerCase())) {
					Player player = (Player) sender;

					EntityType spawnerType = EntityType.fromName(args[1]);

					if (spawnerType == null) {
						sender.sendMessage(ChatColor.RED + "Invalid spawner type!");
						return true;
					}

					short durability = spawnerType.getTypeId();
					String spawnerName = spawnerType.getName();

					// make an ItemStack
					ItemStack newSpawner = new ItemStack(Material.MOB_SPAWNER, 1, durability);

					// formatted name
					String name = SpawnerFunctions.formatName(spawnerName);

					// set lore
					newSpawner = SpawnerFunctions.setSpawnerName(newSpawner, name);

					// set durability
					newSpawner.setDurability(durability);

					// drop spawner at player location or add it to their inv if they have space
					PlayerInventory inventory = player.getInventory();
					if (inventory.firstEmpty() == -1) {
						player.getWorld().dropItem(player.getLocation().add(0, 1, 0), newSpawner);
					} else {
						int invSlot = inventory.firstEmpty();
						inventory.setItem(invSlot, newSpawner);
					}

					sender.sendMessage(ChatColor.GREEN + "You were given a " + spawnerName + " spawner.");
				} else {
					sender.sendMessage(ChatColor.RED + "You don't have permission to use that command!");
				}

				return true;

			} else if (args.length == 3) {
				// give a spawner to others

				if (sender.hasPermission("spawner.give.others.*") || sender.hasPermission("spawner.give.others." + args[1].toLowerCase())) {
					// give a spawner

					EntityType spawnerType = EntityType.fromName(args[1]);

					if (spawnerType == null) {
						sender.sendMessage(ChatColor.RED + "Invalid spawner type!");
						return true;
					}

					short durability = spawnerType.getTypeId();
					String spawnerName = spawnerType.getName();

					// make an ItemStack
					ItemStack newSpawner = new ItemStack(Material.MOB_SPAWNER, 1, durability);

					// formatted name
					String name = SpawnerFunctions.formatName(spawnerName);

					// set lore
					newSpawner = SpawnerFunctions.setSpawnerName(newSpawner, name);

					// set durability
					newSpawner.setDurability(durability);

					Player targetPlayer = plugin.getServer().getPlayer(args[2]);
					if (targetPlayer != null) {

						PlayerInventory inventory = targetPlayer.getInventory();
						int invSlot = inventory.firstEmpty();

						// drop spawner at player location or add it to their inv if they have space
						if (invSlot == -1) {

							// if target player is online drop it at their feet and tell them
							targetPlayer.getWorld().dropItem(targetPlayer.getLocation().add(0, 1, 0), newSpawner);
							targetPlayer.sendMessage(ChatColor.GREEN + "A " + spawnerName + " spawner was dropped at your feet because your inventory is full.");

						} else {

							inventory.setItem(invSlot, newSpawner);

							if (targetPlayer != null) {
								targetPlayer.sendMessage(ChatColor.GREEN + "You were given a " + spawnerName + " spawner.");
							}

							sender.sendMessage(ChatColor.GREEN + "You gave a " + spawnerName + " spawner to " + args[2] + ".");
						}

						return true;

					} else {
						// tell sender the target didn't get the spawner
						sender.sendMessage(ChatColor.RED + "The spawner was not delivered because " + args[2] + " is offline.");
					}

				} else {
					sender.sendMessage(ChatColor.RED + "You don't have permission to use that command!");
				}

				return true;

			} else {
				sender.sendMessage(ChatColor.RED + "You don't have permission to use that command!");
				return true;
			}
		}

		return true;
	}
}
