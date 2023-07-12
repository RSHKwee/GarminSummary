package kwee.garminSummary.main;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserSettingTest {
  private static final Logger LOGGER = Logger.getLogger(Class.class.getName());
  private UserSetting m_param = new UserSetting();
  private UserSetting m_orgparam = new UserSetting();

  @Before
  public void setUp() throws Exception {
    // LOGGER.log(Level.INFO, "m_Param: " + m_param.print());
  }

  @After
  public void tearDown() throws Exception {
    // LOGGER.log(Level.INFO, m_param.print());
  }

  @Test
  public void testCopy() {
    // fail("Not yet implemented");
    // m_orgparam = m_param.copy();
    // LOGGER.log(Level.INFO, "m_orgParam: " + m_orgparam.print());

  }

}
