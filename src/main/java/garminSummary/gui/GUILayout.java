package garminSummary.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import garminSummary.main.Main;
import library.UserSetting;
/**
 * Garmin GUI
 */
import logger.MyLogger;
import logger.TextAreaHandler;
import net.miginfocom.swing.MigLayout;

/**
 * Define GUI for Garmin trip summary tool.
 * 
 * @author rshkw
 *
 */
public class GUILayout extends JPanel implements ItemListener {
  private static final Logger LOGGER = Logger.getLogger(Class.class.getName());
  private static final long serialVersionUID = 1L;

  // Loglevels: OFF SEVERE WARNING INFO CONFIG FINE FINER FINEST ALL
  static final String[] c_levels = { "OFF", "SEVERE", "WARNING", "INFO", "CONFIG", "FINE", "FINER", "FINEST", "ALL" };
  static final String[] c_LogToDisk = { "Yes", "No" };

  // Variables
  private String m_LogDir = "c:\\";
  private boolean m_OutputFolderModified = false;
  private JTextArea output;

  private JProgressBar m_ProgressBarFiles = new JProgressBar();
  private JProgressBar m_ProgressBarTracks = new JProgressBar();
  private JProgressBar m_ProgressBarSegments = new JProgressBar();
  private JLabel lblProgressLabel;
  private JLabel lblFileProgressLabel;

  // Preferences
  private UserSetting m_param = Main.m_param;

  private File m_InputFolder;
  private File m_OutputFolder;
  private File[] m_GpxFiles;
  private boolean m_toDisk = false;
  private boolean m_Append = false;
  private Level m_Level = Level.INFO;

