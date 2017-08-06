package com.estrelsteel.wowbot.command.audio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.file.GameFile;

public class Play implements Command {
	
	private GameFile file;
	private HashMap<String, String> songs;
	private long time;
	
	public Play(GameFile file) {
		songs = new HashMap<String, String>();
		try {
			file.setLines(file.readFile());
			String[] args;
			for(int i = 0; i < file.getLines().size(); i++) {
				args = file.getLines().get(i).split(" ");
				songs.put(args[0], args[1]);
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		this.file = file;
	}
	
	public GameFile getFile() {
		return file;
	}
	
	public void setFile(GameFile file) {
		this.file = file;
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		if(args.length > 1) {
			return true;
		}
		else {
			e.getTextChannel().sendMessage(help()).queue();
			return false;
		}
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		if(args[1].trim().equalsIgnoreCase("list")) {
			ArrayList<String> names = new ArrayList<String>();
			names.addAll(songs.keySet());
			String o = WowBot.convertListToString(names);
			System.out.println(WowBot.getMsgStart() + e.getAuthor().getName() + " requested the list of sounds.");
			e.getTextChannel().sendMessage("Here's the list of sounds:\n```" + o + "```").queue();
		}
		else if(args[1].trim().equalsIgnoreCase("help")) {
			e.getTextChannel().sendMessage(help()).queue();
		}
		else {
			VoiceChannel c = determineChannel(e);
			if(c != null) {
				String song = songs.get(args[1].trim());
				TextChannel t = findHiddenChannel(e);
				System.out.println(WowBot.getMsgStart() + e.getAuthor().getName() + " attempted to play " + args[1].trim() + " in " + c.getName() + ".");
				if(song != null) {
					AudioManager am = e.getGuild().getAudioManager();
					am.openAudioConnection(c);
					time = System.currentTimeMillis();
					while(System.currentTimeMillis() - time < 1000) {}
					t.sendMessage("!play " + song).queue();
					time = System.currentTimeMillis();
					while(System.currentTimeMillis() - time < 500) {}
					am.closeAudioConnection();
					VoiceChannel h = determineHidden(e);
					am.openAudioConnection(h);
					am = null;
				}
				else {
					e.getTextChannel().sendMessage("No such sound exists.").queue();
					return;
				}
				
			}
			else {
				e.getTextChannel().sendMessage("You need to be in a voice channel to use this command.").queue();
			}
		}
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "play [item | list]";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
	
	public TextChannel findHiddenChannel(MessageReceivedEvent e) {
		for(int i = 0; i < e.getGuild().getTextChannels().size(); i++) {
			if(e.getGuild().getTextChannels().get(i).getName().equalsIgnoreCase("admin") || e.getGuild().getTextChannels().get(i).getName().equalsIgnoreCase("bots")) {
				return e.getGuild().getTextChannels().get(i);
			}
		}
		return e.getTextChannel();
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
	
	public VoiceChannel determineHidden(MessageReceivedEvent e) {
		for(int i = 0; i < e.getGuild().getVoiceChannels().size(); i++) {
			if(e.getGuild().getVoiceChannels().get(i).getName().equalsIgnoreCase("Bots")) {
				return e.getGuild().getVoiceChannels().get(i);
			}
		}
		return e.getGuild().getAfkChannel();
	}
	

}
