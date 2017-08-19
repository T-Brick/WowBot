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

import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.command.Kaomoji;
import com.estrelsteel.wowbot.command.audio.AudioPerms;
import com.estrelsteel.wowbot.command.audio.Pause;
import com.estrelsteel.wowbot.command.audio.Play;
import com.estrelsteel.wowbot.command.audio.Queue;
import com.estrelsteel.wowbot.command.audio.SFX;
import com.estrelsteel.wowbot.command.audio.Skip;
import com.estrelsteel.wowbot.command.audio.Summon;
import com.estrelsteel.wowbot.command.audio.Volume;
import com.estrelsteel.wowbot.command.audio.WowAudioCore;
import com.estrelsteel.wowbot.command.event.Event;
import com.estrelsteel.wowbot.command.event.EventManager;
import com.estrelsteel.wowbot.command.sys.Changelog;
import com.estrelsteel.wowbot.command.sys.Clean;
import com.estrelsteel.wowbot.command.sys.Help;
import com.estrelsteel.wowbot.command.sys.Info;
import com.estrelsteel.wowbot.command.sys.Random;
import com.estrelsteel.wowbot.command.sys.Save;
import com.estrelsteel.wowbot.command.sys.WhoIs;
import com.estrelsteel.wowbot.command.sys.admin.Restart;
import com.estrelsteel.wowbot.command.sys.admin.ShutDown;
import com.estrelsteel.wowbot.command.sys.admin.TimeOut;
import com.estrelsteel.wowbot.command.user.Assemble;
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
	public static final String title = "WowBot v1.6a (14)";
	public static long owner;
	public static long id;
	public static String path = GameFile.getCurrentPath();
	
	private boolean running;
	
	private JDA jda;
	private final String tolken;
	private HashMap<String, Command> cmds;
	private Parser p = new Parser();
	@SuppressWarnings("unused")
	private GameCentre gc;
	private EventManager em;
	private DynamicWow wow;
	private WowAudioCore wac;
	private SFX sfx;
	private UserHandler uh;
	private long lastSave;
	private WowGame game;
	private Listener l;
	@SuppressWarnings("unused")
	private Serial serial;
	
	public static void main(String[] args) {
//		TicTacToe ttt = new TicTacToe();
//		ttt.printGame();
		System.out.println(title + "\n\tBy: EstrelSteel");
		settings = new Settings();
		if(args.length > 0) {
			path = args[0];
		}
		try {
			new WowBot();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getMsgStart() {
		return "[" + Event.df.format(Calendar.getInstance().getTime()).toString() + "] ";
	}
	
	public HashMap<String, Command> getCommands() {
		return cmds;
	}
	
	public WowBot() throws IOException {
		GameFile info = new GameFile(path + "/wowbot.txt");
		info.setLines(info.readFile());
		tolken = info.getLines().get(0).trim();
		id = Long.parseLong(info.getLines().get(1).trim());
		owner = Long.parseLong(info.getLines().get(2).trim());
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
		wac = new WowAudioCore();
		sfx = new SFX(new GameFile(path + "/sounds.txt"), wac, uh);
		if(info.getLines().get(3).trim().equalsIgnoreCase("true")) {
			System.out.println(WowBot.getMsgStart() + "Hotkeyboard enabled.");
			serial = new Serial(sfx ,wac, info.getLines().get(4).trim().split(" "),info.getLines().get(5).trim().split(" "),  jda.getGuilds());
		}
		
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
		cmds.put("sfx", sfx);
		cmds.put("sound", sfx);
		cmds.put("play", new Play(wac, new GameFile(path + "/audio-whitelist.txt"), uh));
		cmds.put("pause", new Pause(wac, true, uh));
		cmds.put("resume", new Pause(wac, false, uh));
		cmds.put("skip", new Skip(wac, 0.5, uh));
		cmds.put("queue", new Queue(wac));
		cmds.put("summon", new Summon(wac, uh));
		cmds.put("volume", new Volume(wac, uh));
		cmds.put("vol", new Volume(wac, uh));
		cmds.put("audioperms", new AudioPerms(uh));
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
				System.out.println("COMMANDS: help, shutdown, save, info, say, queue");
				break;
			case "queue":
				System.out.println(WowBot.getMsgStart() + "console has the player's queue.");
				if(wac.getPlayer().getPlayingTrack() != null) {
					System.out.println("Currently playing: " + wac.getPlayer().getPlayingTrack().getInfo().title);
					for(int i = 0; i < wac.getAudioQueue().getQueue().size(); i++) {
						System.out.println("\t" + (i + 1) + ".) " + wac.getAudioQueue().getQueue().get(i).getInfo().title);
					}
				}
				else {
					System.out.println("Nothing is playing.");
				}
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
	
	public WowAudioCore getAudioCore() {
		return wac;
	}
	
	public void handleCommand(Parser.CommandContainer cmd) {
		if(System.currentTimeMillis() - lastSave >= 86400000) {
			System.out.println(WowBot.getMsgStart() + "Saving data...");
			save();
		}
		if(cmds.containsKey(cmd.args[0])) {
			if(cmd.args.length > 1 && (cmd.args[1].equalsIgnoreCase("help") || cmd.args[1].equalsIgnoreCase("?"))) {
				cmd.e.getTextChannel().sendMessage(cmds.get(cmd.args[0]).help()).queue();
			}
			else {
				boolean safe = cmds.get(cmd.args[0]).called(cmd.args, cmd.e);
				if(safe) {
					cmds.get(cmd.args[0]).action(cmd.args, cmd.e);
					if(cmds.containsKey(cmd.args[0])) {
						cmds.get(cmd.args[0]).executed(safe, cmd.e);
					}
					
				}
				else {
					cmd.e.getTextChannel().sendMessage(cmds.get(cmd.args[0]).help());
					cmds.get(cmd.args[0]).executed(safe, cmd.e);
				}
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
