package com.forgewareinc.highvoltage;

public class RepeatingTask implements Runnable{
	
	private final HighVoltage plugin;
	private final CableManager cableManager;
	//The time of the last updates in milliseconds
	//private long lastUpdateCheck;
	
	public RepeatingTask(HighVoltage instance) {
		plugin = instance;
		cableManager = plugin.getCableManager();
	}

	public void run() {
		cableManager.checkAll();
		
		/*
		//if current time > time of last update + updateinterval (1 second = 1000 milliseconds)
		if(System.currentTimeMillis() > lastUpdateCheck + plugin.config().getUpdateInterval() * 1000) {
			do something
			lastUpdateCheck = System.currentTimeMillis();
		}*/
	}
}
