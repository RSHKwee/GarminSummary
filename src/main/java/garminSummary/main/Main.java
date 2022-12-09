package garminSummary.main;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import garminSummary.gui.GUILayout;
import library.JarInfo;
import library.UserSetting;

/**
 * Main program for Garmin summary
 * 
 * @author kweers1
 */

public class Main {
	private static final Logger LOGGER = Logger.getLogger(Class.class.getName());
	static String m_MenuTitel = "Garmin GPX Summary";
	static public String m_creationtime;
	static String m_LookAndFeel = "Nimbus";
	public static UserSetting m_param = new UserSetting();
	static boolean m_ConfirmOnExit = false;

	/**
	 * Create the GUI and show it. For thread safety, this method should be invoked
	 * from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		// Set the look and feel.
		initLookAndFeel();

		// Create and set up the window.
		JFrame frame = new JFrame(m_MenuTitel + " (" + m_creationtime + ")");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		frame.addWindowListener(new WindowListener() {
			@Override
			public void windowClosing(WindowEvent e) {
				JFrame frame = (JFrame) e.getSource();
				if (m_ConfirmOnExit) {
					int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit the application?",
					    "Exit Application", JOptionPane.YES_NO_OPTION);

					if (result == JOptionPane.YES_OPTION) {
						m_param.save();
						frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					}
				} else {
					m_param.save();
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}
			}

			/**
			 * Bunch of obliged overridden methods
			 */
			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}
		});
		frame.setVisible(true);

		// Image Garmin logo;
		try {
			URL iconURL = Main.class.getClassLoader().getResource("garminLogo.png");
			// iconURL is null when not found
			ImageIcon icon = new ImageIcon(iconURL);
			frame.setIconImage(icon.getImage());
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Garmin Logo not found.");
		}

		// Create and set up the content pane.
		GUILayout scenGUI = new GUILayout();
		scenGUI.setOpaque(true);
		frame.setContentPane(scenGUI);

		// Display the window.
		frame.validate();
		frame.pack();
		frame.setSize(900, 700);
		frame.setLocation(50, 50);
		frame.setVisible(true);

		LOGGER.log(Level.INFO, " Garmin track summary version : " + m_creationtime);
	}

	/**
	 * Initialize Look and Feel
	 */
	private static void initLookAndFeel() {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				LOGGER.fine(" Look And Feel :" + info.getName());
				if (m_LookAndFeel.equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// If Nimbus is not available, you can set the GUI to another look and feel.
		}
	}

	/**
   * @formatter:off
   * Main start de GUI
   * @param args 0 Confirm on exit "true" or "false" (d)
   *        args 1 Look and feel,  "Nimbus" (d) or "Metal", etc.
   * @formatter:on
   */
	public static void main(String[] argv) {
		m_LookAndFeel = m_param.get_LookAndFeel();
		m_creationtime = JarInfo.getProjectVersion(GUILayout.class);
		m_ConfirmOnExit = m_param.is_ConfirmOnExit();

		switch (argv.length) {
		case 1: {
			if (argv[0].toLowerCase().startsWith("t")) {
				m_ConfirmOnExit = true;
				m_param.set_ConfirmOnExit(m_ConfirmOnExit);
			} else {
				m_ConfirmOnExit = false;
				m_param.set_ConfirmOnExit(m_ConfirmOnExit);
			}
			break;
		}
		case 2: {
			if (argv[0].toLowerCase().startsWith("t")) {
				m_ConfirmOnExit = true;
				m_param.set_ConfirmOnExit(true);
			} else {
				m_ConfirmOnExit = false;
				m_param.set_ConfirmOnExit(false);
			}
			m_LookAndFeel = argv[1];
			m_param.set_LookAndFeel(m_LookAndFeel);
		}
		default: {
		}
		}

		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createAndShowGUI();
			}
		});
	}
}
