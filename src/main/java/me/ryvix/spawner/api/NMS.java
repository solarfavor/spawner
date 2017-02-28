package me.ryvix.spawner.api;

import me.ryvix.spawner.Spawner;
import org.bukkit.inventory.ItemStack;

// https://bukkit.org/threads/support-multiple-minecraft-versions-with-abstraction-maven.115810/
// https://github.com/mbax/AbstractionExamplePlugin
public interface NMS {
	String getEntityNameFromSpawnerNBT(Spawner spawner);

	ItemStack setSpawnerNBT(Spawner spawner);

	String getEntityNameFromSpawnEgg(ItemStack is);
}
