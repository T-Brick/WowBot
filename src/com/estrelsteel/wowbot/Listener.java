package com.estrelsteel.wowbot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.ReconnectedEvent;
import net.dv8tion.jda.core.events.ResumedEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberNickChangeEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.role.GenericRoleEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import com.estrelsteel.wowbot.command.audio.VoiceHelp;
import com.estrelsteel.wowbot.command.audio.WowAudioTrack;
import com.estrelsteel.wowbot.command.audio.skip.SkipReferenceData;
import com.estrelsteel.wowbot.file.GameFile;
import com.estrelsteel.wowbot.user.UserHandler;

public class Listener extends ListenerAdapter {

	private WowBot bot;
	private UserHandler uh;
	private long outage;
	
	public Listener(WowBot bot, UserHandler uh) {
		this.bot = bot;
		this.uh = uh;
		this.outage = -1;
	}
	
	public void setUserHandler(UserHandler uh) {
		this.uh = uh;
	}
	
	@Override
	public void onDisconnect(DisconnectEvent e) {
		System.out.println(WowBot.getMsgStart() + "Disconnected from Discord.");
		outage = System.currentTimeMillis();
	}
	
	@Override
	public void onReconnect(ReconnectedEvent e) {
		System.out.println(WowBot.getMsgStart() + "Reconnected to Discord.");
		System.err.println(WowBot.getMsgStart() + "Outage lasted for " + (System.currentTimeMillis() - outage) + "ms");
	}
	
	@Override
	public void onResume(ResumedEvent e) {
		System.out.println(WowBot.getMsgStart() + "Resumed connection to Discord.");
		System.err.println(WowBot.getMsgStart() + "Outage lasted for " + (System.currentTimeMillis() - outage) + "ms");
	}

