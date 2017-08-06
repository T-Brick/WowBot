package com.estrelsteel.wowbot.command.audio;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import net.dv8tion.jda.core.audio.AudioSendHandler;

public class AudioSender implements AudioSendHandler {

	private File f;
	private byte[] b;
	private int off;
	private int f_len;
	
	public AudioSender(File f) {
		this.f = f;
		this.off = 0;
		this.f_len = (int) (882 * 1);
		this.f_len = 64 * 20;
		this.b = new byte[f_len];
	}
	
	@Override
	public boolean canProvide() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			AudioInputStream ais = AudioSystem.getAudioInputStream(f);
			byte[] buffer = new byte[1024];
			int c;
			while((c = ais.read(buffer)) > 0) {
				baos.write(buffer, 0, c);
			}
			baos.flush();
			byte[] temp = baos.toByteArray();
			for(int i = 0; i < f_len && off < temp.length; i++, off++) {
				b[i] = temp[off];
			}
//			ais.close();
			return true;
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
		catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	@Override
	public byte[] provide20MsAudio() {
		return b;
	}
	
	public boolean isOpus() {
		return false;
	}
	
}