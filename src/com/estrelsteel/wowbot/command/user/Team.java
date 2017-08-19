package com.estrelsteel.wowbot.command.user;

import java.util.ArrayList;

import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;

public class Team implements Command {
	
	private final int maxTeams;
	private boolean lock;
	private ArrayList<Long> vote_lock;
	private int minLock;
	
	public Team(int maxTeams, int minLock) {
		this.maxTeams = maxTeams;
		this.lock = false;
		this.vote_lock = new ArrayList<Long>();
		this.minLock = minLock;
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		if(args.length > 1) {
			return true;
		}
		return false;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		if(args[1].trim().equalsIgnoreCase("lock")) {
			boolean found = false;
			for(int i = 0; i < vote_lock.size(); i++) {
				if(vote_lock.get(i) == e.getAuthor().getIdLong()) {
					found = true;
					vote_lock.remove(i);
					break;
				}
			}
			if(!found) {
				e.getTextChannel().sendMessage(e.getAuthor().getAsMention() + " has voted to lock the teams.").queue();
				System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " has voted to lock the teams.");
				vote_lock.add(e.getAuthor().getIdLong());
				if(vote_lock.size() >= minLock && !lock) {
					e.getTextChannel().sendMessage("The teams are now locked in.").queue();
					System.out.println(WowBot.getMsgStart() + "The teams are now locked in.");
					lock = true;
				}
			}
			else {
				e.getTextChannel().sendMessage(e.getAuthor().getAsMention() + " has voted to unlock the teams.").queue();
				System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " has voted to unlock the teams.");
				if(vote_lock.size() < minLock && lock) {
					e.getTextChannel().sendMessage("The teams are no longer locked in.").queue();
					System.out.println(WowBot.getMsgStart() + "The teams are no longer locked in.");
					lock = false;
				}
			}
		}
		else if(args[1].trim().equalsIgnoreCase("alock") && e.getAuthor().getIdLong() == e.getGuild().getOwner().getUser().getIdLong()) {
			lock = !lock;
			if(lock) {
				e.getTextChannel().sendMessage(e.getAuthor().getAsMention() + " has made the teams locked.").queue();
				System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " has made the teams locked.");
			}
			else {
				e.getTextChannel().sendMessage(e.getAuthor().getAsMention() + " has made the teams unlocked.").queue();
				System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " has made the teams unlocked.");
			}
		}
		else if((args[1].trim().equalsIgnoreCase("clean") || args[1].trim().equalsIgnoreCase("clear")) && e.getAuthor().getIdLong() == e.getGuild().getOwner().getUser().getIdLong()) {
			System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " has cleaned the teams.");
			for(int i = 0; i < e.getGuild().getMembers().size(); i++) {
				for(int j = 0; j < e.getGuild().getMembers().get(i).getRoles().size(); j++) {
					if(e.getGuild().getMembers().get(i).getRoles().get(j).getName().startsWith("Team ")) {
						e.getGuild().getController().removeSingleRoleFromMember(e.getMember(), e.getGuild().getMembers().get(i).getRoles().get(j)).queue();
						j--;
					}
				}
				if(e.getGuild().getMembers().get(i).getVoiceState().inVoiceChannel() 
						&& e.getGuild().getMembers().get(i).getVoiceState().getChannel().getName().startsWith("Team ")) {
					e.getGuild().getController().moveVoiceMember(e.getGuild().getMembers().get(i), e.getGuild().getAfkChannel());
				}
			}
			vote_lock = new ArrayList<Long>();
			e.getTextChannel().sendMessage(e.getAuthor().getAsMention() + " the teams have been cleaned.").queue();
			lock = false;
		}
		else if(!lock) {
			VoiceChannel c = determineChannel(e);
			int team = Integer.parseInt(args[1].trim());
			if(c != null) {
				System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " is attempting to join team " + team + ".");
				if(team <= maxTeams && team > 0) {
					Role r = e.getGuild().getRolesByName("Team " + team, true).get(0);
					e.getGuild().getController().addRolesToMember(e.getMember(), r).queue();
					e.getGuild().getController().moveVoiceMember(e.getMember(), e.getGuild().getVoiceChannelsByName("Team " + team, true).get(0)).queue();
					e.getTextChannel().sendMessage(e.getAuthor().getAsMention() + " has joined Team " + team).queue();
					System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " is now on team " + team + ".");
					for(int i = 0; i < e.getMember().getRoles().size(); i++) {
						if(e.getMember().getRoles().get(i).getName().startsWith("Team ") && !e.getMember().getRoles().get(i).getName().equals("Team " + team)) {
							e.getGuild().getController().removeSingleRoleFromMember(e.getMember(), e.getMember().getRoles().get(i)).queue();;
							i--;
						}
					}
				}
				else {
					e.getTextChannel().sendMessage("The max amounts of teams are " + maxTeams).queue();
					System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " didn't join. Team request was out of bounds.");
				}
			}
			else {
				e.getTextChannel().sendMessage("You need to be in a voice channel.").queue();
			}
		}
		else {
			e.getTextChannel().sendMessage(e.getAuthor().getAsMention() + " the teams are currently locked.").queue();
			System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " attempted to join but the teams were locked.");
			
		}
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "team [number | lock | alock]"
				+ "\nDESC: manages teams."
				+ "\n\t[number] : joins team [number]."
				+ "\n\t[lock] : votes to lock in the teams."
				+ "\n\t[alock] : force locks the teams."
				+ "\nPERMS: all, admin";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
	
	public VoiceChannel determineChannel(MessageReceivedEvent e) {
		for(int i = 0; i < e.getGuild().getVoiceChannels().size(); i++) {
			for(int j = 0; j < e.getGuild().getVoiceChannels().get(i).getMembers().size(); j++) {
				if(e.getGuild().getVoiceChannels().get(i).getMembers().get(j).getUser().getName().equals(e.getAuthor().getName())) {
					return e.getGuild().getVoiceChannels().get(i);
				}
			}
		}
		return null;
	}
}
