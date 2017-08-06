package com.estrelsteel.wowbot.command.audio;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import net.dv8tion.jda.core.audio.AudioConnection;
import net.dv8tion.jda.core.audio.AudioSendHandler;

public class OldAudioSender implements AudioSendHandler {
	
	public static final int PCM_FRAME_SIZE = 4;
    private byte[] buffer = new byte[AudioConnection.OPUS_FRAME_SIZE * PCM_FRAME_SIZE];
    private File file;
 	
	public OldAudioSender(File file) {
		this.file = file;
	}

    @Override
    public boolean canProvide() {
    	try {
    		AudioInputStream in = AudioSystem.getAudioInputStream(file);
            int amountRead = in.read(buffer, 0, buffer.length);
            if (amountRead > -1) {
                if (amountRead < buffer.length) {
                    Arrays.fill(buffer, amountRead, buffer.length - 1, (byte) 0);
                }
                short sample;
                for (int i = 0; i < buffer.length; i+=2) {
                    sample = (short)((buffer[i + 1] & 0xff) | (buffer[i] << 8));
                    buffer[i + 1] = (byte)(sample & 0xff);
                    buffer[i] = (byte)((sample >> 8) & 0xff);
                }
                return true;
            }
            else {
                return false;
            }
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
        return buffer;
    }
/*
	private File file;
	private byte[] b;
	private byte[] audioBytes;
	private int pos;
 	
	public AudioSender(File file) {
		this.file = file;
	}
	
	@Override
	public boolean canProvide() {
		try {
			AudioInputStream in = AudioSystem.getAudioInputStream(file);

			b = new byte[1024];
			
			in.read(b);
			return true;
		}
		catch(IOException e) {
			e.printStackTrace();
		} 
		catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
		if(audioBytes == null) {
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				AudioInputStream in = AudioSystem.getAudioInputStream(file);
			
				int read;
				byte[] buff = new byte[1024];
				while ((read = in.read(buff)) > 0) {
					out.write(buff, 0, read);
				}
				out.flush();
				audioBytes = out.toByteArray();
				pos = 0;
			}
			catch(IOException | UnsupportedAudioFileException e) {
				e.printStackTrace();
			}
		}
		b = new byte[20];
		if(pos >= audioBytes.length) {
			return false;
		}
		for(int i = 0; i < 20 && pos < audioBytes.length; i++, pos++) {
			b[i] = audioBytes[pos];
		}
		return true;
	}

	@Override
	public byte[] provide20MsAudio() {
		return b;
	}
*/
}
