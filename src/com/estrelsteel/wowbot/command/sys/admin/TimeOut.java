package com.estrelsteel.wowbot.command.sys.admin;

import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;

public class TimeOut implements Command {
	
	private HashMap<Guild, Role> timeout;
	
	public TimeOut(List<Guild> guilds) {
		timeout = new HashMap<Guild, Role>();
		boolean add = false;
		for(Guild g : guilds) {
			for(Role r : g.getRoles()) {
				if(r.getName().equalsIgnoreCase("In-Timeout") || r.getName().equalsIgnoreCase("Timeout")) {
					timeout.put(g, r);
					add = true;
				}
			}
			if(!add) {
				timeout.put(g, null);
			}
			add = false;
		}
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		if(e.getGuild().getOwner().equals(e.getMember()) && args.length >= 1 && e.getTextChannel() != null && timeout.get(e.getGuild()) != null) {
			return true;
		}
		return false;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		if(e.getGuild().getOwner().equals(e.getMember())) {
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
				if(t.getRoles().size() > 0 && (t.getRoles().get(0).getName().equalsIgnoreCase("In-Timeout") || t.getRoles().get(0).getName().equalsIgnoreCase("Timeout"))) {
					e.getGuild().getController().removeRolesFromMember(t, timeout.get(e.getGuild())).reason("Admin request").queue();
					System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " removed "+ s + " from time out.");
					e.getTextChannel().sendMessage("Removed " + t.getAsMention() + " from timeout.").queue();
				}
				else {
					e.getGuild().getController().addRolesToMember(t, timeout.get(e.getGuild())).reason("Admin request").queue();
					System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " added "+ s + " to time out.");
					e.getTextChannel().sendMessage("Added " + t.getAsMention() + " to timeout.").queue();
				}
			}
		}
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "timeout [user]"
				+ "\nDESC: manages timeout."
				+ "\n\t[NULL] : puts [user] in timeout."
				+ "\nPERMS: admin";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
}
