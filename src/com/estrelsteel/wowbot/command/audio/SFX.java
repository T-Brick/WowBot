package com.estrelsteel.wowbot.command.audio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.file.GameFile;
import com.estrelsteel.wowbot.user.UserHandler;
import com.estrelsteel.wowbot.user.UserSettings;

public class SFX implements Command {
	
	private GameFile file;
	private HashMap<String, String> songs;
	private UserHandler uh;
	private WowAudioCore wac;
	
	public SFX(GameFile file, WowAudioCore core, UserHandler uh) {
		this.file = file;
		this.wac = core;
		this.uh = uh;
		
		try {
			loadSoundlist();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadSoundlist() throws IOException {
		songs = new HashMap<String, String>();
		file.setLines(file.readFile());
		String[] args;
		for(int i = 0; i < file.getLines().size(); i++) {
			args = file.getLines().get(i).split(" ");
			songs.put(args[0], args[1]);
		}
	}
	
	public void saveSoundlist() throws IOException{
		file.setLines(new ArrayList<String>());
		for(Entry<String, String> s : songs.entrySet())  {
			file.getLines().add(s.getKey() + " " + s.getValue());
		}
		file.saveFile();
	}
	
	public GameFile getFile() {
		return file;
	}
	
	public HashMap<String, String> getSoundlist() {
		return songs;
	}
	
	public void setFile(GameFile file) {
		this.file = file;
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		if(args.length > 1) {
			UserSettings us = uh.findUser(e.getAuthor().getIdLong());
			if(us != null && us.getMusicRules()[1]) {
				return true;
			}
			else {
				e.getTextChannel().sendMessage("You do not have the permissions to do this.").queue();
			}
		}
		return false;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		VoiceChannel c = VoiceHelp.determineChannel(e);
		if(c != null) {
			String request = "";
			boolean find = false;
			int arg = 1;
			String msg;
			e.getMessage().delete().queueAfter(30, TimeUnit.SECONDS);
			if(args[1].trim().equalsIgnoreCase("find")) {
				arg++;
				find = true;
			}
			else if(args[1].trim().equalsIgnoreCase("list")) {
				ArrayList<String> names = new ArrayList<String>();
				names.addAll(songs.keySet());
				String o = WowBot.convertListToString(names);
				System.out.println(WowBot.getMsgStart() + e.getAuthor().getName() + " requested the list of sounds.");
				EmbedBuilder builder = new EmbedBuilder();
				builder.setColor(e.getMember().getColor());
				builder.addField("Sound Effects:", o, false);
				builder.setFooter("Type in the beginning of a sound effect to search for it.", e.getGuild().getMemberById(WowBot.id).getUser().getAvatarUrl());
				e.getTextChannel().sendMessage(builder.build()).queue();
				return;
			}
			else if(args[1].trim().equalsIgnoreCase("add") || args[1].trim().equalsIgnoreCase("+")) {
				if(args.length >= 4) {
					System.out.println(WowBot.getMsgStart() + e.getAuthor().getName() + " is adding sfx " + args[2] + " ( " + args[3] + " )");
					if(songs.containsKey(args[2].toLowerCase())) {
						e.getTextChannel().sendMessage("This label is already in use.").queue();
						return;
					}
					if(songs.containsValue(args[3])) {
						e.getTextChannel().sendMessage("This audio file is already in use.").queue();
						return;
					}
					
					songs.put(args[2].toLowerCase(), args[3]);
					try {
						saveSoundlist();
					}
					catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				else {
					e.getTextChannel().sendMessage(help()).queue();
				}
				return;
			}
			else if(args[1].trim().equalsIgnoreCase("reload")) {
				System.out.println(WowBot.getMsgStart() + e.getAuthor().getName() + " requested a reload of the sound list.");
				try {
					loadSoundlist();
				} 
				catch (IOException e1) {
					e1.printStackTrace();
				}
				return;
			}
			request = args[arg].trim().toLowerCase();
			if(!find) {
				String song = songs.get(request);
				if(song != null) {
					wac.loadTrack(song, wac.switchVoiceChannel(c, false), e, true);
					System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " has requested the '" + request + "' sfx.");
				}
				else {
					System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " requested the '" + args[1].trim() + "' sfx, but it doesn't exist, searching for it.");
					ArrayList<String> match = search(request);
					msg = WowBot.convertListToString(match);
					if(match.size() == 0) {
						UserHandler.sendPublicMessage("That sound does not exist.", e, true);
					}
					else {
						EmbedBuilder builder = new EmbedBuilder();
						builder.setColor(e.getMember().getColor());
						builder.addField("That sound does not exist... But could you mean:", msg, false);
						builder.setFooter("Type in the beginning of a sound effect to search for it.", e.getGuild().getMemberById(WowBot.id).getUser().getAvatarUrl());
						e.getTextChannel().sendMessage(builder.build()).queue();
//						UserHandler.sendPublicMessage("That sound does not exist... But could you mean:\n```" + msg + ".```", e, true);
					}
				}
			}
			else {
				System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " is requesting a sfx with " + request + "in it's name.");
				ArrayList<String> match = search(request);
				msg = WowBot.convertListToString(match);
				if(match.size() == 0) {
					UserHandler.sendPublicMessage("Could not find any matches.", e, true);
				}
				else if(match.size() == 1) {
					UserHandler.sendPublicMessage("1 match found: \n```" + msg + "```.", e, true);
				}
				else {
					UserHandler.sendPublicMessage(match.size() + " matches found: \n```" + msg + ".```", e, true);
				}
			}
		}
		else {
			e.getTextChannel().sendMessage("You need to be in a voice channel to use this command.").queue();
		}
	}
	
	private ArrayList<String> search(String request) {
		ArrayList<String> match = new ArrayList<String>();
		for(String key : songs.keySet()) {
			if(key.contains(request.subSequence(0, request.length()))) {
				match.add(key);
			}
		}
		System.out.println(WowBot.getMsgStart() + "\tfound " + match.size() + " match(es).");
		return match;
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "sfx [item | list | (add [label] [link]) | reload]"
				+ "\nDESC: plays a sound effect."
				+ "\n\t[item] : plays the audio file with the name [item]."
				+ "\n\t[list] : sends the user a list of all the sound effects avaliable."
				+ "\nPERMS: all";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
}
