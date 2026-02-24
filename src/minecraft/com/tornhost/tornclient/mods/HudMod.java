package com.tornhost.tornclient.mods;

public class HudMod extends Mod {

	public int x, y;
	public int width, height; // Track hitbox size
	
	public HudMod(String name, String description, int defaultX, int defaultY) {
		super(name, description);
		this.x = defaultX;
		this.y = defaultY;
	}
	
	public void draw() {
		// Empty by default.
	}
	
	// A helper method to easily draw a background box for the HUD element
	protected void drawBackground(int width, int height) {
		// Save the width and height so the Editor can read them
		this.width = width;
		this.height = height;
		// Draws a slee, very faint background behind the text/UI
		net.minecraft.client.gui.Gui.drawRect(x,  y,  x + width, y + height, 0x60000000);
	}
}
