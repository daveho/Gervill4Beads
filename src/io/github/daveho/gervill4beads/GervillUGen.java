package io.github.daveho.gervill4beads;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.core.UGen;

import com.sun.media.sound.SoftSynthesizer;

/**
 * A UGen that generates audio using the
 * <a href="https://java.net/projects/gervill/pages/Home">Gervill</a>
 * software synthesizer.
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
		// using 16 bits per sample.
		AudioFormat fmt = new AudioFormat(sampleRate, 16, 2, true, true);
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
//			System.out.printf("GervillUGen: received midi message (ts=%d)!\n", timestamp);
			synthRecv.send(msg, timestamp);
		}
	}

	@Override
	public void calculateBuffer() {
		try {
			synthAis.read(sampleBuf);
			DataInputStream din = new DataInputStream(new ByteArrayInputStream(sampleBuf));
			for (int i = 0; i < bufferSize; i++) {
				bufOut[0][i] = din.readShort() / (float)32768.0f;
				bufOut[1][i] = din.readShort() / (float)32768.0f;
			}
		} catch (IOException e) {
			throw new RuntimeException("IOException reading data from Gervill synth", e);
		}
	}

}
