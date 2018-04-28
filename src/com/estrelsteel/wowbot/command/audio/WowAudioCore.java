package com.estrelsteel.wowbot.command.audio;

import java.io.File;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

import com.estrelsteel.wowbot.WowBot;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class WowAudioCore {

	private AudioPlayerManager apm;
	private AudioPlayer player;
	private AudioQueue queue;
	
	private VoiceChannel c;
	
	public WowAudioCore(JDA jda) {
		apm = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerLocalSource(apm);
		AudioSourceManagers.registerRemoteSources(apm);
		player = apm.createPlayer();
		queue = new AudioQueue(player, jda);
	}
	
	public static String getVisibleTitle(AudioTrack track) {
		String title = track.getInfo().title;
		if(title.equalsIgnoreCase("Unknown title")) {
			title = new File(track.getInfo().uri).getName();
			title = title.split(".wav")[0];
			title = title.replaceAll("_", " ");
			String[] title_words = title.split(" ");
			title = "";
			for(int i = 0; i < title_words.length; i++) {
				if(title_words[i].length() > 1) {
					title = title + title_words[i].substring(0, 1).toUpperCase();
					title = title + title_words[i].substring(1) + " ";
				}
				else {
					title = title + title_words[i].toUpperCase() + " ";
				}
			}
			title = title.trim();
		}
		return title;
	}
	
	public AudioPlayerManager getAudioPlayerManager() {
		return apm;
	}
	
	public AudioPlayer getPlayer() {
		return player;
	}
	
	public AudioQueue getAudioQueue() {
		return queue;
	}
	
	public VoiceChannel getVoiceChannel() {
		return c;
	}
	
	public AudioManager switchVoiceChannel(VoiceChannel nc, boolean summon) {
		if(nc != null && (summon || player.getPlayingTrack() == null)) {
			AudioManager am = nc.getGuild().getAudioManager();
			am.openAudioConnection(nc);
			c = nc;
			return am;
		}
		AudioManager am = c.getGuild().getAudioManager();
		am.openAudioConnection(c);
		return am;
	}
	
	public String getStringDuration() {
		return timeToString(queue.getTotalDuration());
	}
	
	public String timeToString(long dur) {
		int d = (int) (dur / 1000);
		String time = "in ";
		String h = (d / 3600) + "";
		String m = (d % 3600 / 60) + "";
		String s = (d % 3600 % 60) + "";
		if(m.length() < 2) {
			m = "0" + m;
		}
		if(s.length() < 2) {
			s = "0" + s;
		}
		if(d / 3600 == 0) {
			if(d % 3600 / 60 == 0) {
				if(d % 3600 % 60 == 0) {
					time = "now";
				}
				else {
					time = time + s + " seconds";
				}
			}
			else {
				time = time + m + ":" + s;
			}
		}
		else {
			time = time + h + ":" + m + ":" + s;
		}
		return time;
	}
	
	public void setAudioPlayerManager(AudioPlayerManager apm) {
		this.apm = apm;
	}
	
	public void setAudioPlayer(AudioPlayer player) {
		this.player = player;
	}
	
	public void setAudioQueue(AudioQueue queue) {
		this.queue = queue;
	}
	
	public void setVoiceChannel(VoiceChannel c) {
		this.c = c;
	}
	
	public void loadTrack(String file, AudioManager am, MessageReceivedEvent e, boolean msg) {
		am.setSendingHandler(new AudioPlayerSendHandler(getPlayer()));
		getAudioPlayerManager().loadItem(file, new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack track) {
				if(e != null) {
					System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " added an audio track (" + getVisibleTitle(track) + " | " + track.getInfo().uri + "), it will play " + getStringDuration() + ".");
					if(msg) {
						e.getTextChannel().sendMessage("**" + getVisibleTitle(track) + "** has been added to the queue. It will start " + getStringDuration() + ".").complete().delete().queueAfter(30, TimeUnit.SECONDS);
					}
					if(queue.getTextChannel() == null || e.getTextChannel().getIdLong() != queue.getTextChannel().getIdLong()) {
						queue.setTextChannel(e.getTextChannel());
					}
				}
				getAudioQueue().queue(track);
				
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				if(e != null) {
					System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " added a playlist (" + playlist.getTracks().size() + " items), it will play " + getStringDuration() + ".");
					if(msg) {
						e.getTextChannel().sendMessage("Loading " + playlist.getTracks().size() + " items.").complete().delete().queueAfter(30, TimeUnit.SECONDS);
					}
					if((queue.getTextChannel() == null || e.getTextChannel().getIdLong() != queue.getTextChannel().getIdLong())) {
						queue.setTextChannel(e.getTextChannel());
					}
				}
				for(int i = 0; i < playlist.getTracks().size(); i++) {
					getAudioQueue().queue(playlist.getTracks().get(i));
				}
			}

			@Override
			public void noMatches() {
				System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " requested a track that doesn't exist.");
				if(msg && e != null) {
					e.getTextChannel().sendMessage("Could not find this item.").queue();
				}
			}

			@Override
			public void loadFailed(FriendlyException exception) {
				exception.printStackTrace();
			}
		});
	}
}
