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
			if (!(sender instanceof Player)) {
				plugin.getLogger().info("This command can only be run by a player!");
				return true;
			}

			// set spawner type
			if (args.length > 0) {

				if (sender.hasPermission("spawner.set")) {
					Player player = (Player) sender;
					Block target = player.getTargetBlock(null, 20);

					if (target.getType() == Material.MOB_SPAWNER) {
						// set type of spawner player is targeting

						Spawner spawner = new Spawner(plugin);
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
						ItemStack newSpawner = new ItemStack(Material.MOB_SPAWNER, 1, durability);

						// formatted name
						String name = SpawnerFunctions.formatName(spawnerName);

						// set lore
						newSpawner = SpawnerFunctions.setSpawnerLore(newSpawner, name);

						// set durability
						newSpawner.setDurability(durability);

						// set item in players hand
						player.setItemInHand(newSpawner);

						sender.sendMessage(ChatColor.GREEN + "Spawner type changed to " + spawnerName);
					}
				}

				return true;

			} else if (args.length == 0) {
				Player player = (Player) sender;
				Block target = player.getTargetBlock(null, 20);

				if (target.getType() == Material.MOB_SPAWNER) {

					Spawner spawner = new Spawner(plugin);
					String type = spawner.getSpawner(target).getName();
					type = type.toLowerCase().replaceFirst(type.substring(0, 1), type.substring(0, 1).toUpperCase());
					sender.sendMessage(ChatColor.GREEN + type + " spawner.");
				} else {
					sender.sendMessage(ChatColor.RED + "Look at a spawner to see what kind it is!");
				}

			} else {
				sender.sendMessage(ChatColor.RED + "You don't have permission to use that command!");
				return true;
			}
		}

		return true;
	}
}
