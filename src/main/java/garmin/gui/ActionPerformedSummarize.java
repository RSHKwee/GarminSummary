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
  private JProgressBar m_pbarFiles;
  private JProgressBar m_pbarTracks;
  private JProgressBar m_pbarSegemnts;

  // Variables
  private File m_OutputFolder;
  private File[] m_GpxFiles;
  private String m_OutFileName;

  private int m_ProcessedFiles = 0;
  private int m_NumberFiles = 0;

  private JTextArea area = new JTextArea(30, 50);
  private JLabel m_ProgressLabel;
  private JLabel m_FileProgressLabel;

  public ActionPerformedSummarize(File[] a_GpXFiles, File a_OutputFolder, String a_OutFileName,
      JProgressBar a_ProgressBarFiles, JLabel a_FileProgresslabel, JProgressBar a_pbarTracks, JLabel a_Progresslabel,
      JProgressBar a_pbarSegments) {
    LOGGER.log(Level.FINE, "Set call ActionPerformedSummarize");
    m_GpxFiles = a_GpXFiles;
    m_OutputFolder = a_OutputFolder;
    m_OutFileName = a_OutFileName;
    m_pbarFiles = a_ProgressBarFiles;
    m_pbarTracks = a_pbarTracks;
    m_pbarSegemnts = a_pbarSegments;
    m_ProgressLabel = a_Progresslabel;
    m_FileProgressLabel = a_FileProgresslabel;
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
    Summary v_sum = new Summary(m_pbarTracks, m_ProgressLabel, m_pbarSegemnts);
    ArrayList<String> v_Regels = new ArrayList<String>();
    v_Regels.add(v_sum.Header());
    m_ProcessedFiles = 0;
    m_NumberFiles = m_GpxFiles.length;
    m_pbarFiles.setMaximum(m_NumberFiles);
    m_pbarFiles.setVisible(true);
    m_FileProgressLabel.setVisible(true);

    // Process GPX files one by one
    for (int i = 0; i < m_GpxFiles.length; i++) {
      v_Regels.addAll(v_sum.TripsSummary(m_GpxFiles[i]));
      verwerkProgressFiles();
    }
    TxtBestand.DumpBestand(m_OutputFolder.getAbsolutePath() + "\\" + m_OutFileName, v_Regels);

    m_pbarFiles.setValue(0);
    m_pbarFiles.setVisible(false);
    m_FileProgressLabel.setVisible(false);
    return null;
  }

  @Override
  protected void done() {
    LOGGER.log(Level.INFO, "");
    LOGGER.log(Level.INFO, "Klaar.");
  }

  private void verwerkProgressFiles() {
    m_ProcessedFiles++;
    try {
      m_pbarFiles.setValue(m_ProcessedFiles);
      Double v_prog = ((double) m_ProcessedFiles / (double) m_NumberFiles) * 100;
      Integer v_iprog = v_prog.intValue();
      m_FileProgressLabel.setText(v_iprog.toString() + "% (" + m_ProcessedFiles + " of " + m_NumberFiles + " files)");
    } catch (Exception e) {
      // Do nothing
    }
  }
}
