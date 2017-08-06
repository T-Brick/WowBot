package com.estrelsteel.wowbot.command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface Command {
	public boolean called(String[] args, MessageReceivedEvent e);
	public void action(String[] args, MessageReceivedEvent e);
	public String help();
	public void executed(boolean success, MessageReceivedEvent e);
}
