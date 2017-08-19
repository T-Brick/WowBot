package com.estrelsteel.wowbot.user;

import java.util.ArrayList;

import net.dv8tion.jda.core.entities.User;

import com.estrelsteel.wowbot.WowBot;

public class UserSettings {
	
	private long id;
	private boolean quiet;
	private int q_length;
	private long q_start;
	private boolean mute;
	private ArrayList<User> watching;
	private boolean[] music;
	// PLAY SFX SKIP PAUSE SUMMON VOLUME
	
	public static final int version = 3;
	public static final String split = "/";
	
	public UserSettings(long id) {
		this.id = id;
		this.quiet = false;
		this.q_length = -1;
		this.q_start = -1;
		this.mute = false;
		this.watching = new ArrayList<User>();
		this.music = new boolean[6];
		for(int i = 0; i < music.length; i++) {
			music[i] = true;
		}
	}
	
	public long getID() {
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
	
	public boolean[] getMusicRules() {
		return music;
	}
	
	private static String[] createArgs(String line) {
		return line.split(split);
	}
	
	public void setID(long id) {
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
	
	public void setMusicRules(boolean[] music) {
		this.music = music;
	}
	
	public UserSettings loadUserSettings(String line) {
		String[] args = createArgs(line);
		int v = Integer.parseInt(args[0].trim());
		if(version == v) {
			setID(Long.parseLong(args[1].trim()));
			setQuiet(Boolean.parseBoolean(args[2].trim()));
			setQuietLength(Integer.parseInt(args[3].trim()));
			setQuietStart(System.currentTimeMillis());
			setMute(Boolean.parseBoolean(args[4].trim()));
			String s;
			for(int i = 0; i < args[5].length(); i++) {
				s = args[5].substring(i, i + 1);
				music[music.length - 1 - i] = s.equals("1");
			}
		}
		else if(v == 2) {
			setID(Long.parseLong(args[1].trim()));
			setQuiet(Boolean.parseBoolean(args[2].trim()));
			setQuietLength(Integer.parseInt(args[3].trim()));
			setQuietStart(System.currentTimeMillis());
			setMute(Boolean.parseBoolean(args[4].trim()));
		}
		else if(v == 1) {
			setID(Long.parseLong(args[1].trim()));
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
		String m = "";
		for(int i = 0; i < music.length; i++) {
			m = m + (music[i] ? "1" : "0");
		}
		s = s + split + m;
		return s;
	}
	
	public void sendPrivateMessage(User u, String msg, boolean ignore) {
		if(ignore || !mute) {
			u.openPrivateChannel().complete().sendMessage(msg).queue();
		}
	}
}
