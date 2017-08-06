package com.estrelsteel.wowbot.command.user;

import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.user.UserHandler;

public class Politics implements Command {
	
	private HashMap<Guild, Role> rl;
	private UserHandler uh;
	
	public Politics(List<Guild> guilds, UserHandler uh) {
		rl = new HashMap<Guild, Role>();
		this.uh = uh;
		boolean add = false;
		for(Guild g : guilds) {
			for(Role r : g.getRoles()) {
				if(r.getName().equals("Politics")) {
					rl.put(g, r);
					add = true;
				}
			}
			if(!add) {
				rl.put(g, null);
			}
			add = false;
		}
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		if(e.getTextChannel() != null && rl.get(e.getGuild()) != null) {
			return true;
		}
		return false;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		List<Role> roles = e.getMember().getRoles();
		for(int i = 0; i < roles.size(); i++) {
			if(roles.get(i).getName().equals("Politics")) {
				e.getGuild().getController().removeRolesFromMember(e.getMember(), roles.get(i)).reason("Requested to be removed").queue();
				System.out.println(WowBot.getMsgStart() + "Removed " + e.getAuthor().getName() + " from the politics channel.");
				uh.findUser(e.getAuthor().getId()).sendPrivateMessage(e.getAuthor(), "Removed " + e.getAuthor().getAsMention() + " from Politics", false);
//				e.getTextChannel().sendMessage("Removed " + e.getAuthor().getAsMention() + " from Politics").queue();
				return;
			}
		}
		Role r = rl.get(e.getGuild());
		if(r != null) {
			e.getGuild().getController().addRolesToMember(e.getMember(), r).reason("Requested to be added").queue();
			System.out.println(WowBot.getMsgStart() + "Added " + e.getAuthor().getName() + " to the politics channel.");
			uh.findUser(e.getAuthor().getId()).sendPrivateMessage(e.getAuthor(), "Added " + e.getAuthor().getAsMention() + " to Politics", false);
//			e.getTextChannel().sendMessage("Added " + e.getAuthor().getAsMention() + " to Politics").queue();
		}
		else {
			System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " attemped to join the politics channel but it doesn't exist.");
		}
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "politics";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
}
