package com.tornhost.tornclient;

public class Client {
	private static final Client INSTANCE = new Client();
	
	public static final Client getInstance() {
		return INSTANCE;
	}
	
	// Client info
	public final String name = "TornClient";
	public final String version = "1.0.0";
	public final String author = "atticl";
	
	// Run when game starts
	public void startup() {
		System.out.println("=======================================");
        System.out.println("Starting " + name + " v" + version + " by " + author);
        System.out.println("=======================================");       
	}
}
