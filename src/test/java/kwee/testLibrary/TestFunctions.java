package kwee.testLibrary;

import java.io.File;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import kwee.garminSummary.main.UserSetting;

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

  /**
   * Copy UserSetings
   * 
   * @param a_UserSetting User settings object.
   * @return
   */
  public UserSetting CopyUserSetting(UserSetting a_UserSetting) {
    UserSetting l_UserSetting = new UserSetting();
    l_UserSetting.set_ConfirmOnExit(a_UserSetting.is_ConfirmOnExit());
    l_UserSetting.set_Level(a_UserSetting.get_Level());
    l_UserSetting.set_LogDir(a_UserSetting.get_LogDir());
    l_UserSetting.set_LookAndFeel(a_UserSetting.get_LookAndFeel());
    l_UserSetting.set_OutputFolder(new File(a_UserSetting.get_OutputFolder()));

    l_UserSetting.set_toDisk(a_UserSetting.is_toDisk());
    return l_UserSetting;
  }

}
