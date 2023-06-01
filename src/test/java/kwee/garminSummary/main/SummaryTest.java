package kwee.garminSummary.main;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import junit.framework.TestCase;
import kwee.library.FileUtils;
import kwee.library.TxtBestand;

public class SummaryTest extends TestCase {
  private String c_GPXFile = "362.gpx";
  private String c_ExpFile = "362.csv";
  private String c_GenFile = "sum_gen.csv";

  private JProgressBar m_pbarTracks = new JProgressBar();
  private JLabel m_Progresslabel = new JLabel();
  private JProgressBar m_pbarSegments = new JProgressBar();
  private File m_GPXFile;
  private Summary m_Summary = new Summary();
  private ArrayList<String> m_Regels = new ArrayList<String>();
  private String m_Directory = "";

  private String m_ExpFile = "";

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    // Get the URL of a resource file
    URL resourceUrl = getClass().getClassLoader().getResource(c_GPXFile);
    if (resourceUrl != null) {
      // Get the resource directory path
      String resourceDirectory = resourceUrl.getPath();
      m_GPXFile = new File(resourceDirectory);
      m_Directory = m_GPXFile.getParent();
    }

    resourceUrl = getClass().getClassLoader().getResource(c_ExpFile);
    if (resourceUrl != null) {
      // Get the resource directory path
      String resourceDirectory = resourceUrl.getPath();
      m_ExpFile = resourceDirectory;
    }
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testSummaryJProgressBarJLabelJProgressBar() {
    Summary v_sum = new Summary(m_pbarTracks, m_Progresslabel, m_pbarSegments);
    assertNotNull(v_sum);
  }

  public void testSummaryFileJProgressBarJLabelJProgressBar() {
    Summary v_sum = new Summary(m_GPXFile, m_pbarTracks, m_Progresslabel, m_pbarSegments);
    assertNotNull(v_sum);
  }

  public void testTripsSummaryFile() {
    m_Regels = m_Summary.TripsSummary();
    assertEquals(m_Regels.size(), 0);
  }

  public void testHeader() {
    String l_header = m_Summary.Header();
    String l_Expected = "Date;Origin;Finish;Longitude(origin);Latitude(origin);Longitude(finish);Latitude(finish);Address origin;Address finish;Distance (km);Duration;Avr speed (km/h)";
    assertTrue(l_header.equalsIgnoreCase(l_Expected));
  }

  public void testTripsSummary() {
    Summary l_Summary = new Summary(m_GPXFile, m_pbarTracks, m_Progresslabel, m_pbarSegments);

    m_Regels = l_Summary.TripsSummary();
    TxtBestand.DumpBestand(m_Directory + "\\" + c_GenFile, m_Regels, false);
    boolean bstat = FileUtils.FileContentsEquals(m_Directory + "\\" + c_GenFile, m_ExpFile);
    assertTrue(bstat);
  }

}
