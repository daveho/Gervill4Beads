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

import javax.sound.midi.MidiMessage;

/**
 * A class to encapsulate a MidiMessage and its timestamp.
 * Useful for queueing or recording MidiMessages for later
 * delivery or playback.
 * 
 * @author David Hovemeyer
 */
public class MidiMessageAndTimeStamp {
	/** The MidiMessage. */
	public final MidiMessage msg;
	
	/** The timestamp. */
	public final long timeStamp;
	
	/**
	 * Constructor.
	 * 
	 * @param msg        the message
	 * @param timeStamp  the timestamp
	 */
	public MidiMessageAndTimeStamp(MidiMessage msg, long timeStamp) {
		this.msg = msg;
		this.timeStamp = timeStamp;
	}
}
