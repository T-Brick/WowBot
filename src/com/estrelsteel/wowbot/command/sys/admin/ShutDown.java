package com.estrelsteel.wowbot.command.sys.admin;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.user.UserHandler;

public class ShutDown implements Command {
	
	private WowBot wow;
	
	public ShutDown(WowBot wow) {
		this.wow = wow;
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		if(e.getAuthor().getId().equals(WowBot.owner)) {
			return true;
		}
		return false;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " has issued a shutdown.");
		System.out.println(WowBot.getMsgStart() + "Shutting down...");
		UserHandler.sendPublicMessage("Goodbye.", e, true);
		wow.shutdown(false);
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "shutdown"
				+ "\nDESC: shutsdown wowbot."
				+ "\nPERMS: owner";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
	

}
