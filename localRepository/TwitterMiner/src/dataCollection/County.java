package dataCollection;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/*
 * Class that represents all information about a county - stored in an inverted index and used to perform analysis
 */
public class County {
	//number of tweets from this category
	public double tweets = 0.0;
	
	//average score of tweets
	public double sentiment = 0.0;
	
	//number of polarized tweets
	public double polarTweets = 0.0;
	
	//average score of non-nuetral tweets
	public double polarSentiment = 0.0;
	
	//name of county
	public String countyName = "";
	
	//HashMap of words in tweets
	public HashMap<String,Integer> dictionary = new HashMap<String,Integer>();
	
	/*
	 * PARAMTERS FOR CENSUS DATASET
	 */
	
	//population of county - UNITS = people
	public int totalPopulation = 0;
	
	//percent of female population - UNITS = percent
	public double femalePopulation = 0.0;
	
	//median house value - UNITS = dollar
	public double homeValue = 0.0;
	
	//per capita income in the past 12 months - UNITS = dollar
	public double perCapitaIncome = 0.0;
	
	//people in poverty - UNITS = percent
	public double percentInPoverty = 0.0;
	
	//population per square mile - UNITS = people
	public double popPerSquareMile = 0.0;
	
	/*
	 * Constructor only accepts county name, the rest are added later
	 */
	public County(String name) {
		countyName = name;		
	}
	
	/*
	 * method to update sentiment of county in constant time
	 */
	public void addTweet(int score) {
		//update polar score if this tweet is non nuetral
		if (score != 2) {
			addPolarizedTweet(score);
		}
		//otherwise, update sentiment
		tweets++;
		double scoreToAdd = score/tweets;	
		double fraction = (tweets-1)/tweets;
		sentiment = sentiment * (fraction);
		sentiment = sentiment + scoreToAdd;		
	}
	/*
	 * Method to add a non-nuetral tweet
	 */
	private void addPolarizedTweet(int score) {
		polarTweets++;
		double scoreToAdd = score/polarTweets;	
		double fraction = (polarTweets-1)/polarTweets;
		polarSentiment = polarSentiment * (fraction);
		polarSentiment = polarSentiment + scoreToAdd;	
	}
	/*
	 * Returns countyName
	 */
	public String getName() {
		return countyName;
	}
	/*
	 * Returns avg sentiment value
	 */
	public double getSentiment() {
		return sentiment;
	}
	/*
	 * Add a tweet to dictionary so we can compare word frequency
	 */
	public void addWords(String[] tweet, HashMap<String,Integer> stopWords) {
		for (String temp : tweet) {
			//convert to lowercase
			temp = temp.toLowerCase();
			//check if the word is a common word, do not include in dictionary if it is not
			if (!stopWords.containsKey(temp) && temp!= null) {
				//update count if word already exists in dictionary
				if (dictionary.containsKey(temp))
					dictionary.put(temp, dictionary.get(temp)+1);
				else
					dictionary.put(temp, 1);
			}
		}
	}
	/*
	 * Prints out sorted list of most common words in this county
	 */
	public String printDictionary() {
		TreeMap<Integer,LinkedList<String>> sortedDic = new TreeMap<Integer,LinkedList<String>>();
		//iterate through dictionary, using count of words as new key
		for (Map.Entry<String, Integer> entry : dictionary.entrySet()) {
			String word = entry.getKey();
			int count = entry.getValue();
			
			//add a linkedlist in sortedDic if it does not exist already
			if (!sortedDic.containsKey(count*-1)) {
				LinkedList<String> list = new LinkedList<String>();
				sortedDic.put(count*-1, list);
			}
			//append word to linkedlist
			LinkedList<String> temp = sortedDic.get(count*-1);
			temp.add(word);
		}
		return sortedDic.toString();
	}

	/*
	 * Prints out values in 1 line
	 */	
	public String toString() {
		return ("County: " + countyName +
				", Sentiment: " + polarSentiment +
				", Total Population: " + totalPopulation +
				", Female Populatio: " + femalePopulation +
				", Home Value: " + homeValue + 
				", Per Capita Income: " + perCapitaIncome + 
				", % in Poverty: " + percentInPoverty + 
				", Pop/Sq Mi: " + popPerSquareMile);
	}
	/*
	 * Prints out values in CSV format
	 */
	public String CSV() {
		return (countyName +
				"," + polarSentiment +
				"," + totalPopulation +
				"," + femalePopulation +
				"," + homeValue + 
				"," + perCapitaIncome + 
				"," + percentInPoverty + 
				"," + popPerSquareMile);
	}
	

}
