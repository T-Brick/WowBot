package com.estrelsteel.wowbot.command.audio.runtime;

import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.command.audio.WowAudioCore;

public class Link implements Command {
	
	private WowAudioCore wac;
	
	public Link(WowAudioCore wac) {
		this.wac = wac;
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		return true;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		String link = wac.getPlayer().getPlayingTrack().getInfo().uri;
		e.getMessage().delete().queue();
		if(link.startsWith("http")) {
			e.getTextChannel().sendMessage("Currently Playing: **" +  wac.getPlayer().getPlayingTrack().getInfo().title + "** (" + link + ")").complete().delete().queueAfter(30, TimeUnit.SECONDS);
		}
		else e.getTextChannel().sendMessage("Sorry, the link is unavaliable").complete().delete().queueAfter(10, TimeUnit.SECONDS);
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "link"
				+ "\nDESC: gives a link to the currently playing track, if avaliable."
				+ "\nPERMS: all";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
}
