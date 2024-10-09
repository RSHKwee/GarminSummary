package kwee.garminSummary.gui;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.Robot;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JCheckBoxFixture;
import org.assertj.swing.fixture.JFileChooserFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.launcher.ApplicationLauncher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import kwee.garminSummary.main.Main;
import kwee.garminSummary.main.UserSetting;
import kwee.library.FileUtils;
import kwee.logger.TestLogger;

public class GUILayoutTest extends TestCase {
  private static final Logger LOGGER = Logger.getLogger(Class.class.getName());
  private FrameFixture frame;
  Object lock = GUILayout.lock;

  private UserSetting m_OrgParam = new UserSetting();
  private String m_OutputDir;

  private String c_GPXFile2 = "361.gpx";
  private String c_GPXFile = "362.gpx";
  private String c_GenFile = "362.csv";

  private String c_ExpFile = "current.csv";
  private String c_ExpFile2 = "a_current.csv";

  private String c_ExpFile3 = "362.csv";
  private String c_ExpFile4 = "a_362.csv";
  private String c_ExpFile5 = "b_362.csv";

  // Expected results in following dirs:
  private String m_DirExpSuffix = "_Exp";

  // Generated results in following dirs:
  private String m_gui = "GUI";
  private String m_gui1 = "GUI1";
  private String m_gui2 = "GUI2";
  private String m_gui3 = "GUI3";
  private String m_gui4 = "GUI4";

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    m_OrgParam = Main.m_param.copy();

    File ll_file = FileUtils.GetResourceFile(c_GPXFile);
    m_OutputDir = ll_file.getParent();
    Main.m_param.set_LogDir(m_OutputDir + "/");
    Main.m_param.set_toDisk(false);
    Main.m_param.set_Language("nl");
    Main.m_param.save();

    // Launch your application or obtain a reference to an existing Swing frame and
    // wait asecond.
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
  public void testGUILayout() {
    File l_File = FileUtils.GetResourceFile(m_gui + m_DirExpSuffix + "/" + c_ExpFile);
    String l_ExpFile = l_File.getAbsolutePath();

    File l_File2 = FileUtils.GetResourceFile(m_gui + m_DirExpSuffix + "/" + c_ExpFile2);
    String l_ExpFile2 = l_File2.getAbsolutePath();

    frame.button("GPX File(s)").click();
    FileUtils.checkCreateDirectory(m_OutputDir + "/" + m_gui);

    JFileChooserFixture fileChooser = frame.fileChooser();
    fileChooser.setCurrentDirectory(new File(m_OutputDir));
    fileChooser.fileNameTextBox().setText(c_GPXFile); // Set the desired file name
    fileChooser.approve();

    frame.button("OutputFolder").click();
    fileChooser = frame.fileChooser();
    fileChooser.setCurrentDirectory(new File(m_OutputDir + "/" + m_gui + "/"));
    fileChooser.approve();

    frame.button("Summarise").click();

    synchronized (lock) {
      boolean bstat = FileUtils.FileContentsEquals(m_OutputDir + "/" + m_gui + "/" + c_ExpFile, l_ExpFile);
      if (!bstat) {
        LOGGER.log(Level.INFO, "Backup result used: " + l_ExpFile2);
        bstat = FileUtils.FileContentsEquals(m_OutputDir + "/" + m_gui + "/" + c_ExpFile, l_ExpFile2);
      }
      assertTrue(bstat);
    }
  }

