package com.estrelsteel.wowbot.user;

import java.util.ArrayList;

import net.dv8tion.jda.core.entities.User;

import com.estrelsteel.wowbot.WowBot;

public class UserSettings {
	
	private String id;
	private boolean quiet;
	private int q_length;
	private long q_start;
	private boolean mute;
	private ArrayList<User> watching;
	
	public static final int version = 2;
	public static final String split = "/";
	
	public UserSettings(String id) {
		this.id = id;
		this.quiet = false;
		this.q_length = -1;
		this.q_start = -1;
		this.mute = false;
		this.watching = new ArrayList<User>();
	}
	
	public String getID() {
		return id;
	}
	
	public boolean isQuiet() {
		if(q_length > 0 && q_start > 0) {
			if(System.currentTimeMillis() - q_start >= q_length) {
				quiet = false;
				q_start = -1;
				q_length = -1;
			}
		}
		return quiet;
	}
	
	public int getQuietLength() {
		return q_length;
	}
	
	public long getQuietStart() {
		return q_start;
	}
	
	public boolean isMute() {
		return mute;
	}
	
	public ArrayList<User> getWatching() {
		return watching;
	}
	
	private static String[] createArgs(String line) {
		return line.split(split);
	}
	
	public void setID(String id) {
		this.id = id;
	}
	
	public void setQuiet(boolean quiet) {
		this.quiet = quiet;
	}
	
	public void setQuietLength(int q_length) {
		this.q_length = q_length;
	}
	
	public void setQuietStart(long q_start) {
		this.q_start = q_start;
	}
	
	public void setMute(boolean mute) {
		this.mute = mute;
	}
	
	public void setWatching(ArrayList<User> watching) {
		this.watching = watching;
	}
	
	public UserSettings loadUserSettings(String line) {
		String[] args = createArgs(line);
		int v = Integer.parseInt(args[0].trim());
		if(version == v) {
			setID(args[1].trim());
			setQuiet(Boolean.parseBoolean(args[2].trim()));
			setQuietLength(Integer.parseInt(args[3].trim()));
			setQuietStart(System.currentTimeMillis());
			setMute(Boolean.parseBoolean(args[4].trim()));
		}
		else if(v == 1) {
			setID(args[1].trim());
			setQuiet(Boolean.parseBoolean(args[2].trim()));
			setQuietLength(Integer.parseInt(args[3].trim()));
			setQuietStart(System.currentTimeMillis());
			setMute(false);
		}
		else if(v <= 0) {
			setQuietLength(-1);
			setQuietStart(-1);
			setMute(false);
		}
		else {
			System.err.println(WowBot.getMsgStart() + "ERROR: Invalid user version.");
		}
		return this;
	}
	
	public String saveUserSettings() {
		String s = version + split + getID() + split + isQuiet() + split;
		if(q_length > 0 && q_start > 0) {
			s = s + (System.currentTimeMillis() - getQuietStart());
		}
		else {
			s = s + q_length;
		}
		s = s + split + isMute();
		return s;
	}
	
	public void sendPrivateMessage(User u, String msg, boolean ignore) {
		if(ignore || !mute) {
			u.openPrivateChannel().complete().sendMessage(msg).queue();
		}
	}
}
