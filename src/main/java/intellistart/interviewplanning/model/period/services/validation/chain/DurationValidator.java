package intellistart.interviewplanning.model.period.services.validation.chain;

import intellistart.interviewplanning.model.period.services.TimeService;
import java.time.LocalTime;
import lombok.NoArgsConstructor;

/**
 * Validator of difference before upper and lower time
 * boundaries is 90 minutes or more.
 */
@NoArgsConstructor
public class DurationValidator implements PeriodChainValidator {

  private static final int MIN_DURATION = 90;

  /**
   * Validate difference before upper and lower time
   * boundaries is 90 minutes or more.
   *
   * @param lowerBoundary LocalTime
   * @param  upperBoundary LocalTime
   *
   * @return true if boundaries correct.
   */
  @Override
  public boolean isCorrect(LocalTime lowerBoundary, LocalTime upperBoundary) {
    return new TimeService().calculateDurationMinutes(lowerBoundary, upperBoundary) >= MIN_DURATION;
  }
}
