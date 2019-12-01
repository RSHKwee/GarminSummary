package library;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

public class TimeConversion {
  public static LocalDateTime timeZoned2Local(Optional<ZonedDateTime> a_time) {
    Optional<ZonedDateTime> v_starttime = a_time;

    ZonedDateTime utcZoned = ZonedDateTime.parse(v_starttime.get().toString());
    ZoneId timeZone = ZoneId.of("Europe/Amsterdam");
    ZonedDateTime timeZoned = utcZoned.withZoneSameInstant(timeZone);
    LocalDateTime timeLocal = timeZoned.toLocalDateTime();
    return timeLocal;
  }

  public static String formatDuration(Duration duration) {
    long seconds = duration.getSeconds();
    long absSeconds = Math.abs(seconds);
    String positive = String.format("%d:%02d:%02d", absSeconds / 3600, (absSeconds % 3600) / 60, absSeconds % 60);
    return seconds < 0 ? "-" + positive : positive;
  }
}
