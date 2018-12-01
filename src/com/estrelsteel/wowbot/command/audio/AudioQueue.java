package com.estrelsteel.wowbot.command.audio;

import java.awt.Color;
import java.util.ArrayList;

import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Game.GameType;
import net.dv8tion.jda.core.entities.TextChannel;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.audio.queue.QueueReferenceData;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class AudioQueue extends AudioEventAdapter {

	private AudioPlayer p;
	private ArrayList<WowAudioTrack> queue;
	private WowAudioTrack currentlyRunning;
	private TextChannel main;
	private WowBot wow;
	
	public AudioQueue(AudioPlayer p, WowBot wow) {
		p.addListener(this);
		this.p = p;
		this.queue = new ArrayList<WowAudioTrack>();
		this.main = null;
		this.wow = wow;
	}
	
	public AudioPlayer getPlayer() {
		return p;
	}
	
	public ArrayList<WowAudioTrack> getQueue() {
		return queue;
	}
	
	public TextChannel getTextChannel() {
		return main;
	}
	
	public Color getTrackColor(String title) {
		if(title.length() < 3) return new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
		int r = 0;
		int g = 0;
		int b = 0;
		for(int i = 0; i < title.length() - 3; i = i + 3) {
			r = r + title.charAt(i);
			g = g + title.charAt(i + 1);
			b = b + title.charAt(i + 2);
		}
		return new Color(r % 256, g % 256, b % 256);
	}
	
	public void queue(WowAudioTrack track) {
		if(!p.startTrack(track.getTrack(), true)) {
			queue.add(track);
		}
		else {
			track.startTimedDequeuer();
			currentlyRunning = track;
			if(WowBot.settings.doDisplayMusic()) wow.getJDA().getPresence().setGame(Game.of(GameType.LISTENING, "*" + WowAudioCore.getVisibleTitle(track) + "*"));
		}
	}
	
	public void nextTrack() {
		if(queue.size() > 0) {
			currentlyRunning = queue.remove(0);
			if(main != null) {
//				main.sendMessage("Now playing **" + WowAudioCore.getVisibleTitle(currentlyRunning) + "**.").complete().delete().queueAfter(currentlyRunning.getDuration(), TimeUnit.MILLISECONDS);
				wow.getAudioOperations().getQueueReset().accept(new QueueReferenceData(main, getTrackColor(WowAudioCore.getVisibleTitle(currentlyRunning))));
//				wow.getQueueCommand().resetQueue();
			}
			System.out.println(WowBot.getMsgStart() + "Now playing " + WowAudioCore.getVisibleTitle(currentlyRunning) + ".");
			if(WowBot.settings.doDisplayMusic()) wow.getJDA().getPresence().setGame(Game.of(GameType.LISTENING, "*" + WowAudioCore.getVisibleTitle(currentlyRunning) + "*"));
			currentlyRunning.startTimedDequeuer();
			p.startTrack(currentlyRunning.getTrack(), false);
			
		}
		else {
			if(WowBot.settings.doDisplayMusic()) wow.getJDA().getPresence().setGame(Game.of(GameType.DEFAULT, WowBot.title));
			p.startTrack(null, false);
			currentlyRunning = null;
		}
	}
	
	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if(endReason.mayStartNext) {
			nextTrack();
		}
	}
	
	public long getTotalDuration() {
		long dur = -1l;
		if(p.getPlayingTrack() != null) {
			dur++;
			if(currentlyRunning != null) dur = currentlyRunning.getDuration() - currentlyRunning.getTrack().getPosition();
			else dur = p.getPlayingTrack().getDuration() - p.getPlayingTrack().getPosition();
			for(int i = 0; i < queue.size(); i++) {
				dur = dur + queue.get(i).getDuration();
			}
		}
		return dur;
	}
	
	public void setPlayer(AudioPlayer p) {
		this.p = p;
	}
	
	public void setQueue(ArrayList<WowAudioTrack> queue) {
		this.queue = queue;
	}
	
	public void setTextChannel(TextChannel main) {
		this.main = main;
	}
}
