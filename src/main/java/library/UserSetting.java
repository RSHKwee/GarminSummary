package library;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * User setting persistence.
 * 
 * @author rshkw
 *
 */
public class UserSetting {
	private static final Logger LOGGER = Logger.getLogger(Class.class.getName());

	private String c_Level = "Level";
	private String c_LevelValue = "INFO";

	private String c_ConfirmOnExit = "ConfirmOnExit";
	private String c_toDisk = "ToDisk";
	private String c_OutputFolder = "OutputFolder";
	private String c_LookAndFeel = "LookAndFeel";
	private String c_LookAndFeelVal = "Nimbus";
	private String c_LogDir = "LogDir";
	private String c_InputFolder = "InputFolder";

	private String m_Level = c_LevelValue;
	private String m_LookAndFeel;
	private String m_OutputFolder = "";
	private String m_LogDir = "";
	private String m_InputFolder = "";

	private boolean m_ConfirmOnExit = false;
	private boolean m_toDisk = false;

	private Preferences pref = Preferences.userRoot();

	/**
	 * Constructor Initialize settings
	 */
	public UserSetting() {
		m_toDisk = pref.getBoolean(c_toDisk, false);

		m_ConfirmOnExit = pref.getBoolean(c_ConfirmOnExit, false);

		m_LookAndFeel = pref.get(c_LookAndFeel, c_LookAndFeelVal);
		m_OutputFolder = pref.get(c_OutputFolder, "");
		m_InputFolder = pref.get(c_InputFolder, "");
		m_Level = pref.get(c_Level, c_LevelValue);
		m_LogDir = pref.get(c_LogDir, "");
	}

	public String get_LogDir() {
		return m_LogDir;
	}

	public void set_LogDir(String m_LogDir) {
		this.m_LogDir = m_LogDir;
	}

	public String get_OutputFolder() {
		return m_OutputFolder;
	}

	public String get_InputFolder() {
		return m_InputFolder;
	}

	public Level get_Level() {
		return Level.parse(m_Level);
	}

	public String get_LookAndFeel() {
		return m_LookAndFeel;
	}

	public boolean is_toDisk() {
		return m_toDisk;
	}

	public boolean is_ConfirmOnExit() {
		return m_ConfirmOnExit;
	}

	public void set_OutputFolder(File a_OutputFolder) {
		pref.put(c_OutputFolder, a_OutputFolder.getAbsolutePath());
		m_OutputFolder = a_OutputFolder.getAbsolutePath();
	}

	public void set_InputFolder(File a_InputFolder) {
		pref.put(c_InputFolder, a_InputFolder.getAbsolutePath());
		m_InputFolder = a_InputFolder.getAbsolutePath();
	}

	public void set_toDisk(boolean a_toDisk) {
		pref.putBoolean(c_toDisk, a_toDisk);
		this.m_toDisk = a_toDisk;
	}

	public void set_Level(Level a_Level) {
		pref.put(c_Level, a_Level.toString());
		this.m_Level = a_Level.toString();
	}

	public void set_LookAndFeel(String a_LookAndFeel) {
		pref.put(c_LookAndFeel, a_LookAndFeel);
		this.m_LookAndFeel = a_LookAndFeel;
	}

	public void set_ConfirmOnExit(boolean a_ConfirmOnExit) {
		pref.putBoolean(c_ConfirmOnExit, a_ConfirmOnExit);
		this.m_ConfirmOnExit = a_ConfirmOnExit;
	}

	/**
	 * Save all settings
	 */
	public void save() {
		try {
			pref.putBoolean(c_toDisk, m_toDisk);

			pref.putBoolean(c_ConfirmOnExit, m_ConfirmOnExit);

			pref.put(c_LookAndFeel, m_LookAndFeel);
			pref.put(c_OutputFolder, m_OutputFolder);
			pref.put(c_InputFolder, m_InputFolder);
			pref.put(c_Level, m_Level);
			pref.put(c_LogDir, m_LogDir);

			pref.flush();
		} catch (BackingStoreException e) {
			LOGGER.log(Level.INFO, e.getMessage());
		}
	}
}
