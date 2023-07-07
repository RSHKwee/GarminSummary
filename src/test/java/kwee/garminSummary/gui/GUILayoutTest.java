package kwee.garminSummary.gui;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JFileChooserFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import kwee.garminSummary.main.Main;
import kwee.garminSummary.main.UserSetting;
import kwee.library.FileUtils;
import kwee.logger.TestLogger;
import kwee.testLibrary.TestFunctions;

public class GUILayoutTest extends TestCase {
  private static final Logger LOGGER = Logger.getLogger(Class.class.getName());
  private FrameFixture frame;

  public kwee.garminSummary.main.UserSetting m_param = Main.m_param;
  private UserSetting m_OrgParam = new UserSetting();
  private TestFunctions m_Functions = new kwee.testLibrary.TestFunctions();
  private String m_OutputDir;

  private String c_GPXFile = "362.gpx";
  private String c_ExpFile = "current.csv";
  private String c_GenFile = "current.csv";

  private String m_ExpFile = "";

  // Expected results in following dirs:
  private String m_DirExp = "GUI_Exp";

  // Generated results in following dirs:
  private String m_gui = "GUI";

  @Before
  public void setUp() throws Exception {
    super.setUp();

    URL resourceUrl = getClass().getClassLoader().getResource(m_DirExp + "/" + c_ExpFile);
    if (resourceUrl != null) {
      // Get the resource directory path
      String resourceDirectory = resourceUrl.getPath();
      m_ExpFile = resourceDirectory;
    }

    File ll_file = m_Functions.GetResourceFile(c_GPXFile);
    m_OutputDir = ll_file.getParent();
    m_param.save();

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

  @After
  public void tearDown() throws Exception {
    super.tearDown();

    m_param = m_Functions.CopyUserSetting(m_OrgParam);
    m_param.save();
    this.frame.cleanUp();
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
    LOGGER.log(Level.INFO, "Wait for 15 seconds.....");
    try {
      TimeUnit.SECONDS.sleep(15);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    boolean bstat = FileUtils.FileContentsEquals(m_OutputDir + "\\" + m_gui + "\\" + c_GenFile, m_ExpFile);
    assertTrue(bstat);
  }

}
