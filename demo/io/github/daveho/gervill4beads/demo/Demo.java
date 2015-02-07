package io.github.daveho.gervill4beads.demo;

import java.util.Collections;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;

import io.github.daveho.gervill4beads.GervillUGen;
import io.github.daveho.gervill4beads.MidiMessageSource;
import net.beadsproject.beads.core.AudioContext;

/**
 * Demo application: captures midi events from whatever input
 * source is available (e.g., a midi keyboard), constructs
 * a beads AudioContext with a {@link GervillUGen} feeding
 * into the AudioContext's output, and adds the GervillUGen
 * as a listener for midi events. 
 * 
 * <p>When you run the demo, you should be able to play notes
 * using the default instrument (piano) using Gervill's
 * default sound bank.
 */
public class Demo {
	public static void main(String[] args) throws MidiUnavailableException {
		AudioContext ac = new AudioContext();
		
		GervillUGen gervill = new GervillUGen(ac, Collections.emptyMap());
		
		Instrument[] instr = gervill.getSynth().getAvailableInstruments();
		System.out.printf("%d available instruments\n", instr.length);
		
		MidiMessageSource midiSource = new MidiMessageSource(ac, 1);
		midiSource.addMessageListener(gervill);
		MidiDevice device = CaptureMidiEvents.getMidiInput(midiSource);
		
		ac.out.addInput(gervill);
		ac.start();
	}
}
