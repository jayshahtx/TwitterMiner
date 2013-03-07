package dataCollection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Add_Locations3 {

	//counts the number of tweets in the US from each file
	public static int UScounts = 0;
	
	//counts num of tweets in each file to know when to create new one
	//temp. add 10 to this - we stopped the program half way
	public static int fileCounts = 0;
	
	//BufferedWriter for allTweets
	public static BufferedWriter writer = null;
	
	//count of output files
	public static Integer fileNum = 0;
	
	/*
	 * The following vars control program input/output
	 * NOTE: Tweets not in US are still included in output file
	 */
	
	//number of tweets per file
	public final static int TWEETS_PER_FILE = 10000;
	
	//directory to write out files
	public static String outDir = "C:\\Users\\User\\Dropbox\\TwitterProject\\CompletedTweets_Located4";
	
	//directory where fils are read from
	//C:\Users\User\Dropbox\TwitterProject\CompletedTweets2
	public static String inDir = "C:\\Users\\User\\Dropbox\\TwitterProject\\CompletedTweets4";
	/**
	 * @param args
	 * @throws IOException 
	 */
	
	@SuppressWarnings("resource")
	//method to read and parse data from input files
	public static void getInput() throws IOException {
		//find the folder where we stored the files
		File folder = new File(inDir);
		File[] listOfFiles = folder.listFiles();
		String fileName = "";
		//iterate through each of the files
		for (int i = 0; i < listOfFiles.length; i++) {
			fileName = listOfFiles[i].getName();
			System.out.println("Getting locations for file: " + fileName);
			
			//scrape contents of each file
			FileInputStream fstream = new FileInputStream(inDir+"\\"+fileName);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				getLocations(strLine);
			}
			in.close();	
			System.out.println();
		}
		
	}
	
	//from each line, get the location of tweet
	public static void getLocations(String str) throws IOException {
		//boolean denoting successful output
		boolean retrievedData = true;
		
		//parse the line using "|||" as the delimiter, extract the coordinates
		String splitted[] = str.split("\\|\\|\\|");
		//get the position where the coordinates are stored, remove them of everything except numbers
		String coords = splitted[2].substring(11);
		String coord_nums[] = coords.split(",");
		String lat = coord_nums[0].substring(coord_nums[0].indexOf("=")+1);
		String lng = coord_nums[1].substring(coord_nums[1].indexOf("=")+1).replaceAll("}", "");
		
		//get the coordinates
		Addresser_FCC finder = new Addresser_FCC();
		String[] locs = new String[4];
		
		//incorrect formatting could yield an incorrect query URL - use try catch to keep program running
		try {
			locs = finder.getLocations(lat, lng, locs);
		}
		catch (FileNotFoundException e) {
			System.out.println("Error retrieving info from FCC API, skipping");
			retrievedData = false;
		}
		
		//output results
		String arrayed = "";
		//compile results 
		for (int i = 0; i < locs.length; i++) {
			arrayed += "|||" + locs[i];
		}
		//write the data to output file
		if (retrievedData) {
			writeOut(str+arrayed);
		}

	}
	//method that handles writing to output files, naming the files, and creating new files
	public static void writeOut(String str) throws IOException {
		//update counts
		UScounts++;
		fileCounts++;
		//instantiate writer if this is the first time calling method
		if (writer == null) {
			writer = new BufferedWriter(new FileWriter(outDir + "\\" + "Analyzed_Located" + fileNum.toString() + ".txt"));
		}
		//create a new file after TWEETS_PER_FILE tweets reached
		if (fileCounts > TWEETS_PER_FILE) {
			fileNum++;
			fileCounts = 0;
			writer.close();
			writer = new BufferedWriter(new FileWriter(outDir + "\\" + "Analyzed_Located" + fileNum.toString() + ".txt"));
		}
		writer.write(str);
		writer.newLine();
		
		//show progress by printing out every 100 tweets
		if (UScounts % 100 == 0)
			System.out.println("Tweets Mapped to County: " + UScounts);
		
		
	}
	
	//kicks off method
	public static void main(String[] args) throws IOException {
		getInput();
	}

}
