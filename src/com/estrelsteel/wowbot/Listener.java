package com.estrelsteel.wowbot;

import java.io.IOException;
import java.util.ArrayList;

import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberNickChangeEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import com.estrelsteel.wowbot.file.GameFile;
import com.estrelsteel.wowbot.user.UserHandler;

public class Listener extends ListenerAdapter {

	private WowBot bot;
	private UserHandler uh;
	
	public Listener(WowBot bot, UserHandler uh) {
		this.bot = bot;
		this.uh = uh;
	}
	
	public void setUserHandler(UserHandler uh) {
		this.uh = uh;
		
	}
	
	public void onMessageReceived(MessageReceivedEvent e) {
		if(e.getMessage().getAuthor().getId() == e.getJDA().getSelfUser().getId()) {
//			bot.msg.add(e.getMessage());
		}
		else if(e.getMessage().getContent().startsWith(WowBot.settings.getTrigger())) {
			bot.handleCommand(bot.getParser().parse(e.getMessage().getContent(), e));
		}
	}
	
	public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
		if(uh != null) {
			for(int i = 0; i < uh.getUsers().size(); i++) {
				for(int j = 0; j < uh.getUsers().get(i).getWatching().size(); j++) {
					if(uh.getUsers().get(i).getWatching().get(j).equals(e.getMember().getUser())) {
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
	}
	
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
		for(int i = 0; i < e.getMember().getRoles().size(); i++) {
			if(e.getMember().getRoles().get(i).getName().startsWith("Team ")) {
				e.getGuild().getPublicChannel().sendMessage(e.getMember().getAsMention() + " has left " + e.getMember().getRoles().get(i).getName() + ".").queue();
				e.getGuild().getController().removeSingleRoleFromMember(e.getMember(), e.getMember().getRoles().get(i)).queue();
			}
		}
	}
	
	public void onGuildMemberNickChange(GuildMemberNickChangeEvent e) {
		if(e.getNewNick() != null && e.getPrevNick() != null) {
			e.getGuild().getTextChannels().get(0).sendMessage(e.getMember().getAsMention() + " has changed their nickname from " + e.getPrevNick() + " to " + e.getNewNick()).queue();
			System.out.println(WowBot.getMsgStart() + "" + e.getMember().getAsMention() + " has changed their nickname from " + e.getPrevNick() + " to " + e.getNewNick());
		}
		else if(e.getNewNick() != null && e.getPrevNick() == null) {
			e.getGuild().getTextChannels().get(0).sendMessage(e.getMember().getAsMention() + " has made " + e.getNewNick() + " their nickname.").queue();
			System.out.println(WowBot.getMsgStart() + "" + e.getMember().getAsMention() + " has made " + e.getNewNick() + " their nickname.");
		}
		else if(e.getNewNick() == null && e.getPrevNick() != null) {
			e.getGuild().getTextChannels().get(0).sendMessage(e.getMember().getAsMention() + " has removed their nickname, " + e.getPrevNick()+ ".").queue();
			System.out.println(WowBot.getMsgStart() + "" + e.getMember().getAsMention() + " has removed their nickname, " + e.getPrevNick() + ".");
		}
	}
	
	public void onGuildMemberJoin(GuildMemberJoinEvent e) {
		if(!e.getUser().isBot()) {
			try {
				GameFile file = new GameFile(WowBot.path + "/users_left/" + e.getUser().getId() + ".txt");
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
	
	public void onGuildMemberLeave(GuildMemberJoinEvent e) {
		if(!e.getUser().isBot()) {
			GameFile file = new GameFile(WowBot.path + "/users_left/" + e.getUser().getId() + ".txt");
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
	
	public void onReady(ReadyEvent e) {
//		bot.log("status", "Logged in as: " + e.getJDA().getSelfUser().getName());
	}
}
