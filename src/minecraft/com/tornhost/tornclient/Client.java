package com.tornhost.tornclient;

import com.tornhost.tornclient.managers.CosmeticManager;
import com.tornhost.tornclient.managers.ModManager;
import com.tornhost.tornclient.managers.DiscordManager;

public class Client {
	private static final Client INSTANCE = new Client();
	
	public static final Client getInstance() {
		return INSTANCE;
	}
	
	// Client info
	public final String name = "TornClient";
	public final String version = "1.0.0";
	public final String author = "atticl";
	
	public ModManager modManager;
	public CosmeticManager cosmeticManager;
	public DiscordManager discordManager;
	
	// Run when game starts
	public void startup() {
		System.out.println("=======================================");
        System.out.println("Starting " + name + " v" + version + " by " + author);
        System.out.println("=======================================");
        
        modManager = new ModManager();
        cosmeticManager = new CosmeticManager();
        
        discordManager = new DiscordManager();
        discordManager.start();
        
        String myUUID = net.minecraft.client.Minecraft.getMinecraft().getSession().getPlayerID();
        cosmeticManager.fetchOwnedCapes(myUUID);
        cosmeticManager.fetchCosmetics(myUUID);
	}
	
	public void shutdown() {
        System.out.println("Shutting down TornClient...");
        if (discordManager != null) {
            discordManager.shutdown();
        }
    }
}