  @Test
  public void testGUILayoutFile() {
    File l_File = FileUtils.GetResourceFile(m_gui1 + m_DirExpSuffix + "/" + c_ExpFile3);
    String l_ExpFile = l_File.getAbsolutePath();

    File l_File2 = FileUtils.GetResourceFile(m_gui1 + m_DirExpSuffix + "/" + c_ExpFile4);
    String l_ExpFile2 = l_File2.getAbsolutePath();

    frame.button("GPX File(s)").click();
    FileUtils.checkCreateDirectory(m_OutputDir + "/" + m_gui1);

    JFileChooserFixture fileChooser = frame.fileChooser();
    fileChooser.setCurrentDirectory(new File(m_OutputDir));
    fileChooser.fileNameTextBox().setText(c_GPXFile); // Set the desired file name
    fileChooser.approve();

    frame.button("OutputFolder").click();
    fileChooser = frame.fileChooser();
    fileChooser.setCurrentDirectory(new File(m_OutputDir + "/" + m_gui1 + "/"));
    fileChooser.approve();

    JTextComponentFixture outputfile = frame.textBox("Output filename");
    outputfile.setText(c_GenFile);

    frame.button("Summarise").click();

    synchronized (lock) {
      boolean bstat = FileUtils.FileContentsEquals(m_OutputDir + "/" + m_gui1 + "/" + c_GenFile, l_ExpFile);
      if (!bstat) {
        LOGGER.log(Level.INFO, "Backup result used: " + l_ExpFile2);
        bstat = FileUtils.FileContentsEquals(m_OutputDir + "/" + m_gui1 + "/" + c_GenFile, l_ExpFile2);
      }
      assertTrue(bstat);
    }
  }

  @Test
  public void testGUILayout2Files() {
    File l_File = FileUtils.GetResourceFile(m_gui2 + m_DirExpSuffix + "/" + c_ExpFile3);
    String l_ExpFile = l_File.getAbsolutePath();

    File l_File2 = FileUtils.GetResourceFile(m_gui2 + m_DirExpSuffix + "/" + c_ExpFile4);
    String l_ExpFile2 = l_File2.getAbsolutePath();

    frame.button("GPX File(s)").click();
    FileUtils.checkCreateDirectory(m_OutputDir + "/" + m_gui2);

    JFileChooserFixture fileChooser = frame.fileChooser();
    fileChooser.setCurrentDirectory(new File(m_OutputDir));

    // Select multiple files
    File file1 = new File(m_OutputDir + "/" + c_GPXFile2);
    File file2 = new File(m_OutputDir + "/" + c_GPXFile);
    fileChooser.selectFiles(file1, file2);
    fileChooser.approve();

    frame.button("OutputFolder").click();
    fileChooser = frame.fileChooser();
    fileChooser.setCurrentDirectory(new File(m_OutputDir + "/" + m_gui2 + "/"));
    fileChooser.approve();

    JTextComponentFixture outputfile = frame.textBox("Output filename");
    outputfile.setText(c_GenFile);

    frame.button("Summarise").click();

    synchronized (lock) {
      boolean bstat = FileUtils.FileContentsEquals(m_OutputDir + "/" + m_gui2 + "/" + c_GenFile, l_ExpFile);
      if (!bstat) {
        LOGGER.log(Level.INFO, "Backup result used: " + l_ExpFile2);
        bstat = FileUtils.FileContentsEquals(m_OutputDir + "/" + m_gui2 + "/" + c_GenFile, l_ExpFile2);
      }
      assertTrue(bstat);
    }
  }

