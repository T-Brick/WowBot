package com.estrelsteel.wowbot.command.audio;

import java.io.File;

import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.audio.queue.QueueReferenceData;
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
	private WowBot wow;
	
	private VoiceChannel c;
	
	public WowAudioCore(WowBot wow) {
		apm = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerLocalSource(apm);
		AudioSourceManagers.registerRemoteSources(apm);
		player = apm.createPlayer();
		queue = new AudioQueue(player, wow);
		this.wow = wow;
	}
	
	public static String getVisibleTitle(WowAudioTrack track) {
		return getVisibleTitle(track.getTrack());
	}
	
	public static String getVisibleTitle(AudioTrack track) {
		if(track == null) {
			return "Loading...";
		}
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
		if(nc != null && (c == null || nc.getIdLong() != c.getIdLong())) {
			VoiceHelp.disconnect(wow.getJDA());
			c = nc.getGuild().getVoiceChannelById(nc.getIdLong());
		}
		if(nc != null && (summon || player.getPlayingTrack() == null)) {
			AudioManager am = nc.getGuild().getAudioManager();
			am.openAudioConnection(nc);
			c = nc.getGuild().getVoiceChannelById(nc.getIdLong());
			return am;
		}
		if(c == null) c = nc.getGuild().getVoiceChannelById(nc.getIdLong());
		AudioManager am = c.getGuild().getAudioManager();
		am.openAudioConnection(c.getGuild().getVoiceChannelById(c.getIdLong()));
		return am;
	}

	public String getStringDuration() {
		return timeToString(queue.getTotalDuration());
	}
	
	public String getUserStringDuration() {
		String msg = getStringDuration();
		if(msg.equals("00")) return "now";
		return "in " + msg;
	}
	
	public String timeToString(long dur) {
		int d = (int) (dur / 1000);
		String time = "";
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
				return time + s + " seconds";
			}
			return time + m + ":" + s;
		}
		return time + h + ":" + m + ":" + s;
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
		loadTrack(file, am, e, msg, 0, -1);
	}
	
	public void loadTrack(String file, AudioManager am, MessageReceivedEvent e, boolean msg, long start, long end) {
		am.setSendingHandler(new AudioPlayerSendHandler(getPlayer()));
		getAudioPlayerManager().loadItem(file, new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack track) {
				track.setPosition(start);
				WowAudioTrack wowtrack = (end >= 0) ? new WowAudioTrack(track, end - start, start) : new WowAudioTrack(track, -1, 0);
				getAudioQueue().queue(wowtrack);
				if(e != null) {
					System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " added an audio track (" + getVisibleTitle(track) + " | " + track.getInfo().uri + "), it will play in " + getUserStringDuration() + ".");
					if(msg) {
//						e.getTextChannel().sendMessage("**" + getVisibleTitle(track) + "** has been added to the queue. It will start " + getUserStringDuration() + ".").complete().delete().queueAfter(30, TimeUnit.SECONDS);
						wow.getAudioOperations().getQueueReset().accept(new QueueReferenceData(e.getTextChannel(), e.getMember().getColor()));
					}
					if(queue.getTextChannel() == null || e.getTextChannel().getIdLong() != queue.getTextChannel().getIdLong()) {
						queue.setTextChannel(e.getTextChannel());
					}
				}
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				for(int i = 0; i < playlist.getTracks().size(); i++) {
					getAudioQueue().queue(new WowAudioTrack(playlist.getTracks().get(i)));
				}
				if(e != null) {
					System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " added a playlist (" + playlist.getTracks().size() + " items), the length is " + getUserStringDuration() + ".");
					if(msg) {
//						e.getTextChannel().sendMessage("Loading " + playlist.getTracks().size() + " items.").complete().delete().queueAfter(30, TimeUnit.SECONDS);
						wow.getAudioOperations().getQueueReset().accept(new QueueReferenceData(e.getTextChannel(), e.getMember().getColor()));
					}
					if((queue.getTextChannel() == null || e.getTextChannel().getIdLong() != queue.getTextChannel().getIdLong())) {
						queue.setTextChannel(e.getTextChannel());
					}
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
