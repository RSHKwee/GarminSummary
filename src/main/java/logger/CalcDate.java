package logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CalcDate {
  static public String calcDate(long millisecs) {
    SimpleDateFormat date_format = new SimpleDateFormat("MMM dd,yyyy HH:mm");
    Date resultdate = new Date(millisecs);
    return date_format.format(resultdate);
  }
}
