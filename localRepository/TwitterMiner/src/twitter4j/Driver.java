package twitter4j;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class Driver {
	
	//keys to access twitter API - register your app with Twitter
	private static String CONSUMER_KEY = "KSLxW76QxmSUZ2QQvIPq7g";
	private static String CONSUMER_SECRET = "09hnGy5WEtn7rWYASaF5oumOjQNyUgeLaSnuVAfnYkU";
	private static String ACCESS_TOKEN = "249999118-viQXaUp63aL1kxORNB9069ER16EkPh2sQJDKK1Ws";
	private static String ACCESS_TOKEN_SECRET = "lLO2iqezwTe1WSqUBSg1RzcpCAX0lymI4CVbhBAfI";
	
	//count of tweets recorded before new fie
	public static int tweetCount = 0;
	
	//count of geo tweets
	public static int geoCount = 0;
	
	//determines when to pause
	public static int throttleCount = 0;
	
	//total tweets over duration or program
	public static int allTweets = 0;
	
	//tweets to mine per cycle
	public static int TWEETS_PER_CYCLE = 500;
	
	//number of cycles to mine
	public static int TWEET_CYCLES = 100;
	
	//twitter object
	public static Twitter twitter;
	
	//twitterStream
	public static TwitterStream twitterStream;
	
	//listener that will be used
	public static StatusListener listener;
	
	//BufferedWriter for allTweets
	public static BufferedWriter allOut = null;
	
	//BufferedWriter for geoTweets
	public static BufferedWriter geoOut = null;
	
	//month
	public static int month;
	
	//day
	public static int day;
	
	//hour
	public static int hour;
	
	//minute;
	public static int minute;
	
	//update the values for the date/time
	public static void setUpTime() {
		Calendar cal = Calendar.getInstance();
		month = cal.get(Calendar.MONTH);
		day = cal.get(Calendar.DAY_OF_MONTH);
		hour = cal.get(Calendar.HOUR_OF_DAY);
		minute = cal.get(Calendar.MINUTE);
	}
	
	public static void setUpWriters(String prefix) throws IOException {
		  
		  if (prefix.equals("all")) {
			  File all = new File("E:\\TwitterData\\" + prefix + "Tweets" + "_" + month + "_" + day + "_" + hour + "_" + minute + ".txt");
			  allOut = new BufferedWriter(new FileWriter(all));
		  }
		  if (prefix.equals("geo")) {
			  File geo = new File("E:\\TwitterData\\" + prefix + "Tweets" + "_" + month + "_" + day + "_" + hour + "_" + minute + ".txt");
			  geoOut = new BufferedWriter(new FileWriter(geo));
		  }
		  
	  }
	
	//method to setup TwitterStream and add a listener
	public static void setUpStream() throws TwitterException, IOException {
		 ConfigurationBuilder cb = new ConfigurationBuilder();
		 cb.setOAuthConsumerKey(CONSUMER_KEY);
		 cb.setOAuthConsumerSecret(CONSUMER_SECRET);
		 cb.setOAuthAccessToken(ACCESS_TOKEN);
		 cb.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
		 
		 //instantiate TwitterSream object
		 Configuration a = cb.build();
		 twitterStream = new TwitterStreamFactory(a).getInstance();
		 twitter = new TwitterFactory(a).getInstance();
		 
	}
	
	//sets up listerner and implements all necessary classes
	public static void setUpListener() throws IOException {
		
		 listener = new StatusListener() {
	            @Override
	            public void onStatus(Status status) {
	            	setUpTime();
	            	throttleCount++;
	            	tweetCount++;
	            	System.out.println("Total count of all tweet: " + allTweets++);
	            	
	            	//write to different file if this tweet is geoCoded
	                if (status.getGeoLocation() != null) {
							try {
								geoOut.write(status.getUser().getScreenName() + "|||" + status.getText() + "|||" +status.getGeoLocation().toString() + "|||" + day + " - " + hour + ":" + minute);
								geoOut.newLine();
								System.out.println(geoCount++);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								System.out.println("TOTAL COUNTED: " +tweetCount);
							}
	                }
	                
	                //write all tweets to the "all" file regardless
	                try {
						allOut.write(status.getUser().getScreenName() + "|||" + status.getText()+ "|||" + day + " - " + hour + ":" + minute);
						allOut.newLine();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                //exit sample stream & close writers once we get to certain # of tweets
	                if (throttleCount >= TWEETS_PER_CYCLE) {
	                	throttleCount = 0;
	                	twitterStream.shutdown();
	                }
	                	
	            }

	            @Override
	            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
//	                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
	            }

	            @Override
	            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
	                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
	            }

	            @Override
	            public void onScrubGeo(long userId, long upToStatusId) {
	                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
	            }

	            @Override
	            public void onStallWarning(StallWarning warning) {
	                System.out.println("Got stall warning:" + warning);
	            }

	            @Override
	            public void onException(Exception ex) {
	                ex.printStackTrace();
	            }
		 };
	}
		
	
	public static void main(String[] args) throws TwitterException, IOException {
		int i = 0;
		setUpTime();
		setUpWriters("all");
		setUpWriters("geo");
		
			
		
		while (i < TWEET_CYCLES) {
		 setUpStream();
		 setUpListener();
			 
		 if (tweetCount >= 1000) {
			 allOut.flush();
		 	 setUpTime();
		 	 setUpWriters("all");
		 	 tweetCount = 0;
		}
		
		if (geoCount >= 1000) {
			geoOut.flush();
			setUpTime();
			setUpWriters("geo");
			geoCount = 0;
		}
		
		 twitterStream.addListener(listener);
		 twitterStream.sample();
		 
		 try {
  			 Thread.sleep(20400);
  			 } catch(InterruptedException e) {
  		 } 
		 
		 try {
	            Map<String ,RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus();
	            for (String endpoint : rateLimitStatus.keySet()) {
	                RateLimitStatus status = rateLimitStatus.get(endpoint);
	                System.out.println("Endpoint: " + endpoint);
	                System.out.println(" Limit: " + status.getLimit());
	                System.out.println(" Remaining: " + status.getRemaining());
	                System.out.println(" ResetTimeInSeconds: " + status.getResetTimeInSeconds());
	                System.out.println(" SecondsUntilReset: " + status.getSecondsUntilReset());
	            }
	            
	        } catch (TwitterException te) {
	            te.printStackTrace();
	            System.out.println("Failed to get rate limit status: " + te.getMessage());
	         
	        }		
	
		 i++;		 
		}
	}
}
