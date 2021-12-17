package library;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApplicationProperties {
  private static final Logger LOGGER = Logger.getLogger(Class.class.getName());

  private final Properties properties;

  ApplicationProperties() {
    properties = new Properties();
    try {
      properties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));

    } catch (IOException ioex) {
      LOGGER.log(Level.ALL, "IOException Occured while loading properties file::::" + ioex.getMessage());
    }
  }

  public String readProperty(String keyName) {
    LOGGER.log(Level.INFO, "Reading Property " + keyName);
    return properties.getProperty(keyName, "There is no key in the properties file");
  }
}
