package io.github.daveho.gervill4beads.demo;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Scanner;

public class Util {
	public static String chooseDirectoryAndFile(Scanner scanner, final String fileExt) {
		System.out.print("Which directory? ");
		String dirName = scanner.nextLine();
		File dir = new File(dirName);
		if (!dir.exists() || !dir.isDirectory()) {
			throw new RuntimeException("No such directory");
		}
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(fileExt);
			}
		});
		for (int i = 0; i < files.length; i++) {
			System.out.printf("[%d] %s\n", i, files[i].getName());
		}
		System.out.println("Load which file? ");
		int choice = scanner.nextInt();
		return files[choice].getPath();
	}
}
