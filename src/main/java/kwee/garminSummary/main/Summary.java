package kwee.garminSummary.main;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Point;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import io.jenetics.jpx.geom.Geoid;
import kwee.library.Address;
import kwee.library.NominatimAPI;
import kwee.library.TimeConversion;

/**
 * 
 * 
 * @author René
 *
 */
public class Summary {
  private static final Logger LOGGER = Logger.getLogger(Class.class.getName());
  private String C_Separator = ";";

  private ArrayList<String> m_Regels = new ArrayList<String>();
  private File m_GPXFile;

  private JProgressBar m_pbarTracks = new JProgressBar();
  private JProgressBar m_pbarSegments = new JProgressBar();
  private JLabel m_ProgressLabel = new JLabel();

  private int m_ProcessedTracks = 0;
  private int m_ProcessedSegments = 0;

  private int m_NumberTracks = 0;
  private int m_NumberSegments = 0;

  public Summary() {
    // Default constructor.
  }

  /**
   * 
   * @param a_pbarTracks
   * @param a_Progresslabel
   * @param a_pbarSegments
   */
  public Summary(JProgressBar a_pbarTracks, JLabel a_Progresslabel, JProgressBar a_pbarSegments) {
    m_pbarTracks = a_pbarTracks;
    m_ProgressLabel = a_Progresslabel;
    m_pbarSegments = a_pbarSegments;
  }

  /**
   * 
   * @param a_GPXFile
   * @param a_pbarTracks
   * @param a_Progresslabel
   * @param a_pbarSegments
   */
  public Summary(File a_GPXFile, JProgressBar a_pbarTracks, JLabel a_Progresslabel, JProgressBar a_pbarSegments) {
    m_pbarTracks = a_pbarTracks;
    m_ProgressLabel = a_Progresslabel;
    m_pbarSegments = a_pbarSegments;

    m_GPXFile = a_GPXFile;
  }

  /**
   * Creeer een samenvatting van tripjes voor de opgegeven GPX-file.
   * 
   * @param a_GPXFile Filenaam GPX
   * @return Tekstregels.
   */
  public ArrayList<String> TripsSummary(File a_GPXFile) {
    m_GPXFile = a_GPXFile;
    return TripsSummary();
  }

  /**
   * Header line for csv sumnmary file.
   * 
   * @return String with header information.
   */
  public String Header() {
    return String.join(C_Separator, "Date", "Origin", "Finish", "Longitude(origin)", "Latitude(origin)",
        "Longitude(finish)", "Latitude(finish)", "Address origin", "Address finish", "Distance (km)", "Duration",
        "Avr speed (km/h)");
  }

