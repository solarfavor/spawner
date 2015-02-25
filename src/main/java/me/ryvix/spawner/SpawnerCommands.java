/**
 * Spawner - Gather mob spawners with silk touch enchanted tools and the ability
 * to change mob types.
 *
 * Copyright (C) 2012-2015 Ryan Rhode - rrhode@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package me.ryvix.spawner;

import me.ryvix.spawner.language.Keys;
import org.apache.commons.lang.StringUtils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class SpawnerCommands implements CommandExecutor {

	/**
	 * "/command" command controller.
	 *
	 * @param sender Command sender
	 * @param cmd Command
	 * @param label Command alias
	 * @param args Other parameters
	 * @return boolean
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String cName = cmd.getName();

		if (cName.equalsIgnoreCase("spawner")) {

			if (args.length == 0) {

				// catch console
				if (!(sender instanceof Player)) {
					Main.language.sendMessage(sender, Main.language.getText(Keys.ConsoleUsageGive));
					return true;
				}

				// get spawner type
				if (sender.hasPermission("spawner.get")) {
					Player player = (Player) sender;
					Block target = SpawnerFunctions.findSpawnerBlock(player, 20);

					if (target.getType() == Material.MOB_SPAWNER) {
						SpawnerType spawnerType = SpawnerFunctions.getSpawner(target);
						if (spawnerType == null) {
							Main.language.sendMessage(sender, Main.language.getText(Keys.InvalidSpawner));
							// it's no longer a valid spawner for some reason
							// maybe no longer in valid_entities so lets break it to stop spawns
							// target.breakNaturally();
							return true;
						}
						String text = SpawnerType.getTextFromType(spawnerType);
						Main.language.sendMessage(sender, text + " " + Main.language.getText(Keys.Spawner));
						return true;

					} else {
						Main.language.sendMessage(sender, Main.language.getText(Keys.LookAtASpawner));
						return true;
					}

				} else {
					Main.language.sendMessage(sender, Main.language.getText(Keys.NoPermission));
					return true;
				}

			} else if (args.length == 1) {

				// spawner reload
				if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("spawner.reload")) {
					Main.instance.reloadFiles();
					Main.instance.getLogger().info("Reloaded");
					if ((sender instanceof Player)) {
						Main.language.sendMessage(sender, ChatColor.GREEN + "Spawner has been reloaded.");
					}
					return true;
				}

				// spawner help
				if (args[0].equalsIgnoreCase("help") && sender.hasPermission("spawner.help")) {

					String contents = SpawnerFunctions.readFile(Main.instance.getDataFolder() + System.getProperty("file.separator") + "help.txt");

					Main.language.sendMessage(sender, contents);
					return true;
				}

				// list entities
				if (args[0].equalsIgnoreCase("list") && sender.hasPermission("spawner.list")) {

					String contents = SpawnerFunctions.readFile(Main.instance.getDataFolder() + System.getProperty("file.separator") + "list.txt");

					Main.language.sendMessage(sender, contents);
					return true;
				}

				// catch console
				if (!(sender instanceof Player)) {
					Main.language.sendMessage(sender, Main.language.getText(Keys.ConsoleUsageGive));
					return true;
				}

				// set spawner type
				if (sender.hasPermission("spawner.set.all") || sender.hasPermission("spawner.set." + SpawnerFunctions.convertAlias(args[0]).toLowerCase())) {
					Player player = (Player) sender;
					Block target = SpawnerFunctions.findSpawnerBlock(player, 20);

					if (target.getType() == Material.MOB_SPAWNER) {
						// set type of spawner player is targeting

						// setSpawner does it's own isValidEntity
						if (SpawnerFunctions.setSpawner(target, args[0])) {
							String type = SpawnerType.getTextFromName(args[0]);
							if (type == null) {
								Main.language.sendMessage(sender, Main.language.getText(Keys.InvalidSpawner));
								return true;
							}
							Main.language.sendMessage(sender, Main.language.getText(Keys.SpawnerChangedTo, type));
							return true;

						} else {
							Main.language.sendMessage(sender, Main.language.getText(Keys.InvalidSpawner));
							return true;
						}

					} else {
						if (player.getItemInHand().getType() == Material.MOB_SPAWNER) {
							// otherwise set type of spawner in players hand
							if (SpawnerFunctions.isValidEntity(args[0])) {
								SpawnerType spawnerType = SpawnerType.fromName(args[0]);
								if (spawnerType == null) {
									Main.language.sendMessage(sender, Main.language.getText(Keys.InvalidSpawner));
									return true;
								}

								String spawnerName = SpawnerType.getTextFromName(args[0]);
								if (spawnerName == null) {
									Main.language.sendMessage(sender, Main.language.getText(Keys.InvalidSpawner));
									return true;
								}

								// make an ItemStack
								Spawner newSpawner = new Spawner(Material.MOB_SPAWNER, player.getItemInHand().getAmount());

								// set name
								newSpawner = SpawnerFunctions.setSpawnerName(newSpawner, spawnerName);

								// set item in players hand
								player.setItemInHand(newSpawner);

								Main.language.sendMessage(sender, Main.language.getText(Keys.SpawnerChangedTo, spawnerName));
								return true;
							} else {
								Main.language.sendMessage(sender, Main.language.getText(Keys.InvalidSpawner));
								return true;
							}
						} else {
							Main.language.sendMessage(sender, Main.language.getText(Keys.NotPossible));
							return true;
						}
					}
				} else {
					Main.language.sendMessage(sender, Main.language.getText(Keys.NoPermission));
				}

				return true;

			} else if (args.length == 2) {

				if (args[0].equalsIgnoreCase("give")) {
					// give a spawner

					// catch console
					if (!(sender instanceof Player)) {
						Main.language.sendMessage(sender, Main.language.getText(Keys.ConsoleUsageGive));
						return true;
					}

					if (sender.hasPermission("spawner.give.all") || sender.hasPermission("spawner.give." + SpawnerFunctions.convertAlias(args[1]).toLowerCase())) {
						Player player = (Player) sender;

						if (SpawnerFunctions.isValidEntity(args[1])) {
							SpawnerType spawnerType = SpawnerFunctions.getSpawnerType(args[1]);
							if (spawnerType == null) {
								Main.language.sendMessage(sender, Main.language.getText(Keys.InvalidSpawner));
								return true;
							}

							String spawnerName = SpawnerType.getTextFromName(args[1]);
							if (spawnerName == null) {
								Main.language.sendMessage(sender, Main.language.getText(Keys.InvalidSpawner));
								return true;
							}

							// make an ItemStack
							Spawner newSpawner = new Spawner(Material.MOB_SPAWNER, 1);

							// set name
							newSpawner = SpawnerFunctions.setSpawnerName(newSpawner, spawnerName);

							// drop spawner at player location or add it to their inv if they have space
							PlayerInventory inventory = player.getInventory();
							if (inventory.firstEmpty() == -1) {
								player.getWorld().dropItem(player.getLocation().add(0, 1, 0), newSpawner);
							} else {
								int invSlot = inventory.firstEmpty();
								inventory.setItem(invSlot, newSpawner);

								// make sure to show the player the item
								player.updateInventory();
							}

							Main.language.sendMessage(sender, Main.language.getText(Keys.GivenSpawner, spawnerName));
							return true;
						} else {
							Main.language.sendMessage(sender, Main.language.getText(Keys.InvalidSpawner));
							return true;
						}
					} else {
						Main.language.sendMessage(sender, Main.language.getText(Keys.NoPermission));
					}

					return true;

				} else if (args[0].equalsIgnoreCase("remove")) {

					// /spawner remove <entity>
					if (sender instanceof Player && sender.hasPermission("spawner.remove")) {
						if (SpawnerFunctions.isValidEntity(args[1])) {
							int radius = Main.instance.config.getInt("remove_radius");
							SpawnerFunctions.removeEntities((Player) sender, args[1].toLowerCase(), radius);
						} else {
							Main.language.sendMessage(sender, Main.language.getText(Keys.InvalidSpawner));
							return true;
						}
					}
				}

			} else if (args.length == 3) {

				if (args[0].equalsIgnoreCase("give")) {

					// amount parameter for give to self
					if (StringUtils.isNumeric(args[2])) {
						// /spawner give <entity> <amount>

						// catch console
						if (!(sender instanceof Player)) {
							Main.language.sendMessage(sender, Main.language.getText(Keys.ConsoleUsageGive));
							return true;
						}
						Player player = (Player) sender;

						if (SpawnerFunctions.isValidEntity(args[1])) {
							SpawnerType spawnerType = SpawnerFunctions.getSpawnerType(args[1]);
							if (spawnerType == null) {
								Main.language.sendMessage(sender, Main.language.getText(Keys.InvalidSpawner));
								return true;
							}

							String spawnerName = SpawnerType.getTextFromName(args[1]);
							if (spawnerName == null) {
								Main.language.sendMessage(sender, Main.language.getText(Keys.InvalidSpawner));
								return true;
							}

							// make an ItemStack
							Spawner newSpawner = new Spawner(Material.MOB_SPAWNER, Integer.parseInt(args[2]));

							// set name
							newSpawner = SpawnerFunctions.setSpawnerName(newSpawner, spawnerName);

							// drop spawner at player location or add it to their inv if they have space
							PlayerInventory inventory = player.getInventory();
							if (inventory.firstEmpty() == -1) {
								player.getWorld().dropItem(player.getLocation().add(0, 1, 0), newSpawner);
							} else {
								int invSlot = inventory.firstEmpty();
								inventory.setItem(invSlot, newSpawner);

								// make sure to show the player the item
								player.updateInventory();
							}

							Main.language.sendMessage(sender, Main.language.getText(Keys.GivenSpawner, spawnerName));
							return true;
						} else {
							Main.language.sendMessage(sender, Main.language.getText(Keys.InvalidSpawner));
							return true;
						}

					}

					// give a spawner to others
					if (sender.hasPermission("spawner.giveothers.all") || sender.hasPermission("spawner.giveothers." + SpawnerFunctions.convertAlias(args[1]).toLowerCase())) {
						// give a spawner
						// /spawner give <entity> <player>

						if (SpawnerFunctions.isValidEntity(args[1])) {
							SpawnerType spawnerType = SpawnerFunctions.getSpawnerType(args[1]);
							if (spawnerType == null) {
								Main.language.sendMessage(sender, Main.language.getText(Keys.InvalidSpawner));
								return true;
							}

							String spawnerName = SpawnerType.getTextFromName(args[1]);
							if (spawnerName == null) {
								Main.language.sendMessage(sender, Main.language.getText(Keys.InvalidSpawner));
								return true;
							}

							// make an ItemStack
							Spawner newSpawner = new Spawner(Material.MOB_SPAWNER, 1);

							// set name
							newSpawner = SpawnerFunctions.setSpawnerName(newSpawner, spawnerName);

							Player targetPlayer = Main.instance.getServer().getPlayer(args[2]);
							if (targetPlayer != null) {

								PlayerInventory inventory = targetPlayer.getInventory();
								int invSlot = inventory.firstEmpty();

								// drop spawner at player location or add it to their inv if they have space
								if (invSlot == -1) {

									// if target player is online drop it at their feet and tell them
									targetPlayer.getWorld().dropItem(targetPlayer.getLocation().add(0, 1, 0), newSpawner);
									Main.language.sendMessage(targetPlayer, Main.language.getText(Keys.SpawnerDropped, spawnerName));
									return true;

								} else {

									inventory.setItem(invSlot, newSpawner);

									// make sure to show the player the item
									targetPlayer.updateInventory();

									if (targetPlayer != null) {
										Main.language.sendMessage(targetPlayer, Main.language.getText(Keys.GivenSpawner, spawnerName));
									} else {
										Main.language.sendMessage(sender, Main.language.getText(Keys.NotDeliveredOffline, args[2]));
										return true;
									}

									String[] vars = new String[2];
									vars[0] = spawnerName;
									vars[1] = targetPlayer.getName();
									Main.language.sendMessage(sender, Main.language.getText(Keys.YouGaveSpawner, vars));
								}

								return true;

							} else {
								// tell sender the target didn't get the spawner
								Main.language.sendMessage(sender, Main.language.getText(Keys.NotDeliveredOffline, args[2]));
								return true;
							}
						} else {
							Main.language.sendMessage(sender, Main.language.getText(Keys.InvalidSpawner));
							return true;
						}

					} else {
						Main.language.sendMessage(sender, Main.language.getText(Keys.NoPermission));
					}

					return true;

				} else if (args[0].equalsIgnoreCase("remove")) {

					// /spawner remove <entity> <radius>
					if (sender instanceof Player && sender.hasPermission("spawner.remove")) {
						if (SpawnerFunctions.isValidEntity(args[1])) {
							int radius;
							try {
								radius = Integer.parseInt(args[2]);
							} catch (NumberFormatException e) {
								Main.language.sendMessage(sender, ChatColor.RED + Main.language.getText(Keys.InvalidRadius));
								return false;
							}

							SpawnerFunctions.removeEntities((Player) sender, args[1].toLowerCase(), radius);
						} else {
							Main.language.sendMessage(sender, Main.language.getText(Keys.InvalidSpawner));
							return true;
						}
					}
				}

			} else if (args.length == 4) {
				// give a spawner to others

				if (args[0].equalsIgnoreCase("give")) {

					// amount parameter for give to others
					if (StringUtils.isNumeric(args[3])) {
						if (sender.hasPermission("spawner.giveothers.all") || sender.hasPermission("spawner.giveothers." + SpawnerFunctions.convertAlias(args[1]).toLowerCase())) {
						// give a spawner
							// /spawner give <entity> <player> <amount>

							// amount parameter for give to self
							if (StringUtils.isNumeric(args[3])) {

								if (SpawnerFunctions.isValidEntity(args[1])) {
									SpawnerType spawnerType = SpawnerFunctions.getSpawnerType(args[1]);
									if (spawnerType == null) {
										Main.language.sendMessage(sender, Main.language.getText(Keys.InvalidSpawner));
										return true;
									}

									String spawnerName = SpawnerType.getTextFromName(args[1]);
									if (spawnerName == null) {
										Main.language.sendMessage(sender, Main.language.getText(Keys.InvalidSpawner));
										return true;
									}

									// make an ItemStack
									Spawner newSpawner = new Spawner(Material.MOB_SPAWNER, Integer.parseInt(args[3]));

									// set name
									newSpawner = SpawnerFunctions.setSpawnerName(newSpawner, spawnerName);
									Player targetPlayer = Main.instance.getServer().getPlayer(args[2]);
									if (targetPlayer != null) {

										PlayerInventory inventory = targetPlayer.getInventory();
										int invSlot = inventory.firstEmpty();

										// drop spawner at player location or add it to their inv if they have space
										if (invSlot == -1) {

											// if target player is online drop it at their feet and tell them
											targetPlayer.getWorld().dropItem(targetPlayer.getLocation().add(0, 1, 0), newSpawner);
											Main.language.sendMessage(targetPlayer, Main.language.getText(Keys.SpawnerDropped, spawnerName));
											return true;

										} else {

											inventory.setItem(invSlot, newSpawner);

											// make sure to show the player the item
											targetPlayer.updateInventory();

											if (targetPlayer != null) {
												Main.language.sendMessage(targetPlayer, Main.language.getText(Keys.GivenSpawner, spawnerName));
											} else {
												Main.language.sendMessage(sender, Main.language.getText(Keys.NotDeliveredOffline, args[2]));
												return true;
											}

											String[] vars = new String[2];
											vars[0] = spawnerName;
											vars[1] = targetPlayer.getName();
											Main.language.sendMessage(sender, Main.language.getText(Keys.YouGaveSpawner, vars));
										}

										return true;

									} else {
										// tell sender the target didn't get the spawner
										Main.language.sendMessage(sender, Main.language.getText(Keys.NotDeliveredOffline, args[2]));
										return true;
									}
								} else {
									Main.language.sendMessage(sender, Main.language.getText(Keys.InvalidSpawner));
									return true;
								}

							}
						}
					}
				}
			} else {
				Main.language.sendMessage(sender, Main.language.getText(Keys.NoPermission));
			}
		}

		return true;
	}
}
