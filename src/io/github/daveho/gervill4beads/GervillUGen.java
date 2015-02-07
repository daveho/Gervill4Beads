package io.github.daveho.gervill4beads;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import com.sun.media.sound.SoftSynthesizer;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.core.UGen;

public class GervillUGen extends UGen {
	private SoftSynthesizer synth;
	private Receiver synthRecv;
	private AudioInputStream synthAis;

	public GervillUGen(AudioContext context, Map<String, Object> info) throws MidiUnavailableException {
		super(context, 2);
		synth = new SoftSynthesizer();
		
		synthRecv = synth.getReceiver();
		
		float sampleRate = context.getSampleRate();
		
		// Create an AudioFormat with same sample rate as AudioContext's,
		// using 16 bits per sample.
		AudioFormat fmt = new AudioFormat(sampleRate, 16, 2, true, true);
		synthAis = synth.openStream(fmt, info);
		
	}
	
	@Override
	protected void messageReceived(Bead message) {
		if (Midi.hasMidiMessage(message)) {
			MidiMessage msg = Midi.getMidiMessage(message);
			long timestamp = Midi.getMidiTimestamp(message);
			System.out.printf("GervillUGen: received midi message (ts=%d)!\n", timestamp);
			synthRecv.send(msg, timestamp);
		}
	}

	@Override
	public void calculateBuffer() {
		try {
			DataInput din = new DataInputStream(synthAis);
			for (int i = 0; i < bufferSize; i++) {
				bufOut[0][i] = din.readShort() / (float)32768.0f;
				bufOut[1][i] = din.readShort() / (float)32768.0f;
			}
		} catch (IOException e) {
			throw new RuntimeException("IOException reading data from Gervill synth", e);
		}
	}

}
