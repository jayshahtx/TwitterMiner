package twitter4j;

import java.util.ArrayList;
import java.util.Date;

import twitter4j.conf.ConfigurationBuilder;

public class Tester {
	
	//keys to access twitter API
	private static String CONSUMER_KEY = "KSLxW76QxmSUZ2QQvIPq7g";
	private static String CONSUMER_SECRET = "09hnGy5WEtn7rWYASaF5oumOjQNyUgeLaSnuVAfnYkU";
	private static String ACCESS_TOKEN = "249999118-viQXaUp63aL1kxORNB9069ER16EkPh2sQJDKK1Ws";
	private static String ACCESS_TOKEN_SECRET = "lLO2iqezwTe1WSqUBSg1RzcpCAX0lymI4CVbhBAfI";
	
	//twitter object
	public static Twitter twitter;
	
	//method to create Twitter object
	public static Twitter makeTwitter() {
		 ConfigurationBuilder cb = new ConfigurationBuilder();
		 cb.setOAuthConsumerKey(CONSUMER_KEY);
		 cb.setOAuthConsumerSecret(CONSUMER_SECRET);
		 cb.setOAuthAccessToken(ACCESS_TOKEN);
		 cb.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
		 Twitter twitter = new TwitterFactory(cb.build()).getInstance();
				 
		 return twitter;
	
	}
	
	
	public static void main(String[] args) {
		 
		 Twitter twitter = makeTwitter();
		 
		 
		 double lat = 45.49985;
		 double lon = 9.191093;
		 double res = 25;
		 String resUnit="mi";
		 
		 
		 Query query = new Query().geoCode(new GeoLocation(lat,lon), res, resUnit);
		 query.setCount(100);
		 try {
			QueryResult result = twitter.search(query);
			ArrayList<Status> tweets = (ArrayList<Status>) result.getTweets();
			
			for (int i=0; i < tweets.size(); i++) {
				Status tweet = (Status)tweets.get(i);
				
				if (tweet.getGeoLocation()!= null){
	                GeoLocation loc=tweet.getGeoLocation();
	                double myLon = loc.getLongitude();
	                double myLat = loc.getLatitude();
	                System.out.println("@" + tweet.getUser() + ": longitude: "+ myLon + " latitude: "+myLat );     
	                System.out.println("result n° " + i);

	              }
				
			}
			
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		

	}
}
