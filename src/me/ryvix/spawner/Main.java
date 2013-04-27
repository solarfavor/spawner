/**
 *   Spawner - Gather mob spawners with silk touch enchanted tools and the
 *   ability to change mob types.
 *
 *   Copyright (C) 2012-2013 Ryan Rhode - rrhode@gmail.com
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

import me.ryvix.spawner.SpawnerCommands;

import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin {

	@Override
	public void onEnable() {

		getServer().getPluginManager().registerEvents(new me.ryvix.spawner.SpawnerEvents(this), this);

		// spawner
		getCommand("spawner").setExecutor(new SpawnerCommands(this));
	}

	@Override
	public void onDisable() {
	}
}