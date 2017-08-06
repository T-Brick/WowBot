package com.estrelsteel.wowbot.command.user;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.user.UserHandler;
import com.estrelsteel.wowbot.user.UserSettings;

public class Watch implements Command {
	
	private UserHandler uh;
	
	public Watch(UserHandler uh) {
		this.uh = uh;
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		if(args.length > 1) {
			return true;
		}
		return false;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		UserSettings u = uh.findUser(e.getAuthor().getId());
		String s = "";
		for(int i = 1; i < args.length; i++) {
			s = s + args[i] + " ";
		}
		s = s.trim();
		if(s.substring(0, 1).equalsIgnoreCase("@")) {
			s = s.substring(1);
		}
		if(s.equalsIgnoreCase("list")) {
			String msg = "```You are watching:";
			if(u.getWatching().size() <= 0) {
				msg = "```You aren't watching anyone.";
			}
			for(int i = 0; i < u.getWatching().size(); i++) {
				msg = msg + "\n" + u.getWatching().get(i).getName();
			}
			msg = msg + "```";
			u.sendPrivateMessage(e.getAuthor(), msg, true);
			System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " has requested their watch-list.");
		}
		else if(s.equalsIgnoreCase("clear")) {
			int c = 0;
			for(int i = 0; i < u.getWatching().size(); i++) {
				u.getWatching().remove(i);
				i--;
				c++;
			}
			u.sendPrivateMessage(e.getAuthor(), "You removed " + c + " people from your watch-list", true);
			System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " has cleared their watch-list.");
		}
		else {
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
				if(t.getUser().equals(e.getAuthor())) {
					u.sendPrivateMessage(e.getAuthor(), "You cannot watch yourself.", true);
					System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " attempt to watch themself.");
				}
				else {
					boolean found = false;
					for(User user : u.getWatching()) {
						if(t.getAsMention().equalsIgnoreCase(user.getAsMention())) {
							found = true;
							break;
						}
					}
					if(!found) {
						u.getWatching().add(t.getUser());
						u.sendPrivateMessage(e.getAuthor(), "You are now watching " + t.getEffectiveName() + " and will be notified "
								+ "when they join a voice channel", true);
						System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " added "+ s + " to their watch-list.");
					}
					else {
						u.getWatching().remove(t.getUser());
						u.sendPrivateMessage(e.getAuthor(), "You are no longer watching " + t.getEffectiveName() + "", true);
						System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " removed "+ s + " from their watch-list.");
					}
				}
			}
		}
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "watch [user/list/clear]";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
}
