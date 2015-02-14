package io.github.daveho.gervill4beads.demo;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.sound.midi.MidiUnavailableException;

import com.sun.media.sound.SF2Soundbank;
import com.sun.media.sound.SoftSynthesizer;

/**
 * Demo to load a sound font (.sf2) file and play live.
 * Plays with whatever the sound font's default instrument is (?).
 */
public class DemoLoadSoundFont extends Demo {
	private String fileName;

	public DemoLoadSoundFont(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void start() throws MidiUnavailableException {
		try {
			SoftSynthesizer synth = gervill.getSynth();
			
			SF2Soundbank sb = new SF2Soundbank(new File(fileName));
			synth.loadAllInstruments(sb);
			
			super.start();
		} catch (IOException e) {
			throw new RuntimeException("Could not load sound font", e);
		}
	}
	
	public static void main(String[] args) throws MidiUnavailableException {
		Scanner keyboard = new Scanner(System.in);
		String fileName = Util.chooseDirectoryAndFile(keyboard, ".sf2");
		DemoLoadSoundFont demo = new DemoLoadSoundFont(fileName);
		demo.start();
	}
}
