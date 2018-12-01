package com.estrelsteel.wowbot.command.audio;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class WowAudioTrack {

	private AudioTrack track;
	private long duration;
	private long start;
	private ScheduledExecutorService exe;
	
	public WowAudioTrack(AudioTrack track, long duration, long start) {
		this.track = track;
		this.duration = duration;
		this.start = start;
		this.exe = null;
		if(hasEndTime()) this.exe = Executors.newSingleThreadScheduledExecutor();
	}
	
	public WowAudioTrack(AudioTrack track) {
		this(track, -1, 0);
	}
	
	
	public AudioTrack getTrack() {
		return track;
	}
	
	public long getDuration() {
		if(hasEndTime()) return duration + start;
		return track.getDuration();
	}
	
	public boolean hasEndTime() {
		return duration >= 0;
	}
	
	public void dequeue() {
		track.setPosition(track.getDuration());
	}
	
	public WowAudioTrack startTimedDequeuer() {
		if(!hasEndTime()) return this;
		assert(exe != null);
		exe.schedule(this::dequeue, duration, TimeUnit.MILLISECONDS);
		return this;
	}
	
}
