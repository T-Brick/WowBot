package com.estrelsteel.wowbot.file;

public class Settings {
	private String trigger;
	
	public Settings() {
		trigger = "~";
	}
	
	public String getTrigger() {
		return trigger;
	}
	
	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}
}
