package io.github.daveho.gervill4beads.demo;

import io.github.daveho.gervill4beads.Midi;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

/**
 * Demo to play percussion sounds by changing midi events to
 * specify channel 10.
 */
public class DemoPercussion extends Demo {

	public DemoPercussion() {
	}
	
	@Override
	protected void captureMidiMessages(final Receiver receiver) throws MidiUnavailableException {
		// Modify all MidiMessages so that they specify channel 10 (encoded as 9,
		// which I suppose means that 0 indicates midi channel 1)
		Receiver setChannelReceiver = new Receiver() {
			@Override
			public void send(MidiMessage message, long timeStamp) {
				if (message instanceof ShortMessage) {
					ShortMessage sm = (ShortMessage) message;
					byte[] data = sm.getMessage();
					byte orig = data[0];
					data[0] = (byte) ((orig | 9) & 0xff);
					message = Midi.createShortMessage(data);
				}
				receiver.send(message, timeStamp);
			}
			
			@Override
			public void close() {
				receiver.close();
			}
		};
		
		super.captureMidiMessages(setChannelReceiver);
	}
	
	public static void main(String[] args) throws MidiUnavailableException {
		DemoPercussion demo = new DemoPercussion();
		demo.start();
	}
}
