/**
 * Spawner - Gather mob spawners with silk touch enchanted tools and the ability
 * to change mob types.
 *
 * Copyright (C) 2012-2015 Ryan Rhode - rrhode@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package me.ryvix.spawner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.ryvix.spawner.language.Entities;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

/**
 * Originally from Bukkit EntityType enum.
 * Using it here for future use to translate durability to entity names.
 */
public enum SpawnerType {

	EXPERIENCE_ORB("XPOrb", Main.language.getEntity(Entities.XPOrb), 2),
	LEASH_HITCH("LeashKnot", Main.language.getEntity(Entities.LeashKnot), 8),
	PAINTING("Painting", Main.language.getEntity(Entities.Painting), 9),
	ARROW("Arrow", Main.language.getEntity(Entities.Arrow), 10),
	SNOWBALL("Snowball", Main.language.getEntity(Entities.Snowball), 11),
	FIREBALL("Fireball", Main.language.getEntity(Entities.Fireball), 12),
	SMALL_FIREBALL("SmallFireball", Main.language.getEntity(Entities.SmallFireball), 13),
	ENDER_PEARL("ThrownEnderpearl", Main.language.getEntity(Entities.ThrownEnderpearl), 14),
	ENDER_SIGNAL("EyeOfEnderSignal", Main.language.getEntity(Entities.EyeOfEnderSignal), 15),
	THROWN_EXP_BOTTLE("ThrownExpBottle", Main.language.getEntity(Entities.ThrownExpBottle), 17),
	ITEM_FRAME("ItemFrame", Main.language.getEntity(Entities.ItemFrame), 18),
	WITHER_SKULL("WitherSkull", Main.language.getEntity(Entities.WitherSkull), 19),
	PRIMED_TNT("PrimedTnt", Main.language.getEntity(Entities.PrimedTnt), 20),
	MINECART_COMMAND("MinecartCommandBlock", Main.language.getEntity(Entities.MinecartCommandBlock), 40),
	BOAT("Boat", Main.language.getEntity(Entities.Boat), 41),
	MINECART("MinecartRideable", Main.language.getEntity(Entities.MinecartRideable), 42),
	MINECART_CHEST("MinecartChest", Main.language.getEntity(Entities.MinecartChest), 43),
	MINECART_FURNACE("MinecartFurnace", Main.language.getEntity(Entities.MinecartFurnace), 44),
	MINECART_TNT("MinecartTNT", Main.language.getEntity(Entities.MinecartTNT), 45),
	MINECART_HOPPER("MinecartHopper", Main.language.getEntity(Entities.MinecartHopper), 46),
	MINECART_MOB_SPAWNER("MinecartMobSpawner", Main.language.getEntity(Entities.MinecartMobSpawner), 47),
	CREEPER("Creeper", Main.language.getEntity(Entities.Creeper), 50),
	SKELETON("Skeleton", Main.language.getEntity(Entities.Skeleton), 51),
	SPIDER("Spider", Main.language.getEntity(Entities.Spider), 52),
	GIANT("Giant", Main.language.getEntity(Entities.Giant), 53),
	ZOMBIE("Zombie", Main.language.getEntity(Entities.Zombie), 54),
	SLIME("Slime", Main.language.getEntity(Entities.Slime), 55),
	GHAST("Ghast", Main.language.getEntity(Entities.Ghast), 56),
	PIG_ZOMBIE("PigZombie", Main.language.getEntity(Entities.PigZombie), 57),
	ENDERMAN("Enderman", Main.language.getEntity(Entities.Enderman), 58),
	CAVE_SPIDER("CaveSpider", Main.language.getEntity(Entities.CaveSpider), 59),
	SILVERFISH("Silverfish", Main.language.getEntity(Entities.Silverfish), 60),
	BLAZE("Blaze", Main.language.getEntity(Entities.Blaze), 61),
	MAGMA_CUBE("LavaSlime", Main.language.getEntity(Entities.LavaSlime), 62),
	ENDER_DRAGON("EnderDragon", Main.language.getEntity(Entities.EnderDragon), 63),
	WITHER("WitherBoss", Main.language.getEntity(Entities.WitherBoss), 64),
	BAT("Bat", Main.language.getEntity(Entities.Bat), 65),
	WITCH("Witch", Main.language.getEntity(Entities.Witch), 66),
	PIG("Pig", Main.language.getEntity(Entities.Pig), 90),
	SHEEP("Sheep", Main.language.getEntity(Entities.Sheep), 91),
	COW("Cow", Main.language.getEntity(Entities.Cow), 92),
	CHICKEN("Chicken", Main.language.getEntity(Entities.Chicken), 93),
	SQUID("Squid", Main.language.getEntity(Entities.Squid), 94),
	WOLF("Wolf", Main.language.getEntity(Entities.Wolf), 95),
	MUSHROOM_COW("MushroomCow", Main.language.getEntity(Entities.MushroomCow), 96),
	SNOWMAN("SnowMan", Main.language.getEntity(Entities.SnowMan), 97),
	OCELOT("Ozelot", Main.language.getEntity(Entities.Ozelot), 98),
	IRON_GOLEM("VillagerGolem", Main.language.getEntity(Entities.VillagerGolem), 99),
	HORSE("EntityHorse", Main.language.getEntity(Entities.EntityHorse), 100),
	VILLAGER("Villager", Main.language.getEntity(Entities.Villager), 120),
	ENDER_CRYSTAL("EnderCrystal", Main.language.getEntity(Entities.EnderCrystal), 200),
	FIREWORK("FireworksRocketEntity", Main.language.getEntity(Entities.FireworksRocketEntity), 22),
	GUARDIAN("Guardian", Main.language.getEntity(Entities.Guardian), 68),
	ENDERMITE("Endermite", Main.language.getEntity(Entities.Endermite), 67),
	RABBIT("Rabbit", Main.language.getEntity(Entities.Rabbit), 101);

