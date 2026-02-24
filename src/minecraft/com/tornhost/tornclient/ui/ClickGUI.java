package com.tornhost.tornclient.ui;

import java.io.IOException;
import net.minecraft.client.gui.GuiScreen;
import com.tornhost.tornclient.Client;
import com.tornhost.tornclient.mods.Mod;

public class ClickGUI extends GuiScreen {

	private float animatedYOffset = 250.0F;
	private boolean isClosing = false;
	
	public enum Tab { MODS, COSMETICS, SETTINGS }
	private Tab currentTab = Tab.MODS;
	
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Darken the game background
        this.drawDefaultBackground();
        
        
        // Made the window larger to give the UI room to breathe
        int windowWidth = 450;
        int windowHeight = 300;
        
        float targetY = isClosing ? (float)this.height : 0.0F;
        animatedYOffset += (targetY - animatedYOffset) * 0.15F; // 0.15 is the Speed. Lower = smoother/slower
        
        int startX = (this.width - windowWidth) / 2;
        int startY = (this.height - windowHeight) / 2 + (int)animatedYOffset;
        
        if (isClosing && startY > this.height) {
        	this.mc.displayGuiScreen(null);
        	return; // Stop drawing immediately
        }
        
        // Main Panel: Deep Navy-Black (Almost charcoal)
        drawRect(startX, startY, startX + windowWidth, startY + windowHeight, 0xFF11151A);
        
        // Sidebar: Absolute Dark Slate (Creates depth)
        drawRect(startX, startY, startX + 110, startY + windowHeight, 0xFF0A0D12);
        
        // Accent Line: Business Blue dividing the sidebar and main panel
        drawRect(startX + 109, startY, startX + 110, startY + windowHeight, 0xFF2563EB);

        int activeColor = 0xFFFFFFFF;
        int inactiveColor = 0xFF64748B;
        
        this.drawString(this.fontRendererObj, "MODS", startX + 20, startY + 30, currentTab == Tab.MODS ? activeColor : inactiveColor);
        this.drawString(this.fontRendererObj, "COSMETICS", startX + 20, startY + 55, currentTab == Tab.COSMETICS ? activeColor : inactiveColor);
        this.drawString(this.fontRendererObj, "SETTINGS", startX + 20, startY + 80, currentTab == Tab.SETTINGS ? activeColor : inactiveColor);
        
        // Branding at the bottom
        this.drawString(this.fontRendererObj, Client.getInstance().name.toUpperCase(), startX + 20, startY + windowHeight - 25, 0xFF2563EB);
        
        int modX = startX + 130; 
        int modY = startY + 25;  
        
