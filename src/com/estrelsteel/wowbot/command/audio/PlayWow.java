package com.estrelsteel.wowbot.command.audio;

import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;
import com.sedmelluq.discord.lavaplayer.demo.jda.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class PlayWow implements Command {
	
	private AudioPlayer p;
	private AudioPlayerManager pm;
	private AudioPlayerSendHandler h;
	private TrackScheduler ts;
	
	public PlayWow() {
		
		pm = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerLocalSource(pm);
		p = pm.createPlayer();
		ts = new TrackScheduler(p);
		p.addListener(ts);
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		return true;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		VoiceChannel c = determineChannel(e);
		if(c != null) {
			AudioManager am = e.getGuild().getAudioManager();
			am.openAudioConnection(c);
			h = new AudioPlayerSendHandler(p);
			pm.loadItem("/Users/justin/Desktop/SERVER/Discord/WowBot/emoteWow.wav", new AudioLoadResultHandler() {
				  @Override
				  public void trackLoaded(AudioTrack track) {
				    ts.queue(track);
				  }

				  @Override
				  public void playlistLoaded(AudioPlaylist playlist) {
				    for (AudioTrack track : playlist.getTracks()) {
				      ts.queue(track);
				    }
				  }

				  @Override
				  public void noMatches() {
				    // Notify the user that we've got nothing
				  }

				  @Override
				  public void loadFailed(FriendlyException throwable) {
				    // Notify the user that everything exploded
				  }
				});
			am.setSendingHandler(h);
		}
		else {
			e.getTextChannel().sendMessage("You need to be in a voice channel to use this command.").queue();
		}
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "playwow";
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
