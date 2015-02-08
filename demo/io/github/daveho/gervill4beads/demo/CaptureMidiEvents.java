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

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

/**
 * Support for capturing midi input events.
 * 
 * @author David Hovemeyer
 */
public class CaptureMidiEvents {
	/**
	 * Capture midi input events, dispatching them to given Receiver.
	 * The MidiDevice returned is the device providing the input, and
	 * should be closed when input events are no longer needed.
	 * Note that this method returns the first MidiDevice which
	 * has at least one transmitter.
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
