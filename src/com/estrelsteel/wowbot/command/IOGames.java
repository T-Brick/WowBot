package com.estrelsteel.wowbot.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.file.GameFile;
import com.estrelsteel.wowbot.user.UserHandler;

public class IOGames implements Command {
	
	private GameFile file;
	private HashMap<String, String> games;
	
	public IOGames(GameFile file) {
		this.file = file;
		
		try {
			loadGamelist();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadGamelist() throws IOException {
		games = new HashMap<String, String>();
		file.setLines(file.readFile());
		String[] args;
		for(int i = 0; i < file.getLines().size(); i++) {
			args = file.getLines().get(i).split(" ");
			games.put(args[0], args[1]);
		}
	}
	
	public void saveGamelist() throws IOException{
		file.setLines(new ArrayList<String>());
		for(Entry<String, String> s : games.entrySet())  {
			file.getLines().add(s.getKey() + " " + s.getValue());
		}
		file.saveFile();
	}
	
	public GameFile getFile() {
		return file;
	}
	
	public HashMap<String, String> getSoundlist() {
		return games;
	}
	
	public void setFile(GameFile file) {
		this.file = file;
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		return true;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		String request = "";
		boolean find = false;
		int arg = 1;
		String msg;
		e.getMessage().delete().queueAfter(30, TimeUnit.SECONDS);
		if(args.length == 1 || args[1].trim().equalsIgnoreCase("ran") || args[1].trim().equalsIgnoreCase("rand") || args[1].trim().equalsIgnoreCase("random")) {
			ArrayList<String> names = new ArrayList<String>();
			names.addAll(games.keySet());
			int ran = (int) (Math.random() * names.size());
			e.getTextChannel().sendMessage(games.get(names.get(ran))).queue();
			return;
		}
		else if(args[1].trim().equalsIgnoreCase("find")) {
			arg++;
			find = true;
		}
		else if(args[1].trim().equalsIgnoreCase("list")) {
			ArrayList<String> names = new ArrayList<String>();
			names.addAll(games.keySet());
			String o = WowBot.convertListToString(names);
			System.out.println(WowBot.getMsgStart() + e.getAuthor().getName() + " requested the list of io games.");
			EmbedBuilder builder = new EmbedBuilder();
			builder.setColor(e.getMember().getColor());
			builder.addField("IO Games:", o, false);
			builder.setFooter("Type in the beginning of an io game to search for it.", e.getGuild().getMemberById(WowBot.id).getUser().getAvatarUrl());
			e.getTextChannel().sendMessage(builder.build()).queue();
			return;
		}
		else if(args[1].trim().equalsIgnoreCase("add") || args[1].trim().equalsIgnoreCase("+")) {
			if(args.length >= 4) {
				System.out.println(WowBot.getMsgStart() + e.getAuthor().getName() + " is adding an io game " + args[2] + " ( " + args[3] + " )");
				if(games.containsKey(args[2].toLowerCase())) {
					e.getTextChannel().sendMessage("This label is already in use.").queue();
					return;
				}
				if(games.containsValue(args[3])) {
					e.getTextChannel().sendMessage("This audio file is already in use.").queue();
					return;
				}
				
				games.put(args[2].toLowerCase(), args[3]);
				try {
					saveGamelist();
				}
				catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			else {
				e.getTextChannel().sendMessage(help()).queue();
			}
			return;
		}
		else if(args[1].trim().equalsIgnoreCase("reload")) {
			System.out.println(WowBot.getMsgStart() + e.getAuthor().getName() + " requested a reload of the io games list.");
			try {
				loadGamelist();
			} 
			catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		}
		request = args[arg].trim().toLowerCase();
		if(!find) {
			String game = games.get(request);
			if(game != null) {
				UserHandler.sendPublicMessage(game, e, true);
				return;
			}
			System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " requested the '" + args[1].trim() + "' io game, but it doesn't exist, now searching for it.");
			ArrayList<String> match = search(request);
			msg = WowBot.convertListToString(match);
			if(match.size() == 0) {
				UserHandler.sendPublicMessage("That io game does not exist.", e, true);
			}
			else {
				EmbedBuilder builder = new EmbedBuilder();
				builder.setColor(e.getMember().getColor());
				builder.addField("That io game does not exist... But could you mean:", msg, false);
				builder.setFooter("Type in the beginning of an io game to search for it.", e.getGuild().getMemberById(WowBot.id).getUser().getAvatarUrl());
				e.getTextChannel().sendMessage(builder.build()).queue();
//						UserHandler.sendPublicMessage("That sound does not exist... But could you mean:\n```" + msg + ".```", e, true);
			}
		}
		else {
			System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " is requesting an io game with " + request + "in it's name.");
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
		for(String key : games.keySet()) {
			if(key.contains(request.subSequence(0, request.length()))) {
				match.add(key);
			}
		}
		System.out.println(WowBot.getMsgStart() + "\tfound " + match.size() + " match(es).");
		return match;
	}

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "iogame (random | item | list | (add [label] [link]) | reload)"
				+ "\nDESC: shows possible io games."
				+ "\n\t[item] : links the io game with the name [item]."
				+ "\n\t[list] : sends the user a list of all the io games avaliable."
				+ "\nPERMS: all";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		return;
	}
}
