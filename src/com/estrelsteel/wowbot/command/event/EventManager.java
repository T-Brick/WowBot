package com.estrelsteel.wowbot.command.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.file.GameFile;
import com.estrelsteel.wowbot.user.UserHandler;

public class EventManager implements Command {

	public static String header = "+----------------------------------------------+"; /* 48 */
	
	private ArrayList<Event> events;
	
	public EventManager() {
		this.events = new ArrayList<Event>();
	}
	
	public ArrayList<Event> getEvents() {
		return events;
	}
	
	public void setEvents(ArrayList<Event> events) {
		this.events = events;
	}
	
	public EventManager loadEvents(String path) throws IOException {
		GameFile gf = new GameFile(path);
		gf.setLines(gf.readFile());
		for(int i = 0; i < gf.getLines().size(); i++) {
			events.add(new Event("NULL").loadEvent(gf.getLines().get(i)));
		}
		return this;
	}
	
	public void saveEvents(String path) throws IOException {
		GameFile gf = new GameFile(path);
		for(int i = 0; i < events.size(); i++) {
			gf.getLines().add(events.get(i).saveEvent());
		}
		gf.saveFile();
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		return true;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " has issued command: " + e.getMessage().getContentRaw());
		if(args.length >= 5) {
			if(args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("+")) {
				String val = Event.version + "";
				val = val + Event.split + args[2].trim() + Event.split + args[3].trim() + Event.split + 3 + Event.split + args[4].trim();
				for(int i = 5; i < args.length; i++) {
					val = val + "-" + args[i];
				}
				events.add(new Event("NULL").loadEvent(val));
				UserHandler.sendPublicMessage("Added the event", e, true);
				System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " has created the event " + args[2].trim().toUpperCase());
			}
		}
		if(args.length >= 3) {
			if(args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("-")) {
				int cleaned = 0;
				for(int i = 0; i < events.size(); i++) {
					if(events.get(i).getName().equalsIgnoreCase(args[2].trim())) {
						events.remove(i);
						i--;
						cleaned++;
					}
				}
				UserHandler.sendPublicMessage("Removed " + cleaned + " event(s)", e, true);
				System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " has deleted " + cleaned + " with the name " + args[2].trim().toUpperCase());
			}
		}
		else if(args.length == 2) {
			if(args[1].equalsIgnoreCase("list")) {
				System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " has requested to view the events.");
				if(events.size() <= 0) {
					UserHandler.sendPublicMessage("There are no scheduled events.", e, true);
				}
				else {
					String msg = "```" + header + "";
					for(int i = 0; i < events.size(); i++) {
						msg = msg + "\n" + events.get(i).getDisplayMessage();
					}
					msg = msg + "```";
					e.getTextChannel().sendMessage(msg).queue();
				}
			}
			else if(args[1].equalsIgnoreCase("help") || args[1].equalsIgnoreCase("?")) {
				UserHandler.sendPublicMessage(help(), e, true);
			}
			else if(args[1].equalsIgnoreCase("clean")) {
				Calendar cal = Calendar.getInstance();
				int cleaned = 0;
				for(int i = 0; i < events.size(); i++) {
					if(cal.after(events.get(i).getCalendar())) {
						events.remove(i);
						i--;
						cleaned++;
					}
				}
				UserHandler.sendPublicMessage("Removed " + cleaned + " outdated event(s).", e, true);
				System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " has cleaned " + cleaned + " outdated event(s).");
			}
		}
		else {
			UserHandler.sendPublicMessage(help(), e, true);
		}
	}
	
	/*
	 * +------------------------------+
	 * |							  |
	 * |							  |
	 * +------------------------------+
	 * 
	 */

	// event [list | (add/+ [name] [description] [year] [month] [day] <[hour] [min] [sec]>) | (remove/- [name]) | clean]
	
	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "event [list | (add/+ [name] [description] [year] [month] [day] <[hour] [min] [sec]>) | (remove/- [name]) | clean]"
				+ "\nDESC: lists, creates, and removes events."
				+ "\n\t[list] : lists the events."
				+ "\n\t[add/+  [name] [description] [year] [month] [day] <[hour] [min] [sec]>] : adds an event with [name] and [description] on [day].[month].[year] at <[hour]:[min]:[sec]>."
				+ "\n\t[remove/-  [name]] : removes event with [name]"
				+ "\n\t[clean] : removes all events that are in the past."
				+ "\nPERMS: all";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		
	}

}
