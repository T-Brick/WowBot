package com.estrelsteel.wowbot.command.sys;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.user.UserHandler;
import com.estrelsteel.wowbot.user.UserSettings;

public class WhoIs implements Command {
	
	private UserHandler uh;
	public static String header = "+----------------------------------------------+"; /* 48 */
	
	public WhoIs(UserHandler uh) {
		this.uh = uh;
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		if(args.length > 1 && e.getTextChannel() != null) {
//			e.getTextChannel().sendMessage(help()).queue();
			return true;
		}
		return false;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		String s = "";
		for(int i = 1; i < args.length; i++) {
			s = s + args[i] + " ";
		}
		s = s.trim();
		if(s.substring(0, 1).equalsIgnoreCase("@")) {
			s = s.substring(1);
		}
		System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " is requesting " + s + "'s info.");
		String msg = "";
		String u_msg = "";
		UserSettings u = null;
		int rval = 0;
		for(Member m : e.getGuild().getMembers()) {
			if(m.getEffectiveName().equalsIgnoreCase(s) || (m.getNickname() != null && m.getNickname().equalsIgnoreCase(s))) {
				if(m.getRoles().size() > 0) {
					if(msg != "") {
						msg = msg + "\n" + header + "\n";
					}
					if(m.getRoles().get(rval).getName().equalsIgnoreCase("In-Timeout") || m.getRoles().get(rval).getName().equalsIgnoreCase("Timeout")) {
						rval = 1;
					}
					u_msg = "" + m.getAsMention() + " is " + m.getRoles().get(rval).getAsMention();
					rval = 0;
				}
				else {
					u_msg = "" + m.getAsMention();
				}
			}
			else {
				for(Role r : m.getRoles()) {
					if(r.getName().equalsIgnoreCase(s)) {
						if(!msg.equalsIgnoreCase("")) {
							msg = msg + "\n" + header + "\n";
						}
						u_msg = "" + r.getAsMention() + " is " + m.getAsMention();
					}
				}
			}
			if(!u_msg.equalsIgnoreCase("")) {
				u = uh.findUser(m.getUser().getIdLong());
				u_msg = u_msg + "\n\t" + "quiet: " + u.isQuiet();
				u_msg = u_msg + "\n\t" + "quiet_start: " + u.getQuietStart();
				u_msg = u_msg + "\n\t" + "quiet_length: " + u.getQuietLength();
				u_msg = u_msg + "\n\t" + "mute: " + u.isMute();
				u_msg = u_msg + "\n\t" + "audio: " + u.getMusicRules().toString();
				msg = msg + u_msg;
				msg = msg + "";
				u_msg = "";
				
			}
		}
		if(msg.equalsIgnoreCase("")) {
			msg = "No user found.";
		}
		e.getTextChannel().sendMessage(msg).queue();
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "whois [user]"
				+ "\nDESC: gets user info."
				+ "\n\t{NULL} : gets info about [user]."
				+ "\nPERMS: all";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
	

}
