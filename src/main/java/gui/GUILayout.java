package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import logger.MyLogger;
import logger.TextAreaHandler;
import net.miginfocom.swing.MigLayout;

/**
 * Post21DataCsv: Opbouw van GUI scherm naar gebruiker. Inclusief afhandeling
 * knoppen, etc.
 * 
 * @author kweers
 *
 */
public class GUILayout extends JPanel implements ItemListener {
  /**
   *
   */
  class SharedListSelectionHandler implements ListSelectionListener {
    @Override
    /**
     *
     * @param e
     */
    public void valueChanged(ListSelectionEvent e) {
      ListSelectionModel lsm = (ListSelectionModel) e.getSource();

      if (lsm.isSelectionEmpty()) {
        btnGenereerScenarios.setEnabled(false);
      } else {
        // Find out which indexes are selected.
        int minIndex = lsm.getMinSelectionIndex();
        int maxIndex = lsm.getMaxSelectionIndex();
        selected.clear();
        for (int i = minIndex; i <= maxIndex; i++) {
          if (lsm.isSelectedIndex(i)) {
            selected.add(i);
          }
        }
        btnGenereerScenarios.setEnabled(true);
      }
    }
  }

  private static final Logger LOGGER = Logger.getLogger(Class.class.getName());
  private static final long serialVersionUID = 1L;

  // * -3 Loglevel: OFF SEVERE WARNING INFO CONFIG FINE FINER FINEST ALL <br>
  private static final String[] c_levels = { "OFF", "SEVERE", "WARNING", "INFO", "CONFIG", "FINE", "FINER", "FINEST",
      "ALL" };
  private static final String[] c_DelFolderContents = { "Ja", "Nee" };
  private static final String[] c_LogToDisk = { "Ja", "Nee" };

  private Level m_Level = Level.INFO;
  private Boolean m_toDisk = false;
  private Boolean m_DelFolderContents = true;
  private StringBuffer choices;
  private String m_csvdir = "c:\\Post21toCsv\\csv";

  // Variables
  private String m_RootDir = "c:\\";
  private String newline = "\n";

//  private SceConfig sconf = new SceConfig();

  private ArrayList<Integer> selected = new ArrayList<Integer>();
  private JCheckBox jenkinsScenButton;
  private JCheckBox summaryButton;

  private final JButton btnGenereerScenarios = new JButton("Converteer XML naar CSV");
  private final JButton btnOpenCnfFile = new JButton("Open configuratie bestand");
  private JLabel lblNewLabel;

  private JTextArea output;
  @SuppressWarnings("rawtypes")
  private JList list;
  @SuppressWarnings("rawtypes")
  private DefaultListModel listModel;
  @SuppressWarnings("rawtypes")
  private DefaultListModel listModel2;
  private ListSelectionModel listSelectionModel;

  private JProgressBar m_ProgressBar = new JProgressBar();
  private JLabel lblProgressLabel;
  private JLabel lblCsvDirLabel;
  private GUILayout m_GUILayout;

