package intellistart.interviewplanning.model.period.services.validation.chain;

import java.time.LocalTime;
import lombok.NoArgsConstructor;

/**
 * Validator of lower and upper time boundaries' minutes are only 30 or 0.
 */
@NoArgsConstructor
public class RoundingMinutesValidator implements PeriodChainValidator {

  /**
   * Validate lower and upper time boundaries' minutes are only 30 or 0.
   *
   * @param lowerBoundary LocalTime
   * @param  upperBoundary LocalTime
   *
   * @return true if boundaries correct.
   */
  @Override
  public boolean isCorrect(LocalTime lowerBoundary, LocalTime upperBoundary) {
    return isCorrectSingle(lowerBoundary) && isCorrectSingle(upperBoundary);
  }

  private boolean isCorrectSingle(LocalTime boundary) {
    int minutes = boundary.getMinute();
    return minutes == 30 || minutes == 0;
  }
}
