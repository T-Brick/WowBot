package com.estrelsteel.wowbot;

import java.util.List;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import net.dv8tion.jda.core.entities.Guild;

import com.estrelsteel.wowbot.command.audio.SFX;
import com.estrelsteel.wowbot.command.audio.VoiceHelp;
import com.estrelsteel.wowbot.command.audio.WowAudioCore;

public class Serial implements SerialPortEventListener {
	
	private SerialPort serialPort;
	private SFX sfx;
	private WowAudioCore wac;
	private boolean[] used;
	private boolean vol;
	private String[] sfx_list;
	private String[] dynamic_list;
	private int dyn_used;
	private List<Guild> guilds;
	private int length;
	private int[] lastDyn;
	private int writeDyn;
	
	
	public Serial(SFX sfx, WowAudioCore wac, String[] sfx_list, String[] dynamic_list, List<Guild> guilds) {
		this.sfx = sfx;
		this.wac = wac;
		this.used = new boolean[4];
		this.sfx_list = sfx_list;
		this.guilds = guilds;
		this.length = 11;
		this.lastDyn = new int[10];
		this.writeDyn = 0;
		this.dynamic_list = dynamic_list;
		this.dyn_used = -1;
		
		for(int i = 0; i < lastDyn.length; i++) {
			lastDyn[i] = -1;
		}
		
		for(int i = 0; i < used.length; i++) {
			this.used[i] = false;
		}
		if(dynamic_list[0].equalsIgnoreCase("true")) {
			vol = true;
		}
		else {
			vol = false;
		}
		serialPort = new SerialPort("/dev/tty.usbserial-DA01L6J1"); 
		try {
			serialPort.openPort();
			serialPort.setParams(9600, 8, 1, 0);
			int mask = SerialPort.MASK_RXCHAR;
			serialPort.setEventsMask(mask);
			serialPort.addEventListener(this);
		}
		catch (SerialPortException ex) {
			ex.printStackTrace();
		}
	}
	
	public void serialEvent(SerialPortEvent e) {
		if(e.isRXCHAR()){
			if(e.getEventValue() >= length){
				try {
					String msg = serialPort.readString();
//					System.out.println(msg);
					msg = msg.trim();
//					System.out.println("0000-0000\n".getBytes().length);
					if(msg.length() == length - 2) {
						for(int i = 0; i < 5; i++) {
							if(msg.substring(i, i + 1).equals("1") && !used[i]) {
								if(sfx_list[i].equalsIgnoreCase("skip")) {
									if(wac.getVoiceChannel() == null) {
										System.out.println(WowBot.getMsgStart() + "You need to be in a voice channel first.");
									}
									else {
										System.out.println(WowBot.getMsgStart() + "Skipping via hotkey.");
										wac.getAudioQueue().nextTrack();
									}
								}
								else if(sfx_list[i].equalsIgnoreCase("summon")) {
									System.out.println(WowBot.getMsgStart() + "Summoning via hotkey.");
									wac.switchVoiceChannel(VoiceHelp.determineOwnerChannel(guilds, WowBot.owner), true);
								}
								else if(sfx_list[i].equalsIgnoreCase("pause")) {
									wac.getPlayer().setPaused(!wac.getPlayer().isPaused());
									System.out.println(WowBot.getMsgStart() + "Changed the pause to: " + wac.getPlayer().isPaused() + " , via hotkey.");
								}
								else {
									if(wac.getVoiceChannel() == null) {
										System.out.println(WowBot.getMsgStart() + "You need to be in a voice channel first.");
									}
									else {
										wac.loadTrack(sfx.getSoundlist().get(sfx_list[i]), wac.switchVoiceChannel(null, false), null, false);
										System.out.println(WowBot.getMsgStart() + "Playing " + sfx_list[i] + " via hotkey.");
									}
								}
								used[i] = true;
							}
							else if(msg.substring(i, i + 1).equals("-")) {
								int dyn = Integer.parseInt(msg.substring(i + 1, i + 5));
								if(dyn  > dyn_used + 12 || dyn < dyn_used - 12) {
									dyn_used = -1;
									lastDyn[writeDyn] = dyn;
									writeDyn++;
									if(writeDyn >= lastDyn.length) {
										writeDyn = 0;
									}
									boolean dynDone = true;
									for(int j = 1; j < lastDyn.length; j++) {
										if(lastDyn[j - 1] != lastDyn[j]) {
											dynDone = false;
											break;
										}
									}
									
									if(dynDone) {
										dyn_used = dyn;
										if(wac.getVoiceChannel() == null) {
											System.out.println(WowBot.getMsgStart() + "You need to be in a voice channel first.");
										}
										else {
											if(vol) {
												wac.getPlayer().setVolume((int) (dyn / 1023.0 * 100));
												System.out.println(WowBot.getMsgStart() + "Changing volume to " + (int) (dyn / 1023.0 * 100) + " via dynamic hotkey.");
											}
											else {
												int s = 0;
												if(dyn >= 0 && dyn < 256) {
													s = 1;
												}
												else if(dyn >= 256 && dyn < 512) {
													s = 2;
												}
												else if(dyn >= 512 && dyn < 768) {
													s = 3;
												}
												else if(dyn >= 768 && dyn < 1024) {
													s = 4;
												}
												wac.loadTrack(sfx.getSoundlist().get(dynamic_list[s]), wac.switchVoiceChannel(null, false), null, false);
												System.out.println(WowBot.getMsgStart() + "Playing " + dynamic_list[s] + " via dynamic hotkey.");
											}
											
										}
									}
								}
							}
							else if(msg.substring(i, i + 1).equals("0")) {
								used[i] = false;
							}
						}
					}
				}
				catch (SerialPortException ex) {
				    ex.printStackTrace();
				}
			}
		}
	}
}
