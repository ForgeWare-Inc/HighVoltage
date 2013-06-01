/*File systems:
 * Cables.cfg:
 * World;X;Y;Z;Owner(Playername)
 */


package com.forgewareinc.highvoltage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Location;

public class FileManager {
	
	private final HighVoltage plugin;
	private File cableFile;
	
	private final String separator = ";";
	
	public FileManager(HighVoltage instance){
		plugin = instance;
		
		cableFile = new File(plugin.getDataFolder() + File.separator + "Cables.cfg");
	}
	
	public void loadAllFiles() {
		//Create the directory
		plugin.getDataFolder().mkdirs();
		
		//Create the files
		loadFile(cableFile);
	}
	
	//Creates a file
	public void loadFile(File file) {
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				plugin.printWarning("Cannot create file " + file.getPath() + File.separator + file.getName());
			}
		}
	}
	
	private String[] getAllLines(File file) {
		FileReader fileReader;
		BufferedReader bufferedReader;
		
		try {
			fileReader = new FileReader(file);
		} catch (FileNotFoundException e) {
			plugin.printWarning("Couldn't find file " + file.getName());
			e.printStackTrace();
			return null;
		}
		
		bufferedReader = new BufferedReader(fileReader);
		
		String line;
		ArrayList<String> lines = new ArrayList<String>();
			
		try {
			while((line = bufferedReader.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			plugin.printWarning(plugin.name + "Unable to get lines from " + file.getName() + ", IOException on reading.");
			e.printStackTrace();
		}
		
		String[] output = new String[lines.size()];
		
		for(int x = 0; x < lines.size(); x++) {
			output[x] = lines.get(x);
		}
		return output;
	}
	
	public ArrayList<Cable> getCableData() {
		String[] lines = getAllLines(cableFile);
		ArrayList<Cable> cableList = new ArrayList<Cable>();
		
		for(String line : lines) {
			String[] split = line.split(separator);
			cableList.add(new Cable(plugin.getServer().getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), split[4]));
		}
		
		plugin.printMessage("Loaded " + cableList.size() + " cables from file.");
		
		return cableList;
	}
	
	public void saveCableData(ArrayList<Cable> cables) {
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(cableFile);
		} catch (IOException e) {
			plugin.printWarning("Unable to create file writer to save cable data, IOException.");
			e.printStackTrace();
			return;
		}
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		
		for(Cable cable : cables) {
			try {
				Location location = cable.getLocation();
				String locationString = location.getWorld().getName() + separator + location.getBlockX() + separator + location.getBlockY() + separator + location.getBlockZ();
				
				bufferedWriter.write(locationString + separator + cable.getOwner());			
				bufferedWriter.newLine();
			} catch (IOException e) {
				plugin.printWarning("Unable to save cable data, IOException on writing.");
				e.printStackTrace();
			}
		}
		
		//Close the stream
		try {
			bufferedWriter.close();
		} catch (IOException e) {
			plugin.printWarning("Unable to close file writer on saving cable data, IOException.");
			e.printStackTrace();
		}
		
		plugin.printMessage("Saved " + cables.size() + " cables to file.");
	}
}