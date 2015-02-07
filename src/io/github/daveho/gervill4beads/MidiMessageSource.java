package io.github.daveho.gervill4beads;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.core.BeadArray;

/**
 * Bead for receiving MidiMessages and notifying other beads when
 * a MidiMessage is received.  Listeners can assume that the received
 * message will be an instance of {@link MidiMessageSource} (i.e.,
 * a reference to this object), and they should call {@link #getMessage()}
 * on that object to get the actual MidiMessage, and {@link #getTimeStamp()} to
 * get the midi timestamp.  Midi timestamps should be considered
 * to be synchronized with the AudioContext.
 */
public class MidiMessageSource extends Bead implements Receiver {
	public static int DEFAULT_NUM_DELAY_FRAMES = 1;
	
	private static class MidiMessageAndTimestamp {
		final MidiMessage msg;
		final long timeStamp;
		public MidiMessageAndTimestamp(MidiMessage msg, long timeStamp) {
			this.msg = msg;
			this.timeStamp = timeStamp;
		}
	}
	
	private AudioContext ac;
	private BeadArray listeners;
	private double msPerFrame;
	private int numDelayFrames;
	private volatile long frameRtStartNanos;
	private volatile double frameTimestampMs;
	private Object lock;
	private LinkedList<MidiMessageAndTimestamp> received;
	private MidiMessage message;
	private long timestamp;
	
	public MidiMessageSource(AudioContext ac) {
		this(ac, DEFAULT_NUM_DELAY_FRAMES);
	}
	
	public MidiMessageSource(AudioContext ac, int numDelayFrames) {
		this.ac = ac;
		this.listeners = new BeadArray();
		this.msPerFrame = ac.samplesToMs(ac.getBufferSize());
		this.numDelayFrames = numDelayFrames;
		this.lock = new Object();
		this.received = new LinkedList<>();
		
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
			long rtOffsetNanos = System.nanoTime() - frameRtStartNanos;
			
			// Compute a millisecond timestamp relative to the start of the
			// current frame
			double timeStampMs = frameTimestampMs + (rtOffsetNanos/1000000L);
			
			// Midi timestamp is the millisecond timestamp converted to
			// microseconds, delayed by one or more frames, to avoid any
			// possibility of an event being scheduled for processing in
			// current frame.
			timeStamp = (long) ((timeStampMs + (numDelayFrames*msPerFrame)) * 1000.0);
		}
		
		// Add to received list
		synchronized (lock) {
			received.add(new MidiMessageAndTimestamp(message, timeStamp));
		}
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
		List<MidiMessageAndTimestamp> toProcess = new ArrayList<>();
		// Microsecond timestamp of end of current audio frame.
		// Only midi messages whose timestamps are earlier than the end
		// of this frame are scheduled for delivery.
		double endOfFrameUs = (frameTimestampMs + msPerFrame) * 1000.0;
		synchronized (lock) {
			Iterator<MidiMessageAndTimestamp> i = received.iterator();
			while (i.hasNext()) {
				MidiMessageAndTimestamp msgAndTs = i.next();
				if (msgAndTs.timeStamp <= endOfFrameUs) {
					toProcess.add(msgAndTs);
					i.remove();
				}
			}
		}
		
		// Notify listeners for each received message/timestamp
		for (MidiMessageAndTimestamp msgAndTs : toProcess) {
			message = msgAndTs.msg;
			timestamp = msgAndTs.timeStamp;
			listeners.messageReceived(this);
		}
	}
	
	/**
	 * Get the received MidiMessage.
	 * 
	 * @return the received MidiMessage
	 */
	public MidiMessage getMessage() {
		return message;
	}

	/**
	 * Get the microsecond timestamp of the received MidiMessage.
	 * 
	 * @return microsecond timestamp of the received MidiMessage
	 */
	public long getTimeStamp() {
		return timestamp;
	}

	@Override
	public void close() {
		// Nothing to do at the moment
	}

}
