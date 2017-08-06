package com.estrelsteel.wowbot;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Parser {
	public CommandContainer parse(String raw, MessageReceivedEvent e) {
		raw = raw.substring(WowBot.settings.getTrigger().length());
		String[] args = raw.split(" ");
		for(int i = 0; i < args.length; i++) {
			args[i] = args[i].trim().toLowerCase();
		}
		return new CommandContainer(args, e, raw);
	}
	
	public class CommandContainer {
		public String[] args;
		public MessageReceivedEvent e;
		public String raw;
		
		public CommandContainer(String[] args, MessageReceivedEvent e, String raw) {
			this.args = args;
			this.e = e;
			this.raw = raw;
		}
	}
}
