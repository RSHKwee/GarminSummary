package library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

public class NominatimQuery {

  /**
   * 
   * @param urlString
   * @return
   * @throws IOException
   */
  private String getJSON(String urlString) throws IOException {
    URL url = new URL(urlString);
    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    conn.addRequestProperty("User-Agent", "Mozilla/4.76");

    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String text;
    StringBuilder result = new StringBuilder();
    while ((text = in.readLine()) != null)
      result.append(text);

    in.close();
    return result.toString();
  }

  /**
   * Zoom level 18
   * 
   * @param a_Latitude  Latitude
   * @param a_Longitude Longitude
   * @return JSONObject met adres informatie
   */
  public JSONObject getAdress(String a_Latitude, String a_Longitude) {
    return getAdress(a_Latitude, a_Longitude, "18");
  }

  /**
   * Level of detail required for the address. Default: 18. This is a number that
   * corresponds roughly to the zoom level used in map frameworks like Leaflet.js,
   * Openlayers etc. In terms of address details the zoom levels are as follows:
   * zoom address detail 3 country 5 state 8 county 10 city 14 suburb 16 major
   * streets 17 major and minor streets 18 building
   * 
   * 
   * @param a_Latitude  Latitude
   * @param a_Longitude Longitude
   * @param a_Zoom      Level of detail.
   * @return JSONObject met adres informatie
   */
  public JSONObject getAdress(String a_Latitude, String a_Longitude, String a_Zoom) {
    String jsonObj;
    JSONObject v_address = new JSONObject();
    try {
      jsonObj = getJSON("https://nominatim.openstreetmap.org/reverse?format=json&lat=" + a_Latitude + "&lon="
          + a_Longitude + "&zoom=" + a_Zoom + "&addressdetails=1");
      JSONObject v_jsonobj = new JSONObject(jsonObj);
      v_address = v_jsonobj.getJSONObject("address");

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return v_address;
  }
}
