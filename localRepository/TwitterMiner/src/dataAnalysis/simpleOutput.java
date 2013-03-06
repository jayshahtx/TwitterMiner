package dataAnalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;


/*
 * Simple class to take in twitter information and output it in CSV format for Google Fusion Tables
 */

public class simpleOutput {

	//dir where tweets are stored
	public static final String TWEET_INPUT_DIR = "C:\\Users\\Jay\\Documents\\Dropbox\\TwitterProject\\CompletedTweets_Located";

	//directory where CSV values will be output
	public static final String CSV_DIR = "C:\\Users\\Jay\\Documents\\Dropbox\\TwitterProject\\Results\\GoogleOutputPolarized.txt";


	//drives input/output
	public static void main(String[] args) throws IOException {
		//count tweets outputed
		int tweetCount = 0;

		//set up output writer
		File out = new File(CSV_DIR);
		BufferedWriter writer = new BufferedWriter(new FileWriter(out));

		File folder = new File(TWEET_INPUT_DIR);
		File[] listOfFiles = folder.listFiles();
		String fileName = "";

		//iterate through each of the files
		for (int i = 0; i < listOfFiles.length; i++) {
			fileName = listOfFiles[i].getName();
			System.out.println("Getting Twitter Data from file: " + fileName);

			//scrape contents of each file
			FileInputStream fstream = new FileInputStream(TWEET_INPUT_DIR+"\\"+fileName);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				String[] allFields = strLine.split("\\|\\|\\|");
				String outTemp = "";
				for (int i1 = 0; i1 < allFields.length; i1++) {
					outTemp += allFields[i1]+"|||d";
				}

				//only output polarized tweets
				try {
					int sentiment =  Integer.parseInt(allFields[4]);
					if (sentiment != 2) {
						writer.write(outTemp);
						writer.newLine();
						tweetCount++;
					}
				}
				catch (NumberFormatException e){
						System.out.println("Formatting Error");
				}
			}
			br.close();
		}
		//close reader/writer
		writer.close();
		System.out.println("Total Tweets in Output: " +tweetCount);
	}
}
