/**
 *
 */
package library;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ReneK
 *
 */
public class GenerateGpsConfig {
  private static final Logger LOGGER = Logger.getLogger(Class.class.getName());
  public static final String C_ROOTDIR = "ROOTDIR";

  Map<String, ArrayList<String>> m_GPXBestanden = new LinkedHashMap<String, ArrayList<String>>();
  Map<String, ArrayList<String>> m_PostProj = new LinkedHashMap<String, ArrayList<String>>();
  Map<Integer, String> vi_Projecten = new LinkedHashMap<Integer, String>();

  public GenerateGpsConfig(String a_PostenDir) {
    GPXBestanden v_GPXBestanden = new GPXBestanden(a_PostenDir);
    m_GPXBestanden = v_GPXBestanden.GetGPXBestanden();
    m_PostProj.clear();
    //
    ArrayList<String> v_Rootdir = new ArrayList<String>();
    v_Rootdir.add(a_PostenDir);
    m_PostProj.put(C_ROOTDIR, v_Rootdir);
  }

  /**
   * Vullen data structuur: <post> <project> <verschilproject>
   *
   * @param a_RootDir
   * @return
   */
  public Map<String, ArrayList<String>> gegenereerdSceConfig() {
    Set<String> v_Posten = m_GPXBestanden.keySet();
    LOGGER.log(Level.FINE, "m_VPTProjecten : " + m_GPXBestanden.toString());
    LOGGER.log(Level.FINE, "v_Posten       : " + v_Posten.toString());

    v_Posten.forEach(v_Post -> {
      vi_Projecten.clear();
      ArrayList<String> v_Projecten = new ArrayList<String>();
      v_Projecten = m_GPXBestanden.get(v_Post);
      v_Projecten.forEach(v_Project -> {
        Integer v_prjvers = GetProjectVersie(v_Project);
        if (v_prjvers.intValue() != -1) {
          vi_Projecten.put(v_prjvers, v_Project);
        }
      });
      SortedSet<Integer> keys = new TreeSet<Integer>(vi_Projecten.keySet());
      LOGGER.log(Level.FINE, "v_Projecten : " + v_Projecten.toString());
      if (!keys.isEmpty()) {
        String v_VerschilPrj = vi_Projecten.get(keys.first());
        try {
          keys.forEach(key -> {
            ArrayList<String> v_SceConfPrjs = new ArrayList<String>();
            v_SceConfPrjs.add(vi_Projecten.get(key));
            v_SceConfPrjs.add(v_VerschilPrj);

            if (m_PostProj.get(v_Post) == null) {
              m_PostProj.put(v_Post, v_SceConfPrjs);
              LOGGER.log(Level.FINE, "PostProj : " + m_PostProj.toString());
            } else {
              // Uniek maken key indien voor een post meerdere projecten
              // worden opgenomen
              // Gebeurt door toevoeging van ";" + volgnummer
              int i = 0;
              String v_key = "";
              ArrayList<String> v_val = new ArrayList<String>();
              do {
                i++;
                v_key = v_Post + ";" + Integer.toString(i);
                v_val = m_PostProj.get(v_key);
                LOGGER.log(Level.FINE, "Dubbele waarde voor Post " + v_key + " waarde: " + v_val);
              } while (v_val != null);

              m_PostProj.put(v_Post + ";" + Integer.toString(i), v_SceConfPrjs);
              LOGGER.log(Level.FINE, "PostProj : " + m_PostProj.toString());
            }
          });
        } catch (java.util.NoSuchElementException e) {
          LOGGER.log(Level.SEVERE, Class.class.getName() + ": " + e.toString());
          e.printStackTrace();
        }
      }
    });
    return m_PostProj;
  }

  /**
   * Bepaal projectversie
   *
   * @param a_Project Project naam: "V<xx>_<naam>*.vpt"
   * @return Projectversie of -1 voor geen projectversie gevonden
   */
  int GetProjectVersie(String a_Project) {
    String[] v_delen = a_Project.split("_");
    String[] vv_delen = v_delen[0].toUpperCase().split("V");
    int v_versie = -1;
    try {
      v_versie = Integer.parseInt(vv_delen[1]);
    } catch (NumberFormatException e) {
      // Geef illegaal projectnummer terug (-1)
    } catch (ArrayIndexOutOfBoundsException e) {
      // Probeer zonder "V"
      try {
        v_versie = Integer.parseInt(v_delen[0]);
      } catch (NumberFormatException es) {
        // Geef illegaal projectnummer terug (-1)
      }
    }
    return v_versie;
  }

}
