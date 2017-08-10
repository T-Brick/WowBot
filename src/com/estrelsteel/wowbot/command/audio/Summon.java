package com.estrelsteel.wowbot.command.audio;

import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;

public class Summon implements Command {
	
	private WowAudioCore wac;
	
	public Summon(WowAudioCore wac) {
		this.wac = wac;
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		return true;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " summoned wowbot.");
		VoiceChannel c = VoiceHelp.determineChannel(e);
		if(c != null) {
			wac.switchVoiceChannel(c, true);
		}
		else {
			e.getTextChannel().sendMessage(e.getAuthor().getAsMention() + " you need to be in a voice channel.").queue();
		}
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "summon"
				+ "\nDESC: brings wowbot to the user's voice channel."
				+ "\nPERMS: all";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
}