        if (currentTab == Tab.MODS) {
            // === DRAW UTILITY MODS ===
            for (Mod mod : Client.getInstance().modManager.getMods()) {
                if (mod.isCosmetic) continue; // Skip cosmetics
                
                drawRect(modX, modY, modX + 290, modY + 35, 0xFF1B222B); 
                this.drawString(this.fontRendererObj, mod.getName(), modX + 15, modY + 13, 0xFFE2E8F0);
                
                int switchColor = mod.isEnabled() ? 0xFF2563EB : 0xFF334155; 
                drawRect(modX + 245, modY + 10, modX + 275, modY + 25, switchColor);
                
                float targetX = mod.isEnabled() ? 15.0F : 0.0F;
                mod.sliderX += (targetX - mod.sliderX) * 0.2F;
                
                float currentNubX = (modX + 247) + mod.sliderX;
                drawRect((int)currentNubX, modY + 12, (int)currentNubX + 11, modY + 23, 0xFFFFFFFF);
                modY += 45; 
            }
        } 
        else if (currentTab == Tab.COSMETICS) {
            // === DRAW CAPE INVENTORY ===
            String myUUID = this.mc.thePlayer.getUniqueID().toString();
            String[] ownedCapes = Client.getInstance().cosmeticManager.getOwnedCapes(myUUID);
            String equippedCape = Client.getInstance().cosmeticManager.getCape(myUUID);
            
            for (String capeName : ownedCapes) {
                if (capeName.equals("none") || capeName.isEmpty()) continue;
                
                // Draw Card
                drawRect(modX, modY, modX + 290, modY + 35, 0xFF1B222B); 
                
                // Clean up the name (e.g., "dev_cape" -> "DEV CAPE")
                String displayName = capeName.toUpperCase().replace("_", " ");
                this.drawString(this.fontRendererObj, displayName, modX + 15, modY + 13, 0xFFE2E8F0);
                
                // Draw "EQUIP" Button
                boolean isEquipped = capeName.equals(equippedCape);
                int btnColor = isEquipped ? 0xFF2563EB : 0xFF334155; // Blue if equipped, gray if not
                drawRect(modX + 210, modY + 8, modX + 280, modY + 27, btnColor);
                
                String btnText = isEquipped ? "EQUIPPED" : "EQUIP";
                int textWidth = this.fontRendererObj.getStringWidth(btnText);
                this.drawString(this.fontRendererObj, btnText, modX + 245 - (textWidth / 2), modY + 13, 0xFFFFFFFF);
                
                modY += 45;
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            
            int windowWidth = 450;
            int windowHeight = 300;
            int startX = (this.width - windowWidth) / 2;
            int startY = (this.height - windowHeight) / 2 + (int)animatedYOffset;
            
            if (mouseX >= startX && mouseX <= startX + 110) {
            	if (mouseY >= startY + 25 && mouseY <= startY + 45) currentTab = Tab.MODS;
                if (mouseY >= startY + 50 && mouseY <= startY + 70) currentTab = Tab.COSMETICS;
                if (mouseY >= startY + 75 && mouseY <= startY + 95) currentTab = Tab.SETTINGS;
                return; // Stop checking other clicks
            }
            
            int modX = startX + 130;
            int modY = startY + 25;
            
            if (currentTab == Tab.MODS) {
                // Mod clicking logic
                for (Mod mod : Client.getInstance().modManager.getMods()) {
                    if (mod.isCosmetic) continue;
                    if (mouseX >= modX + 245 && mouseX <= modX + 275 && mouseY >= modY + 10 && mouseY <= modY + 25) {
                        mod.toggle();
                        this.mc.thePlayer.playSound("gui.button.press", 1.0F, 1.0F); 
                    }
                    modY += 45; 
                }
            } 
            else if (currentTab == Tab.COSMETICS) {
                // Cape equipping logic
                String myUUID = this.mc.thePlayer.getUniqueID().toString();
                String myName = this.mc.getSession().getUsername();
                String[] ownedCapes = Client.getInstance().cosmeticManager.getOwnedCapes(myUUID);
                
                for (String capeName : ownedCapes) {
                    if (capeName.equals("none") || capeName.isEmpty()) continue;
                    
                    // Hitbox for the "EQUIP" button (modX + 210 to 280)
                    if (mouseX >= modX + 210 && mouseX <= modX + 280 && mouseY >= modY + 8 && mouseY <= modY + 27) {
                        
                        // Equip the cape and send it to the database!
                        Client.getInstance().cosmeticManager.setEquippedCape(myUUID, myName, capeName);
                        this.mc.thePlayer.playSound("gui.button.press", 1.0F, 1.0F);
                    }
                    modY += 45;
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false; 
    }
    
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws java.io.IOException {
    	// Keyboard.KEY_ESCAPE is 1, and we also want to check for Right Shift
    	if (keyCode == org.lwjgl.input.Keyboard.KEY_ESCAPE || keyCode == org.lwjgl.input.Keyboard.KEY_RSHIFT) {
    		if (this.animatedYOffset < 10.0F) {
    			// Instead of closing, trigger the slide-out animation!
        		this.isClosing = true;
    		}
    	} else {
    		// If they pressed any other key, let vanilla Minecraft handle it
    		super.keyTyped(typedChar, keyCode);
    	}
    }
}