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
import javax.swing.filechooser.FileFilter;

import garmin.Summary;
import library.TxtBestand;

public class SwingTester {
  static Summary m_sum = new Summary();
  static ArrayList<String> m_Regels = new ArrayList<String>();
  static String m_csvdir = "";
  static String m_outpfile = "";
  static String m_OutpDir = "";
  static File[] m_files;

  public static void main(String[] args) {
    createWindow();
  }

  static private void createWindow() {
    JFrame frame = new JFrame("Garmin track summary");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    createUI(frame);
    frame.setSize(560, 200);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  static private void createUI(final JFrame frame) {
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
          m_files = fileChooser.getSelectedFiles();
        } else {
          label.setText("Open command canceled");
        }
      }
    });

    panel.add(button);
    panel.add(label);

    JButton btnNewButton = new JButton("Output file");
    final JLabel outlabel = new JLabel();

    btnNewButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser();
        fc.setToolTipText("Geef uitvoerbestandsnaam.");
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.setFileFilter(new FileFilter() {
          @Override
          public boolean accept(File f) {
            System.out.println(f.toString());
            return f.isFile() || f.isDirectory();
          }

          @Override
          public String getDescription() {
            System.out.println("Any file");
            return "Any file";
          }
        });

        fc.setDialogType(JFileChooser.SAVE_DIALOG);
        fc.setApproveButtonText("Select");
        fc.setCurrentDirectory(new File(m_csvdir));
        int option = fc.showOpenDialog(frame);
        System.out.println(" Option: " + option);
        // fc.showDialog(m_GUILayout, "Kies CSV output bestand");

        File dir = fc.getSelectedFile();
        String outp_file = fc.getSelectedFile().getName();
        if (!dir.exists()) {
          dir = dir.getParentFile();
          m_outpfile = outp_file;
        } else {
          m_outpfile = outp_file;
        }
        m_OutpDir = dir.toString();
        System.out.println("Gekozen dir: " + m_OutpDir + " file: " + m_outpfile);
        outlabel.setText("OutpFile: " + m_OutpDir + "\\" + m_outpfile);
      }
    });
    panel.add(btnNewButton);

    JButton btnNewButton_1 = new JButton("Uitvoer vullen");
    btnNewButton_1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String fileNames = "";
        // Date (DD/MM/YYYY) Start Time Origin Longitude Origin Latitude Destination
        // Longitude Destination Latitude Origin Destination Distance (km) Time (min)
        // Fuel Economy (l/100km) Fuel Cost (EUR) Carbon Footprint (kg) ecoChallenge
        // Overall ecoChallenge Speed ecoChallenge Acceleration ecoChallenge Braking
        // ecoChallenge Fuel Economy
        m_Regels.clear();
        m_Regels.add(
            "#End Time;Date;Start Time;Origin Longitude;Origin Latitude;Destination Longitude;Destination Latitude;Origin Destination;Distance (km);Time (min);");
        for (File file : m_files) {
          m_Regels.addAll(m_sum.TripsSummary(file.getPath()));

          fileNames += file.getPath() + " ";
          label.setText("File(s) Selected: " + fileNames);
        }
        m_Regels.add("# " + fileNames);
        TxtBestand.DumpBestand(m_OutpDir + "\\" + m_outpfile, m_Regels);
      }
    });
    panel.add(btnNewButton_1);
    panel.add(outlabel);
    frame.getContentPane().add(panel, BorderLayout.CENTER);
  }
}
