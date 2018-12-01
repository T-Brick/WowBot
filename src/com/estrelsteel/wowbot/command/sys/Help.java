package com.estrelsteel.wowbot.command.sys;

import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;

public class Help implements Command {
	
	private WowBot b;
	private String[] cmds;
	
	public Help(WowBot b) {
		this.b = b;
		cmds = new String[31];
		int i = 0;
		cmds[i] = "assemble"; i++;
		cmds[i] = "audioperms"; i++;
		cmds[i] = "changelog"; i++;
		cmds[i] = "clean"; i++;
		cmds[i] = "colour"; i++;
		cmds[i] = "event"; i++;
		cmds[i] = "help"; i++;
		cmds[i] = "iogames"; i++;
		cmds[i] = "info"; i++;
		cmds[i] = "kaomoji"; i++;
		cmds[i] = "link"; i++;
		cmds[i] = "mute"; i++;
		cmds[i] = "pause"; i++;
		cmds[i] = "play"; i++;
		cmds[i] = "politics"; i++;
		cmds[i] = "queue"; i++;
		cmds[i] = "quiet"; i++;
		cmds[i] = "random"; i++;
		cmds[i] = "restart"; i++;
		cmds[i] = "save"; i++;
		cmds[i] = "sfx"; i++;
		cmds[i] = "shutdown"; i++;
		cmds[i] = "skip"; i++;
		cmds[i] = "summon"; i++;
		cmds[i] = "team"; i++;
		cmds[i] = "timeout"; i++;
		cmds[i] = "updateroles"; i++;
		cmds[i] = "volume"; i++;
		cmds[i] = "watch"; i++;
		cmds[i] = "whois"; i++;
		cmds[i] = "wow"; i++;
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		return true;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		if(args.length <= 1) {
			System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " has requested the help page.");
//			UserHandler.sendPublicMessage("```Commands: " 
//				+ WowBot.settings.getTrigger() + "help, " 
//				+ WowBot.settings.getTrigger() + "wow, " 
//				+ WowBot.settings.getTrigger() + "event, " 
//				+ WowBot.settings.getTrigger() + "save, " 
//				+ WowBot.settings.getTrigger() + "shutdown, " 
//				+ WowBot.settings.getTrigger() + "info, " 
//				+ WowBot.settings.getTrigger() + "assemble, " 
//				+ WowBot.settings.getTrigger() + "whois, " 
//				+ WowBot.settings.getTrigger() + "quiet, " 
//				+ WowBot.settings.getTrigger() + "politics, "
//				+ WowBot.settings.getTrigger() + "colour, "
//				+ WowBot.settings.getTrigger() + "mute, "
//				+ WowBot.settings.getTrigger() + "restart, "
//				+ WowBot.settings.getTrigger() + "clean, "
//				+ WowBot.settings.getTrigger() + "timeout, "
//				+ WowBot.settings.getTrigger() + "watch"
//				+ "```",e, true);
			String msg = "";
			for(int i = 0; i < cmds.length; i++) {
				if(i != 0) {
					msg = msg + "\n";
				}
				msg = msg + WowBot.settings.getTrigger() + cmds[i];
			}
			msg = msg + "";

			EmbedBuilder builder = new EmbedBuilder();
			builder.setColor(e.getMember().getColor());
			builder.addField("Command List:", msg, false);
			e.getTextChannel().sendMessage(builder.build()).complete().delete().queueAfter(30, TimeUnit.SECONDS);
		}
		else {
			System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " has requested help for the " + args[1].trim() + " command.");
			e.getTextChannel().sendMessage(b.getCommands().get(args[1]).help()).complete().delete().queueAfter(30, TimeUnit.SECONDS);
		}
		e.getMessage().delete().queue();
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "help <command>"
				+ "\nDESC: gives the player a list of commands."
				+ "\n\t<command> : gives the user info about <command>."
				+ "\nPERMS: all";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
	

}
