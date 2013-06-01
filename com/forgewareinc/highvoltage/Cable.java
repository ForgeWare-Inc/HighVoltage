package com.forgewareinc.highvoltage;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Cable {
	
	private Location location;
	private String owner;
	private Block block;
	
	public Cable(Location location, String owner) {
		this.location = location;
		this.owner = owner;
		
		block = location.getBlock();
	}
	
	public Cable(World world, double x, double y, double z, String owner) {		
		this.owner = owner;		
		this.location = new Location(world, x, y, z);
		
		block = location.getBlock();
	}

	public boolean isOwner(Player player) {
		return owner.equalsIgnoreCase(player.getName());
	}
	
	public boolean isPowered() {
		//If the byte data is higher than 0, the redstone is on
		return block.getData() > 0;
	}

	public Location getLocation() {
		return location;
	}

	public String getOwner() {
		return owner;
	}
	
	public Block getBlock() {
		return block;
	}
}
