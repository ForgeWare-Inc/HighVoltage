package com.forgewareinc.highvoltage;

import java.util.ArrayList;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Wolf;

public class CableManager {
	
	private final HighVoltage plugin;
	
	private Configuration config;
	private HighVoltagePlayerListener playerListener;
	
	private ArrayList<Cable> cableList = new ArrayList<Cable>();
	
	public CableManager(HighVoltage instance) {
		plugin = instance;
	}
	
	public void setupLinkings() {
		config = plugin.config();
		playerListener= plugin.getPlayerListener();
	}
	
	//Check all entities
	public void checkAll() {
		for(World world: plugin.getServer().getWorlds()) {
			for(Chunk chunk : world.getLoadedChunks()) {
				for(Entity entity : chunk.getEntities()) {
					if(!(entity instanceof LivingEntity)) continue;
					
					Cable cable = getCable(entity.getLocation());
					if(cable == null) continue;
					if(!cable.isPowered()) continue;
					
					//Handle players
					if(entity instanceof Player) {
						if(config.isStrikePlayers()) {
							Player player = (Player) entity;
							if(!cable.isOwner(player)) {
								if(!playerListener.checkPermissions(player, "cables.safe", false)) strikeEntity(entity);
							}
						}
						continue;
					}
					
					//Handle friendly mobs
					if(entity instanceof Animals || entity instanceof NPC) {
						//If it is an angry wolf it is handled as hostile mob
						if(entity instanceof Wolf && ((Wolf) entity).isAngry()) {
							if(config.isStrikeHostileMobs()) {
								strikeEntity(entity);
								continue;
							}
						}
						//If enabled in the config, strike the mob
						if(config.isStrikeFriendlyMobs()) {
							strikeEntity(entity);
						}
						continue;
					}

					//Handle hosile mobs, slimes and magma cubes
					if(entity instanceof Monster || entity instanceof Slime) {
						if(config.isStrikeHostileMobs()) {
							strikeEntity(entity);
						}
						continue;
					}
				}
			}
		}
	}
	
	public void strikeEntity(Entity entity) {
		if(config.isRealLightning())entity.getLocation().getWorld().strikeLightning(entity.getLocation());
		else entity.getLocation().getWorld().strikeLightningEffect(entity.getLocation());
		if(entity instanceof LivingEntity) {
			((LivingEntity) entity).damage(config.getDamage());
			if(entity instanceof Player) ((Player) entity).sendMessage(plugin.getPluginName() + "Never play with high voltages!");
		}
	}
	
	public void loadCables() {
		cableList = plugin.getFileManager().getCableData();
	}
	
	public void saveCables() {
		plugin.getFileManager().saveCableData(cableList);
	}
	
	public void addCable(Player player, Location location) {
		cableList.add(new Cable(location, player.getName()));
	}

	public void deleteCable(Cable cable) {
		cableList.remove(cable);
	}
	
	public Cable getCable(Location location) {
		return getCable(location.getBlock());
	}
	
	public Cable getCable(Block block) {
		if(block.getType() == Material.REDSTONE_WIRE) || (block.getType() == Material.IRON_FENCE) {
			for(Cable cable : cableList) {
				if(cable.getBlock().equals(block)) return cable;
			}
		}
		return null;
	}
}
