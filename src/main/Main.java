package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class Main {

	public static void main(String[] args) throws IOException {
		File sourceMal = new File("D:/Disassembly/original/malware");
		File sourceBen = new File("D:/Disassembly/original/benign");
		File outputBen = new File("C:\\Users\\ColbyAdmin\\Desktop\\Disassembly\\Original\\benign");
		File outputMal = new File("C:\\Users\\ColbyAdmin\\Desktop\\Disassembly\\Original\\malware");
		
		File [] sourceBenFiles = sourceBen.listFiles();
		File [] sourceMalFiles = sourceMal.listFiles();

		ArrayList<File> benList = new ArrayList<File>(Arrays.asList(sourceBenFiles));
		ArrayList<File> malList = new ArrayList<File>(Arrays.asList(sourceMalFiles));
		
		Random rand = new Random();
		int count = 0;
		/*
		while(count < 1000) {
				int  n = rand.nextInt(benList.size());
				File f = benList.get(n);
				benList.remove(f);
				count++;
				File output = new File(outputBen.toPath() + File.separator + f.getName());
				FileUtils.copyDirectory(f, output);
		}
		
		count = 0;
		while(count < 1000) {
			int  n = rand.nextInt(malList.size());
			File f = malList.get(n);
			malList.remove(f);
			count++;
			File output = new File(outputMal.toPath() + File.separator + f.getName());
			FileUtils.copyDirectory(f, output);
	}*/
		
		File[] malSourceList = outputMal.listFiles();
		File[] benSourceList = outputBen.listFiles();

		//Move all disassembly into source folders
		System.out.println("MOVING");
		
		count = 0;
		for(File f: malSourceList) {
			moveFromSubDirs(f.getPath());
			count++;
			System.out.println(count);
		}
		count = 0;
		for(File f: benSourceList) {
			moveFromSubDirs(f.getPath());
			count++;
			System.out.println(count);
		}

		//Combine all files into single field
		File comOutDirMal = new File("D:/Disassembly/combined/malware");
		File comOutDirBen = new File("D:/Disassembly/combined/benign");
		comOutDirMal.mkdirs();
		comOutDirBen.mkdirs();
		
		System.out.println("COMBINING");
		
		count = 0;
		for(File f: malSourceList) {
			combineToSingleFile(f.getPath(), comOutDirMal.getPath());
			count++;
			System.out.println(count);
		} 
		count = 0;
		for(File f: benSourceList) {
			combineToSingleFile(f.getPath(), comOutDirBen.getPath());
			count++;
			System.out.println(count);
		}

		//Clean the Disassembly
		File cleanOutDirMal = new File("D:/Disassembly/cleaned/malware");
		File cleanOutDirBen = new File("D:/Disassembly/cleaned/benign");
		cleanOutDirMal.mkdirs();
		cleanOutDirBen.mkdirs();
		
		System.out.println("CLEANING");
		cleanDisassembly(comOutDirMal.getPath(), cleanOutDirMal.getPath());
		cleanDisassembly(comOutDirBen.getPath(), cleanOutDirBen.getPath());
		
		//Create the pair lists
		System.out.println("PAIRING");
		pairListCreator("D:/Disassembly/cleaned");
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


	public static void cleanDisassembly(String s, String d) {
		File source = new File(s);
		File[] fileList = source.listFiles();

		//Read in the dictionary
		BufferedReader br = null;
		BufferedWriter notInDiction = null;
		List<String> dictionary = new ArrayList<String>();
		List<String> NID = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader("C:/Users/colbyadmin/Desktop/school/AndroidCT/Dalvik_Dictionary_Sorted.txt"));
			String line = br.readLine();
			while(line != null) {
				dictionary.add(line);
				line = br.readLine();
			}
			notInDiction = new BufferedWriter(new FileWriter("C:/Users/colbyadmin/Desktop/school/AndroidCT/NotInDictionary.txt"));
			br.close();
		} catch (IOException e) {
			System.out.println("Error opening dictionary file");
			e.printStackTrace();
		}

		int count = 0;
		for(File f: fileList) {
			BufferedWriter output = null;
			try {
				output = new BufferedWriter(new FileWriter(d + File.separator + f.getName()));
			} catch (IOException e) {
				System.out.println("Error opening output file for cleaned disassembly");
				e.printStackTrace();
			}
			List<String> opcodes = new ArrayList<String>();
			readInDisassembly(f, opcodes);
			for(String o: opcodes) {
				if(dictionary.contains(o)) {
					try {
						output.write(o);
						output.write(" ");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (!NID.contains(o)) {
					NID.add(o);
				}
			}
			try {
				output.close();
			} catch (IOException e) {
				System.out.println("Error closing output file");
				e.printStackTrace();
			}
			count++;
			System.out.println(count);
		}
		for(String n: NID) {
			try {
				notInDiction.write(n);
				notInDiction.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			notInDiction.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void readInDisassembly(File f, List<String> opcodes) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = br.readLine();
			while(line != null) {
				String[] words = line.trim().split(" ");
				if(words[0].equals(".end")) {
					opcodes.add(words[0] + "-" + words[1]);
				} else {
					opcodes.add(words[0]);
				}
				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			System.out.println("Error reading in Disassembly");
		}
	}

	public static void pairListCreator(String s) {
		int maxStepSize = 10;

		for(int stepSize = 1; stepSize <= maxStepSize; stepSize++) {
			// Access the directory containing the directories that contain the files
			File sourceDir = new File(s);

			// Create an array of the directories
			File[] dirs = sourceDir.listFiles();

			// Create hashmap and iterator
			HashMap<String, Integer> listOfPairs = new HashMap<String, Integer>();
			int count = 0;

			// Loop through directories
			for(File dir: dirs) {
				// Create list of files in current directory
				File[] dirFiles = dir.listFiles();

				// Loop through files
				for(File f: dirFiles) {
					count++;
					try {
						// Convert file to array list
						List<String> theFile = Files.readAllLines(f.toPath(), Charset.defaultCharset() );
						String[] words = theFile.get(0).split(" ");
						List<String> wordsList = new ArrayList<String>(Arrays.asList(words));

						// Go through the file
						for(int i = 0;i < wordsList.size() - stepSize;i++) {
							if(!wordsList.get(i).equals(".end-method")) {	
								List<String> l = new ArrayList<String>();
								for(int j = 1; j < stepSize; j++) {
									l.add(wordsList.get(i + j));
								}
								if(!l.contains(".end-method")) {
									String p = wordsList.get(i) + " " + wordsList.get(i + stepSize);
									// Add pairing to hashmap
									listOfPairs.put(p, count);
								}
							}
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			// Create sets from hashmaps
			Set<String> uniqueValues = new HashSet<String>(listOfPairs.keySet()); 
			// Output number of unique values in the set
			System.out.println("The number of unique words for step size " + stepSize + ": "+uniqueValues.size());

			// Output pairs to file
			BufferedWriter bw;
			try {
				bw = new BufferedWriter(new FileWriter("D:/Pair-Lists/Pair" + stepSize + ".txt"));
				for(String u: uniqueValues) {
					bw.write(u);
					bw.newLine();
				}
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
