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

package io.github.daveho.gervill4beads;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.core.UGen;

import com.sun.media.sound.SoftSynthesizer;

/**
 * A UGen that generates audio using the
 * <a href="https://java.net/projects/gervill/pages/Home">Gervill</a>
 * software synthesizer.
 * 
 * @author David Hovemeyer
 */
public class GervillUGen extends UGen {
	private SoftSynthesizer synth;
	private Receiver synthRecv;
	private AudioInputStream synthAis;
	private byte[] sampleBuf;

	/**
	 * Constructor.
	 * 
	 * @param context  the AudioContext
	 * @param info     info, used when opening an audio input stream from the synthesizer
	 * @throws MidiUnavailableException
	 */
	public GervillUGen(AudioContext context, Map<String, Object> info) throws MidiUnavailableException {
		super(context, 2);
		synth = new SoftSynthesizer();
		
		synthRecv = synth.getReceiver();
		
		float sampleRate = context.getSampleRate();
		
		// Create an AudioFormat with same sample rate as AudioContext's,
		// using PCM_FLOAT encoding, 32 bits per sample.  This makes
		// calculateBuffer very simple: we just pull floats from Gervill
		// and copy them into the buffer for each output channel.
		int sampleSize = 32;
		AudioFormat fmt = new AudioFormat(Encoding.PCM_FLOAT, sampleRate, sampleSize, 2, ((2*sampleSize)+7)/8, sampleRate, true);
		synthAis = synth.openStream(fmt, info);
		
		sampleBuf = new byte[fmt.getFrameSize() * bufferSize];
	}
	
	/**
	 * Get the Gervill SoftSynthesizer.
	 * 
	 * @return the Gervill SoftSynthesizer
	 */
	public SoftSynthesizer getSynth() {
		return synth;
	}

	@Override
	protected void messageReceived(Bead message) {
		if (Midi.hasMidiMessage(message)) {
			MidiMessage msg = Midi.getMidiMessage(message);
			long timestamp = Midi.getMidiTimestamp(message);
//			System.out.printf("GervillUGen: received midi message (status=%d,ts=%d)!\n", msg.getStatus(), timestamp);
			synthRecv.send(msg, timestamp);
		}
	}

	@Override
	public void calculateBuffer() {
		try {
			synthAis.read(sampleBuf);
			DataInputStream din = new DataInputStream(new ByteArrayInputStream(sampleBuf));
			for (int i = 0; i < bufferSize; i++) {
				bufOut[0][i] = din.readFloat();
				bufOut[1][i] = din.readFloat();
			}
		} catch (IOException e) {
			throw new RuntimeException("IOException reading data from Gervill synth", e);
		}
	}

}
