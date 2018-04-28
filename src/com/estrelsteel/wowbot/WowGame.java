package com.estrelsteel.wowbot;

import net.dv8tion.jda.core.entities.Game;

public class WowGame extends Game {
	public WowGame(String name, String url, GameType type) {
		super(name, url, type);
	}
}