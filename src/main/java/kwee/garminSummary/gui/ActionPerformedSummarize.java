package kwee.garminSummary.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import kwee.library.ApplicationMessages;
import kwee.library.TxtBestand;

public class ActionPerformedSummarize extends SwingWorker<Void, String> implements MyAppendable {
  private static final Logger LOGGER = Logger.getLogger(Class.class.getName());
  private ApplicationMessages bundle = ApplicationMessages.getInstance();

  // Variables
  private JProgressBar m_pbarFiles;
  private JProgressBar m_pbarTracks;
  private JProgressBar m_pbarSegemnts;

  private File m_OutputFolder;
  private File[] m_GpxFiles;
  private String m_OutFileName;

  private int m_ProcessedFiles = 0;
  private int m_NumberFiles = -1;
  private boolean m_Append = false;

  private JTextArea area = new JTextArea(30, 50);
  private JLabel m_ProgressLabel;
  private JLabel m_FileProgressLabel;

  // Object lock = GUILayout.lock;

  /**
   * Constructor, initialize variables.
   * 
   * @param a_GpXFiles      List of GPX file to process
   * @param a_OutputFolder  Directory for result
   * @param a_OutFileName   Filename
   * @param a_pbarTracks    Tracks progress bar
   * @param a_Progresslabel Label
   * @param a_pbarSegments  Segments progress bar
   */
  public ActionPerformedSummarize(File[] a_GpXFiles, File a_OutputFolder, String a_OutFileName, boolean a_Append,
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
    m_Append = a_Append;
  }

  /**
   * Append text to display area.
   * 
   * @param text String to display
   */
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
    m_ProcessedFiles = -1;
    m_NumberFiles = m_GpxFiles.length;
    m_pbarFiles.setMaximum(m_NumberFiles);
    m_pbarFiles.setVisible(true);
    m_FileProgressLabel.setVisible(true);
    verwerkProgressFiles();

    // Process GPX files one by one
    ArrayList<String> v_Regels = new ArrayList<String>();
    synchronized (GUILayout.lock) {
      for (int i = 0; i < m_GpxFiles.length; i++) {
        v_Regels.addAll(v_sum.TripsSummary(m_GpxFiles[i]));
        verwerkProgressFiles();
      }
      String l_file = m_OutputFolder.getAbsolutePath() + "/" + m_OutFileName;
      TxtBestand tbst = new TxtBestand(l_file, v_sum.Header());
      tbst.DumpBestand(v_Regels, m_Append);

      SortTrips.sortTrips(l_file);
      LOGGER.log(Level.INFO, bundle.getMessage("FileCreated", m_OutputFolder.getAbsolutePath(), m_OutFileName));
    }
    m_pbarFiles.setValue(0);
    m_pbarFiles.setVisible(false);
    m_FileProgressLabel.setVisible(false);

    return null;
  }

  /**
   * Display ready message.
   */
  @Override
  protected void done() {
    LOGGER.log(Level.INFO, "");
    LOGGER.log(Level.INFO, "Processing is done.");
    cancel(true);
  }

  /**
   * Display progress processed files.
   */
  private void verwerkProgressFiles() {
    m_ProcessedFiles++;
    try {
      m_pbarFiles.setValue(m_ProcessedFiles);
      Double v_prog = ((double) m_ProcessedFiles / (double) m_NumberFiles) * 100;
      Integer v_iprog = v_prog.intValue();
      m_FileProgressLabel.setText(bundle.getMessage("Progress", v_iprog, m_ProcessedFiles, m_NumberFiles));
    } catch (Exception e) {
      // Do nothing
    }
  }
}
