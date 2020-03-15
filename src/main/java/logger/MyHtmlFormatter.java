package logger;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

// this custom formatter formats parts of a log record to a single line
class MyHtmlFormatter extends Formatter {
	// this method is called for every log records
	@Override
	public String format(LogRecord rec) {
		StringBuffer buf = new StringBuffer(1000);
		buf.append("<tr>\n");

		if (!rec.getMessage().contentEquals(" ") && !rec.getMessage().isEmpty()) {
			// colorize any levels >= WARNING in red
			if (rec.getLevel().intValue() >= Level.WARNING.intValue()) {
				buf.append("\t<td style=\"color:red\">");
				buf.append("<b>");
				buf.append(rec.getLevel());
				buf.append("</b>");
			} else {
				buf.append("\t<td>");
				buf.append(rec.getLevel());
			}

			buf.append("</td>\n");
			buf.append("\t<td>");
			buf.append(CalcDate.calcDate(rec.getMillis()));
			buf.append("</td>\n");

			buf.append("\t<td>");
			buf.append(formatMessage(rec));
			buf.append("</td>\n");
			buf.append("\t<td>");
			buf.append(rec.getSourceMethodName());
			buf.append("</td>\n");
		} else {
			buf.append("\t<td>");
			buf.append(formatMessage(rec));
			buf.append("</td>\n");
		}
		buf.append("</tr>\n");

		return buf.toString();
	}

	// this method is called just after the handler using this
	// formatter is created
	@Override
	public String getHead(Handler h) {
		return "<!DOCTYPE html>\n<head>\n<style>\n" + "table { width: 100% }\n" + "th { font:bold 10pt Tahoma; }\n"
				+ "td { font:normal 10pt Tahoma; }\n" + "h1 {font:normal 11pt Tahoma;}\n" + "</style>\n" + "</head>\n"
				+ "<body>\n" + "<h1>" + (new Date()) + "</h1>\n"
				+ "<table border=\"0\" cellpadding=\"5\" cellspacing=\"3\">\n" + "<tr align=\"left\">\n"
				+ "\t<th style=\"width:10%\">Loglevel</th>\n" + "\t<th style=\"width:15%\">Time</th>\n"
				+ "\t<th style=\"width:75%\">Log Message</th>\n" + "\t<th style=\"width:75%\">Methode</th>\n"
				+ "</tr>\n";
	}

	// this method is called just after the handler using this
	// formatter is closed
	@Override
	public String getTail(Handler h) {
		return "</table>\n</body>\n</html>";
	}
}