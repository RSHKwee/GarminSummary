package gui;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

/**
 * 
 * 
 * @author ReneK
 */
public class ActionPerformedOpenCnfFile extends SwingWorker<Void, String> implements MyAppendable {
  private static final Logger LOGGER = Logger.getLogger(Class.class.getName());
  private String newline = "\n";

  private Map<String, ArrayList<String>> m_PostProj = new LinkedHashMap<String, ArrayList<String>>();

  private JFileChooser fc;
  private JTextArea area = new JTextArea(30, 50);
  private GUILayout GUILayout;
  private File file = null;
  private String initialDirectory = "";

  /**
   * Constructor voor uitvoeren openen configuratie file.
   * <p>
   * 
   * @param guiLayout2        GUI Layout
   * @param a_IntialDirectory Directory waar configuratie bestand te vinden is
   */
  public ActionPerformedOpenCnfFile(GUILayout guiLayout2, String a_IntialDirectory) {
    GUILayout = guiLayout2;
    initialDirectory = a_IntialDirectory;
  }

  @Override
  /**
   * Melding toevoegen aan tekst area tbv. gebruiker.
   */
  public void append(String text) {
    area.append(text);
  }

  /**
   * Opdracht Open configuratie file uitvoeren in background.
   */
  @Override
  protected Void doInBackground() throws Exception {
    // Set up the file chooser.
    if (fc == null) {
      fc = new JFileChooser(initialDirectory);
      // Add a custom file filter.
//      fc.addChoosableFileFilter(new ConfigFilter());
      fc.setAcceptAllFileFilterUsed(true);
    }
    fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

    // Show it.
    int returnVal = fc.showDialog(GUILayout, "Open configbestand of Posten dir");
    // publish(" Approve : " + returnVal);
    // Process the results.

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      file = fc.getSelectedFile();

      if (file.isDirectory()) {
        LOGGER.log(Level.INFO, "Directory gekozen." + file.getName() + " Even geduld aub.... " + newline);
//        GenerateSceConfig vg_sceConf = new GenerateSceConfig(file.getPath());
//        m_PostProj = vg_sceConf.gegenereerdSceConfig();
        LOGGER.log(Level.INFO, "Directory gescanned.");
      } else {
        LOGGER.log(Level.INFO, file.getPath() + newline);
        // m_PostProj = sconf.readConfig(file.getPath());
      }
//      GUILayout.buildProjectenLijst(file, m_PostProj);

    } else {
      LOGGER.log(Level.INFO, "Niets gekozen." + newline);
    }
    // Reset the file chooser for the next time it's shown.
    fc.setSelectedFile(null);
    return null;
  }

  /**
   * Opdracht is uitgevoerd.
   */
  @Override
  protected void done() {
    if (file != null) {
//      GUILayout.buildProjectenLijst(file, m_PostProj);
      LOGGER.log(Level.FINE, "Config file ingelezen." + newline);
    }
  }
}