  /**
   * Create summary.
   * 
   * @return Text lines.
   */
  public ArrayList<String> TripsSummary() {
    m_Regels = new ArrayList<String>();

    GPX gpx;
    try {
      gpx = GPX.reader(GPX.Version.V11).read(m_GPXFile);

      List<Track> v_tracks = gpx.getTracks();
      m_NumberTracks = v_tracks.size();
      LOGGER.log(Level.INFO, "Process GPX-File: " + m_GPXFile + " with content of " + m_NumberTracks + " tracks.");
      m_ProcessedTracks = -1;
      m_pbarTracks.setMaximum(m_NumberTracks);
      m_pbarTracks.setVisible(true);
      processProgressTracks();

      v_tracks.forEach(v_track -> {
        List<TrackSegment> v_segments = v_track.getSegments();
        m_NumberSegments = v_segments.size();
        m_ProcessedSegments = -1;
        m_pbarSegments.setMaximum(m_NumberSegments);
        m_pbarSegments.setVisible(true);
        processProgressSegments();

        v_segments.forEach(v_segment -> {
          List<WayPoint> v_waypoints = v_segment.getPoints();
          int v_eind = v_waypoints.size() - 1;
          Double v_afstand = SegmentLengte(v_waypoints);
          v_afstand = v_afstand / 1000.0;

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
          long vsec = v_period.getSeconds();
          Double v_Speed = v_afstand / (vsec / 3600.0);

          NumberFormat fmt = NumberFormat.getInstance();
          fmt.setGroupingUsed(false);
          fmt.setMaximumIntegerDigits(9999);
          fmt.setMaximumFractionDigits(9999);
          String v_regel = String.join(C_Separator, v_starttijdparts[0], v_starttijdparts[1], v_eindtijdparts[1],
              fmt.format(v_waypoints.get(0).getLongitude().toDegrees()),
              fmt.format(v_waypoints.get(0).getLatitude().toDegrees()),
              fmt.format(v_waypoints.get(v_eind).getLongitude().toDegrees()),
              fmt.format(v_waypoints.get(v_eind).getLatitude().toDegrees()), v_AdrStart.getDisplayName(),
              v_AdrFinish.getDisplayName(), fmt.format(v_afstand), TimeConversion.formatDuration(v_period),
              fmt.format(v_Speed));

          m_Regels.add(v_regel);
          processProgressSegments();
        });
        processProgressTracks();
      });
    } catch (IOException e) {
      LOGGER.log(Level.INFO, e.getMessage());
    } catch (java.lang.Exception e) {
      LOGGER.log(Level.INFO, e.getMessage());
    }

    // Reset propgress bars
    m_ProgressLabel.setText(" ");
    m_pbarTracks.setValue(0);
    m_pbarTracks.setVisible(false);
    m_pbarSegments.setValue(0);
    m_pbarSegments.setVisible(false);

    return m_Regels;
  }

  /**
   * Calculate segment lenght by adding distances between points.
   * 
   * @param a_waypoints List of Waypoints.
   * @return Distance in meters.
   */
  private double SegmentLengte(List<WayPoint> a_waypoints) {
    double v_length = 0.0;
    int v_eind = a_waypoints.size() - 1;

    for (int i = 0; i < v_eind; i++) {
      Point start = WayPoint.of(a_waypoints.get(i).getLatitude().toDegrees(),
          a_waypoints.get(i).getLongitude().toDegrees());
      Point einde = WayPoint.of(a_waypoints.get(i + 1).getLatitude().toDegrees(),
          a_waypoints.get(i + 1).getLongitude().toDegrees());
      Length lengte = Geoid.WGS84.distance(start, einde);
      v_length = v_length + lengte.doubleValue();
    }
    return v_length;
  }

  private String m_ProgresTextTracks = "";
  private String m_ProgresTextSegments = "";

  /**
   * Display progress processing Tracks.
   */
  private void processProgressTracks() {
    m_ProcessedTracks++;
    try {
      m_pbarTracks.setValue(m_ProcessedTracks);
      Double v_prog = ((double) m_ProcessedTracks / (double) m_NumberTracks) * 100;
      Integer v_iprog = v_prog.intValue();
      m_ProgresTextTracks = v_iprog.toString() + "% (" + m_ProcessedTracks + " of " + m_NumberTracks + " tracks)";
      m_ProgressLabel.setText(m_ProgresTextSegments + " | " + m_ProgresTextTracks);
    } catch (Exception e) {
      // Do nothing...
    }
  }

  /**
   * Display progress processing Segments.
   */
  private void processProgressSegments() {
    m_ProcessedSegments++;
    try {
      m_pbarSegments.setValue(m_ProcessedSegments);
      Double v_prog = ((double) m_ProcessedSegments / (double) m_NumberSegments) * 100;
      Integer v_iprog = v_prog.intValue();
      m_ProgresTextSegments = v_iprog.toString() + "% (" + m_ProcessedSegments + " of " + m_NumberSegments
          + " segments)";
      m_ProgressLabel.setText(m_GPXFile.getName() + " " + m_ProgresTextSegments + " | " + m_ProgresTextTracks);
    } catch (Exception e) {
      // Do nothing
    }
  }
}
