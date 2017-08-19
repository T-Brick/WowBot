package com.estrelsteel.wowbot.command.audio;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.user.UserHandler;
import com.estrelsteel.wowbot.user.UserSettings;

public class Skip implements Command {
	
	private WowAudioCore wac;
	private String vid;
	private ArrayList<Long> votes;
	private double percent;
	private UserHandler uh;
	
	public Skip(WowAudioCore wac, double percent, UserHandler uh) {
		this.wac = wac;
		vid = "";
		votes = new ArrayList<Long>();
		this.uh = uh;
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		UserSettings us = uh.findUser(e.getAuthor().getIdLong());
		if(us != null && us.getMusicRules()[2]) {
			return true;
		}
		else {
			e.getTextChannel().sendMessage("You do not have the permissions to do this.").queue();
		}
		return false;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		VoiceChannel c = VoiceHelp.determineChannel(e);
		e.getMessage().delete().queue();
		if(wac.getPlayer().getPlayingTrack() ==  null) {
			System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " attempted to skip but nothing is playing.");
			e.getTextChannel().sendMessage("The player isn't playing.").complete().delete().queueAfter(10, TimeUnit.SECONDS);
		}
		else if(c == null  || c.getIdLong() != wac.getVoiceChannel().getIdLong()) {
			System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " attempted to skip but their not in the voice channel.");
			e.getTextChannel().sendMessage(e.getAuthor().getAsMention() + " you need to be in the voice channel to skip.").complete().delete().queueAfter(30, TimeUnit.SECONDS);
		}
		else {
			if(e.getGuild().getOwner().getUser().getIdLong() == e.getAuthor().getIdLong()) {
				wac.getAudioQueue().nextTrack();
				System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " skipped the current audio track.");
				e.getTextChannel().sendMessage(e.getAuthor().getAsMention() + " skipped the audio track.").complete().delete().queueAfter(30, TimeUnit.SECONDS);
				votes = new ArrayList<Long>();
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
						System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " removed their vote to skip the current audio track.");
						e.getTextChannel().sendMessage(e.getAuthor().getAsMention() + " removed their vote to skip. " + (int) (c.getMembers().size() * percent) 
								+ " votes needed.").complete().delete().queueAfter(30, TimeUnit.SECONDS);
						return;
					}
				}
				votes.add(e.getAuthor().getIdLong());
				if((int) (c.getMembers().size() * percent) > votes.size()) {
					System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " voted to skip the current audio track.");
					e.getTextChannel().sendMessage(e.getAuthor().getAsMention() + " voted to skip. " + (int) (c.getMembers().size() * percent) + " more votes needed.")
						.complete().delete().queueAfter(30, TimeUnit.SECONDS);
				}
				else {
					wac.getAudioQueue().nextTrack();
					System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " skipped the current audio tracks.");
					e.getTextChannel().sendMessage(e.getAuthor().getAsMention() + " skipped the audio track.").complete().delete().queueAfter(30, TimeUnit.SECONDS);
					votes = new ArrayList<Long>();
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
