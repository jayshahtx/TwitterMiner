package dataCollection;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * This version of Addresses works with FCC's API
 */

public class Addresser_FCC {	
	
	String URL1 = "http://data.fcc.gov/api/block/find?format=json&latitude=";
	String URL2 = "&showall=true";
	
	public Addresser_FCC() {
	}
	
	/*
	 * Parameters are String representations of latitute and longitude 
	 * String[] locations consists of county,county ID, state, and state ID
	*/
	public String[] getLocations(String lat, String lng, String[] locations) throws IOException {
		
		//construct the URL to access
		String LL = lat + "&longitude=" + lng;
		URL url = new URL(URL1 + LL + URL2);
		JSONObject j = null;
		
		try {
			 String output = new Scanner(url.openStream()).useDelimiter("\\A").next();
			 j = (JSONObject) JSONSerializer.toJSON( output );  
		}
		catch (java.util.NoSuchElementException e) {
		    //empty result
		}
		//get the JSONObjects containing state and county information	 
		JSONObject countyInfo = j.getJSONObject("County");
		JSONObject stateInfo = j.getJSONObject("State");
		
		//is this location in the US? If not, set all fields to "OutofUS"
		String details = countyInfo.getString("FIPS");
		
		if (details.equals("null")) {
			locations[0] = "OutofUS";
			locations[1] = "OutofUS";
			locations[2] = "OutofUS";
			locations[3] = "OutofUS";
		}
		//store the information in locations array
		else {
			locations[0] = countyInfo.getString("name");
			locations[1] = countyInfo.getString("FIPS");
			locations[2] = stateInfo.getString("name");
			locations[3] = stateInfo.getString("FIPS");
		}
		//return the parameters we found
		return locations;

	}

}
