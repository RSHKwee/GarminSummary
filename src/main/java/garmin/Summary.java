package garmin;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Point;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import io.jenetics.jpx.geom.Geoid;
import library.Address;
import library.NominatimAPI;
import library.TimeConversion;

public class Summary {
	private static final Logger LOGGER = Logger.getLogger(Class.class.getName());
	private ArrayList<String> m_Regels;
	private String m_GPXFile;

	/**
	 * Default constructor
	 */
	public Summary() {
	}

	/**
	 * Constructor waarbij de in te lezen GPX-file wordt opgegeven.
	 * 
	 * @param a_GPXFile
	 */
	public Summary(String a_GPXFile) {
		m_GPXFile = a_GPXFile;
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

	/**
	 * Creeer een samenvatting van tripjes.
	 * 
	 * @return Tekstregels.
	 */
	public ArrayList<String> TripsSummary() {
		m_Regels = new ArrayList<String>();
		LOGGER.log(Level.INFO, " GPX-File:" + m_GPXFile);
		GPX gpx;
		try {
			gpx = GPX.reader(GPX.Version.V11).read(m_GPXFile);

			List<Track> v_tracks = gpx.getTracks();

			v_tracks.forEach(v_track -> {
				List<TrackSegment> v_segments = v_track.getSegments();
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
					String v_regel = String.join(";", v_eindtijdparts[1], v_starttijdparts[0], v_starttijdparts[1],
							fmt.format(v_waypoints.get(0).getLongitude().toDegrees()),
							fmt.format(v_waypoints.get(0).getLatitude().toDegrees()),
							fmt.format(v_waypoints.get(v_eind).getLongitude().toDegrees()),
							fmt.format(v_waypoints.get(v_eind).getLatitude().toDegrees()), v_AdrStart.getDisplayName(),
							v_AdrFinish.getDisplayName(), fmt.format(v_afstand), TimeConversion.formatDuration(v_period));
					m_Regels.add(v_regel);
				});
			});
		} catch (IOException e) {
			e.printStackTrace();
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
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
}
