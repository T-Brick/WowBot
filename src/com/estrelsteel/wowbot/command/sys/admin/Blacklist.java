package com.estrelsteel.wowbot.command.sys.admin;

import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;

public class Blacklist implements Command {
	
	private HashMap<Guild, Role> blacklist;
	
	public Blacklist(List<Guild> guilds) {
		blacklist = new HashMap<Guild, Role>();
		boolean add = false;
		for(Guild g : guilds) {
			for(Role r : g.getRoles()) {
				if(r.getName().startsWith("Blacklist")) {
					blacklist.put(g, r);
					add = true;
				}
			}
			if(!add) {
				blacklist.put(g, null);
			}
			add = false;
		}
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		if((e.getGuild().getOwner().equals(e.getMember()) || (e.getMember().getRoles().size() > 0 && e.getMember().getRoles().get(0).getName().equalsIgnoreCase("admin"))) 
				&& args.length >= 1 && e.getTextChannel() != null && blacklist.get(e.getGuild()) != null) {
			return true;
		}
		return false;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		if(e.getGuild().getOwner().equals(e.getMember()) || (e.getMember().getRoles().size() > 0 && e.getMember().getRoles().get(0).getName().equalsIgnoreCase("admin"))) {
			String s = "";
			for(int i = 1; i < args.length; i++) {
				s = s + args[i] + " ";
			}
			s = s.trim();
			if(s.substring(0, 1).equalsIgnoreCase("@")) {
				s = s.substring(1);
			}
			Member t = null;
			for(Member m : e.getGuild().getMembers()) {
				if(m.getEffectiveName().equalsIgnoreCase(s) || (m.getNickname() != null && m.getNickname().equalsIgnoreCase(s))) {
					t = m;
				}
				else {
					for(Role r : m.getRoles()) {
						if(r.getName().equalsIgnoreCase(s)) {
							t = m;
						}
					}
				}
				if(t != null) {
					break;
				}
			}
			if(t != null) {
				if(t.getRoles().size() > 0 && (t.getRoles().get(0).getName().equalsIgnoreCase("Blacklist") || t.getRoles().get(0).getName().equalsIgnoreCase("Blacklist"))) {
					e.getGuild().getController().removeRolesFromMember(t, blacklist.get(e.getGuild())).reason("Admin request").queue();
					System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " removed "+ s + " from blacklist.");
					e.getTextChannel().sendMessage("Removed " + t.getAsMention() + " from blacklist.").queue();
				}
				else {
					e.getGuild().getController().addRolesToMember(t, blacklist.get(e.getGuild())).reason("Admin request").queue();
					System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " added "+ s + " to blacklist.");
					e.getTextChannel().sendMessage("Added " + t.getAsMention() + " to blacklist.").queue();
				}
			}
		}
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "blacklist [user]"
				+ "\nDESC: manages blacklist."
				+ "\n\t[NULL] : puts [user] in blacklist."
				+ "\nPERMS: admin";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
}
