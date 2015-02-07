package io.github.daveho.gervill4beads.demo;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

/**
 * Support for capturing midi input events.
 */
public class CaptureMidiEvents {
	/**
	 * Capture midi input events, dispatching them to given Receiver.
	 * The MidiDevice returned is the device providing the input, and
	 * should be closed when input events are no longer needed.
	 * 
	 * @param receiver the Receiver to which midi input events should be dispatched
	 * @return the MidiDevice providing the input events
	 * @throws MidiUnavailableException if midi input can't be found
	 */
	public static MidiDevice getMidiInput(Receiver receiver) throws MidiUnavailableException {
		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		for (MidiDevice.Info info : infos) {
			MidiDevice device;
			device = MidiSystem.getMidiDevice(info);
			System.out.println("Found: " + device);
			
			int maxTransmitters = device.getMaxTransmitters();
			System.out.println("  Max transmitters: " + maxTransmitters);
			
			if (maxTransmitters == -1 || maxTransmitters > 0) {
				Transmitter transmitter = device.getTransmitter();
				transmitter.setReceiver(receiver);
				device.open();
				return device;
			}
		}
		
		throw new MidiUnavailableException("Could not find any midi input sources");
	}
	
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
		getMidiInput(new MyReceiver());
		
		while (true) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				System.out.println("Interrupted?");
			}
		}
	}
}
