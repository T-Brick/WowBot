package com.estrelsteel.wowbot.command.audio;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;

public class Queue implements Command {
	
	private WowAudioCore wac;
	
	public Queue(WowAudioCore wac) {
		this.wac = wac;
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		return true;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " requested the audio queue.");
		e.getMessage().delete().queue();
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(e.getMember().getColor());
		String msg = "";
		if(wac.getPlayer().getPlayingTrack() != null) {
			msg = "\t**Currently playing: " + WowAudioCore.getVisibleTitle(wac.getPlayer().getPlayingTrack()) + "**";
		}
		else {
			e.getTextChannel().sendMessage("Nothing is playing.").queue();
			return;
		}
		if(wac.getAudioQueue().getQueue().size() == 0) {
			msg = msg + "\nThe queue is empty";
		}
		else {
			for(int i = 0; i < wac.getAudioQueue().getQueue().size(); i++) {
				msg = msg + "\n" + (i + 1) + ".) " + WowAudioCore.getVisibleTitle(wac.getAudioQueue().getQueue().get(i));
			}
		}
		builder.addField("Queue:", msg, false);
		e.getTextChannel().sendMessage(builder.build()).queue();
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "queue"
				+ "\nDESC: displays the player queue."
				+ "\nPERMS: all";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
}
