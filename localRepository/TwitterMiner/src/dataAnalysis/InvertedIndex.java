package dataAnalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import dataCollection.County;

/*
 * Class that stores twitter data + census information at the county level
 */

public class InvertedIndex {

	//dir where tweets are stored
	public static final String TWEET_INPUT_DIR = "C:\\Users\\User\\Dropbox\\TwitterProject\\MasterCollection";

	//directory where census data is stored
	public static final String CENSUS_DATA_DIR = "C:\\Users\\User\\Dropbox\\TwitterProject\\Census\\DataRelevant.txt";

	//directory where CSV values will be output
	public static final String CSV_DIR = "C:\\Users\\User\\Dropbox\\TwitterProject\\Results\\Final2.txt";

	//directory where stopwords are stored
	public static final String STOPWORDS_DIR = "C:\\Users\\User\\Dropbox\\TwitterProject\\Census\\DataRelevant.txt";
	
	//directory to output custom data - this method is modified for quick testing and serves no definite purpose
	public static final String CUSTOM_DIR = "C:\\Users\\User\\Dropbox\\TwitterProject\\Results\\custom2.txt";

	//hashmap that stores stopwords
	public static HashMap<String,Integer> stopwords = new HashMap<String,Integer>();

	//inverted index used to map FIPS > County data
	public static HashMap<String,County> dataSet = new HashMap<String,County>();
	
	//count of all tweets identified
	public static int totalCount = 0;
	
	//count of tweets in the US analyzed
	public static int USCount = 0;
	
	//count of tweets with an error
	public static int errorCount = 0;

	/*
	 * Method to input Census data
	 */
	public static void getTwitterData() throws IOException {
		//set up directory of files
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
				totalCount++;

				//check if tweet was from US - we use try/catch in case of incorrect formatting
				try {
					if (!allFields[5].equals("OutofUS")) {
						USCount++;
						String countyName = allFields[5];
						String FIPS = allFields[6];
						String[] tweet = allFields[1].split(" "); 
						int sentiment =  Integer.parseInt(allFields[4]);

						//retrieve County instance to update values
						County temp = addCounty(FIPS,countyName);
						temp.addTweet(sentiment);
						temp.addWords(tweet, stopwords);
					}
				}

				catch (Exception e) {
					System.out.println("Error occured: " + e.getCause());
					System.out.println();
					errorCount++;
				}
			}
			in.close();
		}
	}

	/*
	 * Creates county in HashMap (if necessary) 
	 */
	public static County addCounty(String FIPS, String countyName) {
		if (dataSet.containsKey(FIPS)) 
			return dataSet.get(FIPS);
		else {
			County temp = new County(countyName);
			dataSet.put(FIPS, temp);
			return temp;
		}	
	}

	/*
	 * Reads in input from the census file 
	 */
	public static void getCensusData() throws IOException {
		FileInputStream fstream = new FileInputStream(CENSUS_DATA_DIR);
		// Get the object of DataInputStream
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;

		//Read File Line By Line 
		while ((strLine = br.readLine()) != null)   {
			String [] splitted = strLine.split(",");
			String FIPS = splitted[0];

			//extract the values from the text file 
			int totalPop = Integer.parseInt(splitted[1]);
			double femalePop = Double.parseDouble(splitted[2]);
			int homeVal = Integer.parseInt(splitted[3]);
			int incomePercap = Integer.parseInt(splitted[4]);
			double povertyPer = Double.parseDouble(splitted[5]);
			double popDen = Double.parseDouble(splitted[6]);

			//place the values in the county object
			if (dataSet.containsKey(FIPS)) {
				County temp = dataSet.get(FIPS);
				temp.totalPopulation = totalPop;
				temp.femalePopulation = femalePop;
				temp.homeValue = homeVal;
				temp.perCapitaIncome = incomePercap;
				temp.percentInPoverty = povertyPer;
				temp.popPerSquareMile = popDen;
			}
		}
		in.close();
	}
	//method to output files in CSV format for quick regressions in Excel
	public static void writeData() throws IOException {
		File out = new File(CSV_DIR);
		BufferedWriter writer = new BufferedWriter(new FileWriter(out));
		Set<String> counties = dataSet.keySet();
		//write headers
		writer.write("countyName" +
				"," + "polarSentiment" +
				"," + "totalPopulation" +
				"," + "femalePopulation" +
				"," + "homeValue" + 
				"," + "perCapitaIncome" + 
				"," + "percentInPoverty" + 
				"," + "popPerSquareMile");
		writer.newLine();
		//write actual data
		for (String FIPS : counties) {
			County temp = dataSet.get(FIPS);
			if (temp.polarTweets >= 5 && temp.totalPopulation > 0) {
				writer.write(dataSet.get(FIPS).CSV());
				writer.newLine();
			}
		}
		writer.close();
	}

	/*
	 * Loads stopwords from text file into hashmap
	 */
	public static void loadStopWords() throws IOException {
		FileInputStream fstream = new FileInputStream(STOPWORDS_DIR);
		// Get the object of DataInputStream
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		int count = 0;

		//Read File Line By Line 
		while ((strLine = br.readLine()) != null)   {
			stopwords.put(strLine, 1);
			count++;
		}
		System.out.println("Loaded " + count + " stopwords");
	}
	/*
	 * Custom method to print out various data I am curious about
	 */
	public static void writeCustom() throws IOException {
		//make a treemap of the counties - they key values are the average sentiment
		TreeMap<Double,County> ranked = new TreeMap<Double,County>();	
		Set<String> keys = dataSet.keySet();
		for (String k : keys) {
			County temp = dataSet.get(k);
			ranked.put(temp.getSentiment(), temp);
		}		
		//write data about each county
		File out = new File(CUSTOM_DIR);
		BufferedWriter writer = new BufferedWriter(new FileWriter(out));	
		
		for (Double x : ranked.keySet()) {
			County temp = ranked.get(x);
			writer.write("County: " + temp.countyName + ", Sentiment: " + temp.sentiment + ", Tweets: " + temp.tweets);
			writer.newLine();
			writer.write("Dictionary: " + temp.printDictionary());
			writer.newLine();
			writer.newLine();
		}
		writer.close();
		System.out.println("Custom data successfully written in folder: " + CUSTOM_DIR);
	}
	
	/*
	 * Method to print out statistics of data set
	 */
	public static void printStats() {
		System.out.println("Total Tweets in DataSet: " + totalCount);
		System.out.println("US Tweets analyzed: " + USCount);
		System.out.println("Errors encountered: " + errorCount);
		System.out.println("Number of counties analyzed: " + dataSet.size());
	}


	/*
	 * Drives program
	 */
	public static void main(String[] args) throws IOException {
		loadStopWords();
		getTwitterData();
		getCensusData();
		writeData();
		writeCustom();
		printStats();


	}

}
