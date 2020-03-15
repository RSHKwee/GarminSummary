package logger;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import logger.MyLogger;

public class LogOutputWindow {
  private static final Logger LOGGER    = Logger.getLogger(Class.class.getName());

  static public JFrame outputWindow(String a_Title, Level a_Level, String a_RootDir, Boolean a_toDisk) {
    JTextArea output = new JTextArea();
    // Build output area.
    JFrame theFrame = new JFrame();
    theFrame.setTitle(a_Title);
    theFrame.setSize(500, 500);
    theFrame.setLocation(550, 400);

    JPanel outputPanel = new JPanel();
    JScrollPane scrollPane = new JScrollPane(outputPanel);

    try {
      MyLogger.setup(a_Level, a_RootDir, a_toDisk);
    } catch (IOException es) {
      LOGGER.log(Level.SEVERE, Class.class.getName() + ": " + es.toString());
      es.printStackTrace();
    }
    Logger rootLogger = Logger.getLogger("");
    for (Handler handler : rootLogger.getHandlers()) {
      if (handler instanceof TextAreaHandler) {
        TextAreaHandler textAreaHandler = (TextAreaHandler) handler;
        output = textAreaHandler.getTextArea();
      }
    }
    outputPanel.add(output);

    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    int v_hoogte = 500;
    int v_breedte = 700;
    scrollPane.setBounds(10, 10, v_breedte - 20, v_hoogte - 20);
    JPanel contentPane = new JPanel(null);
    contentPane.setPreferredSize(new Dimension(v_breedte, v_hoogte));
    theFrame.setLayout(new BorderLayout()); // !! added
    contentPane.add(scrollPane, BorderLayout.CENTER);
    theFrame.setContentPane(contentPane);

    theFrame.pack();
    theFrame.setVisible(true);
    return theFrame;
  }
}
