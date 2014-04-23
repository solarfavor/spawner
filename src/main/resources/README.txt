/**
 *   Spawner - Gather mob spawners with silk touch enchanted tools and the
 *   ability to change mob types.
 *
 *   Copyright (C) 2012-2014 Ryan Rhode - rrhode@gmail.com
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

Bukkit plugin page: http://dev.bukkit.org/bukkit-plugins/spawner/
Source: https://github.com/ty2u/spawner

This plugin allows you to gather mob spawners with a Silk Touch enchanted tool.
You also can change the type of spawner you are looking at or holding by using
the /spawner command. For example /spawner creeper will change the spawner to a
creeper spawner.

It will tell you what kind of spawner you interact with.

This plugin may be able to replace other plugins that store mob spawner types
as the item durability. It will also attempt to remove the Silk Touch enchant
from the spawner that some plugins add otherwise players can use them as a Silk
Touch enchanted tool.

It will also prevent explosions from destroying spawners because players were
complaining about this on my server. Preventing them from being destroyed saves
staff work from having to replace them when they are accidentally destroyed by
creepers, etc.

This plugin may use various bits of code I found on the Bukkit forums.

Entities

You can make spawners any of the entity names listed here:
http://jd.bukkit.org/rb/apidocs/src-html/org/bukkit/entity/EntityType.html#line.16
Generally you would use the name that appears inside the quotes in the
brackets. For example PigZombie will work while PIG_ZOMBIE will not.


Commands

/spawner help
List Spawner command help.

/spawner list
List valid entity types. The list is editable.

/spawner <entity>
Switch the spawner you're looking at or holding to the given mob entity type.

/spawner give <entity> [player]
Give a spawner to yourself or another player.

/spawner remove <entity> [radius]
Removes entities of the specified type within a given radius.

Permissions

spawner.*
Gives all Spawner permissions.
Default: op

spawner.reload
Gives access to use /spawner reload to reload the plugin.
Default: op

spawner.set.<entity>
Gives access to use /spawner <entity> to set the spawner entity type.
Use spawner.set.* to allow all spawner entity types.
Default: op

spawner.get
Gives access to right-click a spawner or use /spawner to get the spawner entity
type.
Default: op

spawner.break.<entity>
Allows players to break spawners as normal.
Use spawner.break.* to allow all spawner entity types.
Default: op

spawner.mine.<entity>
Allows players to mine spawners with a silk touch enchanted tool.
Use spawner.mine.* to allow all spawner entity types.
Default: op

spawner.set.<entity>
Gives access to use /spawner <entity>
Use spawner.set.* to allow all spawner entity types.
Default: op

spawner.give.<entity>
Gives access to use /spawner give <entity>
Use spawner.give.* to allow all spawner entity types.
Default: op

spawner.give.others.<entity>
Gives access to use /spawner give <entity> <player>
Use spawner.give.others.* to allow all spawner entity types.
This can also be run from console.
Default: op

spawner.remove
Gives access to use /spawner remove <entity> [radius]
Default: op