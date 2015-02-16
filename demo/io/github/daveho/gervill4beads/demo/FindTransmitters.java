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

import io.github.daveho.gervill4beads.CaptureMidiMessages;

import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;

/**
 * This program just lists all of the MidiDevices
 * that are advertised as having one of more
 * Transmitters.
 * 
 * @author David Hovemeyer
 */
public class FindTransmitters {
	public static void main(String[] args) throws MidiUnavailableException {
		List<MidiDevice> transmitterDevices = CaptureMidiMessages.getAvailableTransmitters();
		System.out.printf("Found %d transmitters\n", transmitterDevices.size());
		for (MidiDevice device : transmitterDevices) {
			MidiDevice.Info info = device.getDeviceInfo();
			System.out.println(info.getName() + ": " + info.getDescription());
		}
	}
}
