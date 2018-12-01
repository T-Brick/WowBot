package com.estrelsteel.wowbot.command.sys;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;

public class UpdateRoles implements Command {
	
	private WowBot b;
	
	public UpdateRoles(WowBot b) {
		this.b = b;
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		return true;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		b.updateRoles(e.getGuild());
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "updateroles"
				+ "\nDESC: updates the roles that can be mentioned."
				+ "\nPERMS: all";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
	

}
