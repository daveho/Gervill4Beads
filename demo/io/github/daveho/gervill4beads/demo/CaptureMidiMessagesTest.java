package io.github.daveho.gervill4beads.demo;

import io.github.daveho.gervill4beads.CaptureMidiMessages;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;

public class CaptureMidiMessagesTest {
	
	// Just for testing
	static class MyReceiver implements Receiver {

		@Override
		public void send(MidiMessage message, long timeStamp) {
			int status = message.getStatus();
			System.out.println("Recieved MidiMessage@" + timeStamp + ", status=" + status);
			if (status == 144) {
				byte[] data = message.getMessage();
				int note = data[1];
				int velocity = data[2];
				System.out.printf("note=%d, velocity=%d\n", note, velocity);
			} else if (status == 128) {
				
			}
		}

		@Override
		public void close() {
			System.out.println("Closing...");
		}
	}
	
	// Just for testing
	public static void main(String[] args) throws MidiUnavailableException {
		CaptureMidiMessages.getMidiInput(new MyReceiver());
		
		while (true) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				System.out.println("Interrupted?");
			}
		}
	}

}
