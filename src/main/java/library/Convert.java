package library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;
//import org.json.

public class Convert {

	/**
	 *
	 * @param lng
	 * @param lat
	 * @return
	 */
	    private String getAddressByGpsCoordinates(String lng, String lat)
	            throws MalformedURLException, IOException {
	         
	        URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json?latlng="
	                + lat + "," + lng + "&sensor=true");
	        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
	        String formattedAddress = "";
	 
	        try {
	            InputStream in = url.openStream();
	             BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	            String result, line = reader.readLine();
	            result = line;
	            while ((line = reader.readLine()) != null) {
	                result += line;
	            }
	 
//	            JSONParser parser = new JSONParser();
//	            JSONObject rsp = (JSONObject) parser.parse(result);
	 
//	            if (((Object) rsp).containsKey("results")) {
//	                JSONArray matches = (JSONArray) rsp.get("results");
//	                JSONObject data = (JSONObject) matches.get(0); //TODO: check if idx=0 exists
//	                formattedAddress = (String) data.get("formatted_address");
//	            }
	 
	            return "";
	        } finally {
	            urlConnection.disconnect();
	            return formattedAddress;
	        }
	    }

}
