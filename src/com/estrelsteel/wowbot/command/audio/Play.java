package com.estrelsteel.wowbot.command.audio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.file.GameFile;
import com.estrelsteel.wowbot.user.UserHandler;
import com.estrelsteel.wowbot.user.UserSettings;

public class Play implements Command {
	
	private WowAudioCore wac;
	private ArrayList<String> validAddress;
	private UserHandler uh;
	
	public Play(WowAudioCore core, GameFile file, UserHandler uh) {
		this.wac = core;
		try {
			validAddress = file.readFile();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		this.uh = uh;
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		if(args.length > 1) {
			UserSettings us = uh.findUser(e.getAuthor().getIdLong());
			if(us != null && us.getMusicRules()[0]) {
				return true;
			}
			else {
				e.getTextChannel().sendMessage("You do not have the permissions to do this.").queue();
			}
		}
		return false;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		VoiceChannel c = VoiceHelp.determineChannel(e);
		if(c != null) {
			boolean found = false;
			if(!(args[1].startsWith("http://") || args[1].startsWith("https://"))) {
				found = true;
			}
			else {
				for(String s : validAddress) {
					if(args[1].startsWith(s)) {
						found = true;
					}
				}
			}
			if(found) {
				wac.loadTrack(args[1], wac.switchVoiceChannel(c, false), e, true);
				e.getMessage().delete().queueAfter(30, TimeUnit.SECONDS);
			}
			else {
				e.getTextChannel().sendMessage("This link is not on the whitelist for valid links.\nYou can only play videos from:"
						+ "\n\tYoutube,"
						+ "\n\tSoundCloud,"
						+ "\n\tVimeo,"
						+ "\n\tand Twitch").queue();
			}
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
