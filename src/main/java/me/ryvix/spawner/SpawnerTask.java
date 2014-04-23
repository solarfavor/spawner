/**
 * Spawner - Gather mob spawners with silk touch enchanted tools and the
 * ability to change mob types.
 *
 * Copyright (C) 2012-2014 Ryan Rhode - rrhode@gmail.com
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

import me.ryvix.spawner.language.Keys;

import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SpawnerTask implements Runnable {

	short entityID;
	Block block;
	short d;
	Player player;

	public SpawnerTask(short entityID, short d, Block block, Player player) {
		this.entityID = entityID;
		this.block = block;
		this.d = d;
		this.player = player;
	}

	@Override
	public void run() {

		EntityType spawnerType = SpawnerFunctions.getSpawnerType(d);

		if (spawnerType == null) {
			return;
		}

		Spawner spawner = new Spawner();

		if (spawner.setSpawner(block, spawnerType.getName())) {
			Main.language.sendMessage(player, Main.language.getText(Keys.PlacedSpawner, spawnerType.getName()));
		} else {
			Main.language.sendMessage(player, Main.language.getText(Keys.NotPossible));
		}
	}
}
