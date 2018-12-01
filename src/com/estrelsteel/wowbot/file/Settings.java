package com.estrelsteel.wowbot.file;

public class Settings {
	private String trigger;
	private boolean displayMusic;
	private double skipPercent;
	private int skipMessageDeleteTime;
	
	public Settings() {
		this.trigger = "~";
		this.displayMusic = false;
		this.skipPercent = 0.25;
		this.skipMessageDeleteTime = 5;
	}
	
	public String getTrigger() {
		return trigger;
	}
	
	public boolean doDisplayMusic() {
		return displayMusic;
	}

	public double getSkipPercent() {
		return skipPercent;
	}

	public int getSkipMessageDeleteTime() {
		return skipMessageDeleteTime;
	}
}
