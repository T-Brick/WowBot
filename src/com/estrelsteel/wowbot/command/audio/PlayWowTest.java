package com.estrelsteel.wowbot.command.audio;

import java.io.File;

import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;

public class PlayWowTest implements Command {
	
	private AudioSender as;
	
	public PlayWowTest() {
		as = new AudioSender(new File("/Users/justin/Desktop/SERVER/Discord/WowBot/emoteWow.wav"));
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		return true;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		VoiceChannel c = determineChannel(e);
		if(c != null) {
			AudioManager am = e.getGuild().getAudioManager();
			am.openAudioConnection(c);
			am.setSendingHandler(as);
			am.openAudioConnection(c);
		}
		else {
			e.getTextChannel().sendMessage("You need to be in a voice channel to use this command.").queue();
		}
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "playwow";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
	
	public VoiceChannel determineChannel(MessageReceivedEvent e) {
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
