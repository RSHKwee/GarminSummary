package kwee.garminSummary.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.Set;
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

import kwee.garminSummary.main.Main;
import kwee.garminSummary.main.UserSetting;
import kwee.library.AboutWindow;
import kwee.library.ApplicationMessages;
import kwee.library.ShowPreferences;
/**
 * Garmin GUI
 */
import kwee.logger.MyLogger;
import kwee.logger.TextAreaHandler;
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
  static final String c_CopyrightYear = "2023";
  private static String c_reponame = "GarminSummary";
  public static final Object lock = new Object();
  private ApplicationMessages bundle = ApplicationMessages.getInstance();

  // Loglevels: OFF SEVERE WARNING INFO CONFIG FINE FINER FINEST ALL
  static final String[] c_levels = { "OFF", "SEVERE", "WARNING", "INFO", "CONFIG", "FINE", "FINER", "FINEST", "ALL" };
  static final String[] c_LogToDisk = { "Yes", "No" };

  // Replace "path/to/help/file" with the actual path to your help file
  static final String m_HelpFile = "garminSummary.chm";

  // Variables
  private String m_LogDir = "c:/";
  private boolean m_OutputFolderModified = false;
  private JTextArea output;

  private JProgressBar m_ProgressBarFiles = new JProgressBar();
  private JProgressBar m_ProgressBarTracks = new JProgressBar();
  private JProgressBar m_ProgressBarSegments = new JProgressBar();
  private JLabel lblProgressLabel;
  private JLabel lblFileProgressLabel;

  // Preferences
  private UserSetting m_param = Main.m_param;
  private String m_Language = "nl";

  private File m_InputFolder;
  private File m_OutputFolder;
  private File[] m_GpxFiles;
  private boolean m_toDisk = false;
  private boolean m_Append = false;
  private Level m_Level = Level.INFO;
  private int i = 0;
  private JFrame m_Frame;

  /**
   * Define GUI layout
   * 
   */
  public GUILayout(JFrame frame) {
    m_Frame = frame;
    bundle.changeLanguage(m_param.get_Language());

    // GUI items
    JMenuBar menuBar = new JMenuBar();
    JMenuItem mntmLoglevel = new JMenuItem(bundle.getMessage("Loglevel"));
    JMenuItem mntmLanguages = new JMenuItem(bundle.getMessage("Languages"));
    mntmLoglevel.setName("Loglevel");
    menuBar.setName("menu");

    JTextField txtOutputFilename = new JTextField();
    txtOutputFilename.setName("Output filename");
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
    m_Language = m_param.get_Language();

    // Define Layout
    setLayout(new BorderLayout(0, 0));
    add(menuBar, BorderLayout.NORTH);

    // Define Setting menu in menu bar:
    JMenu mnSettings = new JMenu(bundle.getMessage("Settings"));
    mnSettings.setName("Settings");
    menuBar.add(mnSettings);

    // Option log level
    mntmLoglevel.setHorizontalAlignment(SwingConstants.LEFT);
    mntmLoglevel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame(bundle.getMessage("Loglevel"));
        String level = "";
        level = (String) JOptionPane.showInputDialog(frame, bundle.getMessage("Loglevel") + "?", "INFO",
            JOptionPane.QUESTION_MESSAGE, null, c_levels, m_Level.toString());
        if (level != null) {
          m_Level = Level.parse(level.toUpperCase());
          m_param.set_Level(m_Level);
          MyLogger.changeLogLevel(m_Level);
        }
      }
    });
    mnSettings.add(mntmLoglevel);

    // Add item Look and Feel
    JMenu menu = new JMenu(bundle.getMessage("LookAndFeel"));
    menu.setName("LookandFeel");
    menu.setHorizontalAlignment(SwingConstants.LEFT);
    mnSettings.add(menu);

    // Get all the available look and feel that we are going to use for
    // creating the JMenuItem and assign the action listener to handle
    // the selection of menu item to change the look and feel.
    UIManager.LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();
    for (UIManager.LookAndFeelInfo lookAndFeelInfo : lookAndFeels) {
      JMenuItem item = new JMenuItem(lookAndFeelInfo.getName());
      item.setName(lookAndFeelInfo.getName());
      item.addActionListener(event -> {
        try {
          // Set the look and feel for the frame and update the UI
          // to use a new selected look and feel.
          UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
          SwingUtilities.updateComponentTreeUI(this);
          m_param.set_LookAndFeel(lookAndFeelInfo.getClassName());
        } catch (Exception e) {
          LOGGER.log(Level.WARNING, e.getMessage());
        }
      });
      menu.add(item);
    }

    // Language setting
    mntmLanguages.setHorizontalAlignment(SwingConstants.LEFT);
    mntmLanguages.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame(bundle.getMessage("Language"));
        String language = "nl";
        Set<String> l_languages = bundle.getTranslations();
        String[] la_languages = new String[l_languages.size()];
        i = 0;
        l_languages.forEach(lang -> {
          la_languages[i] = lang;
          i++;
        });

        language = (String) JOptionPane.showInputDialog(frame, bundle.getMessage("Language") + "?", "nl",
            JOptionPane.QUESTION_MESSAGE, null, la_languages, m_Language);
        if (language != null) {
          m_Language = language;
          m_param.set_Language(m_Language);
          m_param.save();
          bundle.changeLanguage(language);
          restartGUI();
        }
      }
    });
    mnSettings.add(mntmLanguages);

    // Option Logging to Disk
    JCheckBoxMenuItem mntmLogToDisk = new JCheckBoxMenuItem(bundle.getMessage("CreateLogfiles"));
    mntmLogToDisk.setName("CreateLogfiles");
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
            LOGGER.log(Level.INFO, bundle.getMessage("LogFolder", file.getAbsolutePath()));
            m_LogDir = file.getAbsolutePath() + "/";
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

    // Option Preferences
    JMenuItem mntmPreferences = new JMenuItem(bundle.getMessage("Preferences"));
    mntmPreferences.setName("Preferences");
    mntmPreferences.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ShowPreferences showpref = new ShowPreferences(UserSetting.NodePrefName);
        showpref.showAllPreferences();
      }
    });
    mnSettings.add(mntmPreferences);

    // ? item
    JMenu mnHelpAbout = new JMenu("?");
    mnHelpAbout.setName("?");
    mnHelpAbout.setHorizontalAlignment(SwingConstants.RIGHT);
    menuBar.add(mnHelpAbout);

    // Help
    JMenuItem mntmHelp = new JMenuItem(bundle.getMessage("Help"));
    mntmHelp.setName("Help");
    mntmHelp.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        File helpFile = new File("help\\" + m_Language + "\\" + m_HelpFile);

        if (helpFile.exists()) {
          try {
            // Open the help file with the default viewer
            Desktop.getDesktop().open(helpFile);
          } catch (IOException e1) {
            e1.printStackTrace();
          }
        } else {
          LOGGER.log(Level.INFO, bundle.getMessage("HelpFileNotFound", helpFile.getAbsolutePath()));
        }
      }
    });
    mnHelpAbout.add(mntmHelp);

    // About
    JMenuItem mntmAbout = new JMenuItem(bundle.getMessage("About"));
    mntmAbout.setName("About");
    mntmAbout.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        AboutWindow l_window = new AboutWindow(c_reponame, Main.m_creationtime, c_CopyrightYear);
        l_window.setVisible(true);
      }
    });
    mnHelpAbout.add(mntmAbout);

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
    JLabel lblGPXFile = new JLabel(bundle.getMessage("SelectGPX"));
    lblGPXFile.setEnabled(false);
    lblGPXFile.setHorizontalAlignment(SwingConstants.RIGHT);
    panel.add(lblGPXFile, "cell 1 0");

    JButton btnSummarize = new JButton(bundle.getMessage("Summarise"));
    JButton btnGPXFile = new JButton("GPX File(s)");
    btnSummarize.setName("Summarise");
    btnGPXFile.setName("GPX File(s)");

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
        FileFilter filter = new FileNameExtensionFilter(bundle.getMessage("GPXFile"), "gpx");
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
    JCheckBox chkbAddToFile = new JCheckBox(bundle.getMessage("AddToFile"));
    chkbAddToFile.setName("Addtofile");

    JButton btnOutputFolder = new JButton(bundle.getMessage("OutputFolder"));
    btnOutputFolder.setName("OutputFolder");
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
          LOGGER.log(Level.INFO, bundle.getMessage("OutputFolderSelected", file.getAbsolutePath()));
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
        LOGGER.log(Level.CONFIG, bundle.getMessage("AppendToFile", Boolean.toString(selected)));
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

  private void restartGUI() {
    // Dispose of the current GUI window or frame
    m_Frame.dispose();

    // Recreate the main GUI window or frame
    m_Frame = Main.createAndShowGUI();
  }

  /**
   * Must be overridden..
   */
  @Override
  public void itemStateChanged(ItemEvent e) {
    LOGGER.log(Level.INFO, "itemStateChanged");
  }
}
