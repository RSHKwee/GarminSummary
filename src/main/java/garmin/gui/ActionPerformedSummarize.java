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

	// Variables
	private File m_OutputFolder;
	private File[] m_GpxFiles;
	private String m_OutFileNaam;

	private JTextArea area = new JTextArea(30, 50);
	private int m_AantalProjecten = 0;
	private int m_VerwerkteProjecten = 0;
	private JLabel m_ProgressLabel;

	public ActionPerformedSummarize(File[] a_GpXFiles, File a_OutputFolder, String a_OutFileNaam, JProgressBar a_pbar,
			JLabel a_Progresslabel) {
		LOGGER.log(Level.FINE, "Set aanroep ActionPerformedGenereerScenarios");

		m_GpxFiles = a_GpXFiles;
		m_OutputFolder = a_OutputFolder;
		m_OutFileNaam = a_OutFileNaam;
		m_pbar = a_pbar;
		m_ProgressLabel = a_Progresslabel;
	}

	@Override
	public void append(String text) {
		area.append(text);
	}

	/**
	 * Voer het genereren van de scenario's uit in de background.
	 */
	@Override
	protected Void doInBackground() throws Exception {
		Summary v_sum = new Summary();
		ArrayList<String> v_Regels = new ArrayList<String>();

		m_VerwerkteProjecten = 0;
		m_pbar.setMaximum(m_GpxFiles.length);

		for (int i = 0; i < m_GpxFiles.length; i++) {
			v_Regels.addAll(v_sum.TripsSummary(m_GpxFiles[i].getAbsolutePath()));
			verwerkProgress(m_GpxFiles[i].getName());
		}
		TxtBestand.DumpBestand(m_OutputFolder.getAbsolutePath() + "//" + m_OutFileNaam, v_Regels);

		m_ProgressLabel.setText(" ");
		m_pbar.setValue(0);
		return null;
	}

	@Override
	protected void done() {
		LOGGER.log(Level.INFO, "");
		LOGGER.log(Level.INFO, "Klaar.");
	}

	void verwerkProgress(String a_post) {
		m_VerwerkteProjecten++;
		m_pbar.setValue(m_VerwerkteProjecten);

		Double v_prog = ((double) m_VerwerkteProjecten / (double) m_AantalProjecten) * 100;
		Integer v_iprog = v_prog.intValue();
		m_ProgressLabel.setText(v_iprog.toString() + "% (Post :" + a_post + ")");
	}

}
