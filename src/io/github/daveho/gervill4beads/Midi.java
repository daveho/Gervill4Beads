package io.github.daveho.gervill4beads;

import javax.sound.midi.MidiMessage;

import net.beadsproject.beads.core.Bead;

/**
 * Helper methods for working with midi messages.
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
}
