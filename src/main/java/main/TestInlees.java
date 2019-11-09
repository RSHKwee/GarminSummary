package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;

public class TestInlees {
  private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

  public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
    InputStream is = new URL(url).openStream();
    try {

      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      String jsonText = readAll(rd);
      JSONObject json = new JSONObject(jsonText);
      return json;
    } finally {
      is.close();
    }
  }

  public static JSONObject readJsonFromUrl(HttpURLConnection con) throws IOException, JSONException {
    con.connect();
    InputStream is = con.getInputStream();
    try {
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      String jsonText = readAll(rd);
      JSONObject json = new JSONObject(jsonText);
      return json;
    } finally {
      is.close();
    }
  }

  private static String getJSON(String urlString) throws IOException {
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

  /*
   * Level of detail required for the address. Default: 18. This is a number that
   * corresponds roughly to the zoom level used in map frameworks like Leaflet.js,
   * Openlayers etc. In terms of address details the zoom levels are as follows:
   * zoom address detail 3 country 5 state 8 county 10 city 14 suburb 16 major
   * streets 17 major and minor streets 18 building
   * 
   */
  public static void main(String[] args) {
    try {
      GPX gpx = GPX.reader(GPX.Version.V11)
          .read("D:\\Users\\René\\OneDrive\\Documenten\\Auto\\Garmin\\Tracks\\2019\\Archive\\279.gpx");

      List<Track> v_tracks = gpx.getTracks();
      v_tracks.forEach(v_track -> {
        List<TrackSegment> v_segments = v_track.getSegments();
        System.out.println(v_track.toString() + " " + v_segments.toString());
        v_segments.forEach(v_segment -> {
          List<WayPoint> v_waypoints = v_segment.getPoints();
          System.out.println(v_waypoints.toString());
          System.out.println(
              "Start Long: " + v_waypoints.get(0).getLongitude() + " Lat: " + v_waypoints.get(0).getLatitude());
          int v_eind = v_waypoints.size() - 1;
          System.out.println("Eind  Long: " + v_waypoints.get(v_eind).getLongitude() + " Lat: "
              + v_waypoints.get(v_eind).getLatitude());
          System.out.println(gpx.toString());

          String json0;
          try {
            json0 = getJSON(
                "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + v_waypoints.get(0).getLatitude()
                    + "&lon=" + v_waypoints.get(0).getLongitude() + "&zoom=18&addressdetails=1");
            System.out.println("Start:" + json0.toString());
            JSONObject v_jsonobj = new JSONObject(json0);
            JSONObject v_address = v_jsonobj.getJSONObject("address");
            // JSONObject v_jsonobjadd = new JSONObject(v_jsonobj.get("address"));
            System.out.println(v_address.toString());

            // json0.
            json0 = getJSON(
                "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + v_waypoints.get(v_eind).getLatitude()
                    + "&lon=" + v_waypoints.get(v_eind).getLongitude() + "&zoom=18&addressdetails=1");
            System.out.println("Eind :" + json0.toString());

          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        });
      });

      System.out.println(gpx.toString());

      String json0 = getJSON(
          "https://nominatim.openstreetmap.org/reverse?format=json&lat=52.093392&lon=5.108171&zoom=18&addressdetails=1");
      System.out.print(json0.toString());

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
