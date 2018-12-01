package com.estrelsteel.wowbot.command.audio.skip;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class SkipReferenceData {
	public TextChannel tc;
	public VoiceChannel vc;
	public User author;
	
	public SkipReferenceData(TextChannel tc, VoiceChannel vc, User author) {
		this.tc = tc;
		this.vc = vc;
		this.author = author;
	}
}