  @Test
  public void testGUILayoutFileByFile() {
    File l_File = FileUtils.GetResourceFile(m_gui3 + m_DirExpSuffix + "/" + c_ExpFile3);
    String l_ExpFile = l_File.getAbsolutePath();

    File l_File2 = FileUtils.GetResourceFile(m_gui3 + m_DirExpSuffix + "/" + c_ExpFile4);
    String l_ExpFile2 = l_File2.getAbsolutePath();
    FileUtils.checkCreateDirectory(m_OutputDir + "/" + m_gui3);

    File l_File3 = FileUtils.GetResourceFile(m_gui3 + m_DirExpSuffix + "/" + c_ExpFile5);
    String l_ExpFile3 = l_File3.getAbsolutePath();
    FileUtils.checkCreateDirectory(m_OutputDir + "/" + m_gui3);

    frame.button("GPX File(s)").click();
    JFileChooserFixture fileChooser = frame.fileChooser();
    fileChooser.setCurrentDirectory(new File(m_OutputDir));
    fileChooser.fileNameTextBox().setText(c_GPXFile2); // Set the desired file name
    fileChooser.approve();

    frame.button("OutputFolder").click();
    fileChooser = frame.fileChooser();
    fileChooser.setCurrentDirectory(new File(m_OutputDir + "/" + m_gui3 + "/"));
    fileChooser.approve();

    JTextComponentFixture outputfile = frame.textBox("Output filename");
    outputfile.setText(c_GenFile);

    frame.button("Summarise").click();

    try {
      TimeUnit.SECONDS.sleep(5);
    } catch (InterruptedException e) {
      LOGGER.log(Level.INFO, e.getMessage());
    }

    synchronized (lock) {
      frame.button("GPX File(s)").click();
      fileChooser = frame.fileChooser();
      fileChooser.setCurrentDirectory(new File(m_OutputDir));
      fileChooser.fileNameTextBox().setText(c_GPXFile); // Set the desired file name
      fileChooser.approve();
    }

    outputfile = frame.textBox("Output filename");
    outputfile.setText(c_GenFile);
    outputfile.click();

    JCheckBoxFixture checkBox = frame.checkBox("Addtofile");
    checkBox.check();

    frame.button("Summarise").click();

    synchronized (lock) {
      boolean bstat = FileUtils.FileContentsEquals(m_OutputDir + "/" + m_gui3 + "/" + c_GenFile, l_ExpFile);
      if (!bstat) {
        LOGGER.log(Level.INFO, "Backup result used: " + l_ExpFile2);
        bstat = FileUtils.FileContentsEquals(m_OutputDir + "/" + m_gui3 + "/" + c_GenFile, l_ExpFile2);
        if (!bstat) {
          LOGGER.log(Level.INFO, "Backup result used: " + l_ExpFile3);
          bstat = FileUtils.FileContentsEquals(m_OutputDir + "/" + m_gui3 + "/" + c_GenFile, l_ExpFile2);
        }
      }
      assertTrue(bstat);
    }
  }

  @Test
  public void testGUILayoutFileByFileReverse() {
    File l_File = FileUtils.GetResourceFile(m_gui4 + m_DirExpSuffix + "/" + c_ExpFile3);
    String l_ExpFile = l_File.getAbsolutePath();

    File l_File2 = FileUtils.GetResourceFile(m_gui4 + m_DirExpSuffix + "/" + c_ExpFile4);
    String l_ExpFile2 = l_File2.getAbsolutePath();
    FileUtils.checkCreateDirectory(m_OutputDir + "/" + m_gui4);

    frame.button("GPX File(s)").click();
    JFileChooserFixture fileChooser = frame.fileChooser();
    fileChooser.setCurrentDirectory(new File(m_OutputDir));
    fileChooser.fileNameTextBox().setText(c_GPXFile); // Set the desired file name
    fileChooser.approve();

    frame.button("OutputFolder").click();
    fileChooser = frame.fileChooser();
    fileChooser.setCurrentDirectory(new File(m_OutputDir + "/" + m_gui4 + "/"));
    fileChooser.approve();

    JTextComponentFixture outputfile = frame.textBox("Output filename");
    outputfile.setText(c_GenFile);

    frame.button("Summarise").click();

    try {
      TimeUnit.SECONDS.sleep(5);
    } catch (InterruptedException e) {
      LOGGER.log(Level.INFO, e.getMessage());
    }

    synchronized (lock) {
      frame.button("GPX File(s)").click();
      fileChooser = frame.fileChooser();
      fileChooser.setCurrentDirectory(new File(m_OutputDir));
      fileChooser.fileNameTextBox().setText(c_GPXFile2); // Set the desired file name
      fileChooser.approve();
    }

    outputfile = frame.textBox("Output filename");
    outputfile.setText(c_GenFile);
    outputfile.click();

    JCheckBoxFixture checkBox = frame.checkBox("Addtofile");
    checkBox.check();

    frame.button("Summarise").click();

    synchronized (lock) {
      boolean bstat = FileUtils.FileContentsEquals(m_OutputDir + "/" + m_gui4 + "/" + c_GenFile, l_ExpFile);
      if (!bstat) {
        LOGGER.log(Level.INFO, "Backup result used: " + l_ExpFile2);
        bstat = FileUtils.FileContentsEquals(m_OutputDir + "/" + m_gui4 + "/" + c_GenFile, l_ExpFile2);
      }
      assertTrue(bstat);
    }
  }

}
