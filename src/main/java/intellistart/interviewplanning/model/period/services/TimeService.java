package intellistart.interviewplanning.model.period.services;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.springframework.stereotype.Component;

/**
 * Utils class to perform time operations.
 */

@Component
public class TimeService {

  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

  /**
   * Convert String to LocalTime by pattern HH:mm.
   *
   * @throws IllegalArgumentException if String doesn't satisfy the pattern
   */
  public LocalTime convert(String source) {
    try {
      return LocalTime.parse(source, formatter);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException();
    }
  }

  /**
   * Calculate duration from "from" to "to" in minutes.
   *
   * @param from - LocalTime
   * @param to - LocalTime
   */
  public int calculateDurationMinutes(LocalTime from, LocalTime to) {
    Duration duration = Duration.between(from, to);

    int minutes = duration.toMinutesPart();
    int hours = duration.toHoursPart();

    return hours * 60 + minutes;
  }
}
