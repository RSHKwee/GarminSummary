package logger;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import logger.MyHtmlFormatter;
import logger.TextAreaHandler;;

/**
 *
 * @author Rene
 *
 */
public class MyLogger {
	static private FileHandler fileTxt;
	static private SimpleFormatter formatterTxt;
	static private SimpleFormatter formatterConsTxt;

	static private FileHandler fileHTML;
	static private Formatter formatterHTML;

	static private TextAreaHandler textAreaHand;

	/**
	 * Setup logging.
	 *
	 * @param a_level
	 *            Log level
	 * @param a_logdir
	 *            Directory logfiles
	 * @throws IOException
	 *             Exception
	 */
	static public void setup(Level a_level, String a_logdir, Boolean a_toFile) throws IOException {

		// suppress the logging output to the console
		Logger rootLogger = Logger.getLogger("");
		Handler[] handlers = rootLogger.getHandlers();

		if (handlers[0] instanceof ConsoleHandler) {
			// rootLogger.removeHandler(handlers[0]);
		}
		rootLogger.setLevel(a_level);

		formatterConsTxt = new MyConsTxtFormatter();
		handlers[0].setFormatter(formatterConsTxt);

		if (a_toFile) {
			// create a TXT formatter
			formatterTxt = new MyTxtFormatter();

			// create an HTML formatter
			formatterHTML = new MyHtmlFormatter();

			try {
				fileTxt = new FileHandler(a_logdir + "Logging.txt");
				fileHTML = new FileHandler(a_logdir + "Logging.html");

				fileTxt.setFormatter(formatterTxt);
				rootLogger.addHandler(fileTxt);

				fileHTML.setFormatter(formatterHTML);
				rootLogger.addHandler(fileHTML);
			} catch (AccessDeniedException e) {
				// Niets
			}
		}
		textAreaHand = new TextAreaHandler();
		textAreaHand.setFormatter(formatterConsTxt);
		rootLogger.addHandler(textAreaHand);
	}

} // Eof