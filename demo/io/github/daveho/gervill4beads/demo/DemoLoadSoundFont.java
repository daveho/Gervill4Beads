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

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.sound.midi.MidiUnavailableException;

import com.sun.media.sound.SF2Soundbank;
import com.sun.media.sound.SoftSynthesizer;

/**
 * Demo to load a sound font (.sf2) file and play live.
 * Plays with whatever the sound font's default instrument is (?).
 * FIXME: I suppose we should allow a patch to be specified?
 * 
 * @author David Hovemeyer
 */
public class DemoLoadSoundFont extends Demo {
	private String fileName;

	public DemoLoadSoundFont(String fileName) {
		this.fileName = fileName;
	}

	@Override
	protected void createGervill() throws MidiUnavailableException {
		super.createGervill();
		
		try {
			SoftSynthesizer synth = gervill.getSynth();
			
			SF2Soundbank sb = new SF2Soundbank(new File(fileName));
			synth.loadAllInstruments(sb);
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
