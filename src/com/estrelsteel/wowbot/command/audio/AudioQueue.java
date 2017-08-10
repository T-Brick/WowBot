package com.estrelsteel.wowbot.command.audio;

import java.util.ArrayList;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class AudioQueue extends AudioEventAdapter {

	private AudioPlayer p;
	private ArrayList<AudioTrack> queue;
	
	public AudioQueue(AudioPlayer p) {
		p.addListener(this);
		this.p = p;
		this.queue = new ArrayList<AudioTrack>();
	}
	
	public AudioPlayer getPlayer() {
		return p;
	}
	
	public ArrayList<AudioTrack> getQueue() {
		return queue;
	}
	
	public void queue(AudioTrack track) {
		if(!p.startTrack(track, true)) {
			queue.add(track);
		}
	}
	
	public void nextTrack() {
		if(queue.size() > 0) {
			p.startTrack(queue.remove(0), false);
		}
		else {
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
}
