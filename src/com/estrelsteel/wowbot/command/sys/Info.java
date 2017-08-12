package com.estrelsteel.wowbot.command.sys;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.user.UserHandler;

public class Info implements Command {
	
	public Info() {
		
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		return true;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " has requested the info page.");
		UserHandler.sendPublicMessage("```" + WowBot.title + "\n\tBy: EstrelSteel"
				+ "\n\n\tGitHub: https://github.com/EstrelSteel/WowBot"
				+ "\n\tInvite: https://discordapp.com/oauth2/authorize?&client_id=266437681242701825&scope=bot&permissions=523349056```", e, true);
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "info"
				+ "\nDESC: sends info about WowBot."
				+ "\nPERMS: all";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
	

}
