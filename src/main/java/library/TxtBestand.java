package library;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class TxtBestand {
  private static final Logger LOGGER = Logger.getLogger(Class.class.getName());
  private String m_Filenaam;
  private ArrayList<String> m_Regels = new ArrayList<String>();

  public static void DumpBestand(String a_OutputFile, ArrayList<String> a_Regels) {
    try {
      OutputTxt logbestand = new OutputTxt(a_OutputFile);
      logbestand.SetFooter("# " + a_OutputFile);
      logbestand.Schrijf(a_Regels);
      logbestand.Close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void DumpBestand(String a_Comment) {
    try {
      OutputTxt logbestand = new OutputTxt(m_Filenaam);
      logbestand.SetFooter(a_Comment + " " + m_Filenaam);
      logbestand.SetComment(a_Comment);
      logbestand.Schrijf(m_Regels);
      logbestand.Close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void LeegBestand(String a_OutputFile) {
    ArrayList<String> v_Regels = new ArrayList<String>();
    v_Regels.add("# Leeg");
    DumpBestand(a_OutputFile, v_Regels);
  }

  public TxtBestand() {
  }

  public TxtBestand(String a_Filenaam) {
    m_Filenaam = a_Filenaam;
  }

  public ArrayList<String> getContentsTxtBestand() {
    return m_Regels;
  }

  public void writeLine(String a_line) {
    m_Regels.add(a_line);
    LOGGER.log(Level.FINE, Class.class.getName() + " Line : " + a_line);
  }

  public void readTxtBestand(String a_path) {
    // read file into stream, try-with-resources
    try (Stream<String> stream = Files.lines(Paths.get(a_path))) {
      stream.forEach(l_line -> {
        writeLine(l_line);
      });
    } catch (IOException e) {
      // LOGGER.log( Level.SEVERE, Class.class.getName() + ": " + e.getMessage());
    }
  }
}