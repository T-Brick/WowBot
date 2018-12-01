package com.estrelsteel.wowbot;

import java.util.ArrayList;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Parser {
	public ArrayList<CommandContainer> parse(String raw, MessageReceivedEvent e) {
		ArrayList<CommandContainer> containers = new ArrayList<CommandContainer>();
		System.out.println(raw);
		String[] lines = raw.split("\n");
		int multiplier = 0;
		for(int j = 0; j < lines.length && j < 10; j++) {
			while(lines[j].startsWith(WowBot.settings.getTrigger()) && multiplier < 11) {
				lines[j] = lines[j].substring(WowBot.settings.getTrigger().length());
				multiplier++;
			}
			String[] args = lines[j].split(" ");
			for(int i = 0; i < args.length; i++) {
				args[i] = args[i].trim();
			}
			if(args.length >= 1) {
				args[0].toLowerCase();
			}
			for(int i = 0; i < multiplier; i++) containers.add(new CommandContainer(args, e, lines[j]));
			multiplier = 0;
		}
		return containers;
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
