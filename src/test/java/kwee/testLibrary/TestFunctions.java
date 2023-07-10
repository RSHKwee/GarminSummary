package kwee.testLibrary;

import java.io.File;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestFunctions {
  private static final Logger LOGGER = Logger.getLogger(Class.class.getName());

  public File GetResourceFile(String a_file) {
    File l_File = null;
    URL resourceUrl;
    try {
      resourceUrl = getClass().getClassLoader().getResource(a_file);
      if (resourceUrl != null) {
        // Get the resource directory path
        String resourceDirectory = resourceUrl.getPath();
        l_File = new File(resourceDirectory);
      } else {
        LOGGER.log(Level.INFO, "File not found: " + a_file);
      }
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, e.getMessage());
    }
    return l_File;
  }
}
