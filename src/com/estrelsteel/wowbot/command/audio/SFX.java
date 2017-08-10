package com.estrelsteel.wowbot.command.audio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.file.GameFile;

public class SFX implements Command {
	
	private GameFile file;
	private HashMap<String, String> songs;
	private boolean deprecated;
	private WowAudioCore wac;
	
	public SFX(GameFile file, WowAudioCore core, boolean deprecated) {
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
		this.deprecated = deprecated;
		this.wac = core;
	}
	
	public GameFile getFile() {
		return file;
	}
	
	public boolean isDeprecated() {
		return deprecated;
	}
	
	public void setFile(GameFile file) {
		this.file = file;
	}
	
	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		if(deprecated) {
			e.getTextChannel().sendMessage("The version of this command '~~play~~' has been marked **deprecated**.\n\nUse the **'sfx'** command instead.").queue();
		}
		if(args.length > 1) {
			return true;
		}
		return false;
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
			VoiceChannel c = VoiceHelp.determineChannel(e);
			if(c != null) {
				String song = songs.get(args[1].trim());
				System.out.println(WowBot.getMsgStart() + e.getAuthor().getName() + " attempted to play " + args[1].trim() + " in " + c.getName() + ".");
				if(song != null) {
					wac.loadTrack(song, wac.switchVoiceChannel(c, false), e, true);
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
		return "USAGE: " + WowBot.settings.getTrigger() + "sfx [item | list]"
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
