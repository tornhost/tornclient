package com.tornhost.tornclient.ui;

import java.io.IOException;
import net.minecraft.client.gui.GuiScreen;
import com.tornhost.tornclient.Client;
import com.tornhost.tornclient.mods.Mod;

public class ClickGUI extends GuiScreen {

	private float animatedYOffset = 250.0F;
	private boolean isClosing = false;
	
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

        this.drawString(this.fontRendererObj, "MODS", startX + 20, startY + 30, 0xFFFFFFFF); // Pure White = Active
        this.drawString(this.fontRendererObj, "COSMETICS", startX + 20, startY + 55, 0xFF64748B); // Slate Gray = Inactive
        this.drawString(this.fontRendererObj, "SETTINGS", startX + 20, startY + 80, 0xFF64748B);
        
        // Branding at the bottom
        this.drawString(this.fontRendererObj, Client.getInstance().name.toUpperCase(), startX + 20, startY + windowHeight - 25, 0xFF2563EB);
        
        int modX = startX + 130; 
        int modY = startY + 25;  
        
        for (Mod mod : Client.getInstance().modManager.getMods()) {
            
            // Card Background: Slightly elevated dark gray
            drawRect(modX, modY, modX + 290, modY + 35, 0xFF1B222B); 
            
            // Mod Name vertically centered
            this.drawString(this.fontRendererObj, mod.getName(), modX + 15, modY + 13, 0xFFE2E8F0);
            
            // The background of the switch (Blue if ON, Dark Gray if OFF)
            int switchColor = mod.isEnabled() ? 0xFF2563EB : 0xFF334155; 
            drawRect(modX + 245, modY + 10, modX + 275, modY + 25, switchColor);
            
            // If ON, we want to slide it 15 pixels to the right. If OFF, it stays at 0.
            float targetX = mod.isEnabled() ? 15.0F : 0.0F;
            
            // Create fast start, smooth decel
            mod.sliderX += (targetX - mod.sliderX) * 0.2F;
            
            // Start at base OFF position
            float currentNubX = (modX + 247) + mod.sliderX;
            drawRect((int)currentNubX, modY + 12, (int)currentNubX + 11, modY + 23, 0xFFFFFFFF);
            
            modY += 45; // Increased vertical padding between cards
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
            
            int modX = startX + 130;
            int modY = startY + 25;
            
            for (Mod mod : Client.getInstance().modManager.getMods()) {
                
                // Updated Hitbox Math to perfectly wrap around the new toggle switch
                if (mouseX >= modX + 245 && mouseX <= modX + 275 && mouseY >= modY + 10 && mouseY <= modY + 25) {
                    mod.toggle();
                    // Using the standard GUI click sound
                    this.mc.thePlayer.playSound("gui.button.press", 1.0F, 1.0F); 
                }
                modY += 45; // Must match the padding in drawScreen
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