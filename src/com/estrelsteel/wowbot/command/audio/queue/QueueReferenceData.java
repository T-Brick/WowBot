package com.estrelsteel.wowbot.command.audio.queue;

import java.awt.Color;

import net.dv8tion.jda.core.entities.TextChannel;

public class QueueReferenceData {
	public TextChannel tc;
	public Color colour;
	
	public QueueReferenceData(TextChannel tc, Color colour) {
		this.tc = tc;
		this.colour = colour;
	}
}