  @SuppressWarnings({ "unchecked", "rawtypes" })
  /**
   * Defineer GUI layout
   * 
   * @param a_ConfigBestand Configuratie bestand
   */
  public GUILayout(String a_ConfigBestand) {
    Map<String, ArrayList<String>> v_PostProj = new LinkedHashMap<String, ArrayList<String>>();
    File v_ConfigFile = new File(a_ConfigBestand);
    m_GUILayout = this;
    // BorderLayout borderLayout = new BorderLayout();
    choices = new StringBuffer("-s");

    listModel = new DefaultListModel();
    listModel2 = new DefaultListModel();
    list = new JList(listModel);

    listSelectionModel = list.getSelectionModel();
    listSelectionModel.addListSelectionListener(new SharedListSelectionHandler());
    setLayout(new BorderLayout(0, 0));
    JScrollPane listPane = new JScrollPane(list);

    // Do the layout.
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    add(splitPane);

    JPanel topHalf = new JPanel();
    topHalf.setLayout(new BoxLayout(topHalf, BoxLayout.LINE_AXIS));
    JPanel listContainer = new JPanel(new GridLayout(1, 1));
    listContainer.setBorder(BorderFactory.createTitledBorder("Projecten"));
    listContainer.add(listPane);

    topHalf.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
    topHalf.add(listContainer);

    topHalf.setMinimumSize(new Dimension(300, 200));
    topHalf.setPreferredSize(new Dimension(130, 110));
    splitPane.add(topHalf);

    JPanel bottomHalf = new JPanel();
    bottomHalf.setLayout(new BoxLayout(bottomHalf, BoxLayout.X_AXIS));

    // Build output area.
    try {
      MyLogger.setup(m_Level, m_RootDir, m_toDisk);
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

    JPanel controlPane = new JPanel();
    outputPane.setColumnHeaderView(controlPane);
    controlPane.setLayout(new MigLayout("", "[129px][65px,grow][111px,grow][105px][87px][76px]", "[23px][][][]"));
    controlPane.add(btnOpenCnfFile, "cell 0 0,alignx left");

    lblNewLabel = new JLabel(" ");
    controlPane.add(lblNewLabel, "cell 1 0,alignx left");

    btnOpenCnfFile.setVerticalAlignment(SwingConstants.TOP);
    btnOpenCnfFile.setHorizontalAlignment(SwingConstants.LEFT);
    btnOpenCnfFile.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ActionPerformedOpenCnfFile v_ActionOpenCnfFile = new ActionPerformedOpenCnfFile(GUILayout.this, m_RootDir);
        btnGenereerScenarios.setEnabled(false);
        v_ActionOpenCnfFile.execute();
      }
    });

    // Create the check boxes.
    jenkinsScenButton = new JCheckBox("Jenkins Posten");
    controlPane.add(jenkinsScenButton, "cell 2 0,alignx left,aligny top");
    jenkinsScenButton.setMnemonic(KeyEvent.VK_J);
    jenkinsScenButton.setSelected(false);
    jenkinsScenButton.addItemListener(this);

    JButton btnCsvOutpDir = new JButton("Csv outp dir");
    controlPane.add(btnCsvOutpDir, "cell 0 1");
    btnCsvOutpDir.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser();

        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setFileFilter(new FileFilter() {
          @Override
          public boolean accept(File f) {
            System.out.println(f.toString());
            return f.isDirectory();
          }

          @Override
          public String getDescription() {
            System.out.println("Any file");
            return "Any file";
          }
        });

        fc.setDialogType(JFileChooser.SAVE_DIALOG);
        fc.setApproveButtonText("Select");
        fc.setCurrentDirectory(new File(m_csvdir));
        fc.showDialog(m_GUILayout, "Kies CSV output bestand");

        File dir = fc.getSelectedFile();
        if (!dir.exists()) {
          dir = dir.getParentFile();
        } else {
          m_csvdir = dir.toString();
        }
        System.out.println("Gekozen file: " + dir.toString());
        lblCsvDirLabel.setText("CsvDir: " + m_csvdir);
      }
    });

    lblCsvDirLabel = new JLabel("CsvDir: " + m_csvdir);
    controlPane.add(lblCsvDirLabel, "cell 1 1,alignx left,aligny top");

    // Create the check boxes.
    summaryButton = new JCheckBox("Samenvatting maken");
    controlPane.add(summaryButton, "cell 2 1,alignx left,aligny top");
    summaryButton.setVerticalAlignment(SwingConstants.TOP);
    summaryButton.setMnemonic(KeyEvent.VK_S);
    summaryButton.setSelected(true);
    summaryButton.addItemListener(this);

    controlPane.add(btnGenereerScenarios, "cell 2 2,alignx left,aligny top");
    btnGenereerScenarios.setVerticalAlignment(SwingConstants.TOP);
    btnGenereerScenarios.setHorizontalAlignment(SwingConstants.LEFT);
    btnGenereerScenarios.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ArrayList<String> v_selectedprojects = new ArrayList<String>();
        selected.forEach(idx -> {
          String v_proj = listModel2.getElementAt(idx).toString();
          v_selectedprojects.add(v_proj);
        });
