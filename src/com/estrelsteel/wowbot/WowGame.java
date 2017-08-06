package com.estrelsteel.wowbot;

import net.dv8tion.jda.core.entities.Game;

public class WowGame implements Game {
	
	private String name;
	private String url;
	private GameType type;
	
	public WowGame(String name, String url, GameType type) {
		this.name = name;
		this.url = url;
		this.type = type;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public GameType getType() {
		return type;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setType(GameType type) {
		this.type = type;
	}
}