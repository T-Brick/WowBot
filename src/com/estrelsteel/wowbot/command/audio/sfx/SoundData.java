package com.estrelsteel.wowbot.command.audio.sfx;

public class SoundData {

	private String path;
	private long start; // ms
	private long end;	// ms
	
	public SoundData(String path, long start, long end) {
		this.path = path;
		this.start = start;
		this.end = end;
	}
	
	public SoundData(String path, long start) {
		this(path, start, -1);
	}
	
	public SoundData(String path) {
		this(path, 0, -1);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getStartTime() {
		return start;
	}

	public long getEndTime() {
		return end;
	}
	
	public String toString() {
		return start + " " + end + " " + path;
	}
}
