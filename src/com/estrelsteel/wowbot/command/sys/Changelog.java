package com.estrelsteel.wowbot.command.sys;

import java.io.IOException;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.file.GameFile;

public class Changelog implements Command {
	
	private GameFile changelog;
	
	public Changelog(GameFile changelog) {
		this.changelog = changelog;
		try {
			this.changelog.setLines(changelog.readFile());
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		return true;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " has requested the changelog.");
		String msg = "```";
		int c = 0;
		for(String line : changelog.getLines()) {
			c++;
			msg = msg + "\n" + line;
			if(c >= 20) {
				msg = msg + "```";
				e.getAuthor().openPrivateChannel().complete().sendMessage(msg).queue();
				msg = "```";
				c = 0;
			}
		}
		msg = msg + "```";
		e.getAuthor().openPrivateChannel().complete().sendMessage(msg).queue();
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "info"
				+ "\nDESC: sends the changelog for WowBot."
				+ "\nPERMS: all";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
	

}
