package com.estrelsteel.wowbot.command.audio;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.TextChannel;

import com.estrelsteel.wowbot.WowBot;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class AudioQueue extends AudioEventAdapter {

	private AudioPlayer p;
	private ArrayList<AudioTrack> queue;
	private TextChannel main;
	private JDA jda;
	
	public AudioQueue(AudioPlayer p, JDA jda) {
		p.addListener(this);
		this.p = p;
		this.queue = new ArrayList<AudioTrack>();
		this.main = null;
		this.jda = jda;
	}
	
	public AudioPlayer getPlayer() {
		return p;
	}
	
	public ArrayList<AudioTrack> getQueue() {
		return queue;
	}
	
	public TextChannel getTextChannel() {
		return main;
	}
	
	public void queue(AudioTrack track) {
		if(!p.startTrack(track, true)) {
			queue.add(track);
		}
		else {
			jda.getPresence().setGame(Game.of("*" + WowAudioCore.getVisibleTitle(track) + "*"));
		}
	}
	
	public void nextTrack() {
		if(queue.size() > 0) {
			if(main != null) {
				main.sendMessage("Now playing **" + WowAudioCore.getVisibleTitle(queue.get(0)) + "**.").complete().delete().queueAfter(queue.get(0).getDuration(), TimeUnit.MILLISECONDS);
			}
			System.out.println(WowBot.getMsgStart() + "Now playing " + WowAudioCore.getVisibleTitle(queue.get(0)) + ".");
			jda.getPresence().setGame(Game.of("*" + WowAudioCore.getVisibleTitle(queue.get(0)) + "*"));
			p.startTrack(queue.remove(0), false);
		}
		else {
			jda.getPresence().setGame(Game.of(WowBot.title));
			p.startTrack(null, false);
		}
	}
	
	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if(endReason.mayStartNext) {
			nextTrack();
		}
	}
	
	public long getTotalDuration() {
		if(p.getPlayingTrack() != null) {
			long dur = p.getPlayingTrack().getDuration();
			for(int i = 0; i < queue.size(); i++) {
				dur = dur + queue.get(i).getDuration();
			}
			return dur;
		}
		return -1l;
	}
	
	public void setPlayer(AudioPlayer p) {
		this.p = p;
	}
	
	public void setQueue(ArrayList<AudioTrack> queue) {
		this.queue = queue;
	}
	
	public void setTextChannel(TextChannel main) {
		this.main = main;
	}
}