	private String name;
	private String text;
	private short typeId;

	private SpawnerType(String name, String text, int typeId) {
		this.name = name;
		this.typeId = (short) typeId;
		this.text = text;
	}

	private static final Map<String, SpawnerType> NAME_MAP = new HashMap<String, SpawnerType>();
	private static final Map<Short, SpawnerType> ID_MAP = new HashMap<Short, SpawnerType>();
	private static final Map<String, String> TEXT_NAME_MAP = new HashMap<String, String>();
	private static final Map<Short, String> TEXT_ID_MAP = new HashMap<Short, String>();
	private static final Map<String, String> TEXT_TYPE_MAP = new HashMap<String, String>();

	static {
		for (SpawnerType type : values()) {
			if (type.name != null) {
				NAME_MAP.put(type.name.toLowerCase(), type);
			}
			if (type.typeId > 0) {
				ID_MAP.put(type.typeId, type);
			}
			if (type.text != null) {
				TEXT_NAME_MAP.put(type.name.toLowerCase(), type.text);
			}
			if (type.text != null) {
				TEXT_ID_MAP.put(type.typeId, type.text);
			}
			if (type.text != null) {
				TEXT_TYPE_MAP.put(type.name().toLowerCase(), type.text);
			}
		}
	}

	/**
	 * Get name
	 * @return 
	 */
    public String getName() {
        return name;
    }

	/**
	 * Get text
	 * @return 
	 */
    public String getText() {
        return text;
    }

	/**
	 * Get typeId
	 * @return 
	 */
    public short getTypeId() {
        return typeId;
    }

	/**
	 * Get EntityType
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
		return SpawnerType.valueOf(entityType.name());
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
		return NAME_MAP.get(convertAlias(name).toLowerCase());
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
	 * @param name
	 * @return
	 */
	public static String getTextFromName(String name) {
		if (name == null) {
			return null;
		}
		return TEXT_NAME_MAP.get(convertAlias(name).toLowerCase());
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
		return TEXT_ID_MAP.get((short) id);
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
		return TEXT_NAME_MAP.get(type.getName().toLowerCase());
	}

	/**
	 * Check config for alias and return the entity type.
	 * Returns input string if no aliases are found.
	 * 
	 * @param entity
	 * @return String
	 */
	public static String convertAlias(String entity) {

		ConfigurationSection aliases = Main.instance.config.getConfigurationSection("aliases");
		for(String key : aliases.getKeys(false)) {
			List<String> aliasList = aliases.getStringList(key);
			for (String alias : aliasList) {
				if (alias.equalsIgnoreCase(entity)) {
					return key;
				}
			}
		}

		return entity;
	}

	public static boolean isValidEntity(String entity) {

		// allow only valid entity types matched against aliases
		List<String> validEntities = Main.instance.config.getStringList("valid_entities");

		// check valid_entities first
		for (String entry : validEntities) {
			if (entry.equalsIgnoreCase(entity)) {
				return true;
			}
		}

		// no valid_entities found so check for aliases
		String aliasCheck = convertAlias(entity);
		return !aliasCheck.isEmpty();
	}

}
