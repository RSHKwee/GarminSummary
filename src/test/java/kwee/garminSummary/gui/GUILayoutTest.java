package kwee.garminSummary.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
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
import kwee.library.CsvFileComparator;
import kwee.library.FileUtils;
import kwee.logger.MyLogger;
import kwee.logger.TestLogger;

public class GUILayoutTest extends TestCase {
  private static final Logger LOGGER = MyLogger.getLogger();
  private FrameFixture frame;
  Object lock = GUILayout.lock;

  private UserSetting m_OrgParam = new UserSetting();
  private String m_OutputDir;

  private String c_GPXFile2 = "361.gpx";
  private String c_GPXFile = "362.gpx";

  private String c_GenFile = "362.csv";

  private String c_ExpGuiLayoutFileCurrent = "current.csv";
  private String c_ExpFile2 = "a_current.csv";

  private String c_ExpGuiLayoutFile362 = "362.csv";
  private String c_ExpFile4 = "a_362.csv";
  private String c_ExpFile5 = "b_362.csv";

  // Expected results in following dirs:
  private String m_DirExpSuffix = "_Exp";

  // Generated results in following dirs:
  private String m_guiLayout = "GuiLayout";
  private String m_guiLayoutFile = "GuiLayoutFile";
  private String m_GuiLayoutFiles = "GuiLayoutFiles";
  private String m_GuiLayoutFileByFile = "GuiLayoutFileByFile";
  private String m_GuiLayoutFileByFileReverse = "GuiLayoutFileByFileReverse";

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
    File l_File = FileUtils.GetResourceFile(m_guiLayout + m_DirExpSuffix + "/" + c_ExpGuiLayoutFileCurrent);
    String l_ExpFile = l_File.getAbsolutePath();

    File l_File2 = FileUtils.GetResourceFile(m_guiLayout + m_DirExpSuffix + "/" + c_ExpFile2);
    String l_ExpFile2 = l_File2.getAbsolutePath();

    frame.button("GPX File(s)").click();
    FileUtils.checkCreateDirectory(m_OutputDir + "/" + m_guiLayout);

    JFileChooserFixture fileChooser = frame.fileChooser();
    fileChooser.setCurrentDirectory(new File(m_OutputDir));
    fileChooser.fileNameTextBox().setText(c_GPXFile); // Set the desired file name
    fileChooser.approve();

    frame.button("OutputFolder").click();
    fileChooser = frame.fileChooser();
    fileChooser.setCurrentDirectory(new File(m_OutputDir + "/" + m_guiLayout + "/"));
    fileChooser.approve();

    frame.button("Summarise").click();

