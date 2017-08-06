package com.estrelsteel.wowbot.command.wow;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;

@Deprecated
public class VVoVV implements Command {
	
	public VVoVV() {
		
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		return true;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " says Wow.");
		e.getTextChannel().sendMessage("*VVOVV*").queue();

	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "vvovv";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
	

}