//        ActionPerformedConverteerCSV v_ActionGenScen = new ActionPerformedConverteerCSV(m_Level, m_RootDir, choices,
//            v_selectedprojects, m_ProgressBar, m_DelFolderContents, m_toDisk, m_csvdir, lblProgressLabel);
//        v_ActionGenScen.execute();
      }
    });
    btnGenereerScenarios.setEnabled(false);

    controlPane.add(m_ProgressBar, "south");
    lblProgressLabel = new JLabel(" ");
    controlPane.add(lblProgressLabel, "cell 3 2,alignx left,aligny top");

    bottomHalf.setMinimumSize(new Dimension(500, 100));
    bottomHalf.setPreferredSize(new Dimension(500, 400));
    splitPane.add(bottomHalf);

    JMenuBar menuBar = new JMenuBar();
    add(menuBar, BorderLayout.NORTH);

    JMenu mnScenarioPrefix = new JMenu("Settings");
    menuBar.add(mnScenarioPrefix);

    JMenuItem mntmLoglevel = new JMenuItem("Loglevel");
    mntmLoglevel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame("Loglevel");
        String level = "";
        level = (String) JOptionPane.showInputDialog(frame, "Loglevel?", "INFO", JOptionPane.QUESTION_MESSAGE, null,
            c_levels, m_Level.toString());
        m_Level = Level.parse(level.toUpperCase());
      }
    });
    mnScenarioPrefix.add(mntmLoglevel);

    JMenuItem mntmDeleteDirContents = new JMenuItem("Delete tmp-directory");
    mntmDeleteDirContents.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame("Delete directory inhoud");
        String DelFolderContents = "";
        DelFolderContents = (String) JOptionPane.showInputDialog(frame, "Delete tmp-directory?", "Nee",
            JOptionPane.QUESTION_MESSAGE, null, c_DelFolderContents, m_DelFolderContents);
        if (DelFolderContents == "Ja") {
          m_DelFolderContents = true;
        } else {
          m_DelFolderContents = false;
        }
      }
    });
    mnScenarioPrefix.add(mntmDeleteDirContents);

    JMenuItem mntmLogToDisk = new JMenuItem("Logfiles aanmaken");
    mntmLogToDisk.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame("Logfiles aanmaken");
        String ToDisk = "";
        ToDisk = (String) JOptionPane.showInputDialog(frame, "Logfiles aanmaken?", "Nee", JOptionPane.QUESTION_MESSAGE,
            null, c_LogToDisk, m_toDisk);
        if (ToDisk == "Ja") {
          m_toDisk = true;
        } else {
          m_toDisk = false;
        }
      }
    });
    mnScenarioPrefix.add(mntmLogToDisk);

    JMenu menu = new JMenu("Look and Feel");
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
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
      menu.add(item);
    }
    mnScenarioPrefix.add(menu);

    JMenuItem mntmCsvDir = new JMenuItem("CSV Output directory");
    mntmCsvDir.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser();

        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setFileFilter(new FileFilter() {
          @Override
          public boolean accept(File f) {
            System.out.println(f.toString());
            return f.isDirectory();
          }

          @Override
          public String getDescription() {
            System.out.println("Any folder");
            return "Any folder";
          }
        });

        fc.setDialogType(JFileChooser.SAVE_DIALOG);
        fc.setApproveButtonText("Select");
        fc.setCurrentDirectory(new File(m_csvdir));
        fc.showDialog(m_GUILayout, "Kies CSV output dir");

        File dir = fc.getSelectedFile();
        if (!dir.exists()) {
          dir = dir.getParentFile();
        } else {
          m_csvdir = dir.toString();
        }
        System.out.println("Gekozen dir: " + dir.toString());
        lblCsvDirLabel.setText("CsvDir: " + m_csvdir);
      }
    });
    mnScenarioPrefix.add(mntmCsvDir);

    if (v_ConfigFile.exists()) {
      if (v_ConfigFile.isDirectory()) {
//        GenerateSceConfig vg_sceConf = new GenerateSceConfig(v_ConfigFile.getPath());
//        v_PostProj = vg_sceConf.gegenereerdSceConfig();
//        buildProjectenLijst(v_PostProj);
      } else {
//        v_PostProj = sconf.readConfig(v_ConfigFile.getPath());
//        buildProjectenLijst(v_PostProj);
      }
    }
  }

  @SuppressWarnings("unchecked")
  /**
   * Opbouwen keuze lijst van projecten waarvoor scenario's kunnen worden
   * gegenereerd.
   *
   * @param a_PostProj Lijst van projecten
   */
  public void buildProjectenLijst(Map<String, ArrayList<String>> a_PostProj) {
    listModel.clear();
    listModel2.clear();

    SortedSet<String> v_posten = new TreeSet<String>(a_PostProj.keySet());
//    v_posten.forEach(v_pst -> {
//      try {
//        if (!v_pst.contains(SceConfig.C_ROOTDIR)) {
//          ArrayList<String> v_proj = a_PostProj.get(v_pst);

    // Posten kunnen een versie nummer bevatten (";<n>").
//          String[] v_lineparts = v_pst.split(";");
//          String v_post = v_lineparts[0];

//          listModel.addElement(v_post + " | " + v_proj.get(0));
//          listModel2.addElement(v_post + ";" + v_proj.get(0) + ";" + v_proj.get(1));
//        } else {
//          ArrayList<String> v_proj = a_PostProj.get(v_pst);
    // m_RootDir = v_proj.get(0);
//          lblNewLabel.setText("Root : " + m_RootDir);
//        }
//      } catch (java.lang.NullPointerException en) {
//        output.append("Inlezen settings bestand mislukt." + newline);
//      }
//    });
  }

  @Override
  /**
   * Nagaan welke boxes zijn geselecteerd.
   *
   * @param e GUI Event
   */
  public void itemStateChanged(ItemEvent e) {
    int index = 0;
    char c = '-';
    Object source = e.getItemSelectable();

    if (source == jenkinsScenButton) {
      index = 0;
      c = 'j';
    }
    if (source == summaryButton) {
      index = 1;
      c = 's';
    }
    // Now that we know which button was pushed, find out
    // whether it was selected or deselected.
    if (e.getStateChange() == ItemEvent.DESELECTED) {
      c = '-';
    }
    // Apply the change to the string.
    choices.setCharAt(index, c);
  }

}
