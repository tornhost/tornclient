package com.tornhost.tornclient.ui;

import java.io.IOException;
import net.minecraft.client.gui.GuiScreen;
import com.tornhost.tornclient.Client;
import com.tornhost.tornclient.mods.Mod;
import com.tornhost.tornclient.mods.HudMod;

public class HUDEditor extends GuiScreen {
	
	// Tracks which mod we are currently holding
	private HudMod draggingMod = null;
	// Tracks exactly where on the mod we clicked
	private int dragX, dragY;
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		// Darken the background
		this.drawDefaultBackground();
		
		// Draw some instructions at the top center
		this.drawCenteredString(this.fontRendererObj, "HUD Editor", this.width / 2, 20, 0xFFFFFF);
		this.drawCenteredString(this.fontRendererObj, "Click and drag to move elements.", this.width / 2, 35, 0xAAAAAA);
		
		if (draggingMod != null) {
			draggingMod.x = mouseX - dragX;
			draggingMod.y = mouseY - dragY;
		}
		
		for (Mod mod : Client.getInstance().modManager.getMods()) {
			if (mod.isEnabled() && mod instanceof HudMod) {
				HudMod hudMod = (HudMod) mod;
				
				// Draw the mod itself
				hudMod.draw();
				
				// Draw a translucent white highlight box over it to show it's editable
				drawRect(hudMod.x - 2, hudMod.y - 2, hudMod.x + hudMod.width + 2, hudMod.y + hudMod.height + 2, 0x44FFFFFF);
			}
		}
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		// Left click
		if (mouseButton == 0) {
			for (Mod mod : Client.getInstance().modManager.getMods()) {
				if (mod.isEnabled() && mod instanceof HudMod) {
					HudMod hudMod = (HudMod) mod;
					
					// Hitbox match: Did they click inside the HUD element?
					if (mouseX >= hudMod.x && mouseX <= hudMod.x + hudMod.width && mouseY >= hudMod.y && mouseY <= hudMod.y + hudMod.height) {
						
						// pick it up!
						draggingMod = hudMod;
						// calculate the offset so it drags smoothly
						dragX = mouseX - hudMod.x;
						dragY = mouseY - hudMod.y;
						
						// Break out of the loop so we only pick up one mod at a time
						break;
					}
				}
			}
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		// When they let go of the mouse, drop the mod!
		if (state == 0) {
			draggingMod = null;
		}
		super.mouseReleased(mouseX, mouseY, state);
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