    synchronized (lock) {
      boolean bstat = false;
      try {
        //@formatter:off
        bstat = CsvFileComparator.assertFilesEqual(
          Path.of(m_OutputDir + "/" + m_guiLayout + "/" + c_ExpGuiLayoutFileCurrent), 
          Path.of(l_ExpFile),
          CsvFileComparator.Options.builder()
                .ignoreColumns(
                    CurrentCsvColumns.ADDR_ORIGIN, 
                    CurrentCsvColumns.ADDR_FINISH, 
                    CurrentCsvColumns.DATE,
                    CurrentCsvColumns.START_TIME, 
                    CurrentCsvColumns.END_TIME)
                .ignoreCommentLines(true) // default al true
                .build());
          //@formatter:on
      } catch (IOException e) {
        LOGGER.log(Level.WARNING, e.getMessage());
      }
      assertTrue(bstat);
    }
  }

  @Test
  public void testGUILayoutFile() {
    File l_File = FileUtils.GetResourceFile(m_guiLayoutFile + m_DirExpSuffix + "/" + c_ExpGuiLayoutFile362);
    String l_ExpFile = l_File.getAbsolutePath();

    File l_File2 = FileUtils.GetResourceFile(m_guiLayoutFile + m_DirExpSuffix + "/" + c_ExpFile4);
    String l_ExpFile2 = l_File2.getAbsolutePath();

    frame.button("GPX File(s)").click();
    FileUtils.checkCreateDirectory(m_OutputDir + "/" + m_guiLayoutFile);

    JFileChooserFixture fileChooser = frame.fileChooser();
    fileChooser.setCurrentDirectory(new File(m_OutputDir));
    fileChooser.fileNameTextBox().setText(c_GPXFile); // Set the desired file name
    fileChooser.approve();

    frame.button("OutputFolder").click();
    fileChooser = frame.fileChooser();
    fileChooser.setCurrentDirectory(new File(m_OutputDir + "/" + m_guiLayoutFile + "/"));
    fileChooser.approve();

    JTextComponentFixture outputfile = frame.textBox("Output filename");
    outputfile.setText(c_GenFile);

    frame.button("Summarise").click();

    synchronized (lock) {
      boolean bstat = false;
      try {
        //@formatter:off
        bstat = CsvFileComparator.assertFilesEqual(
          Path.of(m_OutputDir + "/" + m_guiLayoutFile + "/" + c_GenFile),
          Path.of(l_ExpFile),
          CsvFileComparator.Options.builder()
                .ignoreColumns(
                    CurrentCsvColumns.ADDR_ORIGIN, 
                    CurrentCsvColumns.ADDR_FINISH, 
                    CurrentCsvColumns.DATE,
                    CurrentCsvColumns.START_TIME, 
                    CurrentCsvColumns.END_TIME)
                .ignoreCommentLines(true) // default al true
                .build());
          //@formatter:on
      } catch (IOException e) {
        LOGGER.log(Level.WARNING, e.getMessage());
      }
      assertTrue(bstat);
    }
  }

  @Test
  public void testGUILayoutFiles() {
    File l_File = FileUtils.GetResourceFile(m_GuiLayoutFiles + m_DirExpSuffix + "/" + c_ExpGuiLayoutFile362);
    String l_ExpFile = l_File.getAbsolutePath();

    File l_File2 = FileUtils.GetResourceFile(m_GuiLayoutFiles + m_DirExpSuffix + "/" + c_ExpFile4);
    String l_ExpFile2 = l_File2.getAbsolutePath();

    frame.button("GPX File(s)").click();
    FileUtils.checkCreateDirectory(m_OutputDir + "/" + m_GuiLayoutFiles);

    JFileChooserFixture fileChooser = frame.fileChooser();
    fileChooser.setCurrentDirectory(new File(m_OutputDir));

    // Select multiple files
    File file1 = new File(m_OutputDir + "/" + c_GPXFile2);
    File file2 = new File(m_OutputDir + "/" + c_GPXFile);
    fileChooser.selectFiles(file1, file2);
    fileChooser.approve();

    frame.button("OutputFolder").click();
    fileChooser = frame.fileChooser();
    fileChooser.setCurrentDirectory(new File(m_OutputDir + "/" + m_GuiLayoutFiles + "/"));
    fileChooser.approve();

    JTextComponentFixture outputfile = frame.textBox("Output filename");
    outputfile.setText(c_GenFile);

    frame.button("Summarise").click();

    synchronized (lock) {
      boolean bstat = false;
      try {
        //@formatter:off
        bstat = CsvFileComparator.assertFilesEqual(
          Path.of(m_OutputDir + "/" + m_GuiLayoutFiles + "/" + c_GenFile),
          Path.of(l_ExpFile),
          CsvFileComparator.Options.builder()
                .ignoreColumns(
                    CurrentCsvColumns.ADDR_ORIGIN, 
                    CurrentCsvColumns.ADDR_FINISH, 
                    CurrentCsvColumns.DATE,
                    CurrentCsvColumns.START_TIME, 
                    CurrentCsvColumns.END_TIME)
                .ignoreCommentLines(true) // default al true
                .build());
          //@formatter:on
      } catch (IOException e) {
        LOGGER.log(Level.WARNING, e.getMessage());
      }
      assertTrue(bstat);
    }
  }

  @Test
  public void testGUILayoutFileByFile() {
    File l_File = FileUtils.GetResourceFile(m_GuiLayoutFileByFile + m_DirExpSuffix + "/" + c_ExpGuiLayoutFile362);
    String l_ExpFile = l_File.getAbsolutePath();

    File l_File2 = FileUtils.GetResourceFile(m_GuiLayoutFileByFile + m_DirExpSuffix + "/" + c_ExpFile4);
    String l_ExpFile2 = l_File2.getAbsolutePath();
    FileUtils.checkCreateDirectory(m_OutputDir + "/" + m_GuiLayoutFileByFile);

    File l_File3 = FileUtils.GetResourceFile(m_GuiLayoutFileByFile + m_DirExpSuffix + "/" + c_ExpFile5);
    String l_ExpFile3 = l_File3.getAbsolutePath();
    FileUtils.checkCreateDirectory(m_OutputDir + "/" + m_GuiLayoutFileByFile);

    frame.button("GPX File(s)").click();
    JFileChooserFixture fileChooser = frame.fileChooser();
    fileChooser.setCurrentDirectory(new File(m_OutputDir));
    fileChooser.fileNameTextBox().setText(c_GPXFile2); // Set the desired file name
    fileChooser.approve();

    frame.button("OutputFolder").click();
    fileChooser = frame.fileChooser();
    fileChooser.setCurrentDirectory(new File(m_OutputDir + "/" + m_GuiLayoutFileByFile + "/"));
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
      boolean bstat = false;
      try {
        //@formatter:off
        bstat = CsvFileComparator.assertFilesEqual(
        Path.of(m_OutputDir + "/" + m_GuiLayoutFileByFile + "/" + c_GenFile),
        Path.of(l_ExpFile),
        CsvFileComparator.Options.builder()
              .ignoreColumns(
                  CurrentCsvColumns.ADDR_ORIGIN, 
                  CurrentCsvColumns.ADDR_FINISH, 
                  CurrentCsvColumns.DATE,
                  CurrentCsvColumns.START_TIME, 
                  CurrentCsvColumns.END_TIME)
              .ignoreCommentLines(true) // default al true
              .build());
        //@formatter:on
      } catch (IOException e) {
        LOGGER.log(Level.WARNING, e.getMessage());
      }
      assertTrue(bstat);
    }
  }

  @Test
  public void testGUILayoutFileByFileReverse() {
    File l_File = FileUtils
        .GetResourceFile(m_GuiLayoutFileByFileReverse + m_DirExpSuffix + "/" + c_ExpGuiLayoutFile362);
    String l_ExpFile = l_File.getAbsolutePath();

    File l_File2 = FileUtils.GetResourceFile(m_GuiLayoutFileByFileReverse + m_DirExpSuffix + "/" + c_ExpFile4);
    String l_ExpFile2 = l_File2.getAbsolutePath();
    FileUtils.checkCreateDirectory(m_OutputDir + "/" + m_GuiLayoutFileByFileReverse);

    frame.button("GPX File(s)").click();
    JFileChooserFixture fileChooser = frame.fileChooser();
    fileChooser.setCurrentDirectory(new File(m_OutputDir));
    fileChooser.fileNameTextBox().setText(c_GPXFile); // Set the desired file name
    fileChooser.approve();

    frame.button("OutputFolder").click();
    fileChooser = frame.fileChooser();
    fileChooser.setCurrentDirectory(new File(m_OutputDir + "/" + m_GuiLayoutFileByFileReverse + "/"));
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
      boolean bstat = false;
      try {
        //@formatter:off
        bstat = CsvFileComparator.assertFilesEqual(
        Path.of(m_OutputDir + "/" + m_GuiLayoutFileByFileReverse + "/" + c_GenFile),
        Path.of(l_ExpFile),
        CsvFileComparator.Options.builder()
              .ignoreColumns(
                  CurrentCsvColumns.ADDR_ORIGIN, 
                  CurrentCsvColumns.ADDR_FINISH, 
                  CurrentCsvColumns.DATE,
                  CurrentCsvColumns.START_TIME, 
                  CurrentCsvColumns.END_TIME)
              .ignoreCommentLines(true) // default al true
              .build());
        //@formatter:on
      } catch (IOException e) {
        LOGGER.log(Level.WARNING, e.getMessage());
      }
      assertTrue(bstat);
    }
  }

  public final class CurrentCsvColumns {
    public static final int DATE = 0;
    public static final int START_TIME = 1;
    public static final int END_TIME = 2;
    public static final int LON_ORIGIN = 3;
    public static final int LAT_ORIGIN = 4;
    public static final int LON_FINISH = 5;
    public static final int LAT_FINISH = 6;
    public static final int ADDR_ORIGIN = 7;
    public static final int ADDR_FINISH = 8;
    public static final int DISTANCE = 9;
    public static final int DURATION = 10;
    public static final int SPEED = 11;

    private CurrentCsvColumns() {
    }
  }
}
