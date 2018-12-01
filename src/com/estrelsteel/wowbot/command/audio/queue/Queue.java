package com.estrelsteel.wowbot.command.audio.queue;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.requests.RequestFuture;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.command.audio.WowAudioCore;

public class Queue implements Command {
	
	private WowAudioCore wac;
	private Message sentMessage;
	private EmbedBuilder sentBuild;
	private ScheduledExecutorService exe;
	private RequestFuture<Message> op;
	
	public Queue(WowAudioCore wac) {
		this.wac = wac;
		this.sentMessage = null;
		this.sentBuild = null;
		this.exe = null;
		this.op = null;
	}
	
	public long getQueueMessageId() {
		if(sentMessage == null) return -1;
		return sentMessage.getIdLong();
	}
	
	private EmbedBuilder generateQueue(EmbedBuilder build) {
		String msg = "\t**Currently playing: " + WowAudioCore.getVisibleTitle(wac.getPlayer().getPlayingTrack()) + "**";
		if(wac.getAudioQueue().getQueue().size() == 0) {
			msg = msg + "";
		}
		else {
			for(int i = 0; i < wac.getAudioQueue().getQueue().size(); i++) {
				msg = msg + "\n" + (i + 1) + ".) " + WowAudioCore.getVisibleTitle(wac.getAudioQueue().getQueue().get(i));
				if(msg.toCharArray().length > 900) { 
					msg = msg + "\n\nAnd " + (wac.getAudioQueue().getQueue().size() - i) + " more.";
					break;
				}
			}
		}
		build.getFields().clear();
		build.addField("Queue:", msg, false);
		if(wac.getAudioQueue().getPlayer().getPlayingTrack() != null) {
			build.setFooter("Time remaining: " + wac.getStringDuration() + " (" + wac.timeToString(wac.getPlayer().getPlayingTrack().getDuration() - wac.getPlayer().getPlayingTrack().getPosition()) + ")", null);
		}
		else {
			build.setFooter("Time remaining: " + wac.getStringDuration(), null);
		}
		
		return build;
	}
	
	public void resetQueue(QueueReferenceData d) {
		sentBuild = generateQueue(new EmbedBuilder().setColor(d.colour));
		if(exe != null) exe.shutdownNow();
		if(sentMessage != null) sentMessage.delete().queue();
		sentMessage = d.tc.sendMessage(sentBuild.build()).complete();
		sentMessage.addReaction("\u2B07").queue();	// LINK
		sentMessage.addReaction("\u23EF").queue();	// PAUSE/PLAY
		sentMessage.addReaction("\u23E9").queue();	// SKIP
		sentMessage.addReaction("\u23ED").queue();	// SKIP ALL
		updateQueue();
		exe = Executors.newSingleThreadScheduledExecutor();
		exe.scheduleAtFixedRate(this::updateQueue, 0, 1250, TimeUnit.MILLISECONDS);
	}
	
	public void updateQueue() {
		if(wac.getAudioQueue().getTotalDuration() <= 0 && wac.getAudioQueue().getQueue().size() <= 0) {
			exe.shutdown();
			sentMessage.delete().complete();
			return;
		}
		sentBuild = generateQueue(sentBuild);
		if(op != null) op.cancel(true);
		op = sentMessage.editMessage(sentBuild.build()).submit();
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		return true;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " requested the audio queue.");
		e.getMessage().delete().queue();
		if(wac.getPlayer().getPlayingTrack() != null) {
			resetQueue(new QueueReferenceData(e.getTextChannel(), e.getMember().getColor()));
			return;
		}
		e.getTextChannel().sendMessage("Nothing is playing.").complete().delete().queueAfter(30, TimeUnit.SECONDS);
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "queue"
				+ "\nDESC: displays the player queue."
				+ "\nPERMS: all";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
}
