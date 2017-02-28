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

import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Originally from Bukkit EntityType enum.
 * Using it here for future use to translate durability to entity names.
 * <p>
 * For future reference:
 * https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/browse/src/main/java/org/bukkit/entity/EntityType.java
 */
public enum SpawnerType {
	EXPERIENCE_ORB("xp_orb", Main.instance.getLangHandler().getEntity("xp_orb"), 2),
	LEASH_HITCH("leash_knot", Main.instance.getLangHandler().getEntity("leash_knot"), 8),
	PAINTING("painting", Main.instance.getLangHandler().getEntity("painting"), 9),
	ARROW("arrow", Main.instance.getLangHandler().getEntity("arrow"), 10),
	SNOWBALL("snowball", Main.instance.getLangHandler().getEntity("snowball"), 11),
	FIREBALL("fireball", Main.instance.getLangHandler().getEntity("fireball"), 12),
	SMALL_FIREBALL("small_fireball", Main.instance.getLangHandler().getEntity("small_fireball"), 13),
	ENDER_PEARL("ender_pearl", Main.instance.getLangHandler().getEntity("ender_pearl"), 14),
	ENDER_SIGNAL("eye_of_ender_signal", Main.instance.getLangHandler().getEntity("eye_of_ender_signal"), 15),
	THROWN_EXP_BOTTLE("xp_bottle", Main.instance.getLangHandler().getEntity("xp_bottle"), 17),
	ITEM_FRAME("item_frame", Main.instance.getLangHandler().getEntity("item_frame"), 18),
	WITHER_SKULL("wither_skull", Main.instance.getLangHandler().getEntity("wither_skull"), 19),
	PRIMED_TNT("tnt", Main.instance.getLangHandler().getEntity("tnt"), 20),
	MINECART_COMMAND("commandblock_minecart", Main.instance.getLangHandler().getEntity("commandblock_minecart"), 40),
	BOAT("boat", Main.instance.getLangHandler().getEntity("boat"), 41),
	MINECART("minecart", Main.instance.getLangHandler().getEntity("minecart"), 42),
	MINECART_CHEST("chest_minecart", Main.instance.getLangHandler().getEntity("chest_minecart"), 43),
	MINECART_FURNACE("chest_minecart", Main.instance.getLangHandler().getEntity("chest_minecart"), 44),
	MINECART_TNT("tnt_minecart", Main.instance.getLangHandler().getEntity("tnt_minecart"), 45),
	MINECART_HOPPER("hopper_minecart", Main.instance.getLangHandler().getEntity("hopper_minecart"), 46),
	MINECART_MOB_SPAWNER("spawner_minecart", Main.instance.getLangHandler().getEntity("spawner_minecart"), 47),
	CREEPER("creeper", Main.instance.getLangHandler().getEntity("creeper"), 50),
	SKELETON("skeleton", Main.instance.getLangHandler().getEntity("skeleton"), 51),
	SPIDER("spider", Main.instance.getLangHandler().getEntity("spider"), 52),
	GIANT("giant", Main.instance.getLangHandler().getEntity("giant"), 53),
	ZOMBIE("zombie", Main.instance.getLangHandler().getEntity("zombie"), 54),
	SLIME("slime", Main.instance.getLangHandler().getEntity("slime"), 55),
	GHAST("ghast", Main.instance.getLangHandler().getEntity("ghast"), 56),
	PIG_ZOMBIE("zombie_pigman", Main.instance.getLangHandler().getEntity("zombie_pigman"), 57),
	ENDERMAN("enderman", Main.instance.getLangHandler().getEntity("enderman"), 58),
	CAVE_SPIDER("cave_spider", Main.instance.getLangHandler().getEntity("cave_spider"), 59),
	SILVERFISH("silverfish", Main.instance.getLangHandler().getEntity("silverfish"), 60),
	BLAZE("blaze", Main.instance.getLangHandler().getEntity("blaze"), 61),
	MAGMA_CUBE("magma_cube", Main.instance.getLangHandler().getEntity("magma_cube"), 62),
	ENDER_DRAGON("ender_dragon", Main.instance.getLangHandler().getEntity("ender_dragon"), 63),
	WITHER("wither", Main.instance.getLangHandler().getEntity("wither"), 64),
	BAT("bat", Main.instance.getLangHandler().getEntity("bat"), 65),
	WITCH("witch", Main.instance.getLangHandler().getEntity("witch"), 66),
	PIG("pig", Main.instance.getLangHandler().getEntity("pig"), 90),
	SHEEP("sheep", Main.instance.getLangHandler().getEntity("sheep"), 91),
	COW("cow", Main.instance.getLangHandler().getEntity("cow"), 92),
	CHICKEN("chicken", Main.instance.getLangHandler().getEntity("chicken"), 93),
	SQUID("squid", Main.instance.getLangHandler().getEntity("squid"), 94),
	WOLF("wolf", Main.instance.getLangHandler().getEntity("wolf"), 95),
	MUSHROOM_COW("mooshroom", Main.instance.getLangHandler().getEntity("mooshroom"), 96),
	SNOWMAN("snowMan", Main.instance.getLangHandler().getEntity("snowMan"), 97),
	OCELOT("ocelot", Main.instance.getLangHandler().getEntity("ocelot"), 98),
	IRON_GOLEM("villager_golem", Main.instance.getLangHandler().getEntity("villager_golem"), 99),
	HORSE("horse", Main.instance.getLangHandler().getEntity("horse"), 100),
	VILLAGER("villager", Main.instance.getLangHandler().getEntity("villager"), 120),
	ENDER_CRYSTAL("ender_crystal", Main.instance.getLangHandler().getEntity("ender_crystal"), 200),
	FIREWORK("fireworks_rocket", Main.instance.getLangHandler().getEntity("fireworks_rocket"), 22),
	GUARDIAN("guardian", Main.instance.getLangHandler().getEntity("guardian"), 68),
	ENDERMITE("endermite", Main.instance.getLangHandler().getEntity("endermite"), 67),
	RABBIT("rabbit", Main.instance.getLangHandler().getEntity("rabbit"), 101),
	SHULKER("shulker", Main.instance.getLangHandler().getEntity("shulker"), 69),
	POLAR_BEAR("polar_bear", Main.instance.getLangHandler().getEntity("polar_bear"), 102),
	VINDICATOR("vindication_illager", Main.instance.getLangHandler().getEntity("vindication_illager"), 36),
	VEX("vex", Main.instance.getLangHandler().getEntity("vex"), 35),
	EVOKER("evocation_illager", Main.instance.getLangHandler().getEntity("evocation_illager"), 34),
	MULE("mule", Main.instance.getLangHandler().getEntity("mule"), 32),
	DONKEY("donkey", Main.instance.getLangHandler().getEntity("donkey"), 31),
	ZOMBIE_HORSE("zombie_horse", Main.instance.getLangHandler().getEntity("zombie_horse"), 29),
	SKELETON_HORSE("skeleton_horse", Main.instance.getLangHandler().getEntity("skeleton_horse"), 28),
	ZOMBIE_VILLAGER("zombie_villager", Main.instance.getLangHandler().getEntity("zombie_villager"), 27),
	HUSK("husk", Main.instance.getLangHandler().getEntity("husk"), 23),
	WITHER_SKELETON("wither_skeleton", Main.instance.getLangHandler().getEntity("wither_skeleton"), 5),
	ELDER_GUARDIAN("elder_guardian", Main.instance.getLangHandler().getEntity("elder_guardian"), 4),
	STRAY("stray", Main.instance.getLangHandler().getEntity("stray"), 6),
	LLAMA("llama", Main.instance.getLangHandler().getEntity("llama"), 103);

