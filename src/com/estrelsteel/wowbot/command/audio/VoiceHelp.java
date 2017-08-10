package com.estrelsteel.wowbot.command.audio;

import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class VoiceHelp {
	public static VoiceChannel determineChannel(MessageReceivedEvent e) {
		for(int i = 0; i < e.getGuild().getVoiceChannels().size(); i++) {
			for(int j = 0; j < e.getGuild().getVoiceChannels().get(i).getMembers().size(); j++) {
				if(e.getGuild().getVoiceChannels().get(i).getMembers().get(j).getUser().getName().equals(e.getAuthor().getName())) {
					return e.getGuild().getVoiceChannels().get(i);
				}
			}
		}
		return null;
	}
}
