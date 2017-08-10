package com.estrelsteel.wowbot.command.user;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.user.UserHandler;

public class Mute implements Command {
	
	private UserHandler uh;
	
	public Mute(UserHandler uh) {
		this.uh = uh;
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		return true;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		boolean mute = !uh.findUser(e.getAuthor().getId()).isMute();
		uh.findUser(e.getAuthor().getId()).setMute(mute);
		if(mute) {
			System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " has now muted WowBot.");
			uh.findUser(e.getAuthor().getId()).sendPrivateMessage(e.getAuthor(), "You have now muted WowBot.", true);
		}
		else {
			System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " has now unmuted WowBot.");
			uh.findUser(e.getAuthor().getId()).sendPrivateMessage(e.getAuthor(), "You have now unmuted WowBot.", true);
		}
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "mute"
				+ "\nDESC: stops wowbot from sending personal messages."
				+ "\nPERMS: all";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
}
