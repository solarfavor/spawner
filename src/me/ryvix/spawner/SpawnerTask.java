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

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;


public class SpawnerTask implements Runnable {
	short entityID;
	Block block;
	short d;
	Main plugin;
	Player player;

	public SpawnerTask(short entityID, short d, Block block, Main plugin, Player player) {
		this.entityID = entityID;
		this.block = block;
		this.d = d;
		this.plugin = plugin;
		this.player = player;
	}

	public void run() {

		EntityType spawnerType = EntityType.fromId(d);

		if (spawnerType == null) {
			return;
		}

		Spawner spawner = new Spawner();

		if (spawner.setSpawner(block, spawnerType.getName())) {
			player.sendMessage(ChatColor.GREEN + "Placed " + spawnerType.getName() + " spawner");
		} else {
			player.sendMessage(ChatColor.RED + "It was not possible to change that spawner.");
		}
	}

}