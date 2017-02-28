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

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;

public class SpawnerCommands implements CommandExecutor {

	/**
	 * "/command" command controller.
	 *
	 * @param sender Command sender
	 * @param cmd    Command
	 * @param label  Command alias
	 * @param args   Other parameters
	 * @return boolean
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String cName = cmd.getName();

		if (cName.equalsIgnoreCase("spawner")) {
			if (args.length == 0) {

				// catch console
				if (!(sender instanceof Player)) {
					Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("ConsoleUsageGive"));
					return true;
				}

				// get spawner type
				if (sender.hasPermission("spawner.get")) {
					Player player = (Player) sender;
					Block target = SpawnerFunctions.findSpawnerBlock(player, 20);
					if (target.getType() == Material.MOB_SPAWNER) {
						SpawnerType spawnerType = SpawnerFunctions.getSpawner(target);
						if (spawnerType == null) {
							Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("InvalidSpawner"));
							// it's no longer a valid spawner for some reason
							// maybe no longer in valid_entities so lets break it to stop spawns
							// target.breakNaturally();
							return true;
						}
						String text = SpawnerType.getTextFromType(spawnerType);
						Main.instance.getLangHandler().sendMessage(sender, text + " " + Main.instance.getLangHandler().getText("Spawner"));
						return true;

					} else {
						Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("LookAtASpawner"));
						return true;
					}

				} else {
					Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("NoPermission"));
					return true;
				}

			} else if (args.length == 1) {

				// spawner reload
				if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("spawner.reload")) {
					Main.instance.reloadFiles();
					Main.instance.getLogger().info("Reloaded");
					if ((sender instanceof Player)) {
						Main.instance.getLangHandler().sendMessage(sender, ChatColor.GREEN + "Spawner has been reloaded.");
					}
					return true;
				}

				// spawner help
				if (args[0].equalsIgnoreCase("help") && sender.hasPermission("spawner.help")) {

					String contents = SpawnerFunctions.readFile(Main.instance.getDataFolder() + File.separator + Main.instance.getVersion() + File.separator + "help.txt");

					Main.instance.getLangHandler().sendMessage(sender, contents);
					return true;
				}

				// list entities
				if (args[0].equalsIgnoreCase("list") && sender.hasPermission("spawner.list")) {

					String contents = SpawnerFunctions.readFile(Main.instance.getDataFolder() + File.separator + Main.instance.getVersion() + File.separator + "list.txt");

					Main.instance.getLangHandler().sendMessage(sender, contents);
					return true;
				}

				// catch console
				if (!(sender instanceof Player)) {
					Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("ConsoleUsageGive"));
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
								Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("InvalidSpawner"));
								return true;
							}
							Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("SpawnerChangedTo", type));
							return true;

						} else {
							Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("InvalidSpawner"));
							return true;
						}

					} else {
						// otherwise set type of spawner in players hand

						PlayerInventory playerInv = player.getInventory();
						ItemStack itemInMainHand = playerInv.getItemInMainHand();
						ItemStack itemInOffHand = playerInv.getItemInOffHand();

						if (itemInMainHand.getType() == Material.MOB_SPAWNER || itemInOffHand.getType() == Material.MOB_SPAWNER) {

							if (SpawnerFunctions.isValidEntity(args[0])) {
								SpawnerType spawnerType = SpawnerType.fromName(args[0]);

								if (spawnerType == null) {
									Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("InvalidSpawner"));
									return true;
								}

								String spawnerName = SpawnerType.getTextFromName(args[0]);
								if (spawnerName == null) {
									Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("InvalidSpawner"));
									return true;
								}

								if (itemInMainHand.getType() == Material.MOB_SPAWNER) {

									// get a new spawner
									Spawner newSpawner = SpawnerFunctions.makeSpawner(spawnerName, itemInMainHand.getAmount());

									// set item in players hand
									playerInv.setItemInMainHand(newSpawner);

								} else if (itemInOffHand.getType() == Material.MOB_SPAWNER) {

									// get a new spawner
									Spawner newSpawner = SpawnerFunctions.makeSpawner(spawnerName, itemInOffHand.getAmount());

									// set item in players hand
									playerInv.setItemInOffHand(newSpawner);
								}

								Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("SpawnerChangedTo", spawnerName));
								return true;
							} else {
								Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("InvalidSpawner"));
								return true;
							}
						} else {
							Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("NotPossible"));
							return true;
						}
					}
				} else {
					Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("NoPermission"));
				}

				return true;

			} else if (args.length == 2) {

				if (args[0].equalsIgnoreCase("give")) {
					// give a spawner

					// catch console
					if (!(sender instanceof Player)) {
						Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("ConsoleUsageGive"));
						return true;
					}

					if (sender.hasPermission("spawner.give.all") || sender.hasPermission("spawner.give." + SpawnerFunctions.convertAlias(args[1]).toLowerCase())) {
						Player player = (Player) sender;

						if (SpawnerFunctions.isValidEntity(args[1])) {
							SpawnerType spawnerType = SpawnerFunctions.getSpawnerType(args[1]);
							if (spawnerType == null) {
								Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("InvalidSpawner"));
								return true;
							}

							String spawnerName = SpawnerType.getTextFromName(args[1]);
							if (spawnerName == null) {
								Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("InvalidSpawner"));
								return true;
							}

							// get a new spawner
							Spawner newSpawner = SpawnerFunctions.makeSpawner(spawnerName);

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

							Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("GivenSpawner", spawnerName));
							return true;
						} else {
							Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("InvalidSpawner"));
							return true;
						}
					} else {
						Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("NoPermission"));
					}

					return true;

				} else if (args[0].equalsIgnoreCase("remove")) {

					// /spawner remove <entity>
					if (sender instanceof Player && sender.hasPermission("spawner.remove")) {
						if (SpawnerFunctions.isValidEntity(args[1])) {
							int radius = Main.getSpawnerConfig().getInt("remove_radius");
							SpawnerFunctions.removeEntities((Player) sender, args[1].toLowerCase(), radius);
						} else {
							Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("InvalidSpawner"));
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
							Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("ConsoleUsageGive"));
							return true;
						}
						Player player = (Player) sender;

						if (SpawnerFunctions.isValidEntity(args[1])) {
							SpawnerType spawnerType = SpawnerFunctions.getSpawnerType(args[1]);
							if (spawnerType == null) {
								Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("InvalidSpawner"));
								return true;
							}

							String spawnerName = SpawnerType.getTextFromName(args[1]);
							if (spawnerName == null) {
								Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("InvalidSpawner"));
								return true;
							}

							// get a new spawner
							Spawner newSpawner = SpawnerFunctions.makeSpawner(spawnerName, Integer.parseInt(args[2]));

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

							Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("GivenSpawner", spawnerName));
							return true;
						} else {
							Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("InvalidSpawner"));
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
								Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("InvalidSpawner"));
								return true;
							}

							String spawnerName = SpawnerType.getTextFromName(args[1]);
							if (spawnerName == null) {
								Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("InvalidSpawner"));
								return true;
							}

							// get a new spawner
							Spawner newSpawner = SpawnerFunctions.makeSpawner(spawnerName);
							Player targetPlayer = Main.instance.getServer().getPlayer(args[2]);
							if (targetPlayer != null) {

								PlayerInventory inventory = targetPlayer.getInventory();
								int invSlot = inventory.firstEmpty();

								// drop spawner at player location or add it to their inv if they have space
								if (invSlot == -1) {

									// if target player is online drop it at their feet and tell them
									targetPlayer.getWorld().dropItem(targetPlayer.getLocation().add(0, 1, 0), newSpawner);
									Main.instance.getLangHandler().sendMessage(targetPlayer, Main.instance.getLangHandler().getText("SpawnerDropped", spawnerName));
									return true;

								} else {

									inventory.setItem(invSlot, newSpawner);

									// make sure to show the player the item
									targetPlayer.updateInventory();

									if (targetPlayer != null) {
										Main.instance.getLangHandler().sendMessage(targetPlayer, Main.instance.getLangHandler().getText("GivenSpawner", spawnerName));
									} else {
										Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("NotDeliveredOffline", args[2]));
										return true;
									}

									String[] vars = new String[2];
									vars[0] = spawnerName;
									vars[1] = targetPlayer.getName();
									Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("YouGaveSpawner", vars));
								}

								return true;

							} else {
								// tell sender the target didn't get the spawner
								Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("NotDeliveredOffline", args[2]));
								return true;
							}
						} else {
							Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("InvalidSpawner"));
							return true;
						}

					} else {
						Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("NoPermission"));
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
								Main.instance.getLangHandler().sendMessage(sender, ChatColor.RED + Main.instance.getLangHandler().getText("InvalidRadius"));
								return false;
							}

							SpawnerFunctions.removeEntities((Player) sender, args[1].toLowerCase(), radius);
						} else {
							Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("InvalidSpawner"));
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
										Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("InvalidSpawner"));
										return true;
									}

									String spawnerName = SpawnerType.getTextFromName(args[1]);
									if (spawnerName == null) {
										Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("InvalidSpawner"));
										return true;
									}

									// get a new spawner
									Spawner newSpawner = SpawnerFunctions.makeSpawner(spawnerName, Integer.parseInt(args[3]));

									// set name
									Player targetPlayer = Main.instance.getServer().getPlayer(args[2]);
									if (targetPlayer != null) {

										PlayerInventory inventory = targetPlayer.getInventory();
										int invSlot = inventory.firstEmpty();

										// drop spawner at player location or add it to their inv if they have space
										if (invSlot == -1) {

											// if target player is online drop it at their feet and tell them
											targetPlayer.getWorld().dropItem(targetPlayer.getLocation().add(0, 1, 0), newSpawner);
											Main.instance.getLangHandler().sendMessage(targetPlayer, Main.instance.getLangHandler().getText("SpawnerDropped", spawnerName));
											return true;

										} else {

											inventory.setItem(invSlot, newSpawner);

											// make sure to show the player the item
											targetPlayer.updateInventory();

											if (targetPlayer != null) {
												Main.instance.getLangHandler().sendMessage(targetPlayer, Main.instance.getLangHandler().getText("GivenSpawner", spawnerName));
											} else {
												Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("NotDeliveredOffline", args[2]));
												return true;
											}

											String[] vars = new String[2];
											vars[0] = spawnerName;
											vars[1] = targetPlayer.getName();
											Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("YouGaveSpawner", vars));
										}

										return true;

									} else {
										// tell sender the target didn't get the spawner
										Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("NotDeliveredOffline", args[2]));
										return true;
									}
								} else {
									Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("InvalidSpawner"));
									return true;
								}

							}
						}
					}
				}
			} else {
				Main.instance.getLangHandler().sendMessage(sender, Main.instance.getLangHandler().getText("NoPermission"));
			}
		}

		return true;
	}
}
