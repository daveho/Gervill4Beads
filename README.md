# Gervill4Beads

This is an experiment to integrate the [Gervill](https://java.net/projects/gervill/pages/Home)
software synthesizer with the [Beads](http://www.beadsproject.net) library, such that a
Gervill `SoftSynthesizer` can be added to a Beads network as an output-only UGen.

## Midi events

Gervill generates audio in response to midi events.  A class called
`MidiMessageSource` is provided as a midi `Receiver`.  Each received
`MidiMessage` is delivered to listener `Bead`s &mdash; i.e., the
`GervillUGen` &mdash; with its midi timestamp synchronized with
the Beads `AudioContext`.

## Limitations

Some details are currently hard-coded: for example, audio output
from the GervillUGen is hard-coded as two channels with 16 bits per sample.

## Compiling, demo app

The repository is an Eclipse project.  Run the command `./fetchlibs.sh` to download the
required jar files.

If you run the main method of the `Demo` class, and you have a midi keyboard connected
to your computer, you should be able to play piano.

## License, contact info

The code is distributed under the MIT license.  See `LICENSE.txt` for details.

Please send comments to <david.hovemeyer@gmail.com>