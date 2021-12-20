package library;

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

public class Summary {
	private static final Logger LOGGER = Logger.getLogger(Class.class.getName());
	private ArrayList<String> m_Regels;
	private String m_GPXFile;

	private JProgressBar m_pbarTracks;
	private JProgressBar m_pbarSegments;
	private int m_VerwerkteTracks = 0;
	private int m_VerwerkteSegments = 0;

	private JLabel m_ProgressLabel;
	private int m_AantalTracks = 0;
	private int m_AantalSegments = 0;

	/**
	 * Constructor
	 * 
	 * @param a_pbarTracks          Progresbar
	 * @param a_Progresslabel Label
	 */
	public Summary(JProgressBar a_pbarTracks, JLabel a_Progresslabel, JProgressBar a_pbarSegments) {
		m_pbarTracks = a_pbarTracks;
		m_ProgressLabel = a_Progresslabel;
		m_pbarSegments = a_pbarSegments;
	}

	/**
	 * Creeer een samenvatting van tripjes voor de opgegeven GPX-file.
	 * 
	 * @param a_GPXFile Filenaam GPX
	 * @return Tekstregels.
	 */
	public ArrayList<String> TripsSummary(String a_GPXFile) {
		m_GPXFile = a_GPXFile;
		return TripsSummary();
	}

	public String Header() {
		return String.join(";", "Date", "Origin", "Finish", "Long(origin)", "Dest(origin)", "Long(finish)", "Dest(finish)",
				"Address origin", "Address finish", "Distance", "Duration");
	}

	/**
	 * Creeer een samenvatting van tripjes.
	 * 
	 * @return Tekstregels.
	 */
	public ArrayList<String> TripsSummary() {
		m_Regels = new ArrayList<String>();

		GPX gpx;
		try {
			gpx = GPX.reader(GPX.Version.V11).read(m_GPXFile);

			List<Track> v_tracks = gpx.getTracks();
			m_AantalTracks = v_tracks.size();
			LOGGER.log(Level.INFO, "Process GPX-File: " + m_GPXFile + " with content of " + m_AantalTracks + " tracks.");
			m_VerwerkteTracks = 0;
			m_pbarTracks.setMaximum(m_AantalTracks);
			m_pbarTracks.setVisible(true);

			v_tracks.forEach(v_track -> {
				List<TrackSegment> v_segments = v_track.getSegments();
				m_AantalSegments = v_segments.size();
				m_VerwerkteSegments = 0;
				m_pbarSegments.setMaximum(m_AantalSegments);
				m_pbarSegments.setVisible(true);
				v_segments.forEach(v_segment -> {
					List<WayPoint> v_waypoints = v_segment.getPoints();
					int v_eind = v_waypoints.size() - 1;
					Double v_afstand = SegmentLengte(v_waypoints);
					v_afstand = v_afstand / 1000.0;
					// System.out.println("Afstand: " + v_afstand);

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

					NumberFormat fmt = NumberFormat.getInstance();
					fmt.setGroupingUsed(false);
					fmt.setMaximumIntegerDigits(9999);
					fmt.setMaximumFractionDigits(9999);
					String v_regel = String.join(";", v_starttijdparts[0], v_starttijdparts[1], v_eindtijdparts[1],
							fmt.format(v_waypoints.get(0).getLongitude().toDegrees()),
							fmt.format(v_waypoints.get(0).getLatitude().toDegrees()),
							fmt.format(v_waypoints.get(v_eind).getLongitude().toDegrees()),
							fmt.format(v_waypoints.get(v_eind).getLatitude().toDegrees()), v_AdrStart.getDisplayName(),
							v_AdrFinish.getDisplayName(), fmt.format(v_afstand), TimeConversion.formatDuration(v_period));
					m_Regels.add(v_regel);
					verwerkProgressSegments();
				});
				verwerkProgressTracks();
			});
		} catch (IOException e) {
			e.printStackTrace();
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
		m_ProgressLabel.setText(" ");
		m_pbarTracks.setValue(0);
		m_pbarTracks.setVisible(false);
		m_pbarSegments.setValue(0);
		m_pbarSegments.setVisible(false);

		return m_Regels;
	}

	/**
	 * Bereken segment lengte door de agstanden tussen de afzonderlijke punten op te
	 * tellen.
	 * 
	 * @param a_waypoints Lijst van Waypoints.
	 * @return Afstand in meters.
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

	private void verwerkProgressTracks() {
		m_VerwerkteTracks++;
		try {
			m_pbarTracks.setValue(m_VerwerkteTracks);
			Double v_prog = ((double) m_VerwerkteTracks / (double) m_AantalTracks) * 100;
			Integer v_iprog = v_prog.intValue();
			m_ProgresTextTracks = v_iprog.toString() + "% (" + m_VerwerkteTracks + " van " + m_AantalTracks + " tracks)";
			m_ProgressLabel.setText(m_ProgresTextSegments + " | " + m_ProgresTextTracks);
		} catch (Exception e) {
		}
	}

	private void verwerkProgressSegments() {
		m_VerwerkteSegments++;
		try {
			m_pbarSegments.setValue(m_VerwerkteSegments);
			Double v_prog = ((double) m_VerwerkteSegments / (double) m_AantalSegments) * 100;
			Integer v_iprog = v_prog.intValue();
			m_ProgresTextSegments = v_iprog.toString() + "% (" + m_VerwerkteSegments + " van " + m_AantalSegments
					+ " segments)";
			m_ProgressLabel.setText(m_ProgresTextSegments + " | " + m_ProgresTextTracks);
		} catch (Exception e) {
		}
	}
}
