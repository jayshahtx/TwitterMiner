package dataCollection;

import java.util.Scanner;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class Addresser_Google {

	/**
	 * This version of Addresser works with Google's API
	 */
	
	private static final String URL = "http://maps.googleapis.com/maps/api/geocode/json";
		
	public Addresser_Google() {
	}
	
	//paramters are String representations of latitute and longitude
	//String[] locations consists of county,city,and country in that order
	public String[] getLocations(String lat, String lng, String[] locations) throws IOException {
		String LL = "latlng=" + lat + "," + lng;
		URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json?" + LL + "&sensor=false");
		JSONObject j = null;
		
		try {
			 String output = new Scanner(url.openStream()).useDelimiter("\\A").next();
			 j = (JSONObject) JSONSerializer.toJSON( output );  
		}
		catch (java.util.NoSuchElementException e) {
		    //empty result
		}
			 

		//access the block of JSON elements pertaining to the full address 
		JSONArray results = j.getJSONArray("results");
		JSONObject more = results.getJSONObject(0);
		JSONArray address_list = (JSONArray) more.get("address_components");

		//which of the fields is the city, county, and country? Store them after finding them
		for (int i = 0; i < address_list.length();) {
			JSONObject temp = address_list.getJSONObject(i);
			JSONArray types = (JSONArray) temp.get("types");

			for (int n = 0; n < types.length(); n++) {
				//is this the county?
				if (types.get(n).equals("administrative_area_level_2")) {
					JSONObject temp2 = address_list.getJSONObject(i);
					locations[0] = temp2.getString("long_name");
				}
				//is this the city?
				if (types.get(n).equals("locality")) {
					JSONObject temp2 = address_list.getJSONObject(i);
					locations[1] = temp2.getString("long_name");
				}
				//is this the country?
				if (types.get(n).equals("country")) {
					JSONObject temp2 = address_list.getJSONObject(i);
					locations[2] = temp2.getString("long_name");
				}
			} 
		} 
		//return the parameters we found
		return locations;

	}

}
