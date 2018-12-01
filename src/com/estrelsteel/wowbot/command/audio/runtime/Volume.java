package com.estrelsteel.wowbot.command.audio.runtime;

import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.command.audio.WowAudioCore;
import com.estrelsteel.wowbot.user.UserHandler;
import com.estrelsteel.wowbot.user.UserSettings;

public class Volume implements Command {
	
	private WowAudioCore wac;
	private UserHandler uh;
	
	public Volume(WowAudioCore wac, UserHandler uh) {
		this.wac = wac;
		this.uh = uh;
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		UserSettings us = uh.findUser(e.getAuthor().getIdLong());
		if(us != null && us.getMusicRules()[5]) {
			return true;
		}
		else {
			e.getTextChannel().sendMessage("You do not have the permissions to do this.").queue();
		}
		return false;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		if(args.length > 1) {
			System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " is attempting to changing the volume to " + args[1] + ".");
			try {
				int vol = Integer.parseInt(args[1]);
				int old = wac.getPlayer().getVolume();
				wac.getPlayer().setVolume(vol);
				e.getTextChannel().sendMessage("Changed the volume from " + old + " to " + vol + ".").complete().delete().queueAfter(30, TimeUnit.SECONDS);
				e.getMessage().delete().queueAfter(30, TimeUnit.SECONDS);
				System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " changed the volume from " + old + " to " + vol + ".");
			}
			catch(NumberFormatException er) {
				e.getTextChannel().sendMessage("The volume should be between 1 and 100").queue();
			}
		}
		else {
			System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " is requesting the volume.");
			e.getTextChannel().sendMessage("The volume is" + wac.getPlayer().getVolume() + ".").queue();
		}
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "volume <value>"
				+ "\nDESC: gets and sets the volume for the player."
				+ "\n\t<NULL> : replies with the current volume."
				+ "\n\t<value> : sets the volume to the value between 1 and 100."
				+ "\nPERMS: all";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
}
