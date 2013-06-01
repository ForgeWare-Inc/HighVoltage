package com.forgewareinc.highvoltage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class Configuration {

	private static HighVoltage plugin;
	private static YamlConfiguration config;
	private File configFile;
	
	private ConfigurationSection mainSection;
	
	//Options
	
	private boolean permissions;	
	
	private int updateInterval;
	private boolean debug;
	private boolean strikePlayers;
	private boolean strikeHostileMobs;
	private boolean strikeFriendlyMobs;
	private int damage;
	private boolean realLightning;
	private boolean rightClick;
	private boolean buildMessages;
	private String configVersion;
	
	public Configuration (HighVoltage instance) {
		plugin = instance;
		configFile = new File(plugin.getDataFolder() + File.separator + "Configuration.yml");
		config = new YamlConfiguration();
	}
	
	//Check the config version and return if it is correct
	public boolean checkVersion() {
		//If the config has an older version than the plugin, print a warning
        if(!configVersion.matches(plugin.getPdf().getVersion())) {
        	plugin.printWarning("The config version does not matches the plugin version!");
        	plugin.printWarning("If erros occur back it up and generate a new one.");
        	return false;
        }
        return true;
	}
	
	public void loadConfig() {
		if(!configFile.exists()) createNewConfig();
		//Loading config
		try {
			config.load(configFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
		//Loading Sections
		mainSection = config.getConfigurationSection("Config");
		if(mainSection == null) mainSection = config.createSection("Config");

		//Loading options
		permissions = getBooleanParm(mainSection, "use-permissions", true);        
        updateInterval = getIntParm(mainSection, "update-interval", 4);
        debug = getBooleanParm(mainSection, "debug-mode", false);
        strikePlayers = getBooleanParm(mainSection, "strike-players", true);
        strikeHostileMobs = getBooleanParm(mainSection, "strike-hostile-mobs", true);
        strikeFriendlyMobs = getBooleanParm(mainSection, "strike-friendly-mobs", true);
        damage = getIntParm(mainSection, "damage", 7);
        realLightning = getBooleanParm(mainSection, "use-real-lightning", false);
        rightClick = getBooleanParm(mainSection, "enable-right-click", true);
        buildMessages = getBooleanParm(mainSection, "send-build-messages", true);
        configVersion = getStringParm(mainSection, "config-version", plugin.getPdf().getVersion());
        
        plugin.printMessage("Configuration loaded from file.");
		
		saveConfig();	
	}

	public YamlConfiguration getConfig() {
		return config;
	}
	
	public void createNewConfig() {
		plugin.printMessage("Creating new configuration.");
		//Creating directories
		if(!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
		//Creating file
		if(!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		config = new YamlConfiguration();
		
		//Creating header
		config.options().header("Config:\n" +
								"use-permissions: If true, Bukkit permissions will be used.\n" +
								"update-interval: How often the plugin should check and strike entities, in server ticks.\n" +
								"debug-mode: In debug mode the plugin will print messages for nearly every event to the console\n" +
								"strike-players: Activate cables for players.\n" +
								"strike-hostile-mobs/strike-firendly-mobs: Same as above for mobs\n" +
								"damage: Damage dealt per lightning.\n" +
								"use-real-lightning: Activate real lightnings instead of effects, deals additional damage and impacts the world.\n" +
								"enable-right-click: If true, you can right click cables to see their owner.\n" +
								"send-build-messages: Enable messages for every cable build and remove.\n" +
								"config-version: The version of the config, for internal use, so DON'T touch it!\n");
		
		//Creating sections
		mainSection = config.createSection("Config");
		
		saveConfig();
	}
	
	public void saveConfig() {
		try {
			config.save(configFile);
			plugin.printMessage("Configuration saved to file.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void reloadConfig() {
		loadConfig();
	}

	//Setting options in config
	public void setProperty (ConfigurationSection section, String path, Object prop) {
		section.set(path, prop);
	}
		
	
	//Loading options from config
	
	
	public ConfigurationSection getConfigurationSectionParm(ConfigurationSection section, String name) {
		if(!section.isConfigurationSection(name)) section.createSection(name);
		
		return section.getConfigurationSection(name);
	}
	
	
	public Boolean getBooleanParm(ConfigurationSection section, String path, Boolean def) {
		//If the value wasn't set already, create the property
		if (!section.contains(path)) section.set(path, def);
		
		//Return the actual value
		return section.getBoolean(path);
	}
	
	public int getIntParm(ConfigurationSection section, String path, int def) {
		if (!section.contains(path)) section.set(path, def);
		
		return section.getInt(path);
	}

	public double getDoubleParm(ConfigurationSection section, String path, double def) {
		if (!section.contains(path)) section.set(path, def);
		
		return section.getDouble(path);
	}

	public String getStringParm(ConfigurationSection section, String path, String def) {
		if (!section.contains(path)) section.set(path, def);
		
		return section.getString(path);
	}
	
	
	//Getters for ConfigurationSections
	
	public ConfigurationSection getMainSection() {
		return mainSection;
	}
	
	//Getters and Setter

	public boolean isPermissions() {
		return permissions;
	}

	public void setPermissions(boolean permissions) {
		this.permissions = permissions;
		mainSection.set("use-permissions", permissions);
	}

	public String getConfigVersion() {
		return configVersion;
	}

	public void setConfigVersion(String configVersion) {
		this.configVersion = configVersion;
		mainSection.set("config-version", configVersion);
	}

	public int getUpdateInterval() {
		return updateInterval;
	}

	public void setUpdateInterval(int updateInterval) {
		this.updateInterval = updateInterval;
		mainSection.set("update-interval", updateInterval);
	}

	public boolean isStrikePlayers() {
		return strikePlayers;
	}

	public void setStrikePlayers(boolean strikePlayers) {
		this.strikePlayers = strikePlayers;
		mainSection.set("strike-players", strikePlayers);
	}

	public boolean isStrikeHostileMobs() {
		return strikeHostileMobs;
	}

	public void setStrikeHostileMobs(boolean strikeHostileMobs) {
		this.strikeHostileMobs = strikeHostileMobs;
		mainSection.set("strike-hostile-mobs", strikeHostileMobs);
	}

	public boolean isStrikeFriendlyMobs() {
		return strikeFriendlyMobs;
	}

	public void setStrikeFriendlyMobs(boolean strikeFriendlyMobs) {
		this.strikeFriendlyMobs = strikeFriendlyMobs;
		mainSection.set("strike-friendly-mobs", strikeFriendlyMobs);
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
		mainSection.set("damage", damage);
	}

	public boolean isRealLightning() {
		return realLightning;
	}

	public void setRealLightning(boolean realLightning) {
		this.realLightning = realLightning;
		mainSection.set("use-real-lightning", realLightning);
	}

	public boolean isRightClick() {
		return rightClick;
	}

	public void setRightClick(boolean rightClick) {
		this.rightClick = rightClick;
		mainSection.set("enable-right-click", rightClick);
	}

	public boolean isBuildMessages() {
		return buildMessages;
	}

	public void setBuildMessages(boolean buildMessages) {
		this.buildMessages = buildMessages;
		mainSection.set("send-build-messages", buildMessages);
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
		mainSection.set("debug-mode", debug);
	}
}