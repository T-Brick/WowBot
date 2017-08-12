package com.estrelsteel.wowbot.command.audio;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.user.UserHandler;
import com.estrelsteel.wowbot.user.UserSettings;

public class Pause implements Command {
	
	private WowAudioCore wac;
	private boolean toggle;
	private UserHandler uh;
	
	public Pause(WowAudioCore wac, boolean toggle, UserHandler uh) {
		this.wac = wac;
		this.toggle = toggle;
		this.uh = uh;
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		UserSettings us = uh.findUser(e.getAuthor().getIdLong());
		if(us != null && us.getMusicRules()[3]) {
			return true;
		}
		else {
			e.getTextChannel().sendMessage("You do not have the permissions to do this.").queue();
		}
		return false;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		if(toggle) {
			wac.getPlayer().setPaused(!wac.getPlayer().isPaused());
		}
		else {
			wac.getPlayer().setPaused(false);
		}
		if(wac.getPlayer().isPaused()) {
			System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " paused the player.");
			e.getTextChannel().sendMessage("The player is now paused").queue();
		}
		else {
			System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " resumed the player.");
			e.getTextChannel().sendMessage("The player is now resumed").queue();
		}
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "pause"
				+ "\nDESC: pauses the music player."
				+ "\nPERMS: all";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
}
