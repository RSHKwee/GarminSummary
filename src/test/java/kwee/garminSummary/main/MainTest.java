package kwee.garminSummary.main;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.Robot;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JFileChooserFixture;
import org.assertj.swing.launcher.ApplicationLauncher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import kwee.garminSummary.gui.GUILayout;
import kwee.library.FileUtils;
import kwee.logger.TestLogger;

public class MainTest extends TestCase {
  private static final Logger LOGGER = Logger.getLogger(Class.class.getName());
  private FrameFixture frame;
  Object lock = GUILayout.lock;

  private UserSetting m_OrgParam = new UserSetting();
  private String m_OutputDir;

  private String c_GPXFile = "362.gpx";

  private String c_ExpFile = "current.csv";
  private String c_ExpFile2 = "a_current.csv";

  // Expected results in following dirs:
  private String m_DirExp = "GUI_Exp";

  // Generated results in following dirs:
  private String m_gui = "MAIN";

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    m_OrgParam = Main.m_param.copy();

    File ll_file = FileUtils.GetResourceFile(c_GPXFile);
    m_OutputDir = ll_file.getParent();
    Main.m_param.set_LogDir(m_OutputDir + "//");
    Main.m_param.set_toDisk(false);
    Main.m_param.save();

    // Launch your application or obtain a reference to an existing Swing frame
    ApplicationLauncher.application(kwee.garminSummary.main.Main.class).start();

    try {
      TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException e) {
      LOGGER.log(Level.INFO, e.getMessage());
    }

    // Create a FrameFixture instance
    JFrame l_frame = Main.createAndShowGUI();
    l_frame.setName("DEFAULT");

    // Get the robot associated with the FrameFixture
    Robot robot = BasicRobot.robotWithCurrentAwtHierarchy();
    frame = new FrameFixture(robot, l_frame);
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
  public void testMain() {
    File l_File = FileUtils.GetResourceFile(m_DirExp + "/" + c_ExpFile);
    String l_ExpFile = l_File.getAbsolutePath();

    File l_File2 = FileUtils.GetResourceFile(m_DirExp + "/" + c_ExpFile2);
    String l_ExpFile2 = l_File2.getAbsolutePath();

    frame.button("GPX File(s)").click();
    FileUtils.checkCreateDirectory(m_OutputDir + "/" + m_gui);

    JFileChooserFixture fileChooser = frame.fileChooser();
    fileChooser.setCurrentDirectory(new File(m_OutputDir));
    fileChooser.fileNameTextBox().setText(c_GPXFile); // Set the desired file name
    fileChooser.approve();

    frame.button("Output folder").click();
    fileChooser = frame.fileChooser();
    fileChooser.setCurrentDirectory(new File(m_OutputDir + "/" + m_gui + "\\"));
    fileChooser.approve();

    frame.button("Summarise").click();

    synchronized (lock) {
      boolean bstat = FileUtils.FileContentsEquals(m_OutputDir + "\\" + m_gui + "\\" + c_ExpFile, l_ExpFile);
      if (!bstat) {
        LOGGER.log(Level.INFO, "Backup result used: " + l_ExpFile2);
        bstat = FileUtils.FileContentsEquals(m_OutputDir + "\\" + m_gui + "\\" + c_ExpFile, l_ExpFile2);
      }
      assertTrue(bstat);
    }
  }

}
