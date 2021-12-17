package library;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.JarURLConnection;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

/**
 * Ophalen wijzigings tijdstip van opgegeven CLASS file in jar file.
 *
 * @author Rene
 *
 */
public class JarInfo {
  /**
   * Converteer time naar een leesbare tekst.
   *
   * @param time Long
   * @return Tekst
   */
  static String convertTime(long time) {
    Date date = new Date(time);
    Format format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    return format.format(date);
  }

  /**
   * Class naam waar info voor wordt opgehaald.
   *
   * @param cl Class naam
   * @return Long
   */
  public static Long getTime(Class<?> cl) {
    try {
      String rn = cl.getName().replace('.', '/') + ".class";
      JarURLConnection j = (JarURLConnection) ClassLoader.getSystemResource(rn).openConnection();
      return j.getJarFile().getEntry("META-INF/MANIFEST.MF").getTime();
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Class naam waar info voor wordt opgehaald.
   * 
   * @param cl Class naam
   * @return Tekst met tijd informatie, retouneert "-" als geen info bekend is.
   */
  public static String getTimeStr(Class<?> cl) {
    String v_time = "-";
    if (getTime(cl) != null) {
      v_time = convertTime(getTime(cl));
    } else {
      LocalDate today = LocalDate.now();
      LocalTime time = LocalTime.now();
      v_time = "IDE " + today.toString() + " " + time.toString();
    }
    return v_time;
  }

  public static String getJarFilenaam(Class<?> cl) throws FileNotFoundException {
    // File jarDir = new
    // File(ClassLoader.getSystemClassLoader().getResource(".").getPath());
    // return jarDir.getAbsolutePath();

    // File(cl.class.getProtectionDomain().getCodeSource().getLocation().toURI());‌

    String path = cl.getResource(cl.getSimpleName() + ".class").getFile();
    if (path.startsWith("/")) {
      throw new FileNotFoundException("This is not a jar file: \n" + path);
    }
    path = ClassLoader.getSystemClassLoader().getResource(path).getFile();

    return new File(path.substring(0, path.lastIndexOf('!'))).toString();
  }

  public static String getProjectVersion(Class<?> cl) {
    String l_version = "";
    try {
      ApplicationProperties l_properties = new ApplicationProperties();
      l_version = l_properties.readProperty("version") + " (" + getTimeStr(cl) + ")";
    } catch (Exception e) {
      l_version = getTimeStr(cl);
    }
    return l_version;
  }
}
