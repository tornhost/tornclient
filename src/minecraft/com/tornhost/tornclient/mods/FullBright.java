package com.tornhost.tornclient.mods;

public class FullBright extends Mod {
	
	// Remember old gamma
	private float oldGamma;
	
	public FullBright() {
		super("FullBright", "Removes all darkness from the game.");
		this.setEnabled(true); // Hard code enabled for testing
	}
	
	@Override
	public void onEnable() {
		// Save the current brightness
		this.oldGamma = mc.gameSettings.gammaSetting;
		
		// Crank it to 100
		mc.gameSettings.gammaSetting = 100.0F;
	}
	
	@Override
	public void onDisable() {
		// Restore original brightness
		mc.gameSettings.gammaSetting = this.oldGamma;
	}
}
