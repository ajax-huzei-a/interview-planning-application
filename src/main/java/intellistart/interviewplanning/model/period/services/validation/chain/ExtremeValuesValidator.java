package intellistart.interviewplanning.model.period.services.validation.chain;

import java.time.LocalTime;

/**
 * Validator of lower time boundary is 8:00 or more
 * and upper time boundary is 22:00 or less.
 */
public class ExtremeValuesValidator implements PeriodChainValidator {

  private static final short LOWER_EXTREME = 8;
  private static final short HIGHER_EXTREME = 22;

  /**
   * Validate lower time boundary is 8:00 or more
   * and upper time boundary is 22:00 or less.
   *
   * @param lowerBoundary LocalTime
   * @param  upperBoundary LocalTime
   *
   * @return true if boundaries correct.
   */
  @Override
  public boolean isCorrect(LocalTime lowerBoundary, LocalTime upperBoundary) {
    return isLowerCorrect(lowerBoundary) && isUpperCorrect(upperBoundary);
  }

  /**
   * Check if LocalTime lower boundary of period is correct.
   */
  private boolean isLowerCorrect(LocalTime lowerBoundary) {
    return lowerBoundary.getHour() >= LOWER_EXTREME;
  }

  /**
   * Check if LocalTime upper boundary of period is correct.
   */
  private boolean isUpperCorrect(LocalTime upperBoundary) {
    int hour = upperBoundary.getHour();

    if (hour > HIGHER_EXTREME) {
      return false;
    }
    if (hour < HIGHER_EXTREME) {
      return true;
    }
    //if hour == 22, minutes should be only 0
    return upperBoundary.getMinute() == 0;
  }
}
