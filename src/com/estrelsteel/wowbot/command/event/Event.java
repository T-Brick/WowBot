package com.estrelsteel.wowbot.command.event;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.estrelsteel.wowbot.WowBot;

public class Event {
	public static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss");
	public static SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy 'at' kk:mm:ss");
	public static final int version = 0;
	public static final String split = "/";
	
	private String name;
	private String desc;
	private Calendar cal;
	private Alert alert;
	private String[] display;
	
	public Event(String name) {
		this.name = name;
		desc = "This event does not have a description";
		cal = (Calendar) Calendar.getInstance().clone();
		alert = Alert.NONE;
		display = new String[2];
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return desc;
	}
	
	public Calendar getCalendar() {
		return cal;
	}
	
	public Alert getAlert() {
		return alert;
	}
	
	public String[] getDisplay() {
		return display;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDescription(String desc) {
		this.desc = desc;
	}
	
	public void setCalendar(Calendar cal) {
		this.cal = cal;
	}
	
	public void setAlert(Alert alert) {
		this.alert = alert;
	}
	
	public void setDisplay(String[] display) {
		this.display = display;
	}
	
	private static String[] createArgs(String line) {
		return line.split(split);
	}
	
	private void loadCal(String arg) {
		String[] dr = arg.trim().split("-");
		int[] d = new int[dr.length];
		for(int i = 0; i < dr.length; i++) {
			d[i] = Integer.parseInt(dr[i].trim());
		}
		if(d.length >= 6) getCalendar().set(d[0], d[1] - 1, d[2], d[3], d[4], d[5]);
		else if(d.length >= 5) getCalendar().set(d[0], d[1] - 1, d[2], d[3], d[4]);
		else if(d.length >= 3) getCalendar().set(d[0], d[1] - 1, d[2]);
		else System.err.println(WowBot.getMsgStart() + "ERROR: Invalid date format.");
	}
	
	public Event loadEvent(String line) {
		String[] args = createArgs(line);
		if(version == Integer.parseInt(args[0].trim())) {
			setName(args[1].trim());
			setDescription(args[2].trim());
			setAlert(Alert.values()[Integer.parseInt(args[3].trim())]);
			loadCal(args[4]);
			loadDisplay();
		}
		else {
			System.err.println(WowBot.getMsgStart() + "ERROR: Invalid event version.");
		}
		return this;
	}
	
	private String getSaveCal() {
		return sf.format(cal.getTime()).toString();
	}
	
	public String saveEvent() {
		return version + split + getName() + split + getDescription() + split + alert.ordinal() + split + getSaveCal();
	}
	
	private String[] addDisplayEnds(String[] d) {
		for(int i = 0; i < d.length; i++) {
			for(int j = d[i].length(); j < EventManager.header.length() - 1; j++) {
				d[i] = d[i] + " ";
			}
			d[i] = d[i] + "|";
		}
		return d;
	}
	
	public void loadDisplay() {
		String[] d = new String[2];
		d[0] = "| " + name.toUpperCase() + " > " + desc;
		d[1] = "| " + df.format(cal.getTime()).toString();
		addDisplayEnds(d);
		display = d;
	}
	
	public String getDisplayMessage() {
		String s = "";
		for(int i = 0; i < display.length; i++) {
			s = s + display[i] + "\n";
		}
		s = s + EventManager.header;
		return s;
	}
}
