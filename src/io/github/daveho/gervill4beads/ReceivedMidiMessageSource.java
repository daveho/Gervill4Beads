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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.core.BeadArray;

/**
 * Implementation of {@link MidiMessageSource} that implements the midi
 * <code>Receiver</code> interface.  Realtime messages (those with a
 * timestamp of -1) are assigned a timestamp synchronized with the
 * AudioContext's timing information.  In general, all messages
 * will be delivered to recipient Beads just before the audio frame
 * in which the message occurs (by default, immediately before the
 * frame).  Use {@link #addMessageListener(Bead)} to add recipient Beads.
 * 
 * @author David Hovemeyer
 */
public class ReceivedMidiMessageSource extends Bead implements Receiver, MidiMessageSource {
	/**
	 * By default, received MidiMessages are assigned timestamps this
	 * many frames in the future (to avoid trying to deliver midi messages
	 * while the current frame's audio is being computed.)
	 */
	public static int DEFAULT_NUM_DELAY_FRAMES = 1;
	
	private AudioContext ac;
	private BeadArray listeners;
	private double msPerFrame;
	private int numDelayFrames;
	private volatile long frameRtStartNanos;
	private volatile double frameTimestampMs;
	private ConcurrentSkipListMap<Long, MidiMessage> received;
	private MidiMessage message;
	private long timestamp;
	
	/**
	 * Constructor.
	 * 
	 * @param ac the AudioContext
	 */
	public ReceivedMidiMessageSource(AudioContext ac) {
		this(ac, DEFAULT_NUM_DELAY_FRAMES);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param ac              the AudioContext
	 * @param numDelayFrames  received MidiMessages are assigned timestamps this many
	 *                        frames in the future
	 */
	public ReceivedMidiMessageSource(AudioContext ac, int numDelayFrames) {
		this.ac = ac;
		this.listeners = new BeadArray();
		this.msPerFrame = ac.samplesToMs(ac.getBufferSize());
		this.numDelayFrames = numDelayFrames;
		this.frameRtStartNanos = -1L;
		this.frameTimestampMs = 0.0;
		this.received = new ConcurrentSkipListMap<Long, MidiMessage>();
		
		// Schedule a message before every audio frame: we use this
		// to compute timestamps for incoming MidiMessages, relative
		// to the AudioContext's time 0, and also to dispatch received
		// messages to listeners.
		ac.invokeBeforeEveryFrame(new Bead() {
			@Override
			protected void messageReceived(Bead message) {
				frameStart();
			}
		});
	}
	
	/**
	 * Add a listener Bead, which will receive a message (with this Bead as
	 * the message) when a MidiMessage is received.  The recipient can
	 * invoke the {@link #getMessage()} and {@link #getTimeStamp()}
	 * methods to get the midi message data.
	 * 
	 * @param bead a listener Bead to add
	 */
	public void addMessageListener(Bead bead) {
		listeners.add(bead);
	}

	@Override
	public void send(MidiMessage message, long timeStamp) {
//		System.out.println("Received midi message!");

		if (timeStamp < 0L) {
			// Real-time message!
			
			// Offset of this event (occurring right now, in real time)
			// from the beginning of the frame.
			long rtOffsetNanos;
			
			if (frameRtStartNanos < 0L) {
				// Audio processing hasn't started.  We'll schedule
				// this message for processing at the beginning of the
				// first frame.
				rtOffsetNanos = 0L;
			} else {
				rtOffsetNanos = System.nanoTime() - frameRtStartNanos;
			}
			
			// Compute a millisecond timestamp relative to the start of the
			// current frame
			double timeStampMs = frameTimestampMs + (rtOffsetNanos/1000000.0);
			
			// Midi timestamp is the millisecond timestamp converted to
			// microseconds, delayed by one or more frames, to avoid any
			// possibility of an event being scheduled for processing in
			// current frame.
			timeStamp = (long) ((timeStampMs + (numDelayFrames*msPerFrame)) * 1000.0);
		}
		
		// Add to received list
		received.put(timeStamp, message);
	}

	private void frameStart() {
		// Update frame start timestamps
		
		// Real time start of frame in nanoseconds.
		// This is for processing realtime messages, so that they
		// can be assigned a midi timestamp that is synchronized
		// with the AudioContext.
		frameRtStartNanos = System.nanoTime();
		
		// AudioContext time in milliseconds.
		frameTimestampMs = ac.getTime();

		// Collect all of the messages which should be processed
		// in this audio frame
		List<MidiMessageAndTimeStamp> toProcess = new ArrayList<MidiMessageAndTimeStamp>();

		// Microsecond timestamp of end of current audio frame.
		// Only midi messages whose timestamps are earlier than the end
		// of this frame are scheduled for delivery.
		double endOfFrameUs = (frameTimestampMs + msPerFrame) * 1000.0;

		// Schedule all MidiMessages whose timestamps indicate this frame
		// for dispatching to listeners
		Long ts;
		while (!received.isEmpty() && (ts = received.firstKey()) < endOfFrameUs) {
			MidiMessage value = received.remove(ts);
			toProcess.add(new MidiMessageAndTimeStamp(value, ts));
		}
		
		// Notify listeners for each received message/timestamp
		for (MidiMessageAndTimeStamp msgAndTs : toProcess) {
			message = msgAndTs.msg;
			timestamp = msgAndTs.timeStamp;
			listeners.messageReceived(this);
		}
	}
	
	/* (non-Javadoc)
	 * @see io.github.daveho.gervill4beads.MidiMessageSource#getMessage()
	 */
	@Override
	public MidiMessage getMessage() {
		return message;
	}

	/* (non-Javadoc)
	 * @see io.github.daveho.gervill4beads.MidiMessageSource#getTimeStamp()
	 */
	@Override
	public long getTimeStamp() {
		return timestamp;
	}

	@Override
	public void close() {
		// Nothing to do at the moment
	}

}
