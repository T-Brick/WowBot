package com.estrelsteel.wowbot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Scanner;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game.GameType;

import com.estrelsteel.wowbot.command.Assemble;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.command.Kaomoji;
import com.estrelsteel.wowbot.command.audio.SFX;
import com.estrelsteel.wowbot.command.event.Event;
import com.estrelsteel.wowbot.command.event.EventManager;
import com.estrelsteel.wowbot.command.sys.Changelog;
import com.estrelsteel.wowbot.command.sys.Clean;
import com.estrelsteel.wowbot.command.sys.Help;
import com.estrelsteel.wowbot.command.sys.Info;
import com.estrelsteel.wowbot.command.sys.Random;
import com.estrelsteel.wowbot.command.sys.Restart;
import com.estrelsteel.wowbot.command.sys.Save;
import com.estrelsteel.wowbot.command.sys.ShutDown;
import com.estrelsteel.wowbot.command.sys.TimeOut;
import com.estrelsteel.wowbot.command.sys.WhoIs;
import com.estrelsteel.wowbot.command.user.Colour;
import com.estrelsteel.wowbot.command.user.Politics;
import com.estrelsteel.wowbot.command.user.Quiet;
import com.estrelsteel.wowbot.command.user.Team;
import com.estrelsteel.wowbot.command.user.Watch;
import com.estrelsteel.wowbot.command.wow.DynamicWow;
import com.estrelsteel.wowbot.file.GameFile;
import com.estrelsteel.wowbot.file.Settings;
import com.estrelsteel.wowbot.games.GameCentre;
import com.estrelsteel.wowbot.user.UserHandler;

public class WowBot {
	
	public static Settings settings;	
	public static final String title = "WowBot v1.5b (12)";
	public static final String owner = "167026252597690369";
	public static final String id = "266437681242701825";
	public static final String path = "/Users/justin/Desktop/SERVER/Discord/WowBot";
	
	private boolean running;
	
	private JDA jda;
	private final String tolken = "MjY2NDM3NjgxMjQyNzAxODI1.C09q9A.aZMKQZqEvJH2acIYfzyp8K-eZuM";
	private HashMap<String, Command> cmds;
	private Parser p = new Parser();
	@SuppressWarnings("unused")
	private GameCentre gc;
	private EventManager em;
	private DynamicWow wow;
	private UserHandler uh;
	private long lastSave;
	private WowGame game;
	private Listener l;
	
	public static void main(String[] args) {
//		TicTacToe ttt = new TicTacToe();
//		ttt.printGame();
		System.out.println(title + "\n\tBy: EstrelSteel");
		settings = new Settings();
		new WowBot();
	}
	
	public static String getMsgStart() {
		return "[" + Event.df.format(Calendar.getInstance().getTime()).toString() + "] ";
	}
	
	public HashMap<String, Command> getCommands() {
		return cmds;
	}
	
