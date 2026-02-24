package com.tornhost.tornclient;

import java.util.ArrayList;
import java.util.List;
import com.tornhost.tornclient.mods.Mod;

public class ModManager {
	
	// Master list of all mods
	private List<Mod> mods = new ArrayList<>();
	
	public ModManager() {
		// Register every mod
		mods.add(new com.tornhost.tornclient.mods.ToggleSprint());
		mods.add(new com.tornhost.tornclient.mods.FullBright());
	}
	
	// Returns the full list of mods
	public List<Mod> getMods() {
		return mods;
	}
	
	// A handy tool to find a specific mod by its name
	public Mod getModByName(String name) {
		for (Mod mod : mods) {
			if (mod.getName().equalsIgnoreCase(name)) {
				return mod;
			}
		}
		return null;
	}
	
	// THis is the heartbeat of the client. It fires every tick.
	public void update() {
		for (Mod mod : mods) {
			// Only update if mod is turned on!
			if (mod.isEnabled()) {
				mod.onUpdate();
			}
		}
	}
}
