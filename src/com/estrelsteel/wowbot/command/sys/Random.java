package com.estrelsteel.wowbot.command.sys;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;

public class Random implements Command {
	
	private int a;
	private boolean coin;
	
	public Random(int a, boolean coin) {
		this.a = a;
		this.coin = coin;
	}
	
	public Random(int a) {
		this.a = a;
		this.coin = false;
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		if((args.length == 1 && a > 0) || (args.length > 1 && a == 0)) {
			return true;
		}
		return false;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		int num;
		if(a == 0) {
			num = (int) (Math.random() * Integer.parseInt(args[0].trim())) + 1;
		}
		else {
			num = (int) (Math.random() * a) + 1;
		}
		if(!coin) {
			System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " rolled a " + num + ".");
			e.getTextChannel().sendMessage(e.getAuthor().getAsMention() + "rolled a " + num + ".").queue();
		}
		else {
			String val = "tails";
			if(num == 2) {
				val = "heads";
			}
			System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " flipped a " + val + ".");
			e.getTextChannel().sendMessage(e.getAuthor().getAsMention() + " flipped a " + val + ".").queue();
		}
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "random [sides]";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
}
