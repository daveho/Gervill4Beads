// Gervill4Beads - integrate Gervill with the Beads library
// Copyright (c) 2015, David H. Hovemeyer <david.hovemeyer@gmail.com>
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

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
 * 
 * @author David Hovemeyer
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
