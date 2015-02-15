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
 * An interface to be implemented by Beads that are a source
 * of MidiMessages.  The idea is that a recipient Bead, in its
 * <code>messageReceived</code> method, should check to see
 * whether the message is an object implementing this interface.
 * If so, it should cast it to <code>MidiMessageSource</code>,
 * then call the {@link #getMessage()} and {@link #getTimeStamp()}
 * methods to get the MidiMessage and its timestamp.  Timestamps
 * may be assumed to be synchronized with the AudioContext.
 * Ideally, Beads implementing this interface will notify
 * recipients of available messages just before the audio frame
 * in which the messages' timestamps occur.
 * 
 * @author David Hovemeyer
 */
public interface MidiMessageSource {
	/**
	 * Get the received MidiMessage.
	 * 
	 * @return the received MidiMessage
	 */
	public MidiMessage getMessage();

	/**
	 * Get the microsecond timestamp of the received MidiMessage.
	 * 
	 * @return microsecond timestamp of the received MidiMessage
	 */
	public long getTimeStamp();
}