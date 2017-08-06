package com.estrelsteel.wowbot.command.group;

import java.util.HashMap;

import com.estrelsteel.wowbot.WowBot;

@Deprecated
public class Group {
	public static final int version = 0;
	public static final String split = "/";
	
	private String name;
	private String desc;
	private String[] display;
	private HashMap<String, String> members; //username, id
	
	public Group(String name) {
		this.name = name;
		desc = "This group does not have a description";
		display = new String[2];
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return desc;
	}
	
	public String[] getDisplay() {
		return display;
	}
	
	public HashMap<String, String> getMembers() {
		return members;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDescription(String desc) {
		this.desc = desc;
	}
	
	public void setDisplay(String[] display) {
		this.display = display;
	}
	
	public void setMembers(HashMap<String, String> members) {
		this.members = members;
	}
	
	private static String[] createArgs(String line) {
		return line.split(split);
	}
	
	public Group loadGroup(String line) {
		String[] args = createArgs(line);
		if(version == Integer.parseInt(args[0].trim())) {
			setName(args[1].trim());
			setDescription(args[2].trim());
			loadDisplay();
		}
		else {
			System.err.println(WowBot.getMsgStart() + "ERROR: Invalid group version.");
		}
		return this;
	}
	
	public String saveGroup() {
		return version + split + getName() + split + getDescription();
	}
	
	private String[] addDisplayEnds(String[] d) {
		for(int i = 0; i < d.length; i++) {
			for(int j = d[i].length(); j < GroupManager.header.length() - 1; j++) {
				d[i] = d[i] + " ";
			}
			d[i] = d[i] + "|";
		}
		return d;
	}
	
	public void loadDisplay() {
		String[] d = new String[2];
		d[0] = "| " + name.toUpperCase() + " > " + desc;
		addDisplayEnds(d);
		display = d;
	}
	
	public String getDisplayMessage() {
		String s = "";
		for(int i = 0; i < display.length; i++) {
			s = s + display[i] + "\n";
		}
		s = s + GroupManager.header;
		return s;
	}
}
