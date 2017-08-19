package com.estrelsteel.wowbot.command.user;

import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.user.UserHandler;
import com.estrelsteel.wowbot.user.UserSettings;

public class Assemble implements Command {
	
	private UserHandler uh;
	
	public Assemble(UserHandler uh) {
		this.uh = uh;
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		if(e.getTextChannel() != null) {
			return true;
		}
		return false;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		if(args.length > 1) {
			String s = "";
			for(int i = 1; i < args.length; i++) {
				s = s + args[i] + " ";
			}
			s = s.trim();
			if(s.substring(0, 1).equalsIgnoreCase("@")) {
				s = s.substring(1);
			}
			System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " is assembling the " + s + ".");
			int r = -1;
			for(int i = 0; i < e.getGuild().getRoles().size(); i++) {
				if(e.getGuild().getRoles().get(i).getName().equalsIgnoreCase(s)) {
					r = i;
					break;
				}
			}
			if(r >= 0) {
				String msg = "Assembling " + s + "...";
				UserSettings u = null;
				for(Member m : e.getGuild().getMembers()) {
					if(m.getOnlineStatus() != OnlineStatus.ONLINE) {
						u = uh.findUser(m.getUser().getIdLong());
						if(!u.isQuiet()) {
							for(Role ro : m.getRoles()) {
								if(ro.equals(e.getGuild().getRoles().get(r))) {
									msg = msg + "\n" + m.getRoles().get(0).getAsMention() + " assemble!";
								}
							}
						}
					}
				}
				if(msg.split("\n").length == 1) {
					e.getTextChannel().sendMessage("Everyone in " + s + " is already online.").queue();
				}
				else {
					e.getTextChannel().sendMessage(msg).queue();
				}
			}
			else {
				System.out.println(WowBot.getMsgStart() + "Unknown group: " + s + "");
			}
		}
		else {
			e.getTextChannel().sendMessage(help()).queue();
		}
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "assemble [group]"
				+ "\nDESC: gathers a group."
				+ "\n\t[group] : messages everyone in [group] who is offline."
				+ "\nPERMS: all";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
	

}
