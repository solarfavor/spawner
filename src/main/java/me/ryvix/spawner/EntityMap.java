package me.ryvix.spawner;

import java.util.HashMap;
import java.util.Map;

/**
 * In Spigot 1.11 all EntityType names changed to the new format. For backwards compatibility they must be mapped.
 *
 * https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/diff/src/main/java/org/bukkit/entity/EntityType.java?until=dd1c703c9ff5fcaf625b479c5f800489663fc746
 */
public class EntityMap {
	private static Map<String, String> entities = new HashMap<>();

	public static void addMap(String from, String to) {
		entities.put(from, to);
	}

	public static String getValue(String entity) {
		return entities.get(entity);
	}

	public static String getKey(String entity) {
		String key = null;
		for (Map.Entry<String, String> e : entities.entrySet()) {
			if(e.getKey().equals(entity)) {
				key = e.getKey();
				break;
			}
		}
		return key;
	}
}
