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

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

import net.beadsproject.beads.core.Bead;

/**
 * Helper methods for working with midi messages.
 * 
 * @author David Hovemeyer
 */
public class Midi {
	/**
	 * Get the note from a MidiMessage.
	 * 
	 * @param msg a MidiMessage
	 * @return the note
	 */
	public static int getNote(MidiMessage msg) {
		return msg.getMessage()[1];
	}
	
	/**
	 * Get the velocity from a MidiMessage.
	 * 
	 * @param msg a MidiMessage
	 * @return the velocity
	 */
	public static int getVelocity(MidiMessage msg) {
		return msg.getMessage()[2];
	}

	/**
	 * Check whether the given Bead has a midi message to deliver.
	 * 
	 * @param bead a Bead (message sender)
	 * @return true if the Bead has a midi message to deliver, false otherwise
	 */
	public static boolean hasMidiMessage(Bead bead) {
		return bead instanceof MidiMessageSource;
	}

	/**
	 * Get the current MidiMessage from given Bead.
	 * Assumes that {@link #hasMidiMessage(Bead)} returned true.
	 * 
	 * @param message a Bead (message sender)
	 * @return the current MidiMessage
	 */
	public static MidiMessage getMidiMessage(Bead message) {
		return ((MidiMessageSource)message).getMessage();
	}

	/**
	 * Get the current midi timestamp from given Bead.
	 * Assumes that {@link #hasMidiMessage(Bead)} returned true.
	 * 
	 * @param message a Bead (message sender)
	 * @return the current midi timestamp
	 */
	public static long getMidiTimestamp(Bead message) {
		return ((MidiMessageSource)message).getTimeStamp();
	}
	
	/**
	 * Create a ShortMessage from given byte array.
	 * Throws a RuntimeException if the data is not a valid
	 * midi messsage.
	 * 
	 * @param data message data, should have between 1 and 4 bytes
	 * @return a ShortMessage
	 */
	public static ShortMessage createShortMessage(byte[] data) {
		try {
			switch (data.length) {
			case 1:
				return new ShortMessage(data[0] & 0xff);
			case 2:
				return new ShortMessage(data[0] & 0xff, data[1] & 0xff, 0);
			case 3:
				return new ShortMessage(data[0] & 0xff, data[1] & 0xff, data[2] & 0xff);
			case 4:
				return new ShortMessage(data[0] & 0xff, data[1] & 0xff, data[2] & 0xff, data[3] & 0xff);
			default:
				throw new RuntimeException("Wrong data size for short message: " + data.length);
			}
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException("Invalid midi data", e);
		}
	}
}
