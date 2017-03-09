package me.ryvix.spawner.api;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

// https://bukkit.org/threads/support-multiple-minecraft-versions-with-abstraction-maven.115810/
// https://github.com/mbax/AbstractionExamplePlugin
public interface Language {
	void setFileName(String fileName);
	void loadText();
	FileConfiguration getConfig();
	String getText(String key, String... vars);
	String getEntity(String entity);
	void saveDefaultConfig();
	void setValues();
	void makeFolder();
	void createConfig();
	void saveConfig();
	void loadConfig();
	void sendMessage(UUID player, String text);
	void sendMessage(CommandSender sender, String text);
	String translateEntity(String inputName, String type);
}
