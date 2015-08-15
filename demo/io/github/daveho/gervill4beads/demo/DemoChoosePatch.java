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

import java.util.Scanner;

import javax.sound.midi.MidiUnavailableException;

/**
 * Another live playing demo: works like {@link Demo}, but
 * prompts the user to enter a patch number, e.g., from the
 * <a href="http://www.midi.org/techspecs/gm1sound.php">GM1 sound set</a>.
 * 
 * @author David Hovemeyer
 */
public class DemoChoosePatch extends Demo {
	private int patch;

	public DemoChoosePatch(int patch) {
		this.patch = patch;
	}
	
	@Override
	protected void createMidiSource() {
		super.createMidiSource();
		setPatch(this.patch);
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
