package com.estrelsteel.wowbot.command.audio.skip;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.command.audio.VoiceHelp;
import com.estrelsteel.wowbot.command.audio.WowAudioCore;
import com.estrelsteel.wowbot.command.audio.WowAudioTrack;
import com.estrelsteel.wowbot.user.UserHandler;
import com.estrelsteel.wowbot.user.UserSettings;

public class Skip implements Command {
	
	private WowAudioCore wac;
	private String vid;
	private ArrayList<Long> votes;
	private UserHandler uh;
	
	public Skip(WowAudioCore wac, UserHandler uh) {
		this.wac = wac;
		vid = "";
		votes = new ArrayList<Long>();
		this.uh = uh;
	}
	
	public void skipTrack(SkipReferenceData d) {
		if(wac.getPlayer().getPlayingTrack() == null) {
			System.out.println(WowBot.getMsgStart() + "" + d.author.getName() + " attempted to skip but nothing is playing.");
			d.tc.sendMessage("The player isn't playing.").complete().delete().queueAfter(WowBot.settings.getSkipMessageDeleteTime(), TimeUnit.SECONDS);
		}
		else if(d.vc == null || d.vc.getIdLong() != wac.getVoiceChannel().getIdLong()) {
			System.out.println(WowBot.getMsgStart() + "" + d.author.getName() + " attempted to skip but they're not in the voice channel.");
			d.tc.sendMessage(d.author.getAsMention() + " you need to be in the voice channel to skip.").complete().delete().queueAfter(WowBot.settings.getSkipMessageDeleteTime(), TimeUnit.SECONDS);
		}
		else {
			if(d.tc.getGuild().getOwner().getUser().getIdLong() == d.author.getIdLong()) {
				wac.getAudioQueue().nextTrack();
				System.out.println(WowBot.getMsgStart() + "" + d.author.getName() + " skipped the current audio track.");
				d.tc.sendMessage(d.author.getAsMention() + " skipped the audio track.").complete().delete().queueAfter(WowBot.settings.getSkipMessageDeleteTime(), TimeUnit.SECONDS);
				votes = new ArrayList<Long>();
			}
			else {
				if(!wac.getPlayer().getPlayingTrack().getIdentifier().equals(vid)) {
					vid = wac.getPlayer().getPlayingTrack().getIdentifier();
					votes = new ArrayList<Long>();
				}
				for(int i = 0; i < votes.size(); i++) {
					if(votes.get(i) == d.author.getIdLong()) {
						votes.remove(i);
						i--;
						System.out.println(WowBot.getMsgStart() + "" + d.author.getName() + " removed their vote to skip the current audio track.");
						d.tc.sendMessage(d.author.getAsMention() + " removed their vote to skip. " + (int) (d.vc.getMembers().size() * WowBot.settings.getSkipPercent()) 
								+ " votes needed.").complete().delete().queueAfter(30, TimeUnit.SECONDS);
						return;
					}
				}
				votes.add(d.author.getIdLong());
				if((int) (d.vc.getMembers().size() * WowBot.settings.getSkipPercent()) > votes.size()) {
					System.out.println(WowBot.getMsgStart() + "" + d.author.getName() + " voted to skip the current audio track.");
					d.tc.sendMessage(d.author.getAsMention() + " voted to skip. " + (int) (d.vc.getMembers().size() * WowBot.settings.getSkipPercent()) + " more votes needed.")
						.complete().delete().queueAfter(30, TimeUnit.SECONDS);
				}
				else {
					wac.getAudioQueue().nextTrack();
					System.out.println(WowBot.getMsgStart() + "" + d.author.getName() + " skipped the current audio tracks.");
					d.tc.sendMessage(d.author.getAsMention() + " skipped the audio track.").complete().delete().queueAfter(WowBot.settings.getSkipMessageDeleteTime(), TimeUnit.SECONDS);
					votes = new ArrayList<Long>();
				}
			}
		}
		return;
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
		if(args.length > 1 && args[1].equalsIgnoreCase("all")) {
			wac.getAudioQueue().setQueue(new ArrayList<WowAudioTrack>());
		}
		skipTrack(new SkipReferenceData(e.getTextChannel(), c, e.getAuthor()));
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "skip <all>"
				+ "\nDESC: sends a vote to skip the on going song."
				+ "\nPERMS: all, admin";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
}
