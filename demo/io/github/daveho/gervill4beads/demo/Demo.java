package io.github.daveho.gervill4beads.demo;

import io.github.daveho.gervill4beads.GervillUGen;
import io.github.daveho.gervill4beads.MidiMessageSource;

import java.util.Collections;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;

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
	protected AudioContext ac;
	protected GervillUGen gervill;
	protected MidiMessageSource midiSource;
	protected MidiDevice device;
	
	public Demo() throws MidiUnavailableException {
		this.ac = new AudioContext();
		
		this.gervill = new GervillUGen(ac, Collections.emptyMap());
		
		Instrument[] instr = gervill.getSynth().getAvailableInstruments();
		System.out.printf("%d available instruments\n", instr.length);
		
		this.midiSource = new MidiMessageSource(ac, 1);
		midiSource.addMessageListener(gervill);
		this.device = CaptureMidiEvents.getMidiInput(midiSource);
		
		ac.out.addInput(gervill);
	}
	
	public void start() {
		ac.start();
	}
	
	public void close() {
		device.close();
	}
	
	public static void main(String[] args) throws MidiUnavailableException, InvalidMidiDataException {
		Demo demo = new Demo();
		demo.start();
	}
}
