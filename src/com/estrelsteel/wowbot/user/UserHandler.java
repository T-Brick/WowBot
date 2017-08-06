package com.estrelsteel.wowbot.user;

import java.io.IOException;
import java.util.ArrayList;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.file.GameFile;

public class UserHandler {
	private ArrayList<UserSettings> users;
	
	public UserHandler() {
		users = new ArrayList<UserSettings>();
	}
	
	public ArrayList<UserSettings> getUsers() {
		return users;
	}
	
	public UserHandler loadUsers(String path) throws IOException {
		GameFile gf = new GameFile(path);
		gf.setLines(gf.readFile());
		for(int i = 0; i < gf.getLines().size(); i++) {
			users.add(new UserSettings("NULL").loadUserSettings(gf.getLines().get(i)));
		}
		return this;
	}
	
	public void saveUsers(String path) throws IOException {
		GameFile gf = new GameFile(path);
		for(int i = 0; i < users.size(); i++) {
			gf.getLines().add(users.get(i).saveUserSettings());
		}
		gf.saveFile();
	}
	
	public UserSettings findUser(String id) {
		for(int i = 0; i < users.size(); i++) {
			if(users.get(i).getID().equals("NULL")) {
				users.remove(i);
				i--;
			}
			else if(users.get(i).getID().equals(id)) {
				return users.get(i);
			}
		}
		System.out.println(WowBot.getMsgStart() + "unknown user, building data.");
		UserSettings s = new UserSettings(id);
		users.add(s);
		return s;
	}
	
	public void setUsers(ArrayList<UserSettings> users) {
		this.users = users;
	}
	
	public static boolean sendPublicMessage(String msg, MessageReceivedEvent e, boolean p) {
		if(e.getTextChannel() != null) {
			e.getTextChannel().sendMessage(msg).queue();
			return true;
		}
		else if(p) {
			e.getAuthor().openPrivateChannel().complete().sendMessage(msg).queue();
			return true;
		}
		return false;
	}
}
