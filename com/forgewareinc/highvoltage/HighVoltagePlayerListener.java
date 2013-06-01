package com.forgewareinc.highvoltage;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class HighVoltagePlayerListener implements Listener{
	
	private final HighVoltage plugin;
	
	private Configuration config;
	private CableManager cableManager;
	
	//Saves all players which are in building mode
	private ArrayList<Player> builderList = new ArrayList<Player>();
	
	public HighVoltagePlayerListener(HighVoltage instance){
		plugin = instance;
	}
	
	public void setupLinkings() {
		config = plugin.config();
		cableManager = plugin.getCableManager();
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split){
		if(!label.equalsIgnoreCase("voltage")) return false;
		if(split.length < 1) {
			sender.sendMessage(plugin.getPluginName() + "To show help use /voltage help");
			return true;
		}
		
		if(split[0].equalsIgnoreCase("build")) {
			if(checkPermissions(sender, "cables.builder.build", true)) {
				if(sender instanceof Player) {
					Player player = (Player) sender;
					if(builderList.contains(player)) {
						builderList.remove(player);
						player.sendMessage(plugin.getPluginName() + "Build mode disabled.");
					}else {
						builderList.add(player);
						player.sendMessage(plugin.getPluginName() + "Build mode enabled.");
						
					}
				}else sender.sendMessage(plugin.getName(false) + "Only players can use this command.");
			}
			return true;
		}
		
		if(split[0].equalsIgnoreCase("debug")) {
			if(checkPermissions(sender, "admin.debug", true)) {
				config.setDebug(!config.isDebug());
				if(config.isDebug()) sender.sendMessage(plugin.getPluginName() + "Debug mode enabled.");
				else sender.sendMessage(plugin.getPluginName() + "Debug mode disabled.");
			}
			return true;
		}
		
		if(split[0].equalsIgnoreCase("reloadconfig")) {
			if(checkPermissions(sender, "admin.accesconfig", true)) {
				config.reloadConfig();
				sender.sendMessage(plugin.getPluginName() + "Config reloaded from file.");
			}
		}
		
		if(split[0].equalsIgnoreCase("saveconfig")) {
			if(checkPermissions(sender, "admin.accesconfig", true)) {
				config.saveConfig();
				sender.sendMessage(plugin.getPluginName() + "Config saved to file.");
			}
		}
		
		if(split[0].equalsIgnoreCase("help")) {
			sender.sendMessage(plugin.getPluginName() + "Commands: /voltage +");
			sender.sendMessage(ChatColor.DARK_GRAY + "build - Toggle cable building mode.");
			sender.sendMessage(ChatColor.DARK_GRAY + "adminhelp - Show admin commands.");
			sender.sendMessage(ChatColor.DARK_GRAY + "help - Show this help.");
			
			return true;
		}
		
		if(split[0].equalsIgnoreCase("adminhelp")) {
			if(checkPermissions(sender, "admin", true)) {
				sender.sendMessage(plugin.getPluginName() + "Admin commands: /voltage +");
				sender.sendMessage(ChatColor.DARK_GRAY + "debug - Toggle debug mode.");
				sender.sendMessage(ChatColor.DARK_GRAY + "reloadconfig - Reload the config from file.");
				sender.sendMessage(ChatColor.DARK_GRAY + "saveconfig - Save the config to file.");
			}
			return true;
		}
		
		return false;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		//If a player right clicked redstone, send him details about the cable		
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK && config.isRightClick()) {
			if(event.getClickedBlock().getType() != Material.REDSTONE_WIRE) || (event.getClickedBlock().getType() != Material.IRON_FENCE
					) return;
			Player player = event.getPlayer();

			if(checkPermissions(player, "cables.info", true)) {
				Cable cable = cableManager.getCable(event.getClickedBlock());
				
				if(cable != null) {				
						player.sendMessage(plugin.getPluginName() + "This cable is owned by " + ChatColor.GOLD + cable.getOwner() + ChatColor.RED + ".");
				}else player.sendMessage(plugin.getPluginName() + "This is not a cable.");
			}
		}
	}

	public boolean hasBuildingMode(Player player) {
		return builderList.contains(player);
	}
	
	public boolean checkPermissions(CommandSender sender, String permission, boolean sendMessage) {
		if(config.isPermissions()) {
			if(sender.hasPermission("highvoltage." + permission)) {
				plugin.printDebug("The command sender " + sender.getName() + " has the permission highvoltage." + permission);
				return true;
			}
			else {
				plugin.printDebug("The command sender " + sender.getName() + " does not have the permission highvoltage." + permission);
				if(sendMessage) sender.sendMessage(plugin.getPluginName() + "You don't have the permissions to do this.");
				return false;
			}
		}else plugin.printDebug("Permissions are disabled, accepting the permission " + permission + " for " + sender.getName());
		
		return true;
	}
}		