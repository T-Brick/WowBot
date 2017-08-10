package com.estrelsteel.wowbot.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.file.GameFile;
import com.estrelsteel.wowbot.user.UserHandler;

public class Kaomoji implements Command {
	
	private GameFile kaomoji_file;
	private HashMap<String, String> kaomoji;
	private UserHandler uh;
	
	public Kaomoji(GameFile kaomoji_file, UserHandler uh) {
		this.kaomoji_file = kaomoji_file;
		this.uh = uh;
		try {
			this.kaomoji_file.setLines(kaomoji_file.readFile());
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		kaomoji = new HashMap<String, String>();
		String[] args;
		String last = "";
		String key = "";
		System.out.println(WowBot.getMsgStart() + "Loading kaomoji...");
		for(String line : this.kaomoji_file.getLines()) {
			args = line.split("\t");
			key = args[1].trim().toLowerCase();
			if(key.equalsIgnoreCase(last)) {
				key = key + " 2";
			}
			kaomoji.put(key, args[0].trim());
			last = args[1].trim().toLowerCase();
		}
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		if(args.length > 1 || (args.length > 2 && (args[1].trim().equalsIgnoreCase("find")))) {
			return true;
		}
		return false;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		String request = "";
		int i = 0;
		boolean find = false;
		String msg;
		if(args[1].trim().equalsIgnoreCase("find")) {
			i++;
			find = true;
		}
		else if(args[1].trim().equalsIgnoreCase("&all&")) {
			ArrayList<String> all = new ArrayList<String>();
			all.addAll(kaomoji.keySet());
			System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " has requested all kaomoji.");
			msg = WowBot.convertListToString(all);
			System.out.println(msg.length());
			uh.findUser(e.getAuthor().getId()).sendPrivateMessage(e.getAuthor(), "```" + msg.substring(0, 1801) + "```", false);
			uh.findUser(e.getAuthor().getId()).sendPrivateMessage(e.getAuthor(), "```" + msg.substring(1801) + "```", false);
			return;
		}
		for(i++; i < args.length; i++) {
			request = request + args[i].trim() + " ";
		}
		request = request.trim().toLowerCase();
		if(!find) {
			String k = kaomoji.get(request);
			if(k != null) {
				UserHandler.sendPublicMessage(k, e, true);
				System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " has requested the '" + args[1].trim() + "' kaomoji.");
			}
			else {
				System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " requested the '" + args[1].trim() + "' kaomoji, but it doesn't exist, searching for it.");
				ArrayList<String> match = search(request);
				msg = WowBot.convertListToString(match);
				if(match.size() == 0) {
					UserHandler.sendPublicMessage("That kaomoji does not exist.", e, true);
				}
				else {
					UserHandler.sendPublicMessage("That kaomoji does not exist... But could you mean:\n```" + msg + ".```", e, true);
				}
			}
		}
		else {
			System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " is requesting kaomoji with " + request + "in it's name.");
			ArrayList<String> match = search(request);
			msg = WowBot.convertListToString(match);
			if(match.size() == 0) {
				UserHandler.sendPublicMessage("Could not find any matches.", e, true);
			}
			else if(match.size() == 1) {
				UserHandler.sendPublicMessage("1 match found: \n```" + msg + "```.", e, true);
			}
			else {
				UserHandler.sendPublicMessage(match.size() + " matches found: \n```" + msg + ".```", e, true);
			}
			
		}
	}
	
	private ArrayList<String> search(String request) {
		ArrayList<String> match = new ArrayList<String>();
		for(String key : kaomoji.keySet()) {
			if(key.contains(request.subSequence(0, request.length()))) {
				match.add(key);
			}
		}
		System.out.println(WowBot.getMsgStart() + "\tfound " + match.size() + " match(es).");
		return match;
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "kaomoji [name | &all&]"
				+ "\nDESC: sends a kaomoji from a list."
				+ "\n\t[name] : replies with the specified kaomoji."
				+ "\n\t[&all&] : sends the user a list of all the kaomoji avaliable."
				+ "\nPERMS: all";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
	

}
