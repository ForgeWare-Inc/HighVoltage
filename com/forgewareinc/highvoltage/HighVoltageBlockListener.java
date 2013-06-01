package com.forgewareinc.highvoltage;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;


public class HighVoltageBlockListener implements Listener {
	
	private final HighVoltage plugin;
	
	private HighVoltagePlayerListener playerListener;
	private Configuration config;
	
	public HighVoltageBlockListener(HighVoltage instance) {
		plugin = instance;
	}
	
	public void setupLinkings() {
		playerListener = plugin.getPlayerListener();
		config = plugin.config();
	}

	//This is to check all placed redstone
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent event) {
		if(event.getBlock().getType() == Material.REDSTONE_WIRE) || (event.getBlock().getType() == Material.IRON_FENCE) {
			if(plugin.getPlayerListener().hasBuildingMode(event.getPlayer())) {
				plugin.getCableManager().addCable(event.getPlayer(), event.getBlock().getLocation());
				if(config.isBuildMessages()) event.getPlayer().sendMessage(plugin.getPluginName() + "Cable created.");
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event) {
		Cable cable = plugin.getCableManager().getCable(event.getBlock());
		
		if(cable != null) {
			Player player = event.getPlayer();
			if(!cable.getOwner().equalsIgnoreCase(player.getName())) {
				if(!playerListener.checkPermissions(player, "cables.removeall", false)) {
					plugin.printDebug("Player " + player.getName() + " tried to destroy a cable which does not belong to him, striking him.");
					plugin.getCableManager().strikeEntity(player);
					event.setCancelled(true);
				}
			}else {
				if(playerListener.checkPermissions(player, "cables.builder.removeown", true)) {
					plugin.printDebug(player.getName() + " destroyed a cable at " + cable.getLocation().toString() + " , removing it.");
					plugin.getCableManager().deleteCable(cable);
					if(config.isBuildMessages()) player.sendMessage(plugin.getPluginName() + "Cable from " + ChatColor.GOLD + cable.getOwner() + ChatColor.RED + " removed.");
				}
			}
		}
	}
}
