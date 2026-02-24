package com.tornhost.tornclient.mods;

public class ToggleSprint extends Mod {

	public ToggleSprint() {
		super("ToggleSprint", "Keeps you sprinting automatically");
	}
	
	@Override
	public void onUpdate() {
		if (this.isEnabled()) {
			if (mc.thePlayer.movementInput.moveForward >= 0.8F && !mc.thePlayer.isSneaking() && !mc.thePlayer.isUsingItem() && !mc.thePlayer.isCollidedHorizontally && mc.thePlayer.getFoodStats().getFoodLevel() > 6.0F) {
				mc.thePlayer.setSprinting(true);
			}
		}
	}
}
