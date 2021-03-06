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

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class SpawnerTask implements Runnable {

	private Block block;
	private String name;
	private Player player;

	SpawnerTask(String name, Block block, Player player) {
		this.block = block;
		this.name = name;
		this.player = player;
	}

	@Override
	public void run() {
		if (SpawnerFunctions.setSpawner(block, name)) {
			Main.instance.getLangHandler().sendMessage(player, Main.instance.getLangHandler().getText("PlacedSpawner", SpawnerFunctions.getTextFromName(name)));
		} else {
			Main.instance.getLangHandler().sendMessage(player, Main.instance.getLangHandler().getText("NotPossible"));
		}
	}
}
