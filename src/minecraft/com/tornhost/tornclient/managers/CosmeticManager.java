package com.tornhost.tornclient.managers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class CosmeticManager {
	
	// Stores the UUIDs and their assigned cape names
	private HashMap<String, String> playerCapes = new HashMap<>();
	private HashMap<String, String[]> playerOwnedCapes = new HashMap<>();
	private HashMap<String, net.minecraft.util.ResourceLocation> downloadedTextures = new HashMap<>();
	
	// API URL
	private final String API_URL = "http://127.0.0.1:8000/cosmetics/";
	
	public CosmeticManager() {
		// Constructor
	}
	
	public void fetchCosmetics(String uuid) {
        // If we already know what they are wearing, don't spam the API!
        if (playerCapes.containsKey(uuid)) return; 
        
        new Thread(() -> {
            try {
                // Hits the original endpoint we made!
                java.net.URL url = new java.net.URL("http://127.0.0.1:8000/cosmetics/" + uuid);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000); 
                
                if (connection.getResponseCode() == 200) {
                    java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
                    String response = in.readLine(); 
                    in.close();
                    
                    if (response != null && !response.equals("none")) {
                        // Save their equipped cape to our map!
                        playerCapes.put(uuid, response);
                        System.out.println("Fetched currently equipped cape for " + uuid + ": " + response);
                    } else {
                        playerCapes.put(uuid, "none"); 
                    }
                }
            } catch (Exception e) {
                System.out.println("Failed to fetch equipped cape for " + uuid);
                playerCapes.put(uuid, "none"); 
            }
        }).start();
    }
	
	public void fetchOwnedCapes(String uuid) {
        if (playerOwnedCapes.containsKey(uuid)) return; // Don't spam the API
        
        new Thread(() -> {
            try {
                URL url = new URL("http://127.0.0.1:8000/cosmetics/" + uuid + "/owned");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                
                if (connection.getResponseCode() == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String response = in.readLine();
                    in.close();
                    
                    if (response != null && !response.equals("none")) {
                        // Split the comma-separated string into a Java Array!
                        playerOwnedCapes.put(uuid, response.split(","));
                        System.out.println("Fetched inventory for " + uuid + ": " + response);
                    } else {
                        playerOwnedCapes.put(uuid, new String[]{"none"});
                    }
                }
            } catch (Exception e) {
                System.out.println("Failed to fetch cape inventory for " + uuid);
                playerOwnedCapes.put(uuid, new String[]{"none"}); 
            }
        }).start();
    }
	
	// Call this when the player clicks the "EQUIP" button in the GUI
    public void setEquippedCape(String uuid, String username, String newCape) {
        playerCapes.put(uuid, newCape);

        new Thread(() -> {
            try {
                java.net.URL url = new java.net.URL("http://127.0.0.1:8000/set_cape");
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Build the JSON string that FastAPI expects
                String json = String.format("{\"uuid\":\"%s\", \"username\":\"%s\", \"cape_name\":\"%s\"}", uuid, username, newCape);

                // Send the payload
                java.io.OutputStream os = conn.getOutputStream();
                os.write(json.getBytes());
                os.flush();
                os.close();

                // Trigger the request
                conn.getResponseCode(); 
                System.out.println("Successfully saved equipped cape to database!");
                
            } catch (Exception e) {
                System.out.println("Failed to update database: " + e.getMessage());
            }
        }).start();
    }

    // The UI will call this to see what buttons to draw
    public String[] getOwnedCapes(String uuid) {
        return playerOwnedCapes.getOrDefault(uuid, new String[]{"none"});
    }
	
	public String getCape(String uuid) {
		return playerCapes.getOrDefault(uuid, "none");
	}
	
	public net.minecraft.util.ResourceLocation getCapeTexture(String capeName) {
		if (capeName == null || capeName.equals("none")) {
			return null;
		}
		
		if (downloadedTextures.containsKey(capeName)) {
			return downloadedTextures.get(capeName);
		}
		
		System.out.println("Downloading new cape texture: " + capeName);
		
		net.minecraft.util.ResourceLocation location = new net.minecraft.util.ResourceLocation("tornclient_capes", capeName);
		
		String url = "http://127.0.0.1:8000/capes/" + capeName + ".png";
		
		net.minecraft.client.renderer.ThreadDownloadImageData downloader = 
	            new net.minecraft.client.renderer.ThreadDownloadImageData(null, url, null, null);
		
		net.minecraft.client.Minecraft.getMinecraft().getTextureManager().loadTexture(location, downloader);
		
		downloadedTextures.put(capeName, location);
		
		return location;
	}
}
