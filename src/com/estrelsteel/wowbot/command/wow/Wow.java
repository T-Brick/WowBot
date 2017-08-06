package com.estrelsteel.wowbot.command.wow;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;

@Deprecated
public class Wow implements Command {
	
	public Wow() {
		
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		return true;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " says Wow.");
		e.getTextChannel().sendMessage("*WOW*").queue();
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "wow";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
}
