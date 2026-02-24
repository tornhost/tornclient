package com.tornhost.tornclient.mods;

import net.minecraft.client.Minecraft;

public class Mod {
	
	protected Minecraft mc = Minecraft.getMinecraft();
	
	private String name;
	private String description;
	private boolean enabled = false;
	
	public float sliderX = 0.0F;
	
	public boolean isCosmetic = false;
	
	public Mod (String name, String description) {
		this.name = name;
		this.description = description;
		this.enabled = false;
	}
	
	// Toggles the mod on and off
	public void toggle() {
		this.enabled = !this.enabled;
		if (this.enabled) {
			onEnable();
		} else {
			onDisable();
		}
	}
	
	// What happens when turned on
	public void onEnable() {
		// We will register this to our Event System later
	}
	
	// What happens when turned off
	public void onDisable() {
		// We will register this to our Event System later
	}
	
	// What happens every single tick (20 times a second)
	public void onUpdate() {
		// Empty by default. Mods will override this if they need to.
	}
	
	// Getters and Setters
	public String getName() { return name; }
	public String getDescription() { return description; }
	public boolean isEnabled() { return enabled; }
	public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
