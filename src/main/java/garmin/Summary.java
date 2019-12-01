package garmin;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import library.Address;
import library.NominatimAPI;
import library.TimeConversion;

public class Summary {
  private ArrayList<String> m_Regels;
  private String m_GPXFile;

  public Summary(String a_GPXFile) {
    m_GPXFile = a_GPXFile;
  }

  public ArrayList<String> TripsSummary(String a_GPXFile) {
    m_GPXFile = a_GPXFile;
    return TripsSummary();
  }

  public ArrayList<String> TripsSummary() {
    m_Regels = new ArrayList<String>();
    System.out.println(" GPX-File:" + m_GPXFile);
    GPX gpx;
    try {
      gpx = GPX.reader(GPX.Version.V11).read(m_GPXFile);

      List<Track> v_tracks = gpx.getTracks();
      v_tracks.forEach(v_track -> {
        List<TrackSegment> v_segments = v_track.getSegments();
        v_segments.forEach(v_segment -> {
          List<WayPoint> v_waypoints = v_segment.getPoints();
          int v_eind = v_waypoints.size() - 1;

          NominatimAPI v_nomi = new NominatimAPI();

          Address v_AdrStart = v_nomi.getAdress(v_waypoints.get(0).getLatitude().toDegrees(),
              v_waypoints.get(0).getLongitude().toDegrees());
          LocalDateTime v_StartTime = TimeConversion.timeZoned2Local(v_waypoints.get(0).getTime());
          String[] v_starttijdparts = v_StartTime.toString().split("T");

          Address v_AdrFinish = v_nomi.getAdress(v_waypoints.get(v_eind).getLatitude().toDegrees(),
              v_waypoints.get(v_eind).getLongitude().toDegrees());
          LocalDateTime v_FinishTime = TimeConversion.timeZoned2Local(v_waypoints.get(v_eind).getTime());
          String[] v_eindtijdparts = v_FinishTime.toString().split("T");

          Duration v_period = Duration.between(v_StartTime, v_FinishTime);

          // Date (DD/MM/YYYY) Start Time Origin Longitude Origin Latitude Destination
          // Longitude Destination Latitude Origin Destination Distance (km) Time (min)
          // Fuel Economy (l/100km) Fuel Cost (EUR) Carbon Footprint (kg) ecoChallenge
          // Overall ecoChallenge Speed ecoChallenge Acceleration ecoChallenge Braking
          // ecoChallenge Fuel Economy

          String v_regel = String.join(";", v_eindtijdparts[1], v_starttijdparts[0], v_starttijdparts[1],
              Double.toString(v_waypoints.get(0).getLongitude().toDegrees()),
              Double.toString(v_waypoints.get(0).getLatitude().toDegrees()),
              Double.toString(v_waypoints.get(v_eind).getLongitude().toDegrees()),
              Double.toString(v_waypoints.get(v_eind).getLatitude().toDegrees()), v_AdrStart.getDisplayName(),
              v_AdrFinish.getDisplayName(), TimeConversion.formatDuration(v_period));
          m_Regels.add(v_regel);
        });
      });
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return m_Regels;
  }
}
