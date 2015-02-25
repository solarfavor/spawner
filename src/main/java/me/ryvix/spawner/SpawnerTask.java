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

import me.ryvix.spawner.language.Keys;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class SpawnerTask implements Runnable {

	short entityID;
	Block block;
	String name;
	Player player;

	public SpawnerTask(short entityID, String name, Block block, Player player) {
		this.entityID = entityID;
		this.block = block;
		this.name = name;
		this.player = player;
	}

	@Override
	public void run() {

		SpawnerType spawnerType = SpawnerFunctions.getSpawnerType(name);

		if (spawnerType == null) {
			return;
		}

		if (SpawnerFunctions.setSpawner(block, spawnerType.getName())) {
			Main.language.sendMessage(player, Main.language.getText(Keys.PlacedSpawner, SpawnerType.getTextFromName(name)));
		} else {
			Main.language.sendMessage(player, Main.language.getText(Keys.NotPossible));
		}
	}
}
