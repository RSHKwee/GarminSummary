package library;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateToNumeric {

  public static String dateToNumeric(Date a_date) {
    String ls_date = "";
    try {
      // Instantiating the SimpleDateFormat class
      SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
      ls_date = formatter.format(a_date);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ls_date;
  }
}
