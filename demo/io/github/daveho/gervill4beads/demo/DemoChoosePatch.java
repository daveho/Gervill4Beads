package io.github.daveho.gervill4beads.demo;

import java.util.Scanner;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;

/**
 * Another live playing demo: works like {@link Demo}, but
 * prompts the user to enter a patch number, e.g., from the
 * <a href="http://www.midi.org/techspecs/gm1sound.php">GM1 sound set</a>.
 * 
 * @author David Hovemeyer
 */
public class DemoChoosePatch extends Demo {
	private int patch;

	public DemoChoosePatch(int patch) throws MidiUnavailableException {
		super();
		this.patch = patch;
	}

	@Override
	public void start() {
		super.start();
		try {
			// By sending a PROGRAM_CHANGE message to the MidiMessageSource
			// Bead, it will be dispatched to the Gervill SoftSynthesizer
			midiSource.send(new ShortMessage(ShortMessage.PROGRAM_CHANGE, patch, 0), -1);
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException("This should not happen", e);
		}
	}
	
	public static void main(String[] args) throws MidiUnavailableException {
		@SuppressWarnings("resource")
		Scanner keyboard = new Scanner(System.in);
		System.out.print("Choose a patch: ");
		int patch = keyboard.nextInt();
		DemoChoosePatch demo = new DemoChoosePatch(patch);
		demo.start();
	}
}
