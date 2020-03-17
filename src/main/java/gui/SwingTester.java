package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import garmin.Summary;
import library.TxtBestand;

public class SwingTester {
  static Summary m_sum = new Summary();
  static ArrayList<String> m_Regels = new ArrayList<String>();

  public static void main(String[] args) {
    createWindow();
  }

  private static void createWindow() {
    JFrame frame = new JFrame("Garmin track summary");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    createUI(frame);
    frame.setSize(560, 200);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  private static void createUI(final JFrame frame) {
    JPanel panel = new JPanel();
    LayoutManager layout = new FlowLayout();
    panel.setLayout(layout);

    JButton button = new JButton("Kies bestanden");
    final JLabel label = new JLabel();

    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);

        int option = fileChooser.showOpenDialog(frame);
        if (option == JFileChooser.APPROVE_OPTION) {
          File[] files = fileChooser.getSelectedFiles();
          String fileNames = "";
          for (File file : files) {
            m_Regels.addAll(m_sum.TripsSummary(file.getPath()));

            fileNames += file.getPath() + " ";
          }
          label.setText("File(s) Selected: " + fileNames);
          TxtBestand.DumpBestand("D:\\" + "current_2019_3.csv", m_Regels);

        } else {
          label.setText("Open command canceled");
        }
      }
    });

    panel.add(button);
    panel.add(label);
    frame.getContentPane().add(panel, BorderLayout.CENTER);
  }
}
