package kwee.garminSummary.main;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import junit.framework.TestCase;

public class SummaryTest extends TestCase {
  JProgressBar m_pbarTracks = new JProgressBar();
  JLabel m_Progresslabel = new JLabel();
  JProgressBar m_pbarSegments = new JProgressBar();
  File m_GPXFile = new File("resources/362.gpx");
  Summary m_Summary;
  ArrayList<String> m_Regels = new ArrayList<String>();

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    // Get the URL of a resource file
    URL resourceUrl = getClass().getClassLoader().getResource("362.gpx");

    if (resourceUrl != null) {
      // Get the resource directory path
      String resourceDirectory = resourceUrl.getPath();
      System.out.println("Directory :" + resourceDirectory);
    }

    m_Summary = new Summary(m_GPXFile, m_pbarTracks, m_Progresslabel, m_pbarSegments);
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
    fail("Not yet implemented");
  }

  public void testHeader() {
    // fail("Not yet implemented");
  }

  public void testTripsSummary() {
    // fail("Not yet implemented");
  }

}
