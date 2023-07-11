package kwee.garminSummary.gui;

import java.io.File;
import java.util.logging.Level;

import javax.swing.JFrame;

import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JFileChooserFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import kwee.garminSummary.main.Main;
import kwee.garminSummary.main.UserSetting;
import kwee.library.FileUtils;
import kwee.logger.TestLogger;

public class GUILayoutTest extends TestCase {
  // private static final Logger LOGGER = Logger.getLogger(Class.class.getName());
  private FrameFixture frame;
  Object lock = GUILayout.lock;

//  public UserSetting m_param = Main.m_param;
  private UserSetting m_OrgParam = new UserSetting();
  private String m_OutputDir;

  private String c_GPXFile = "362.gpx";
  private String c_ExpFile = "current.csv";
  private String c_GenFile = "362.csv";
  private String c_ExpFile2 = "a_current.csv";

  private String m_ExpFile = "";
  private String m_ExpFile2 = "";

  // Expected results in following dirs:
  private String m_DirExp = "GUI_Exp";

  // Generated results in following dirs:
  private String m_gui = "GUI";

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    m_OrgParam = Main.m_param.copy();

    File l_File = FileUtils.GetResourceFile(m_DirExp + "/" + c_ExpFile);
    m_ExpFile = l_File.getAbsolutePath();

    File l_File2 = FileUtils.GetResourceFile(m_DirExp + "/" + c_ExpFile2);
    m_ExpFile2 = l_File2.getAbsolutePath();

    File ll_file = FileUtils.GetResourceFile(c_GPXFile);
    m_OutputDir = ll_file.getParent();
    Main.m_param.save();

    // Start GUI, with prepared Usersettings
    if (frame != null) {
      frame.cleanUp();
    }

    JFrame l_frame = new JFrame();
    GUILayout guilayout = new GUILayout();
    l_frame.add(guilayout);
    frame = new FrameFixture(l_frame);
    frame.show();

    TestLogger.setup(Level.INFO);
  }

  @Override
  @After
  public void tearDown() throws Exception {
    super.tearDown();
    this.frame.cleanUp();

    Main.m_param = m_OrgParam.copy();
    Main.m_param.save();
    TestLogger.close();
  }

  @Test
  public void testGUILayout() {
    frame.button("GPX File(s)").click();
    FileUtils.checkCreateDirectory(m_OutputDir + "\\" + m_gui);

    JFileChooserFixture fileChooser = frame.fileChooser();
    fileChooser.setCurrentDirectory(new File(m_OutputDir));
    fileChooser.fileNameTextBox().setText(c_GPXFile); // Set the desired file name
    fileChooser.approve();

    frame.button("Output folder").click();
    fileChooser = frame.fileChooser();
    fileChooser.setCurrentDirectory(new File(m_OutputDir + "\\" + m_gui + "\\"));
    fileChooser.approve();

    frame.button("Summarise").click();

    synchronized (lock) {
      boolean bstat = FileUtils.FileContentsEquals(m_OutputDir + "\\" + m_gui + "\\" + c_ExpFile, m_ExpFile);
      if (!bstat) {
        bstat = FileUtils.FileContentsEquals(m_OutputDir + "\\" + m_gui + "\\" + c_ExpFile, m_ExpFile2);
      }
      assertTrue(bstat);
    }
  }

  @Test
  public void testGUILayoutFile() {
    frame.button("GPX File(s)").click();
    FileUtils.checkCreateDirectory(m_OutputDir + "\\" + m_gui);

    JFileChooserFixture fileChooser = frame.fileChooser();
    fileChooser.setCurrentDirectory(new File(m_OutputDir));
    fileChooser.fileNameTextBox().setText(c_GPXFile); // Set the desired file name
    fileChooser.approve();

    frame.button("Output folder").click();
    fileChooser = frame.fileChooser();
    fileChooser.setCurrentDirectory(new File(m_OutputDir + "\\" + m_gui + "\\"));
    fileChooser.approve();

    JTextComponentFixture outputfile = frame.textBox("Outputfilename");
    outputfile.setText(c_GenFile);

    frame.button("Summarise").click();

    synchronized (lock) {
      boolean bstat = FileUtils.FileContentsEquals(m_OutputDir + "\\" + m_gui + "\\" + c_GenFile, m_ExpFile);
      if (!bstat) {
        bstat = FileUtils.FileContentsEquals(m_OutputDir + "\\" + m_gui + "\\" + c_GenFile, m_ExpFile2);
      }
      assertTrue(bstat);
    }
  }

}
