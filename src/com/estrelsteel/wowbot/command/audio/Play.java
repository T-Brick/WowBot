package com.estrelsteel.wowbot.command.audio;

import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;

public class Play implements Command {
	
	private WowAudioCore wac;
	
	public Play(WowAudioCore core) {
		this.wac = core;
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		if(args.length > 1) {
			return true;
		}
		return false;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		VoiceChannel c = VoiceHelp.determineChannel(e);
		if(c != null) {
			wac.loadTrack(args[1], wac.switchVoiceChannel(c, false), e, true);
		}
		else {
			e.getTextChannel().sendMessage("You need to be in a voice channel to use this command.").queue();
		}
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "play [file | link]"
				+ "\nDESC: plays a video or audio file in a voice channel."
				+ "\n\t[file] : plays a specific file from the host computer."
				+ "\n\t[link] : plays a specfiic file from a forgein computer."
				+ "\nPERMS: all";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
}