  /**
   * Define GUI layout
   * 
   */
  public GUILayout() {
    // GUI items
    JMenuBar menuBar = new JMenuBar();
    JMenuItem mntmLoglevel = new JMenuItem("Loglevel");

    JTextField txtOutputFilename = new JTextField();
    JLabel lblOutputFolder = new JLabel("");

    // Initialize parameters
    if (!m_param.get_OutputFolder().isBlank()) {
      m_OutputFolder = new File(m_param.get_OutputFolder());
      lblOutputFolder.setText(m_OutputFolder.getAbsolutePath());
    }

    // Initialize parameters
    if (!m_param.get_InputFolder().isBlank()) {
      m_InputFolder = new File(m_param.get_InputFolder());
    }
    m_Level = m_param.get_Level();
    m_toDisk = m_param.is_toDisk();
    m_LogDir = m_param.get_LogDir();

    // Define Layout
    setLayout(new BorderLayout(0, 0));
    add(menuBar, BorderLayout.NORTH);

    // Define Setting menu in menu bar:
    JMenu mnSettings = new JMenu("Settings");
    menuBar.add(mnSettings);

    // Option log level
    mntmLoglevel.setHorizontalAlignment(SwingConstants.LEFT);
    mntmLoglevel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame("Loglevel");
        String level = "";
        level = (String) JOptionPane.showInputDialog(frame, "Loglevel?", "INFO", JOptionPane.QUESTION_MESSAGE, null,
            c_levels, m_Level.toString());
        if (level != null) {
          m_Level = Level.parse(level.toUpperCase());
          m_param.set_Level(m_Level);
          MyLogger.changeLogLevel(m_Level);
        }
      }
    });
    mnSettings.add(mntmLoglevel);

    // Add item Look and Feel
    JMenu menu = new JMenu("Look and Feel");
    menu.setHorizontalAlignment(SwingConstants.LEFT);
    mnSettings.add(menu);

    // Get all the available look and feel that we are going to use for
    // creating the JMenuItem and assign the action listener to handle
    // the selection of menu item to change the look and feel.
    UIManager.LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();
    for (UIManager.LookAndFeelInfo lookAndFeelInfo : lookAndFeels) {
      JMenuItem item = new JMenuItem(lookAndFeelInfo.getName());
      item.addActionListener(event -> {
        try {
          // Set the look and feel for the frame and update the UI
          // to use a new selected look and feel.
          UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
          SwingUtilities.updateComponentTreeUI(this);
          m_param.set_LookAndFeel(lookAndFeelInfo.getClassName());
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
      menu.add(item);
    }

    // Option Logging to Disk
    JCheckBoxMenuItem mntmLogToDisk = new JCheckBoxMenuItem("Create logfiles");
    mntmLogToDisk.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        boolean selected = mntmLogToDisk.isSelected();
        if (selected) {
          JFileChooser fileChooser = new JFileChooser();
          fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
          fileChooser.setSelectedFile(new File(m_LogDir));
          int option = fileChooser.showOpenDialog(GUILayout.this);
          if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            LOGGER.log(Level.INFO, "Log folder: " + file.getAbsolutePath());
            m_LogDir = file.getAbsolutePath() + "\\";
            m_param.set_LogDir(m_LogDir);
            m_param.set_toDisk(true);
            m_toDisk = selected;
          }
        } else {
          m_param.set_toDisk(false);
          m_toDisk = selected;
        }
        try {
          MyLogger.setup(m_Level, m_LogDir, m_toDisk);
        } catch (IOException es) {
          LOGGER.log(Level.SEVERE, Class.class.getName() + ": " + es.toString());
          es.printStackTrace();
        }
      }
    });
    mnSettings.add(mntmLogToDisk);

    // Do the layout.
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    add(splitPane);

    JPanel topHalf = new JPanel();
    topHalf.setLayout(new BoxLayout(topHalf, BoxLayout.LINE_AXIS));

    topHalf.setMinimumSize(new Dimension(1, 1));
    topHalf.setPreferredSize(new Dimension(1, 1));
    splitPane.add(topHalf);

    JPanel bottomHalf = new JPanel();
    bottomHalf.setLayout(new BoxLayout(bottomHalf, BoxLayout.X_AXIS));

    // Build output area.
    try {
      MyLogger.setup(m_Level, m_LogDir, m_toDisk);
    } catch (IOException es) {
      LOGGER.log(Level.SEVERE, Class.class.getName() + ": " + es.toString());
      es.printStackTrace();
    }
    Logger rootLogger = Logger.getLogger("");
    for (Handler handler : rootLogger.getHandlers()) {
      if (handler instanceof TextAreaHandler) {
        TextAreaHandler textAreaHandler = (TextAreaHandler) handler;
        output = textAreaHandler.getTextArea();
      }
    }

    output.setEditable(false);
    output.setTabSize(4);
    JScrollPane outputPane = new JScrollPane(output, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    bottomHalf.add(outputPane);

    JPanel panel = new JPanel();
    outputPane.setColumnHeaderView(panel);
    panel.setLayout(new MigLayout("", "[129px][65px,grow][111px,grow][105px]", "[23px][][][][][][][][][][]"));
    outputPane.setColumnHeaderView(panel);
    outputPane.setSize(300, 500);

    panel.setLayout(
        new MigLayout("", "[46px,grow][][grow][205px,grow]", "[23px][23px][23px][23px][23px][23px][][][][]"));

    panel.setMinimumSize(new Dimension(350, 300));
    panel.setPreferredSize(new Dimension(350, 290));

    // Choose GPX File(s)
    JLabel lblGPXFile = new JLabel("Select one or more GPX files");
    lblGPXFile.setEnabled(false);
    lblGPXFile.setHorizontalAlignment(SwingConstants.RIGHT);
    panel.add(lblGPXFile, "cell 1 0");

    JButton btnSummarize = new JButton("Summarise");
    JButton btnGPXFile = new JButton("GPX File(s)");
    btnGPXFile.setHorizontalAlignment(SwingConstants.RIGHT);
    btnGPXFile.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(true);
        if (m_InputFolder != null) {
          fileChooser.setSelectedFile(m_InputFolder);
        }
        FileFilter filter = new FileNameExtensionFilter("GPX File", "gpx");
        fileChooser.setFileFilter(filter);
        if (m_GpxFiles != null) {
          if (m_GpxFiles.length >= 1) {
            fileChooser.setSelectedFile(m_GpxFiles[0]);
          }
        }
        int option = fileChooser.showOpenDialog(GUILayout.this);
        if (option == JFileChooser.APPROVE_OPTION) {
          m_GpxFiles = fileChooser.getSelectedFiles();
          if (m_GpxFiles.length == 1) {
            lblGPXFile.setText(m_GpxFiles[0].getName());
          } else {
            // Define label text GPX files
            String l_gpxfiles = m_GpxFiles[0].getName();
            boolean l_overflow = false;
            for (int i = 1; i < m_GpxFiles.length; i++) {
              if (l_gpxfiles.length() < 90) {
                l_gpxfiles = l_gpxfiles + " " + m_GpxFiles[i].getName();
              } else {
                if (!l_overflow) {
                  l_gpxfiles = l_gpxfiles + " ... " + m_GpxFiles[m_GpxFiles.length - 1].getName();
                  l_overflow = true;
                }
              }
            }
            lblGPXFile.setText(l_gpxfiles);
          }
          lblGPXFile.setEnabled(true);
          m_InputFolder = new File(m_GpxFiles[0].getAbsoluteFile().toString());
          m_param.set_InputFolder(m_InputFolder);
          if (!m_OutputFolderModified) {
            lblOutputFolder.setText(m_GpxFiles[0].getParent());
            m_OutputFolder = new File(m_GpxFiles[0].getParent());
            m_param.set_OutputFolder(m_OutputFolder);
            txtOutputFilename.setEnabled(true);
          }
          btnSummarize.setEnabled(true);
        }
      }
    });
    panel.add(btnGPXFile, "cell 0 0");

    // Define output folder & filename
    JCheckBox chkbAddToFile = new JCheckBox("Add to file");

    JButton btnOutputFolder = new JButton("Output folder");
    btnOutputFolder.setHorizontalAlignment(SwingConstants.RIGHT);
    btnOutputFolder.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setSelectedFile(new File(lblOutputFolder.getText() + "//"));
        int option = fileChooser.showOpenDialog(GUILayout.this);
        if (option == JFileChooser.APPROVE_OPTION) {
          File file = fileChooser.getSelectedFile();
          LOGGER.log(Level.INFO, "Output folder: " + file.getAbsolutePath());
          lblOutputFolder.setText(file.getAbsolutePath());
          m_OutputFolder = new File(file.getAbsolutePath());
          m_param.set_OutputFolder(m_OutputFolder);
          m_OutputFolderModified = true;
          txtOutputFilename.setEnabled(true);
          btnSummarize.setEnabled(true);
          chkbAddToFile.setEnabled(true);
        }
      }
    });
    panel.add(btnOutputFolder, "cell 0 1");

    lblOutputFolder.setHorizontalAlignment(SwingConstants.LEFT);
    panel.add(lblOutputFolder, "cell 1 1");

    txtOutputFilename.setHorizontalAlignment(SwingConstants.LEFT);
    txtOutputFilename.setText("current.csv");
    txtOutputFilename.setEnabled(false);
    txtOutputFilename.setColumns(100);
    panel.add(txtOutputFilename, "cell 1 2");

    // JCheckBox("Add to file");
    chkbAddToFile.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        boolean selected = chkbAddToFile.isSelected();
        m_Append = selected;
        m_param.set_Append(selected);
        LOGGER.log(Level.CONFIG, "Append to file:" + Boolean.toString(selected));
      }
    });

    panel.add(chkbAddToFile, "cell 1 3");

    // Summarize button
    btnSummarize.setEnabled(false);
    btnSummarize.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        m_param.save();
        ActionPerformedSummarize act = new ActionPerformedSummarize(m_GpxFiles, m_OutputFolder,
            txtOutputFilename.getText(), m_Append, m_ProgressBarFiles, lblFileProgressLabel, m_ProgressBarTracks,
            lblProgressLabel, m_ProgressBarSegments);
        act.execute();
      }
    });
    panel.add(btnSummarize, "cell 1 4");

    // Progress bars
    lblFileProgressLabel = new JLabel(" ");
    panel.add(lblFileProgressLabel, "cell 1 5, alignx right, aligny top");

    lblProgressLabel = new JLabel(" ");
    panel.add(lblProgressLabel, "cell 1 6, alignx right, aligny top");

    m_ProgressBarFiles.setVisible(false);
    m_ProgressBarTracks.setVisible(false);
    m_ProgressBarSegments.setVisible(false);
    panel.add(m_ProgressBarSegments, "south");
    panel.add(m_ProgressBarTracks, "south");
    panel.add(m_ProgressBarFiles, "south");

    bottomHalf.setMinimumSize(new Dimension(500, 100));
    bottomHalf.setPreferredSize(new Dimension(500, 300));
    splitPane.add(bottomHalf);
  }

  /**
   * Must be overridden..
   */
  @Override
  public void itemStateChanged(ItemEvent e) {
    LOGGER.log(Level.INFO, "itemStateChanged");
  }
}
