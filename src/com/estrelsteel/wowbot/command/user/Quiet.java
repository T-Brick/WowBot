package com.estrelsteel.wowbot.command.user;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.user.UserHandler;
import com.estrelsteel.wowbot.user.UserSettings;

public class Quiet implements Command {
	
	private UserHandler uh;
	
	public Quiet(UserHandler uh) {
		this.uh = uh;
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		return true;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		UserSettings u = uh.findUser(e.getAuthor().getId());
		u.setQuiet(!u.isQuiet());
		if(args.length > 1) {
			if(u.isQuiet()) {
				u.setQuietLength(Integer.parseInt(args[1].trim()));
				u.setQuietStart(System.currentTimeMillis());
				u.sendPrivateMessage(e.getAuthor(), e.getAuthor().getAsMention() + " is now quiet for " + (Integer.parseInt(args[1].trim()) / 1000) + " seconds.", false);
//				e.getTextChannel().sendMessage(e.getAuthor().getAsMention() + " is now quiet for " + (Integer.parseInt(args[1].trim()) / 1000) + " seconds.").queue();
				System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " is now quiet for " + (Integer.parseInt(args[1].trim()) / 1000) + " seconds.");
			}
			else {
				u.setQuietLength(-1);
				u.setQuietStart(-1);
				u.sendPrivateMessage(e.getAuthor(), e.getAuthor().getAsMention() + " is no longer quiet.", false);
//				e.getTextChannel().sendMessage(e.getAuthor().getAsMention() + " is no longer quiet.").queue();
				System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " is no longer quiet.");
			}
		}
		else {
			u.setQuietLength(-1);
			u.setQuietStart(-1);
			if(u.isQuiet()) {
				e.getTextChannel().sendMessage(e.getAuthor().getAsMention() + " is now quiet.").queue();
				System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " is now quiet.");
			}
			else {
				e.getTextChannel().sendMessage(e.getAuthor().getAsMention() + " is no longer quiet.").queue();
				System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " is no longer quiet.");
			}
		}
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "quiet <time>"
				+ "\nDESC: stops the user from being messaged in assemble command."
				+ "\n\t<time> : for <time> milliseconds."
				+ "\nPERMS: all";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
	

}
