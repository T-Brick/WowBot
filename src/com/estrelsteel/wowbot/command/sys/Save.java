package com.estrelsteel.wowbot.command.sys;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.user.UserHandler;

public class Save implements Command {
	
	private WowBot wow;
	
	public Save(WowBot wow) {
		this.wow = wow;
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		return true;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " has requested to save.");
		System.out.println(WowBot.getMsgStart() + "Saving data...");
		UserHandler.sendPublicMessage("Saving...", e, true);
		wow.save();
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "save";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
	

}
