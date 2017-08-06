package com.estrelsteel.wowbot.command.wow;

import java.io.File;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.user.UserHandler;

public class DynamicWow implements Command {
	
	public DynamicWow() {
		
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		return true;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		boolean b = false;
		boolean i = false;
		boolean u = false;
		boolean s = false;
		boolean c = false;
		@SuppressWarnings("unused")
		boolean em = false;
		boolean n = false;
		boolean p = false;
		String mods = "";
		for(int j = 1; j < args.length; j++) {
			switch(args[j].toLowerCase().toCharArray()[0]) {
			case 'b':
				b = true;
				mods = mods + "b";
				break;
			case 'i':
				i = true;
				mods = mods + "i";
				break;
			case 'u':
				u = true;
				mods = mods + "u";
				break;
			case 's':
				s = true;
				mods = mods + "s";
				break;
			case 'c':
				c = true;
				mods = mods + "c";
				break;
			case 'e':
				em = true;
				mods = mods + "e";
				break;
			case 'n':
				n = true;
				mods = mods + "n";
				break;
			case 'p':
				p = true;
				mods = mods + "p";
				break;
			default:
				break;
			}
		}
		if(mods.equals("")) {
			mods = "no modifiers";
		}
		System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " says " + args[0].toUpperCase() + " with " + mods + ".");
		String starter = "";
		String ender = "";
		if(p) {
			if(e.getTextChannel() != null) {
				e.getTextChannel().sendFile(new File(WowBot.path + "/wow.png"), null).queue();
			}
			else {
				e.getAuthor().openPrivateChannel().complete().sendFile(new File(WowBot.path + "/wow.png"), null).queue();
			}
		}
		else {
			if(!n) {
				args[0] = args[0].toUpperCase();
			}
			if(s) {
				starter = starter + "~~";
				ender = ender + "~~";
			}
			else if(c) {
				starter = starter + "```";
				ender = ender + "```";
			}
	//		else if(em) {
	//			starter = starter + ":";
	//			ender = ender + ":";
	//			args[0] = args[0].toLowerCase();
	//		}
			else {
				if(u) {
					starter = starter + "__";
					ender = "__" + ender;
				}
				if(b) {
					starter = starter + "**";
					ender = "**" + ender;
				}
				if(i) {
					starter = starter + "*";
					ender = "*" + ender;
				}
			}
			UserHandler.sendPublicMessage(starter + args[0] + ender, e, true);
		}
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "wow [b] [i] [s] [c] [n] [p]";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
}
