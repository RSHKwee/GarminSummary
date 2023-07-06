package kwee.garminSummary.gui;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import org.assertj.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import kwee.garminSummary.main.Main;
import kwee.garminSummary.main.UserSetting;
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
  private String c_ExpFile = "362.csv";

  // Expected results in following dirs:
  private String m_DirExp_Suffux = "_Exp";

  @Before
  public void setUp() throws Exception {
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

  }

  @Test
  public void testGUILayout() {
    fail("Not yet implemented");
  }

}