	public WowBot() {
		game = new WowGame(title, "", GameType.DEFAULT);
		
		try {
			l = new Listener(this, null);
			jda = new JDABuilder(AccountType.BOT).setGame(game).addEventListener(l).setToken(tolken).buildBlocking();
			jda.setAutoReconnect(true);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		lastSave = System.currentTimeMillis();
		try {
			em = new EventManager().loadEvents(path + "/events.txt");
			uh = new UserHandler().loadUsers(path + "/users.txt");
			l.setUserHandler(uh);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		gc = new GameCentre();
		cmds = new HashMap<String, Command>();
		wow = new DynamicWow();
		
		cmds.put("wow", wow);
		cmds.put("vvovv", wow);
		cmds.put("wovv", wow);
		cmds.put("vvow", wow);
		cmds.put("w0w", wow);
		cmds.put("vv0vv", wow);
		cmds.put("w0vv", wow);
		cmds.put("vv0w", wow);
		
//		cmds.put("ttt", new TTT(gc));
		cmds.put("event", em);
		cmds.put("shutdown", new ShutDown(this));
		cmds.put("stop", new ShutDown(this));
		cmds.put("exit", new ShutDown(this));
		cmds.put("save", new Save(this));
		cmds.put("help", new Help(this));
		cmds.put("?", new Help(this));
		cmds.put("info", new Info());
		cmds.put("assemble", new Assemble(uh));
		cmds.put("whois", new WhoIs(uh));
		cmds.put("about", new WhoIs(uh));
		cmds.put("quiet", new Quiet(uh));
		cmds.put("politics", new Politics(jda.getGuilds(), uh));
		cmds.put("color", new Colour(uh));
		cmds.put("colour", new Colour(uh));
//		cmds.put("playwow", new PlayWow());
		cmds.put("clean", new Clean(this));
		cmds.put("timeout", new TimeOut(jda.getGuilds()));
		cmds.put("restart", new Restart(this));
		cmds.put("watch", new Watch(uh));
		cmds.put("changelog", new Changelog(new GameFile(path + "/changelog.txt")));
		cmds.put("kaomoji", new Kaomoji(new GameFile(path + "/kaomoji.txt"), uh));
		cmds.put("play", new SFX(new GameFile(path + "/sounds.txt"), true));
		cmds.put("sfx", new SFX(new GameFile(path + "/sounds.txt"), false));
		cmds.put("sound", new SFX(new GameFile(path + "/sounds.txt"), false));
		cmds.put("team", new Team(2, 2));
		cmds.put("die", new Random(6));
		cmds.put("dice", new Random(6));
		cmds.put("coin", new Random(2, true));
		cmds.put("random", new Random(0));
		cmds.put("rand", new Random(0));
		cmds.put("ran", new Random(0));
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		running = true;
		String s;
		String[] args;
		while(running) {
			s = scan.nextLine();
			args = s.split(" ");
			switch(args[0]) {
			case "shutdown":
				System.out.println(WowBot.getMsgStart() + "console has issued a shutdown.");
				System.out.println(WowBot.getMsgStart() + "Shutting down...");
				shutdown(false);
				break;
			case "stop":
				System.out.println(WowBot.getMsgStart() + "console has issued a shutdown.");
				System.out.println(WowBot.getMsgStart() + "Shutting down...");
				shutdown(false);
				break;
			case "exit":
				System.out.println(WowBot.getMsgStart() + "console has issued a shutdown.");
				System.out.println(WowBot.getMsgStart() + "Shutting down...");
				shutdown(false);
				break;
			case "save":
				System.out.println(WowBot.getMsgStart() + "console has requested to save.");
				System.out.println(WowBot.getMsgStart() + "Saving data...");
				save();
				break;
			case "info":
				System.out.println(WowBot.getMsgStart() + "console has requested the info page.");
				System.out.println("" + WowBot.title + "\n\tBy: EstrelSteel\n\nGitHub: https://github.com/EstrelSteel/WowBot");
				break;
			case "help":
				System.out.println(WowBot.getMsgStart() + "console has requested the help page.");
				System.out.println("COMMANDS: help, shutdown, save, info, say");
				break;
			case "say":
				if(args.length > 1) {
					String msg = "";
					for(int i = 2; i < args.length; i++) {
						msg = msg + args[i] + " ";
					}
					msg.trim();
					String[] c = args[1].split("/");
					int serverCount = 0;
					int channelCount = 0;
					for(int i = 0; i < jda.getGuilds().size(); i++) {
						if(c[0].equalsIgnoreCase("*all*") || c[0].equalsIgnoreCase(jda.getGuilds().get(i).getName())) {
							if(c.length > 1) {
								for(int j = 0; j < jda.getGuilds().get(i).getTextChannels().size(); j++) {
									if(c[1].equalsIgnoreCase("*all*") || c[1].equalsIgnoreCase(jda.getGuilds().get(i).getTextChannels().get(j).getName())) {
										jda.getGuilds().get(i).getTextChannels().get(j).sendMessage(msg).queue();
										channelCount++;
									}
								}
							}
							else {
								jda.getGuilds().get(i).getTextChannels().get(0).sendMessage(msg).queue();
								channelCount++;
							}
							serverCount++;
						}
					}
					System.out.println(WowBot.getMsgStart() + "sent '" + msg.trim() + "' to " + serverCount + " server(s) and " + channelCount + " channel(s).");
				}
				else {
					System.out.println(WowBot.getMsgStart() + "ARGS: say <server name(/textchannel | /*all*) | *all*> <msg>");
				}
				break;
			case "restart":
				System.out.println(WowBot.getMsgStart() + "console has issued a restart.");
				System.out.println(WowBot.getMsgStart() + "Restarting...");
				shutdown(true);
				WowBot.main(null);
				break;
			default:
				System.out.println("Unknown command, " + args[0] + "\nUse help for a list of commands.");
			}
		}
	}
	
	public Parser getParser() {
		return p;
	}
	
	public UserHandler getUserHandler() {
		return uh;
	}
	
	public void handleCommand(Parser.CommandContainer cmd) {
		if(System.currentTimeMillis() - lastSave >= 86400000) {
			System.out.println(WowBot.getMsgStart() + "Saving data...");
			save();
		}
		if(cmds.containsKey(cmd.args[0])) {
			boolean safe = cmds.get(cmd.args[0]).called(cmd.args, cmd.e);
			if(safe) {
				cmds.get(cmd.args[0]).action(cmd.args, cmd.e);
				if(cmds.containsKey(cmd.args[0])) {
					cmds.get(cmd.args[0]).executed(safe, cmd.e);
				}
				
			}
			else {
				cmds.get(cmd.args[0]).executed(safe, cmd.e);
			}
		}
	}
	
	public void save() {
		lastSave = System.currentTimeMillis();
		try {
			em.saveEvents(path + "/events.txt");
			uh.saveUsers(path + "/users.txt");
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void shutdown(boolean restart) {
		save();
		running = false;
		if(!restart) {
			jda.shutdown();
			System.exit(0);
		}
	}
	
	public static String convertListToString(ArrayList<String> list) {
		String msg = "";
		for(int j = 0; j < list.size(); j++) {
			if(j == 0) {
				msg = list.get(j);
			}
			else {
				msg = msg + ", " + list.get(j);
			}
		}
		return msg;
	}
	
	public static String convertListToString(String[] list) {
		String msg = "";
		for(int j = 0; j < list.length; j++) {
			if(j == 0) {
				msg = list[j];
			}
			else {
				msg = msg + ", " + list[j];
			}
		}
		return msg;
	}
}
