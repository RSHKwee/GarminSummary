package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

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

  public static void main(String[] args) {
    try {
      // GPX gpx = GPX.reader(GPX.Version.V11)
//          .read("F:\\Users\\René\\OneDrive\\Documenten\\Auto\\Garmin\\Tracks\\2019\\Archive\\279.gpx");

      // System.out.println(gpx.toString());

      String json0 = getJSON(
          "https://nominatim.openstreetmap.org/reverse?format=json&lat=52.093392&lon=5.108171&zoom=18&addressdetails=1");
      System.out.print(json0.toString());

      URL url = new URL(
          "https://nominatim.openstreetmap.org/reverse?format=json&lat=52.093392&lon=5.108171&zoom=18&addressdetails=1");
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestProperty("User-agent",
          "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
      con.setRequestProperty("User-Agent",
          "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
      JSONObject json = readJsonFromUrl(con);
      System.out.print(json.toString());

      URLConnection hc = url.openConnection();
      hc.setRequestProperty("User-Agent",
          "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
      hc.setRequestProperty("User-agent",
          "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
      System.out.println(hc.getContentType());

      JSONObject json1 = readJsonFromUrl(
          "https://nominatim.openstreetmap.org/reverse?format=json&lat=52.093392&lon=5.108171&zoom=18&addressdetails=1");

      System.out.print(json1.toString());

      URL url1 = new URL(
          "https://nominatim.openstreetmap.org/reverse?format=xml&lat=52.093392&lon=5.108171&zoom=18&addressdetails=1");
      URLConnection conn = url1.openConnection();

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(conn.getInputStream());

      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer xform = transformerFactory.newTransformer();

      // that’s the default xform; use a stylesheet to get a real one
      xform.transform(new DOMSource(doc), new StreamResult(System.out));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (TransformerException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SAXException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
