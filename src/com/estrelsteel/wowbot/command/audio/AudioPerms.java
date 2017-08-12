package com.estrelsteel.wowbot.command.audio;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.user.UserHandler;

public class AudioPerms implements Command {
	
	private UserHandler uh;
	
	public AudioPerms(UserHandler uh) {
		this.uh = uh;
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		if(e.getMember().isOwner() && args.length >= 8) {
			return true;
		}
		return false;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " is attempting to change audio permissions for " + args[1] + ".");
		String s = args[1];
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
			boolean[] music = new boolean[6];
			for(int i = 2; i < args.length && i - 2 < music.length; i++) {
				music[i - 2] = args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("yes") || args[2].equalsIgnoreCase("1") || args[2].equalsIgnoreCase("y") || args[2].equalsIgnoreCase("t"); 
			}
			uh.findUser(t.getUser().getIdLong()).setMusicRules(music);
			e.getTextChannel().sendMessage("Updated their permissions.").queue();
		}
		else {
			e.getTextChannel().sendMessage("Couldn't find that user.").queue();
		}
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "audioperms [Player] [true | false] [true | false] [true | false] [true | false] [true | false] [true | false]"
				+ "\nDESC: Changes audio perms. label audioperms PLAY SFX SKIP PAUSE SUMMON VOLUME"
				+ "\nPERMS: admin";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
}