	private String name;
	private String text;
	private short typeId;

	SpawnerType(String name, String text, int typeId) {
		this.name = name;
		this.typeId = (short) typeId;
		this.text = text;
	}

	private static final Map<String, SpawnerType> NAME_MAP = new HashMap<>();
	private static final Map<Short, SpawnerType> ID_MAP = new HashMap<>();
	private static final Map<String, String> TEXT_NAME_MAP = new HashMap<>();
	private static final Map<Short, String> TEXT_ID_MAP = new HashMap<>();
	private static final Map<String, String> TEXT_TYPE_MAP = new HashMap<>();

	static {
		try {
			for (SpawnerType type : values()) {
				if (type.name != null) {
					NAME_MAP.put(type.name.toLowerCase(), type);
				}
				if (type.typeId > 0) {
					ID_MAP.put(type.typeId, type);
				}
				if (type.name != null && type.text != null) {
					TEXT_NAME_MAP.put(type.name.toLowerCase(), type.text);
				}
				if (type.text != null) {
					TEXT_ID_MAP.put(type.typeId, type.text);
				}
				if (type.text != null) {
					TEXT_TYPE_MAP.put(type.name().toLowerCase(), type.text);
				}
			}
		} catch (Throwable t) {
			Main.instance.getLogger().severe("SpawnerType static error:");
			Main.instance.getLogger().severe(t.getLocalizedMessage());
			Main.instance.getLogger().severe(Arrays.toString(t.getStackTrace()));
			throw t;
		}
	}

