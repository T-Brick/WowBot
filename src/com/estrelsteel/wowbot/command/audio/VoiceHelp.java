package com.estrelsteel.wowbot.command.audio;

import java.util.List;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class VoiceHelp {
	
	public static void disconnect(JDA jda) {
		for(Guild g : jda.getGuilds()) {
			if(g.getAudioManager() != null && g.getAudioManager().isConnected()) g.getAudioManager().closeAudioConnection();
		}
	}
	
	public static VoiceChannel determineChannel(Guild g, long id) {
		for(int i = 0; i < g.getVoiceChannels().size(); i++) {
			for(int j = 0; j < g.getVoiceChannels().get(i).getMembers().size(); j++) {
				if(g.getVoiceChannels().get(i).getMembers().get(j).getUser().getIdLong() == id) {
					return g.getVoiceChannels().get(i);
				}
			}
		}
		return null;
	}
	
	public static VoiceChannel determineChannel(MessageReceivedEvent e) {
		return determineChannel(e.getGuild(), e.getAuthor().getIdLong());
	}
	
	public static VoiceChannel determineOwnerChannel(List<Guild> guilds, long id) {
		VoiceChannel v;
		for(int k = 0; k < guilds.size(); k++) {
			v = determineChannel(guilds.get(k), id);
			if(v != null) return v;
		}
		return null;
	}
}
