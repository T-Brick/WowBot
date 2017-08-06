package com.estrelsteel.wowbot.command.group;

import java.io.IOException;
import java.util.ArrayList;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.file.GameFile;

@Deprecated
public class GroupManager implements Command {

	public static String header = "+----------------------------------------------+"; /* 48 */
	
	private ArrayList<Group> events;
	
	public GroupManager() {
		this.events = new ArrayList<Group>();
	}
	
	public ArrayList<Group> getEvents() {
		return events;
	}
	
	public void setEvents(ArrayList<Group> events) {
		this.events = events;
	}
	
	public GroupManager loadEvents(String path) throws IOException {
		GameFile gf = new GameFile(path);
		gf.setLines(gf.readFile());
		for(int i = 0; i < gf.getLines().size(); i++) {
			events.add(new Group("NULL").loadGroup(gf.getLines().get(i)));
		}
		return this;
	}
	
	public void saveEvents(String path) throws IOException {
		GameFile gf = new GameFile(path);
		for(int i = 0; i < events.size(); i++) {
			gf.getLines().add(events.get(i).saveGroup());
		}
		gf.saveFile();
	}
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent e) {
		return true;
	}

	//~group create name 
	@Override
	public void action(String[] args, MessageReceivedEvent e) {
		System.out.println(WowBot.getMsgStart() + "" + e.getAuthor().getName() + " has issued command: " + e.getMessage().getContent());
		if(args.length >= 3) {
			if(args[1].equalsIgnoreCase("create")) {
				String val = Group.version + "";
				val = val + Group.split + args[2].trim() + Group.split + args[3].trim() + Group.split + 3 + Group.split + args[4].trim();
			}
		}
	}
	
	/*
	 * +------------------------------+
	 * |							  |
	 * |							  |
	 * +------------------------------+
	 * 
	 */

	@Override
	public String help() {
		return "USAGE: " + WowBot.settings.getTrigger() + "group create [name]\n"
				+ "\t" + WowBot.settings.getTrigger() + "group delete [name]\n"
				+ "\t" + WowBot.settings.getTrigger() + "group join [name]"
				+ "\t" + WowBot.settings.getTrigger() + "group leave [name]"
				+ "\t" + WowBot.settings.getTrigger() + "group call [msg]"
				+ "\t" + WowBot.settings.getTrigger() + "group help";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent e) {
		
	}

}
