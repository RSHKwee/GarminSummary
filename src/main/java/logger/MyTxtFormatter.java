package logger;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

// this custom formatter formats parts of a log record to a single line
class MyTxtFormatter extends SimpleFormatter {
	private String lineSeparator = "\r\n";

	// this method is called for every log records
	@Override
	public synchronized String format(LogRecord rec) {
		StringBuffer buf = new StringBuffer(1000);
		if (!rec.getMessage().contentEquals(" ") && !rec.getMessage().isEmpty()) {
			buf.append(CalcDate.calcDate(rec.getMillis()));
			buf.append(" ");
			buf.append(rec.getLevel());
			buf.append(" ");
			buf.append(formatMessage(rec));
			buf.append(" ");
			buf.append(rec.getSourceClassName() + " ; " + rec.getSourceMethodName());
			buf.append(lineSeparator);
		}
		return buf.toString();
	}

	// this method is called just after the handler using this
	// formatter is created
	@Override
	public String getHead(Handler h) {
		return "";
	}

	// this method is called just after the handler using this
	// formatter is closed
	@Override
	public String getTail(Handler h) {
		return "";
	}
}