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

	private String c_GnuCashExe = "GnuCashExe";
	private String c_GnuCashExeValue = "C:\\Program Files (x86)\\gnucash\\bin\\gnucash.exe";

	private String c_Level = "Level";
	private String c_LevelValue = "INFO";

	private String c_ConfirmOnExit = "ConfirmOnExit";
	private String c_toDisk = "ToDisk";
	private String c_AccountSepOfx = "AccountSepOfx";
	private String c_ConvertDecimalSeparator = "ConvertDecimalSeperator";
	private String c_ConvertDateFormat = "ConvertDateFormat";
	private String c_SeparatorComma = "SeparatorComma";
	private String c_OutputFolder = "OutputFolder";
	private String c_CsvFile = "CsvFile";
	private String c_LookAndFeel = "LookAndFeel";
	private String c_LookAndFeelVal = "Nimbus";
	private String c_Interest = "Interest";
	private String c_Savings = "Savings";
	private String c_Java = "UseJava";
	private String c_LogDir = "LogDir";

	private String m_Level = c_LevelValue;
	private String m_LookAndFeel;
	private String m_GnuCashExecutable = c_LookAndFeelVal;
	private String m_OutputFolder = "";
	private String m_CsvFile = "";
	private String m_LogDir = "";

	private boolean m_ConfirmOnExit = false;
	private boolean m_toDisk = false;
	private boolean m_AcountSeparateOFX = true;
	private boolean m_ConvertDecimalSeparator = false;
	private boolean m_ConvertDateFormat = false;
	private boolean m_SeparatorComma = false;
	private boolean m_Interest = true;
	private boolean m_Savings = false;
	private boolean m_Java = true;

	private Preferences pref = Preferences.userRoot();

	/**
	 * Constructor Initialize settings
	 */
	public UserSetting() {
		m_toDisk = pref.getBoolean(c_toDisk, false);

		m_ConfirmOnExit = pref.getBoolean(c_ConfirmOnExit, false);
		m_AcountSeparateOFX = pref.getBoolean(c_AccountSepOfx, true);
		m_ConvertDecimalSeparator = pref.getBoolean(c_ConvertDecimalSeparator, false);
		m_ConvertDateFormat = pref.getBoolean(c_ConvertDateFormat, false);
		m_SeparatorComma = pref.getBoolean(c_SeparatorComma, false);
		m_Interest = pref.getBoolean(c_Interest, true);
		m_Savings = pref.getBoolean(c_Savings, false);
		m_Java = pref.getBoolean(c_Java, true);

		m_LookAndFeel = pref.get(c_LookAndFeel, c_LookAndFeelVal);
		m_GnuCashExecutable = pref.get(c_GnuCashExe, c_GnuCashExeValue);
		m_OutputFolder = pref.get(c_OutputFolder, "");
		m_CsvFile = pref.get(c_CsvFile, "");
		m_Level = pref.get(c_Level, c_LevelValue);
		m_LogDir = pref.get(c_LogDir, "");
	}

	public String get_LogDir() {
		return m_LogDir;
	}

	public void set_LogDir(String m_LogDir) {
		this.m_LogDir = m_LogDir;
	}

	/**
	 * 
	 * @return
	 */
	public String get_GnuCashExecutable() {
		return m_GnuCashExecutable;
	}

	public String get_OutputFolder() {
		return m_OutputFolder;
	}

	public String get_CsvFile() {
		return m_CsvFile;
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

	public boolean is_AcountSeparateOFX() {
		return m_AcountSeparateOFX;
	}

	public boolean is_ConfirmOnExit() {
		return m_ConfirmOnExit;
	}

	public boolean is_ConvertDateFormat() {
		return m_ConvertDateFormat;
	}

	public boolean is_ConvertDecimalSeparator() {
		return m_ConvertDecimalSeparator;
	}

	public boolean is_Interest() {
		return m_Interest;
	}

	public boolean is_Savings() {
		return m_Savings;
	}

	public boolean is_SeparatorComma() {
		return m_SeparatorComma;
	}

	public boolean is_Java() {
		return m_Java;
	}

	public void set_GnuCashExecutable(File a_GnuCashExecutable) {
		pref.put(c_GnuCashExe, a_GnuCashExecutable.getAbsolutePath());
		m_GnuCashExecutable = a_GnuCashExecutable.getAbsolutePath();
	}

	public void set_OutputFolder(File a_OutputFolder) {
		pref.put(c_OutputFolder, a_OutputFolder.getAbsolutePath());
		m_OutputFolder = a_OutputFolder.getAbsolutePath();
	}

	public void set_CsvFile(File a_CsvFile) {
		pref.put(c_CsvFile, a_CsvFile.getAbsolutePath());
		m_CsvFile = a_CsvFile.getAbsolutePath();
	}

	public void set_toDisk(boolean a_toDisk) {
		pref.putBoolean(c_toDisk, a_toDisk);
		this.m_toDisk = a_toDisk;
	}

	public void set_Level(Level a_Level) {
		pref.put(c_Level, a_Level.toString());
		this.m_Level = a_Level.toString();
	}

	public void set_AcountSeparateOFX(boolean a_AcountSeparateOFX) {
		pref.putBoolean(c_AccountSepOfx, a_AcountSeparateOFX);
		this.m_AcountSeparateOFX = a_AcountSeparateOFX;
	}

	public void set_ConvertDecimalSeparator(boolean a_ConvertDecimalSeparator) {
		pref.putBoolean(c_ConvertDecimalSeparator, a_ConvertDecimalSeparator);
		this.m_ConvertDecimalSeparator = a_ConvertDecimalSeparator;
	}

	public void set_ConvertDateFormat(boolean a_ConvertDateFormat) {
		pref.putBoolean(c_ConvertDateFormat, a_ConvertDateFormat);
		this.m_ConvertDateFormat = a_ConvertDateFormat;
	}

	public void set_SeparatorComma(boolean a_SeperatorComma) {
		pref.putBoolean(c_SeparatorComma, a_SeperatorComma);
		this.m_SeparatorComma = a_SeperatorComma;
	}

	public void set_LookAndFeel(String a_LookAndFeel) {
		pref.put(c_LookAndFeel, a_LookAndFeel);
		this.m_LookAndFeel = a_LookAndFeel;
	}

	public void set_Interest(boolean a_Interest) {
		pref.putBoolean(c_Interest, a_Interest);
		this.m_Interest = a_Interest;
	}

	public void set_Savings(boolean a_Savings) {
		pref.putBoolean(c_Savings, a_Savings);
		this.m_Savings = a_Savings;
	}

	public void set_Java(boolean a_Java) {
		pref.putBoolean(c_Java, a_Java);
		this.m_Java = a_Java;
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

			pref.putBoolean(c_AccountSepOfx, m_AcountSeparateOFX);
			pref.putBoolean(c_ConvertDecimalSeparator, m_ConvertDecimalSeparator);
			pref.putBoolean(c_ConvertDateFormat, m_ConvertDateFormat);
			pref.putBoolean(c_SeparatorComma, m_SeparatorComma);
			pref.putBoolean(c_Interest, m_Interest);
			pref.putBoolean(c_Savings, m_Savings);
			pref.putBoolean(c_ConfirmOnExit, m_ConfirmOnExit);
			pref.putBoolean(c_Java, m_Java);

			pref.put(c_LookAndFeel, m_LookAndFeel);
			pref.put(c_GnuCashExe, m_GnuCashExecutable);
			pref.put(c_OutputFolder, m_OutputFolder);
			pref.put(c_CsvFile, m_CsvFile);
			pref.put(c_Level, m_Level);
			pref.put(c_LogDir, m_LogDir);

			pref.flush();
		} catch (BackingStoreException e) {
			LOGGER.log(Level.INFO, e.getMessage());
		}
	}
}
