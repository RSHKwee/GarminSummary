package logger;

import java.io.PrintWriter;
import java.io.StringWriter;
//import java.text.SimpleDateFormat;
//import java.util.Date;
import java.util.logging.LogRecord;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class TextAreaHandler extends java.util.logging.Handler {

	private JTextArea textArea = new JTextArea(50, 50);

	@Override
	public void close() throws SecurityException {
	}

	@Override
	public void flush() {
	}

	public JTextArea getTextArea() {
		return this.textArea;
	}

	@Override
	public void publish(final LogRecord record) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				StringWriter text = new StringWriter();
				PrintWriter out = new PrintWriter(text);
				if (!record.getMessage().contentEquals(" ") && !record.getMessage().isEmpty()) {
					out.println(textArea.getText());
					out.printf(" [%s] %s", record.getLevel(), record.getMessage());
				} else {
					out.println(textArea.getText());
					out.printf("  %s", record.getMessage());
				}
				textArea.setText(text.toString());
			}
		});
	}
}
