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

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Map;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Synthesizer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.core.UGen;

/**
 * A UGen that generates audio using the
 * <a href="https://java.net/projects/gervill/pages/Home">Gervill</a>
 * software synthesizer.
 * 
 * @author David Hovemeyer
 */
public class GervillUGen extends UGen {
	private Synthesizer synth;
	private Receiver synthRecv;
	private AudioInputStream synthAis;
	private ByteBuffer byteBuffer;
	private FloatBuffer floatBuffer;

	/**
	 * Constructor.
	 * 
	 * @param context  the AudioContext
	 * @param info     info, used when opening an audio input stream from the synthesizer
	 * @throws MidiUnavailableException
	 */
	public GervillUGen(AudioContext context, Map<String, Object> info) throws MidiUnavailableException {
		super(context, 2);
		
		// Use reflection to instantiate a SoftSynthesizer object.
		// We definitely do NOT want to do this via MidiSystem, since
		//   (1) we won't necessarily get a SoftSynthesizer, and
		//   (2) it won't allow multiple SoftSynthesizers to be open
		//       at the same time
		try {
			Class<?> synthCls = Class.forName("com.sun.media.sound.SoftSynthesizer");
			Object synthObj = synthCls.newInstance();
			this.synth = (Synthesizer) synthObj;
		} catch (ClassNotFoundException e) {
			throw new MidiUnavailableException("Could not find SoftSynthesizer class: " + e.toString());
		} catch (Throwable e) {
			throw new MidiUnavailableException("Could not create SoftSynthesizer object: " + e.toString());
		}

		// Get the synthesizer's MIDI Receiver
		synthRecv = synth.getReceiver();

		// Use the Beads AudioContext's sample rate
		float sampleRate = context.getSampleRate();
		
		// Create an AudioFormat with same sample rate as AudioContext's,
		// using PCM_FLOAT encoding, 32 bits per sample.  This makes
		// calculateBuffer very simple: we just pull floats from Gervill
		// and copy them into the buffer for each output channel.
		int sampleSize = 32;
		AudioFormat fmt = new AudioFormat(Encoding.PCM_FLOAT, sampleRate, sampleSize, 2, ((2*sampleSize)+7)/8, sampleRate, true);

		// The AudioSynthesizer/SoftSynthesizer types aren't exported.
		// So, cheat and use reflection to call openStream.  There literally
		// doesn't seem to be any other way of getting an AudioInputStream
		// from a Synthesizer in Java!
		try {
			Method openStream = synth.getClass().getMethod("openStream", new Class[]{AudioFormat.class, Map.class});
			this.synthAis = (AudioInputStream) openStream.invoke(synth, fmt, info);
		} catch (Throwable e) {
			throw new MidiUnavailableException("Could not get openStream method for Synthesizer: " + e.toString());
		}

		// Use a ByteBuffer (with a FloatBuffer view) to store audio data
		// produce by Gervill.  This is a simple and efficient mechanism
		// for grabbing the data.
		byteBuffer = ByteBuffer.allocate(fmt.getFrameSize() * bufferSize);
		floatBuffer = byteBuffer.asFloatBuffer();
	}
	
	/**
	 * Get the Gervill SoftSynthesizer.
	 * 
	 * @return the Gervill SoftSynthesizer
	 */
	public Synthesizer getSynth() {
		return synth;
	}
	
	/**
	 * Get the SoftSynthesizer's midi Receiver.
	 * 
	 * @return the SoftSynthesizer's midi Receiver
	 */
	public Receiver getSynthRecv() {
		return synthRecv;
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
			synthAis.read(byteBuffer.array());
			for (int i = 0; i < bufferSize; i++) {
				bufOut[0][i] = floatBuffer.get(i*2);
				bufOut[1][i] = floatBuffer.get(i*2 + 1);
			}
		} catch (IOException e) {
			throw new RuntimeException("IOException reading data from Gervill synth", e);
		}
	}
}
