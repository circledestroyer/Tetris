package tetris;

import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {
	public static Clip loadMusic(String dir) {
		try {
		Clip song = AudioSystem.getClip();
		song.open(AudioSystem.getAudioInputStream(Sound.class.getResource(dir)));
		return song;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
