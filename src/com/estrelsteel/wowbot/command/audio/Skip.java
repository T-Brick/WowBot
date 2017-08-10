package com.estrelsteel.wowbot.command.audio;

import java.util.ArrayList;

import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;

public class Skip implements Command {
	
	private WowAudioCore wac;
	private String vid;
	private ArrayList<Long> votes;
	private double percent;
	
	public Skip(WowAudioCore wac, double percent) {
		this.wac = wac;
		vid = "";
		votes = new ArrayList<Long>();
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		return true;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		VoiceChannel c = VoiceHelp.determineChannel(e);
		if(c == null  || c.getIdLong() == wac.getVoiceChannel().getIdLong()) {
			System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " attempted to skip will not being in the voice channel.");
			e.getTextChannel().sendMessage(e.getAuthor().getAsMention() + " you need to be in the voice channel.").queue();
		}
		else {
			if(e.getMember().isOwner()) {
				wac.getAudioQueue().nextTrack();
				System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " skipped the current song.");
				e.getTextChannel().sendMessage(e.getAuthor().getAsMention() + " skipped the song.").queue();
			}
			else {
				if(!wac.getPlayer().getPlayingTrack().getIdentifier().equals(vid)) {
					vid = wac.getPlayer().getPlayingTrack().getIdentifier();
					votes = new ArrayList<Long>();
				}
				for(int i = 0; i < votes.size(); i++) {
					if(votes.get(i) == e.getAuthor().getIdLong()) {
						votes.remove(i);
						i--;
						System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " removed their vote to skip the current song.");
						e.getTextChannel().sendMessage(e.getAuthor().getAsMention() + " removed their vote to skip. " + (int) (c.getMembers().size() * percent) + " votes needed.").queue();
						return;
					}
				}
				votes.add(e.getAuthor().getIdLong());
				if((int) (c.getMembers().size() * percent) > 0) {
					System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " voted to skip the current song.");
					e.getTextChannel().sendMessage(e.getAuthor().getAsMention() + " voted to skip. " + (int) (c.getMembers().size() * percent) + " more votes needed.").queue();
				}
				else {
					wac.getAudioQueue().nextTrack();
					System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " skipped the current song.");
					e.getTextChannel().sendMessage(e.getAuthor().getAsMention() + " skipped the song.").queue();
				}
			}
		}
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "skip"
				+ "\nDESC: sends a vote to skip the on going song."
				+ "\nPERMS: all, admin";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
}
