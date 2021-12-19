package garmin.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import library.Summary;
import library.TxtBestand;

public class ActionPerformedSummarize extends SwingWorker<Void, String> implements MyAppendable {
  private static final Logger LOGGER = Logger.getLogger(Class.class.getName());
  private JProgressBar m_pbar;
  private JProgressBar m_pbar1;

  // Variables
  private File m_OutputFolder;
  private File[] m_GpxFiles;
  private String m_OutFileName;

  private JTextArea area = new JTextArea(30, 50);
  private JLabel m_ProgressLabel;

  public ActionPerformedSummarize(File[] a_GpXFiles, File a_OutputFolder, String a_OutFileName, JProgressBar a_pbar,
      JLabel a_Progresslabel, JProgressBar a_pbar1) {
    LOGGER.log(Level.FINE, "Set call ActionPerformedSummarize");
    m_GpxFiles = a_GpXFiles;
    m_OutputFolder = a_OutputFolder;
    m_OutFileName = a_OutFileName;

    m_pbar = a_pbar;
    m_pbar1 = a_pbar1;
    m_ProgressLabel = a_Progresslabel;
  }

  @Override
  public void append(String text) {
    area.append(text);
  }

  /**
   * Process in background.
   */
  @Override
  protected Void doInBackground() throws Exception {
    Summary v_sum = new Summary(m_pbar, m_ProgressLabel, m_pbar1);
    ArrayList<String> v_Regels = new ArrayList<String>();
    v_Regels.add(v_sum.Header());

    // Process GPX files one by one
    for (int i = 0; i < m_GpxFiles.length; i++) {
      v_Regels.addAll(v_sum.TripsSummary(m_GpxFiles[i].getAbsolutePath()));
    }
    TxtBestand.DumpBestand(m_OutputFolder.getAbsolutePath() + "//" + m_OutFileName, v_Regels);
    return null;
  }

  @Override
  protected void done() {
    LOGGER.log(Level.INFO, "");
    LOGGER.log(Level.INFO, "Klaar.");
  }

}