	@Override
    public void onMessageReactionAdd(MessageReactionAddEvent e) {
    	if(e.getMember().getUser().getIdLong() == WowBot.id) return;
    	try {
			if(e.getMessageIdLong() == bot.getAudioOperations().getQueueMessageId().call()) {
				if(e.getReactionEmote().getName().equals("\u23EF")) { // PAUSE PLAY
					bot.getAudioCore().getPlayer().setPaused(!bot.getAudioCore().getPlayer().isPaused());
				}
				else if(e.getReactionEmote().getName().equals("\u23ED")) { // SKIP ALL
					bot.getAudioCore().getAudioQueue().setQueue(new ArrayList<WowAudioTrack>());
					bot.getAudioOperations().getSkip().accept(new SkipReferenceData(e.getTextChannel(), VoiceHelp.determineChannel(e.getGuild(), e.getUser().getIdLong()), e.getUser()));
				}
				else if(e.getReactionEmote().getName().equals("\u23E9")) { // SKIP
					bot.getAudioOperations().getSkip().accept(new SkipReferenceData(e.getTextChannel(), VoiceHelp.determineChannel(e.getGuild(), e.getUser().getIdLong()), e.getUser()));
				}
				else if(e.getReactionEmote().getName().equals("\u2B07")) { // LINK
					String link = bot.getAudioCore().getPlayer().getPlayingTrack().getInfo().uri;
					if(link.startsWith("http")) {
						e.getTextChannel().sendMessage("Currently Playing: **" +  bot.getAudioCore().getPlayer().getPlayingTrack().getInfo().title + "** (" + link + ")").complete().delete().queueAfter(30, TimeUnit.SECONDS);
					}
					else e.getTextChannel().sendMessage("Sorry, the link is unavaliable").complete().delete().queueAfter(10, TimeUnit.SECONDS);
				}
				e.getReaction().removeReaction(e.getUser()).queue();
				
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    }
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if(e.getMessage().getAuthor().getId() == e.getJDA().getSelfUser().getId()) {
//			bot.msg.add(e.getMessage());
		}
		else if(e.getMessage().getContentRaw().startsWith(WowBot.settings.getTrigger())) {
			bot.handleCommand(bot.getParser().parse(e.getMessage().getContentRaw(), e));
		}
	}
	
	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
		if(uh != null) {
			for(int i = 0; i < uh.getUsers().size(); i++) {
				for(int j = 0; j < uh.getUsers().get(i).getWatching().size(); j++) {
					if(uh.getUsers().get(i).getWatching().get(j).getIdLong() == e.getMember().getUser().getIdLong()) {
						uh.getUsers().get(i).sendPrivateMessage(e.getGuild().getMemberById(uh.getUsers().get(i).getID()).getUser(), 
								e.getMember().getEffectiveName() + " has joined the '" + e.getChannelJoined().getName()
								+ "' voice channel in the '" + e.getGuild().getName() + "' guild.", true);
						System.out.println(WowBot.getMsgStart() + "" + e.getGuild().getMemberById(uh.getUsers().get(i).getID()).getEffectiveName()
								+ " has been notified that "+ uh.getUsers().get(i).getWatching().get(j).getName() + " joined a voice channel.");
						uh.getUsers().get(i).getWatching().remove(j);
						j--;
					}
				}
			}
		}
		if(e.getMember().getUser().getIdLong() == WowBot.id) {
			bot.getAudioCore().setVoiceChannel(e.getChannelJoined());
		}
	}
	
	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
		for(int i = 0; i < e.getMember().getRoles().size(); i++) {
			if(e.getMember().getRoles().get(i).getName().startsWith("Team ")) {
				e.getGuild().getDefaultChannel().sendMessage(e.getMember().getAsMention() + " has left " + e.getMember().getRoles().get(i).getName() + ".").queue();
				e.getGuild().getController().removeSingleRoleFromMember(e.getMember(), e.getMember().getRoles().get(i)).queue();
			}
		}
		if(e.getMember().getUser().getIdLong() == WowBot.id) {
			bot.getAudioCore().setVoiceChannel(null);
		}
	}
	
	@Override
	public void onGuildMemberNickChange(GuildMemberNickChangeEvent e) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(e.getMember().getColor());
		
		if(e.getNewNick() != null && e.getPrevNick() != null) {
			builder.addField("", e.getPrevNick() + " has changed their nickname to " + e.getUser().getAsMention(), false);
			e.getGuild().getTextChannels().get(0).sendMessage(builder.build()).queue();
			System.out.println(WowBot.getMsgStart() + "" + e.getUser().getAsMention() + " has changed their nickname from " + e.getPrevNick() + " to " + e.getNewNick());
		}
		else if(e.getNewNick() != null && e.getPrevNick() == null) {
			builder.addField("", e.getUser().getAsMention() + " has made " + e.getNewNick() + " their nickname.", false);
			e.getGuild().getTextChannels().get(0).sendMessage(builder.build()).queue();
			System.out.println(WowBot.getMsgStart() + "" + e.getUser().getAsMention() + " has made " + e.getNewNick() + " their nickname.");
		}
		else if(e.getNewNick() == null && e.getPrevNick() != null) {
			builder.addField("", e.getUser().getAsMention() + " has removed their nickname, " + e.getPrevNick() + ".", false);
			e.getGuild().getTextChannels().get(0).sendMessage(builder.build()).queue();
			System.out.println(WowBot.getMsgStart() + "" + e.getUser().getAsMention() + " has removed their nickname, " + e.getPrevNick() + ".");
		}
	}
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent e) {
		if(!e.getUser().isBot()) {
			try {
				GameFile file = new GameFile(WowBot.path + "/users_left/" + e.getGuild().getIdLong() + " " + e.getUser().getIdLong() + ".txt");
				file.setLines(file.readFile());
				if(e.getUser().getId().equals(file.getLines().get(0).trim())) {
					e.getGuild().getTextChannels().get(0).sendMessage(e.getMember().getAsMention() + " has re-joined the server.").queue();
					e.getGuild().getController().setNickname(e.getMember(), file.getLines().get(1).trim());
					for(int i = 3; i < file.getLines().size(); i++) {
						if(file.getLines().get(i).trim().equalsIgnoreCase("###")) {
							break;
						}
						e.getGuild().getController().addRolesToMember(e.getMember(), e.getGuild().getRoleById(file.getLines().get(i).trim()));
					}
					return;
				}
			}
			catch(IOException e1) {}
			e.getGuild().getTextChannels().get(0).sendMessage(e.getMember().getAsMention() + " has joined the server.").queue();
		}
	}
	
	@Override
	public void onGuildMemberLeave(GuildMemberLeaveEvent e) {
		if(!e.getUser().isBot()) {
			GameFile file = new GameFile(WowBot.path + "/users_left/" + e.getGuild().getIdLong() + " " + e.getUser().getIdLong() + ".txt");
			ArrayList<String> lines = new ArrayList<String>();
			System.out.println(WowBot.getMsgStart() + "" + e.getMember().getAsMention() + " left the server. Backing up their data..");
			lines.add(e.getUser().getId());
			lines.add(e.getMember().getNickname());
			lines.add("###");
			for(int i = 0; i < e.getMember().getRoles().size(); i++) {
				lines.add(e.getMember().getRoles().get(i).getId());
			}
			lines.add("###");
			try {
				file.saveFile();
			} 
			catch (IOException e1) {
				e1.printStackTrace();
			}
			e.getGuild().getTextChannels().get(0).sendMessage(e.getMember().getAsMention() + " has left the server.").queue();
		}
	}
	
	@Override
	public void onGenericRole(GenericRoleEvent e) {
		bot.updateRoles(e.getGuild());
	}
//	@Override
//	public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent e) {
//		bot.updateRoles(e.getGuild());
//	}
//	@Override
//	public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent e) {
//		bot.updateRoles(e.getGuild());
//	}
}
