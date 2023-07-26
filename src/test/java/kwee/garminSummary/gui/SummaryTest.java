package kwee.garminSummary.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import junit.framework.TestCase;
import kwee.library.FileUtils;
import kwee.library.TxtBestand;

public class SummaryTest extends TestCase {
  private static final Logger LOGGER = Logger.getLogger(Class.class.getName());

  private String c_GPXFile = "362.gpx";
  private String c_ExpFile = "sum_gen.csv";
  private String c_ExpFile2 = "a_sum_gen.csv";
  private String c_GenFile = "sum_gen.csv";

  private JProgressBar m_pbarTracks = new JProgressBar();
  private JLabel m_Progresslabel = new JLabel();
  private JProgressBar m_pbarSegments = new JProgressBar();
  private File m_GPXFile;
  private Summary m_Summary = new Summary();
  private ArrayList<String> m_Regels = new ArrayList<String>();
  private String m_Directory = "";

  private String m_DirGen = "Summary";
  private String m_ExpFile = "";
  private String m_ExpFile2 = "";
  private String m_DirExp = "Summary_Exp";

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    m_GPXFile = FileUtils.GetResourceFile(c_GPXFile);
    m_Directory = m_GPXFile.getParent();

    File lfile2 = FileUtils.GetResourceFile(m_DirExp + "/" + c_ExpFile);
    m_ExpFile = lfile2.getAbsolutePath();

    File lfile3 = FileUtils.GetResourceFile(m_DirExp + "/" + c_ExpFile2);
    m_ExpFile2 = lfile3.getAbsolutePath();
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
    FileUtils.checkCreateDirectory(m_Directory + "/" + m_DirGen);

    TxtBestand tbest = new TxtBestand(m_Directory + "/" + m_DirGen + "/" + c_GenFile, l_Summary.Header());
    m_Regels = l_Summary.TripsSummary();
    tbest.DumpBestand(m_Regels, false);

    boolean bstat = FileUtils.FileContentsEquals(m_Directory + "/" + m_DirGen + "/" + c_GenFile, m_ExpFile);
    if (!bstat) {
      LOGGER.log(Level.INFO, "Backup result used: " + m_ExpFile2);
      bstat = FileUtils.FileContentsEquals(m_Directory + "/" + m_DirGen + "/" + c_GenFile, m_ExpFile2);
    }
    assertTrue(bstat);
  }

}
