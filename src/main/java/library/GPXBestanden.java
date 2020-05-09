package library;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lijst van SB-beelden opstellen, in een opgeven SB-directory wordt gezocht
 * naar "*.DGN" bestanden. <br>
 * Deze bestanden, zonder extensie en directorypad, worden opgenomen in de
 * SB-lijst.
 *
 * @author kweers1
 *
 */
public class GPXBestanden {
  private static final Logger LOGGER = Logger.getLogger(Class.class.getName());
  Map<String, ArrayList<String>> m_GPXBesanden = new LinkedHashMap<String, ArrayList<String>>();
  String m_jaar = "";

  ArrayList<String> m_gpxbestand = new ArrayList<String>();

  /**
   * Bepaling lijst aanwezige projecten
   *
   * @param a_PostenDir Directory van de VPT-projecten.
   */
  public GPXBestanden(String a_PostenDir) {
    LOGGER.entering(Class.class.getName(), "GPXBestanden", a_PostenDir);
    try {
      Files.find(Paths.get(a_PostenDir), Integer.MAX_VALUE,
          (filePath, fileAttr) -> fileAttr.isRegularFile() | fileAttr.isOther()).forEach(path -> {
            leesBestand(path);
          });
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, Class.class.getName() + ": " + e.getMessage());
    }
    if (!m_gpxbestand.isEmpty()) {
      ArrayList<String> m_vptbestand1 = new ArrayList<String>(m_gpxbestand);
      m_GPXBesanden.put(m_jaar, m_vptbestand1);
    }
    LOGGER.exiting(Class.class.getName(), "GPXBestanden");
  }

  /**
   * Geef lijst VPT Projecten
   *
   * @return Lijst van VPT Projecten
   */
  public Map<String, ArrayList<String>> GetGPXBestanden() {
    return m_GPXBesanden;
  }

  /**
   * Hulp methode voor VPT bestanden zoeken.
   *
   * @param path Directory waar VPT bestanden staan.
   */
  void leesBestand(Path a_path) {
    LOGGER.entering(Class.class.getName(), "leesBestand", a_path.toString());
    // Filter op IA Header bestand
    String v_filenaam = a_path.toString();
    String v_jaar = "";
    if (v_filenaam.toUpperCase().contains("GPX")) {
      String[] dirparts = v_filenaam.replaceAll("\\\\", "/").split("/");
      for (int i = 0; i < dirparts.length; i++) {
        if (dirparts[i].toUpperCase().endsWith("GPX")) {
          String[] fileparts = dirparts[i].split("\\.");
          if (i > 3) {
            if (dirparts[i - 3].toUpperCase().equals("TRACKS")) {
              v_jaar = dirparts[i - 2];
              if (!v_jaar.equals(m_jaar)) {
                ArrayList<String> v_gpxbestand1 = new ArrayList<String>(m_gpxbestand);
                if (!v_gpxbestand1.isEmpty() && !m_jaar.equals("")) {
                  m_GPXBesanden.put(m_jaar, v_gpxbestand1);
                  m_jaar = v_jaar;
                  m_gpxbestand.clear();
                } else if (!v_gpxbestand1.isEmpty() && m_jaar.equals("")) {
                  m_GPXBesanden.put(v_jaar, v_gpxbestand1);
                  m_jaar = v_jaar;
                } else {
                  m_jaar = v_jaar;
                }
              }
              String vv_vptbestand = fileparts[0];
              for (int j = 0; j < fileparts.length; j++) {
                if (fileparts[j].toUpperCase().contains("GPX")) {
                  m_gpxbestand.add(vv_vptbestand);
                } else if (j > 0) {
                  vv_vptbestand = vv_vptbestand + "." + fileparts[j];
                }
              }
              System.out.println(vv_vptbestand);
            }
          }
        }
      }
      LOGGER.log(Level.FINE, "GPX Bestanden :" + m_GPXBesanden);
    }
    LOGGER.exiting(Class.class.getName(), "leesBestand");
  }

} // eof
