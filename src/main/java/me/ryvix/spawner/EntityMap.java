package me.ryvix.spawner;

import java.util.Map;
import java.util.TreeMap;

/**
 * In Spigot 1.11 all EntityType names changed to the new format. For backwards compatibility they must be mapped.
 * <p>
 * https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/diff/src/main/java/org/bukkit/entity/EntityType.java?until=dd1c703c9ff5fcaf625b479c5f800489663fc746
 */
public class EntityMap {
	private static Map<String, String> entities = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

	public static void addMap(String from, String to) {
		entities.put(from, to);
	}

	public static String getValue(String entity) {
		return entities.get(entity);
	}

	public static String getKey(String entity) {
		for (String e : entities.keySet()) {
			if (entities.get(e).equals(entity)) {
				return e;
			}
		}

		return null;
	}


}
