package com.tornhost.tornclient.managers;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRichPresence;

public class DiscordManager {

    private long startTimestamp = 0;

    public void start() {
        String rootPath = System.getProperty("user.dir");
        System.out.println("----------------------------------------------");
        System.out.println("DIAGNOSTIC: Searching for DLL in: " + rootPath);
        
        try {
            java.io.File file1 = new java.io.File(rootPath + "/natives/discord-rpc.dll");
            java.io.File file2 = new java.io.File(rootPath + "/jars/discord-rpc.dll");

            if (file1.exists()) {
                System.load(file1.getAbsolutePath());
                System.out.println("SUCCESS: Loaded from /natives/");
            } else if (file2.exists()) {
                System.load(file2.getAbsolutePath());
                System.out.println("SUCCESS: Loaded from /jars/");
            } else {
                System.err.println("CRITICAL: NO DLL FOUND!");
                System.err.println("EXPECTED AT: " + file1.getAbsolutePath());
                return;
            }
        } catch (Exception e) {
            System.err.println("LOAD ERROR: " + e.getMessage());
            return;
        }

        // We use the full path to the INSTANCE here so it doesn't crash on class load
        this.startTimestamp = System.currentTimeMillis() / 1000;
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        
        club.minnced.discord.rpc.DiscordRPC.INSTANCE.Discord_Initialize("1475890341004578978", handlers, true, "");

        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                club.minnced.discord.rpc.DiscordRPC.INSTANCE.Discord_RunCallbacks();
                try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            }
        }, "RPC-Callback-Handler").start();
        
        update("In the Main Menu", "Idle");
    }

    public void update(String firstLine, String secondLine) {
        DiscordRichPresence presence = new DiscordRichPresence();
        presence.details = firstLine;
        presence.state = secondLine;
        presence.startTimestamp = this.startTimestamp;
        presence.largeImageKey = "logo";
        club.minnced.discord.rpc.DiscordRPC.INSTANCE.Discord_UpdatePresence(presence);
    }
    
    public void updateStatus(net.minecraft.client.Minecraft mc) {
        if (mc.theWorld == null) {
            update("In the Main Menu", "Idle");
        } else if (mc.isSingleplayer()) {
            update("Playing Singleplayer", "Exploring " + mc.theWorld.getWorldInfo().getWorldName());
        } else if (mc.getCurrentServerData() != null) {
            update("Playing Multiplayer", "Server: " + mc.getCurrentServerData().serverIP);
        }
    }

    public void shutdown() {
        club.minnced.discord.rpc.DiscordRPC.INSTANCE.Discord_Shutdown();
    }
}