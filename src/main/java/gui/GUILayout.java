package gui;

/**
 * Post 21 Scenario generator
 */
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

import logger.MyLogger;
import logger.TextAreaHandler;
import net.miginfocom.swing.MigLayout;

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
  static final String[] c_levels = { "OFF", "SEVERE", "WARNING", "INFO", "CONFIG", "FINE", "FINER", "FINEST", "ALL" };
  static final String[] c_DelFolderContents = { "Ja", "Nee" };
  static final String[] c_LogToDisk = { "Ja", "Nee" };

  private Level m_Level = Level.INFO;
  private Boolean m_toDisk = false;
  private Boolean m_DelFolderContents = false;
  private StringBuffer choices;

  // Variables
  private String m_RootDir = "c:\\";
  private String m_SceDirPrefix = "gen";
  private String m_SceDirJenkinsPrefix = "Jenkins";
  private String newline = "\n";

  private Map<String, ArrayList<String>> m_SceOverneem;
  // private SceConfig sconf = new SceConfig();

  private ArrayList<Integer> selected = new ArrayList<Integer>();
  private JCheckBox perPostButton;
  private JCheckBox jenkinsScenButton;
  private JCheckBox jenkRegressButton;

  private JCheckBox jenkCopyButton;
  private final JButton btnGenereerScenarios = new JButton("Genereer scenario's");
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

  /**
   * Defineer GUI layout
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public GUILayout(String a_ConfigBestand) {
    Map<String, ArrayList<String>> v_PostProj = new LinkedHashMap<String, ArrayList<String>>();
    File v_ConfigFile = new File(a_ConfigBestand);
    choices = new StringBuffer("psrc");

    listModel = new DefaultListModel();
    listModel2 = new DefaultListModel();
    list = new JList(listModel);

    listSelectionModel = list.getSelectionModel();
    listSelectionModel.addListSelectionListener(new SharedListSelectionHandler());
    setLayout(new BorderLayout(0, 0));

    JMenuBar menuBar = new JMenuBar();
    add(menuBar, BorderLayout.NORTH);

    // Defineren Setting menu in menubalk:
    JMenu mnScenarioPrefix = new JMenu("Settings");
    menuBar.add(mnScenarioPrefix);

    // Optie Scenario prefix
    JMenuItem mntmScenarioPrefix = new JMenuItem("Scenario prefix");
    mntmScenarioPrefix.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame("Scenario prefix");
        m_SceDirPrefix = JOptionPane.showInputDialog(frame, "Scenario prefix?", m_SceDirPrefix);
      }
    });
    mnScenarioPrefix.add(mntmScenarioPrefix);

    // Optie Jenkins prefix
    JMenuItem mntmJenkinsPrefix = new JMenuItem("Jenkins prefix");
    mntmJenkinsPrefix.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame("Jenkins prefix");
        m_SceDirJenkinsPrefix = JOptionPane.showInputDialog(frame, "Jenkins prefix?", m_SceDirJenkinsPrefix);
      }
    });
    mnScenarioPrefix.add(mntmJenkinsPrefix);

    // Optie log level
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

    // Optie Start met delete directory inhoud
    JMenuItem mntmDeleteDirContents = new JMenuItem("Delete directory inhoud");
    mntmDeleteDirContents.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame("Delete directory inhoud");
        String DelFolderContents = "";
        DelFolderContents = (String) JOptionPane.showInputDialog(frame, "Delete directory inhoud?", "Nee",
            JOptionPane.QUESTION_MESSAGE, null, c_DelFolderContents, m_DelFolderContents);
        if (DelFolderContents == "Ja") {
          m_DelFolderContents = true;
        } else {
          m_DelFolderContents = false;
        }
      }
    });
    mnScenarioPrefix.add(mntmDeleteDirContents);

    // Optie aanmaken logfiles op disk
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

    // Toevoegen Look and Feel
    JMenu menu = new JMenu("Look and Feel");
    mnScenarioPrefix.add(menu);

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

    // Do the layout.
    JScrollPane listPane = new JScrollPane(list);
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

    // output = new JTextArea(12, 10);
    output.setEditable(false);
    output.setTabSize(4);
    JScrollPane outputPane = new JScrollPane(output, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    bottomHalf.add(outputPane);

    JPanel controlPane = new JPanel();
    outputPane.setColumnHeaderView(controlPane);
    controlPane.setLayout(new MigLayout("", "[129px][65px,grow][111px,grow][105px][87px][76px]", "[23px][][][]"));
    controlPane.add(btnOpenCnfFile, "cell 0 0,alignx left,aligny top");

    lblNewLabel = new JLabel(" ");
    controlPane.add(lblNewLabel, "cell 1 0,alignx trailing");

    btnOpenCnfFile.setVerticalAlignment(SwingConstants.TOP);
    btnOpenCnfFile.setHorizontalAlignment(SwingConstants.LEFT);
    btnOpenCnfFile.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // ActionPerformedOpenCnfFile v_ActionOpenCnfFile = new
        // ActionPerformedOpenCnfFile(GUILayout.this, m_RootDir);
        btnGenereerScenarios.setEnabled(false);
        // v_ActionOpenCnfFile.execute();
      }
    });

    // Create the check boxes.
    perPostButton = new JCheckBox("RT Scenario's");
    controlPane.add(perPostButton, "cell 0 1,alignx left,aligny top");
    perPostButton.setVerticalAlignment(SwingConstants.TOP);
    perPostButton.setMnemonic(KeyEvent.VK_P);
    perPostButton.setSelected(true);
    perPostButton.addItemListener(this); // Register a listener for the check boxes.

    jenkRegressButton = new JCheckBox("Jenkins RT scenario");
    controlPane.add(jenkRegressButton, "cell 1 1,alignx left,aligny top");
    jenkRegressButton.setMnemonic(KeyEvent.VK_R);
    jenkRegressButton.setSelected(true);
    jenkRegressButton.addItemListener(this);

    jenkinsScenButton = new JCheckBox("Jenkins scenario's");
    controlPane.add(jenkinsScenButton, "cell 0 2,alignx left,aligny top");
    jenkinsScenButton.setMnemonic(KeyEvent.VK_S);
    jenkinsScenButton.setSelected(true);
    jenkinsScenButton.addItemListener(this);

    jenkCopyButton = new JCheckBox("Jenkins copy scenario's");
    jenkCopyButton.setMnemonic(KeyEvent.VK_C);
    jenkCopyButton.setSelected(true);
    controlPane.add(jenkCopyButton, "cell 1 2,alignx left,aligny top");
    jenkCopyButton.addItemListener(this);

    controlPane.add(btnGenereerScenarios, "cell 2 2,alignx left,aligny top");
    btnGenereerScenarios.setVerticalAlignment(SwingConstants.TOP);
    btnGenereerScenarios.setHorizontalAlignment(SwingConstants.LEFT);
    btnGenereerScenarios.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // ProgressBar v_ProgressBar = new ProgressBar("Genereren scenario's ... ",
        // "Genereren .. ");
        ArrayList<String> v_selectedprojects = new ArrayList<String>();
        selected.forEach(idx -> {
          String v_proj = listModel2.getElementAt(idx).toString();
          v_selectedprojects.add(v_proj);
        });
        /*
         * ActionPerformedGenereerScenarios v_ActionGenScen = new
         * ActionPerformedGenereerScenarios(m_Level, m_RootDir, m_SceOverneem, choices,
         * m_SceDirPrefix, m_SceDirJenkinsPrefix, v_selectedprojects, m_ProgressBar,
         * m_DelFolderContents, m_toDisk, lblProgressLabel); v_ActionGenScen.execute();
         */
      }
    });
    btnGenereerScenarios.setEnabled(false);

    controlPane.add(m_ProgressBar, "south");
    lblProgressLabel = new JLabel(" ");
    controlPane.add(lblProgressLabel, "cell 3 2,alignx left,aligny top");

    bottomHalf.setMinimumSize(new Dimension(500, 100));
    bottomHalf.setPreferredSize(new Dimension(500, 400));
    splitPane.add(bottomHalf);

    if (v_ConfigFile.exists()) {
      if (v_ConfigFile.isDirectory()) {
//        GenerateSceConfig vg_sceConf = new GenerateSceConfig(v_ConfigFile.getPath());
//        v_PostProj = vg_sceConf.gegenereerdSceConfig();
        buildProjectenLijst(v_ConfigFile, v_PostProj);
      } else {
//        v_PostProj = sconf.readConfig(v_ConfigFile.getPath());
        buildProjectenLijst(v_ConfigFile, v_PostProj);
      }
    }
  }

  @SuppressWarnings("unchecked")
  /**
   * Opbouwen keuze lijst van projecten waarvoor scenario's kunnen worden
   * gegenereerd.
   *
   * @param a_file     Configuratie file met overneem relaties
   * @param a_PostProj Lijst van projecten
   */
  public void buildProjectenLijst(File a_file, Map<String, ArrayList<String>> a_PostProj) {
//    SceOverneemConfig v_sceOverConf = new SceOverneemConfig();

    String v_overneem = a_file.getPath().replace(a_file.getName(), "SceOvernemen.txt");
    // m_SceOverneem = v_sceOverConf.readConfig(v_overneem);
    listModel.clear();
    listModel2.clear();

    SortedSet<String> v_posten = new TreeSet<String>(a_PostProj.keySet());
    v_posten.forEach(v_pst -> {
      try {
        /*
         * if (!v_pst.contains(SceConfig.C_ROOTDIR)) { ArrayList<String> v_proj =
         * a_PostProj.get(v_pst);
         * 
         * // Posten kunnen een versie nummer bevatten (";<n>"). String[] v_lineparts =
         * v_pst.split(";"); String v_post = v_lineparts[0];
         * 
         * listModel.addElement(v_post + " | " + v_proj.get(0));
         * listModel2.addElement(v_post + ";" + v_proj.get(0) + ";" + v_proj.get(1)); }
         * else { ArrayList<String> v_proj = a_PostProj.get(v_pst); m_RootDir =
         * v_proj.get(0); lblNewLabel.setText("Root : " + m_RootDir); }
         */
      } catch (java.lang.NullPointerException en) {
        output.append("Inlezen settings bestand mislukt." + newline);
      }
    });
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

    if (source == perPostButton) {
      index = 0;
      c = 'p';
    } else if (source == jenkinsScenButton) {
      index = 1;
      c = 's';
    } else if (source == jenkRegressButton) {
      index = 2;
      c = 'r';
    } else if (source == jenkCopyButton) {
      index = 3;
      c = 'c';
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
