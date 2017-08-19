package com.estrelsteel.wowbot.command.audio;

import java.util.List;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class VoiceHelp {
	public static VoiceChannel determineChannel(MessageReceivedEvent e) {
		for(int i = 0; i < e.getGuild().getVoiceChannels().size(); i++) {
			for(int j = 0; j < e.getGuild().getVoiceChannels().get(i).getMembers().size(); j++) {
				if(e.getGuild().getVoiceChannels().get(i).getMembers().get(j).getUser().getIdLong() == e.getAuthor().getIdLong()) {
					return e.getGuild().getVoiceChannels().get(i);
				}
			}
		}
		return null;
	}
	
	public static VoiceChannel determineOwnerChannel(List<Guild> guilds, long id) {
		for(int k = 0; k < guilds.size(); k++) {
			for(int i = 0; i < guilds.get(k).getVoiceChannels().size(); i++) {
				for(int j = 0; j < guilds.get(k).getVoiceChannels().get(i).getMembers().size(); j++) {
					if(guilds.get(k).getVoiceChannels().get(i).getMembers().get(j).getUser().getIdLong() == id) {
						return guilds.get(k).getVoiceChannels().get(i);
					}
				}
			}
		}
		return null;
	}
}
