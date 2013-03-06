/*
 * Copyright 2007 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package twitter4j;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

/**
 * <p>This is a code example of Twitter4J Streaming API - sample method support.<br>
 * Usage: java twitter4j.examples.PrintSampleStream<br>
 * </p>
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public final class PrintSampleStream {

	//keys to access twitter API
	private static String CONSUMER_KEY = "KSLxW76QxmSUZ2QQvIPq7g";
	private static String CONSUMER_SECRET = "09hnGy5WEtn7rWYASaF5oumOjQNyUgeLaSnuVAfnYkU";
	private static String ACCESS_TOKEN = "249999118-viQXaUp63aL1kxORNB9069ER16EkPh2sQJDKK1Ws";
	private static String ACCESS_TOKEN_SECRET = "lLO2iqezwTe1WSqUBSg1RzcpCAX0lymI4CVbhBAfI";
	
    public static void main(String[] args) throws TwitterException {
    	ConfigurationBuilder cb = new ConfigurationBuilder();
		 cb.setOAuthConsumerKey(CONSUMER_KEY);
		 cb.setOAuthConsumerSecret(CONSUMER_SECRET);
		 cb.setOAuthAccessToken(ACCESS_TOKEN);
		 cb.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
    	
    	
        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
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
        twitterStream.addListener(listener);
        twitterStream.sample();
    }
}
