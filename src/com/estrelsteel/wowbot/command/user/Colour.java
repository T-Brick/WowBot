package com.estrelsteel.wowbot.command.user;

import java.awt.Color;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.user.UserHandler;

public class Colour implements Command {
	
	private UserHandler uh;
	
	public Colour(UserHandler uh) {
		this.uh = uh;
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		if(args.length >= 4 && e.getTextChannel() != null && e.getMember().getRoles().size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		Color c = new Color(Integer.parseInt(args[1].trim()), Integer.parseInt(args[2].trim()), Integer.parseInt(args[3].trim()));
		Color old = e.getMember().getRoles().get(0).getColor();
		if(old != null) {
			System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " changed their colour from " + old.getRed() + ", "
				+ old.getGreen() + ", " + old.getBlue() + " (RGB) to " + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + " (RGB).");
		}
		else {
			System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " changed their colour to " + c.getRed() + ", " + c.getGreen() + ", "
				+ c.getBlue() + " (RGB).");
		}
		e.getMember().getRoles().get(0).getManager().setColor(c).reason("User requested the change.").queue();
		uh.findUser(e.getAuthor().getId()).sendPrivateMessage(e.getAuthor(), "You changed your colour!", false);
//		if(!e.getAuthor().hasPrivateChannel()) {
//			e.getAuthor().openPrivateChannel().queue();
//		}
//		e.getAuthor().getPrivateChannel().sendMessage("You changed your colour!").queue();
//		e.getTextChannel().sendMessage("You changed your colour!").queue();
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "color [red] [green] [blue]";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
}
