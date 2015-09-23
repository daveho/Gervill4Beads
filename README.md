# Gervill4Beads

This is an experiment to integrate the [Gervill](https://java.net/projects/gervill/pages/Home)
software synthesizer with the [Beads](http://www.beadsproject.net) library, such that a
Gervill `SoftSynthesizer` can be added to a Beads network as an output-only UGen.

Gervill is built into the Java 7 and Java 8 standard library, and Gervill4Beads
will use the built-in version.  So, Gervill4Beads requires at least Java 7,
and the only dependency is Beads.  Note that the classes and interfaces
needed to read audio data from Gervill aren't made public in Java 7/8,
and Gervill4Beads uses reflection to work around this limitation.  This is
fairly dodgy, and could easily stop working in a future Java release.

## Midi events

Gervill generates audio in response to midi events.  A class called
`MidiMessageSource` is provided as a midi `Receiver`.  Each received
`MidiMessage` is delivered to listener `Bead`s &mdash; i.e., the
`GervillUGen` &mdash; with its midi timestamp synchronized with
the Beads `AudioContext`.

## Limitations

Some details are currently hard-coded: for example, audio output
from the GervillUGen is hard-coded as two channels with 32 bit PCM_FLOAT
encoding.

The code currently assumes that when the `GervillUGen`'s `calculateBuffer` method
is called, a full frame of audio will be available from the `AudioInputStream`
reading Gervill's output.

## Compiling, demo app

The repository is an Eclipse project.  Run the command `./fetchlibs.sh` to download the
required jar files.

If you run the main method of the `Demo` class, and you have a midi keyboard connected
to your computer, you should be able to play piano.  The `DemoChoosePatch` program
is similar, except that it allows you to choose a midi patch (see the
[GM1 sound set](http://www.midi.org/techspecs/gm1sound.php)).

## License, contact info

The code is distributed under the MIT license.  See `LICENSE.txt` for details.

Please send comments to <david.hovemeyer@gmail.com>
