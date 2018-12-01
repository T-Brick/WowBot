package com.estrelsteel.wowbot.command.sys;

import java.util.List;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.user.UserHandler;

public class Clean implements Command {
	
	@SuppressWarnings("unused")
	private WowBot wow;
	
	public Clean(WowBot wow) {
		this.wow = wow;
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		return true;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		boolean cmds = true;
		if(args.length >= 2) {
			cmds = Boolean.parseBoolean(args[1]);
		}
		System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " has requested to clean posts.");
//		List<Message> m = e.getChannel().getHistoryAround(e.getMessage(), 50).block().getCachedHistory();
		List<Message> m = e.getChannel().getHistoryAround(e.getMessage(), 100).complete().getRetrievedHistory();
		int count = 0;
		for(int i = 0; i < m.size(); i++) {
			if(!m.get(i).isPinned()) {
				if(m.get(i).getAuthor().getIdLong() == WowBot.id) {
					m.get(i).delete().reason("Removing clutter").queue();
					count++;
				}
				else if(cmds && m.get(i).getContentRaw().startsWith("~")) {
					m.get(i).delete().reason("Removing clutter").queue();
					count++;
				}
			}
		}
		if(!cmds) {
			UserHandler.sendPublicMessage("Cleaned " + count + " message(s).", e, true);
			System.out.println(WowBot.getMsgStart() + "Cleaned " + count + " message(s).");
			e.getTextChannel().sendMessage("Cleaned " + count + " message(s).").complete().delete().queueAfter(10, TimeUnit.SECONDS);
			e.getMessage().delete().reason("Reducing clutter").queueAfter(3, TimeUnit.SECONDS);
		}
		else {
			UserHandler.sendPublicMessage("Cleaned " + count + " message(s) including commands.", e, true);
			System.out.println(WowBot.getMsgStart() + "Cleaned " + count + " message(s) including commands.");
		}
		
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "clean <cmds>"
				+ "\nDESC: deletes messages from WowBot."
				+ "\n\t<cmds> : boolean, should it delete user command messages."
				+ "\nPERMS: all";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
}
