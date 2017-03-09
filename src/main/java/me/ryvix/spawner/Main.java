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

import me.ryvix.spawner.api.Config;
import me.ryvix.spawner.api.Language;
import me.ryvix.spawner.api.NMS;
import me.ryvix.spawner.metrics.Metrics;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {

	public static Main instance;

	private static String version;

	private static Configuration config;

	private NMS nmsHandler;
	private Config configHandler;
	private Language langHandler;

	@Override
	public void onEnable() {
		instance = this;

		// Get full package string of CraftServer.
		// org.bukkit.craftbukkit.version
		String packageName = instance.getServer().getClass().getPackage().getName();

		// Get the last element of the package
		setVersion(packageName.substring(packageName.lastIndexOf('.') + 1));

		try {
			// NMS
			final Class<?> nmsClazz = Class.forName("me.ryvix.spawner.nms." + getVersion() + ".NMSHandler");
			// Check if we have a NMSHandler class at that location.
			// Make sure it actually implements NMS
			if (NMS.class.isAssignableFrom(nmsClazz)) {
				// Set our handler
				instance.setNmsHandler((NMS) nmsClazz.getConstructor().newInstance());
			}

			// Config
			final Class<?> configClazz = Class.forName("me.ryvix.spawner.config." + getVersion() + ".ConfigHandler");
			// Check if we have a ConfigHandler class at that location.
			// Make sure it actually implements Config functions
			if (Config.class.isAssignableFrom(configClazz)) {
				// Set our handler
				instance.setConfigHandler((Config) configClazz.getConstructor().newInstance());
			}

			// Language
			final Class<?> langClazz = Class.forName("me.ryvix.spawner.language." + getVersion() + ".LangHandler");
			// Check if we have a Language class at that location.
			// Make sure it actually implements Language functions
			if (Language.class.isAssignableFrom(langClazz)) {
				// Set our handler
				instance.setLangHandler((Language) langClazz.getConstructor().newInstance());
			}

		} catch (final Exception e) {
			e.printStackTrace();
			instance.getLogger().severe("Could not find support for " + getVersion());
			instance.getLogger().info("Check for updates at " + instance.getDescription().getWebsite());
			instance.setEnabled(false);
			return;
		}
		instance.getLogger().info("Loading support for " + getVersion());

		// metrics
		// https://bstats.org/
		Metrics metrics = new Metrics(this);

		// load entity map
		loadEntityMap();

		// load files
		getConfigHandler().loadFiles();

		// register events
		getServer().getPluginManager().registerEvents(new SpawnerEvents(), this);

		// spawner
		getCommand("spawner").setExecutor(new SpawnerCommands());
	}

	@Override
	public void onDisable() {
		config = null;
		nmsHandler = null;
		configHandler = null;
		langHandler = null;
	}

	/**
	 * Reload Spawner's files from disk.
	 */
	public void reloadFiles() {
		reloadConfig();
		getConfigHandler().loadFiles();
	}

	/**
	 * Load entity map.
	 *
	 * @link https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/browse/src/main/java/org/bukkit/entity/EntityType.java
	 */
	private void loadEntityMap() {
		EntityMap.addMap("Item", "item");
		EntityMap.addMap("XPOrb", "xp_orb");
		EntityMap.addMap("area_effect_cloud", "area_effect_cloud");
		EntityMap.addMap("elder_guardian", "elder_guardian");
		EntityMap.addMap("wither_skeleton", "wither_skeleton");
		EntityMap.addMap("stray", "stray");
		EntityMap.addMap("egg", "egg");
		EntityMap.addMap("LeashKnot", "leash_knot");
		EntityMap.addMap("Painting", "painting");
		EntityMap.addMap("Arrow", "arrow");
		EntityMap.addMap("Snowball", "snowball");
		EntityMap.addMap("Fireball", "fireball");
		EntityMap.addMap("SmallFireball", "small_fireball");
		EntityMap.addMap("ThrownEnderpearl", "ender_pearl");
		EntityMap.addMap("EyeOfEnderSignal", "eye_of_ender_signal");
		EntityMap.addMap("potion", "potion");
		EntityMap.addMap("ThrownExpBottle", "xp_bottle");
		EntityMap.addMap("ItemFrame", "item_frame");
		EntityMap.addMap("WitherSkull", "wither_skull");
		EntityMap.addMap("PrimedTnt", "tnt");
		EntityMap.addMap("FallingSand", "falling_block");
		EntityMap.addMap("FireworksRocketEntity", "fireworks_rocket");
		EntityMap.addMap("husk", "husk");
		EntityMap.addMap("SpectralArrow", "spectral_arrow");
		EntityMap.addMap("ShulkerBullet", "shulker_bullet");
		EntityMap.addMap("DragonFireball", "dragon_fireball");
		EntityMap.addMap("zombie_villager", "zombie_villager");
		EntityMap.addMap("skeleton_horse", "skeleton_horse");
		EntityMap.addMap("zombie_horse", "zombie_horse");
		EntityMap.addMap("ArmorStand", "armor_stand");
		EntityMap.addMap("donkey", "donkey");
		EntityMap.addMap("mule", "mule");
		EntityMap.addMap("evocation_fangs", "evocation_fangs");
		EntityMap.addMap("evocation_illager", "evocation_illager");
		EntityMap.addMap("vex", "vex");
		EntityMap.addMap("vindication_illager", "vindication_illager");
		EntityMap.addMap("MinecartCommandBlock", "commandblock_minecart");
		EntityMap.addMap("Boat", "boat");
		EntityMap.addMap("MinecartRideable", "minecart");
		EntityMap.addMap("MinecartChest", "chest_minecart");
		EntityMap.addMap("MinecartFurnace", "chest_minecart");
		EntityMap.addMap("MinecartTNT", "tnt_minecart");
		EntityMap.addMap("MinecartHopper", "hopper_minecart");
		EntityMap.addMap("MinecartMobSpawner", "spawner_minecart");
		EntityMap.addMap("Creeper", "creeper");
		EntityMap.addMap("Skeleton", "skeleton");
		EntityMap.addMap("Spider", "spider");
		EntityMap.addMap("Giant", "giant");
		EntityMap.addMap("Zombie", "zombie");
		EntityMap.addMap("Slime", "slime");
		EntityMap.addMap("Ghast", "ghast");
		EntityMap.addMap("PigZombie", "zombie_pigman");
		EntityMap.addMap("Enderman", "enderman");
		EntityMap.addMap("CaveSpider", "cave_spider");
		EntityMap.addMap("Silverfish", "silverfish");
		EntityMap.addMap("Blaze", "blaze");
		EntityMap.addMap("LavaSlime", "magma_cube");
		EntityMap.addMap("EnderDragon", "ender_dragon");
		EntityMap.addMap("WitherBoss", "wither");
		EntityMap.addMap("Bat", "bat");
		EntityMap.addMap("Witch", "witch");
		EntityMap.addMap("Endermite", "endermite");
		EntityMap.addMap("Guardian", "guardian");
		EntityMap.addMap("Shulker", "shulker");
		EntityMap.addMap("Pig", "pig");
		EntityMap.addMap("Sheep", "sheep");
		EntityMap.addMap("Cow", "cow");
		EntityMap.addMap("Chicken", "chicken");
		EntityMap.addMap("Squid", "squid");
		EntityMap.addMap("Wolf", "wolf");
		EntityMap.addMap("MushroomCow", "mooshroom");
		EntityMap.addMap("SnowMan", "snowman");
		EntityMap.addMap("Ozelot", "ocelot");
		EntityMap.addMap("VillagerGolem", "villager_golem");
		EntityMap.addMap("EntityHorse", "horse");
		EntityMap.addMap("Rabbit", "rabbit");
		EntityMap.addMap("PolarBear", "polar_bear");
		EntityMap.addMap("llama", "llama");
		EntityMap.addMap("llama_spit", "llama_spit");
		EntityMap.addMap("Villager", "villager");
		EntityMap.addMap("EnderCrystal", "ender_crystal");
		EntityMap.addMap("TippedArrow", "TippedArrow");
	}

	public static Configuration getSpawnerConfig() {
		return config;
	}

	public static void setSpawnerConfig(Configuration config) {
		Main.config = config;
	}

	public Config getConfigHandler() {
		return configHandler;
	}

	public void setConfigHandler(Config configHandler) {
		this.configHandler = configHandler;
	}

	public Language getLangHandler() {
		return langHandler;
	}

	public void setLangHandler(Language langHandler) {
		this.langHandler = langHandler;
	}

	public NMS getNmsHandler() {
		return nmsHandler;
	}

	public void setNmsHandler(NMS nmsHandler) {
		this.nmsHandler = nmsHandler;
	}

	public static File spawnerGetDataFolder() {
		return instance.getDataFolder();
	}

	public void setVersion(String ver) {
		version = ver;
	}

	public String getVersion() {
		return version;
	}
}
