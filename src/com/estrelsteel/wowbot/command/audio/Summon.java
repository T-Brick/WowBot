package com.estrelsteel.wowbot.command.audio;

import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.user.UserHandler;
import com.estrelsteel.wowbot.user.UserSettings;

public class Summon implements Command {
	
	private WowAudioCore wac;
	private UserHandler uh;
	
	public Summon(WowAudioCore wac, UserHandler uh) {
		this.wac = wac;
		this.uh = uh;
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		UserSettings us = uh.findUser(e.getAuthor().getIdLong());
		if(us != null && us.getMusicRules()[4]) {
			return true;
		}
		else {
			e.getTextChannel().sendMessage("You do not have the permissions to do this.").queue();
		}
		return false;
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
