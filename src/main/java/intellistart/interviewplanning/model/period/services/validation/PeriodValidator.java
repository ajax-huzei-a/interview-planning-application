package intellistart.interviewplanning.model.period.services.validation;

import intellistart.interviewplanning.exceptions.SlotException;
import intellistart.interviewplanning.exceptions.SlotException.SlotExceptionProfile;
import intellistart.interviewplanning.model.period.services.validation.chain.DurationValidator;
import intellistart.interviewplanning.model.period.services.validation.chain.ExtremeValuesValidator;
import intellistart.interviewplanning.model.period.services.validation.chain.PeriodChainValidator;
import intellistart.interviewplanning.model.period.services.validation.chain.RoundingMinutesValidator;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Business logic validator class.
 */
@Service
public class PeriodValidator {

  private final List<PeriodChainValidator> validators = new ArrayList<>(Arrays.asList(
      new ExtremeValuesValidator(),
      new RoundingMinutesValidator(),
      new DurationValidator()
  ));

  /**
   * Validate lower and upper boundaries of future period.
   *
   * <ul>
   * <li>minimal duration validation
   * <li>extreme values validation
   * <li>rounding of minutes validation.
   * </ul>
   *
   * @param from LocalTime, lower time boundary
   * @param to LocalTime, upper time boundary
   *
   * @throws SlotException  when validation is incorrect
   */
  public void validate(LocalTime from, LocalTime to) throws SlotException {
    for (PeriodChainValidator validator : validators) {
      if (!validator.isCorrect(from, to)) {
        throw new SlotException(SlotExceptionProfile.INVALID_BOUNDARIES);
      }
    }
  }

}
