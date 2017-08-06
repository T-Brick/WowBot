package com.estrelsteel.wowbot.command.sys;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;

public class Restart implements Command {
	
	private WowBot bot;
	
	public Restart(WowBot bot) {
		this.bot = bot;
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
		System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " has issued a restart.");
		System.out.println(WowBot.getMsgStart() + "Restarting...");
		bot.shutdown(true);
		WowBot.main(null);
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "restart";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
}