	/**
	 * Get name
	 *
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get text
	 *
	 * @return
	 */
	public String getText() {
		return text;
	}

	/**
	 * Get typeId
	 *
	 * @return
	 */
	public short getTypeId() {
		return typeId;
	}

	/**
	 * Get EntityType
	 *
	 * @return
	 */
	public EntityType getEntityType() {
		return EntityType.valueOf(name());
	}

	/**
	 * Get SpawnerType from EntityType
	 *
	 * @param entityType
	 * @return
	 */
	public static SpawnerType fromEntityType(EntityType entityType) {
		if (entityType == null) {
			return null;
		}

		try {
			return SpawnerType.valueOf(entityType.name());
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get type from clean name
	 *
	 * @param name
	 * @return
	 */
	public static SpawnerType fromCleanName(String name) {
		if (name == null) {
			return null;
		}
		return NAME_MAP.get(name.toLowerCase());
	}

	/**
	 * Get type from name
	 *
	 * @param name
	 * @return
	 */
	public static SpawnerType fromName(String name) {
		if (name == null) {
			return null;
		}
		return NAME_MAP.get(SpawnerFunctions.getSpawnerName(SpawnerFunctions.convertAlias(name), "key").toLowerCase());
	}

	/**
	 * Get type from id/durability
	 *
	 * @param id
	 * @return
	 */
	public static SpawnerType fromId(int id) {
		if (id > Short.MAX_VALUE) {
			return null;
		}
		return ID_MAP.get((short) id);
	}

	/**
	 * Get text from name
	 *
	 * @param arg
	 * @return
	 */
	public static String getTextFromName(String arg) {
		String name = SpawnerFunctions.getSpawnerName(arg, "key");

		if (name == null) {
			return null;
		}
		return SpawnerFunctions.getSpawnerName(TEXT_NAME_MAP.get(SpawnerFunctions.convertAlias(name).toLowerCase()), "value");
	}

	/**
	 * Get unformatted text from name
	 *
	 * @param arg
	 * @return
	 */
	public static String getUnformattedTextFromName(String arg) {
		if (arg == null) {
			return null;
		}
		return SpawnerFunctions.getSpawnerName(arg, "key");
	}

	/**
	 * Get text from id/durability
	 *
	 * @param id
	 * @return
	 */
	public static String getTextFromId(int id) {
		if (id > Short.MAX_VALUE) {
			return null;
		}
		return SpawnerFunctions.getSpawnerName(TEXT_ID_MAP.get((short) id), "value");
	}

	/**
	 * Get text from type
	 *
	 * @param type
	 * @return
	 */
	public static String getTextFromType(SpawnerType type) {
		if (type == null) {
			return null;
		}
		return SpawnerFunctions.getSpawnerName(TEXT_NAME_MAP.get(type.getName().toLowerCase()), "value");
	}

}
