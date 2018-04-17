package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//moveFromSubDirs("C:\\Users\\colbyadmin\\Desktop\\out");
		
		//combineToSingleFile("C:\\Users\\colbyadmin\\Desktop\\out", "C:\\Users\\colbyadmin\\Desktop\\output");
		
		cleanDisassembly("C:\\users\\colbyadmin\\Desktop\\output");
	}

	/**
	 * This method recursively goes through the sub folders of a given source and copies those files back
	 * into the source.
	 * 
	 * Finished
	 * 
	 * @param s: the source to start from
	 */
	public static void moveFromSubDirs(String s) {
		File source = new File(s);
		File[] files = source.listFiles();

		for(File f: files) {
			if(f.isDirectory()) {
				moveFromSubDirs(f.getPath());
				File[] subFileList = f.listFiles();
				for(File sf: subFileList) {
					if(!sf.isDirectory()) {
						try {
							File output = new File(source.toPath() + File.separator + sf.getName());
							Files.move(sf.toPath(), output.toPath(), StandardCopyOption.REPLACE_EXISTING);
						} catch (IOException e) {
							System.out.println("Error moving file");
							e.printStackTrace();
						}
					}
				}
				if(f.list().length == 0) {
					f.delete();
				}
			}
		}
	}

	
	/**
	 * This method reads in all the files in a folder and combines them into one file
	 * 
	 * Finished
	 * 
	 * @param s
	 * @param output
	 */
	public static void combineToSingleFile(String s, String output) {
		File source = new File(s);
		File[] files = source.listFiles();
		
		BufferedWriter outputFile = null;
		try {
			outputFile = new BufferedWriter(new FileWriter(output + File.separator + source.getName() + ".txt"));
		} catch (IOException e) {
			System.out.println("Error creating output file");
			e.printStackTrace();
		}
		
		for(File f: files) {
			try {
				BufferedReader input = new BufferedReader(new FileReader(f));
				String line = input.readLine();
				while(line != null) {
					outputFile.write(line);
					line = input.readLine();
					outputFile.newLine();
				}
				input.close();
			} catch (IOException e) {
				System.out.println("Error opening one of the source files");
				e.printStackTrace();
			}
		}
		
		try {
			outputFile.close();
		} catch (IOException e) {
			System.out.println("Error closing output file");
			e.printStackTrace();
		}
	}


	public static void cleanDisassembly(String s) {
		File source = new File(s);
		File[] fileList = source.listFiles();
		
		for(File f: fileList) {
			List<String> opcodes = new ArrayList<String>();
			readInDisassembly(f, opcodes);
		}
	}
	
	public static void readInDisassembly(File f, List<String> opcodes) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = br.readLine();
			while(line != null) {
				String[] words = line.split(" ");
				System.out.println(words[0]);
				line = br.readLine();
			}
		} catch (Exception e) {
			
		}
	}
}
