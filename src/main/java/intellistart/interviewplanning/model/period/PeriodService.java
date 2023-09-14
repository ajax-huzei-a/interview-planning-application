package intellistart.interviewplanning.model.period;

import intellistart.interviewplanning.exceptions.SlotException;
import intellistart.interviewplanning.exceptions.SlotException.SlotExceptionProfile;
import intellistart.interviewplanning.model.period.services.TimeService;
import intellistart.interviewplanning.model.period.services.validation.PeriodValidator;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for Period entity.
 */
@Service
public class PeriodService {

  private final PeriodRepository periodRepository;
  private final PeriodValidator periodValidator;
  private final TimeService timeService;

  /**
   * Constructor.
   */
  @Autowired
  public PeriodService(
      PeriodRepository periodRepository,
      PeriodValidator periodValidator,
      TimeService timeService) {

    this.periodRepository = periodRepository;
    this.periodValidator = periodValidator;
    this.timeService = timeService;
  }


  /**
  * Alias for {@link #obtainPeriod(LocalTime, LocalTime)} with time conversion.
  *
  * @throws SlotException  when parameters are invalid:
  *     can't be read as time
  *     wrong business logic
  */
  public Period obtainPeriod(String fromString, String toString) throws SlotException {
    LocalTime from;
    LocalTime to;
    try {
      from = timeService.convert(fromString);
      to = timeService.convert(toString);
    } catch (IllegalArgumentException iae) {
      throw new SlotException(SlotExceptionProfile.INVALID_BOUNDARIES);
    }
    return obtainPeriod(from, to);
  }

  /**
   * Obtain period by "from" and "to": find if exists, create if not.
   *
   * @param from - LocalTime lower time boundary
   * @param to - LocalTime upper time boundary
   *
   * @throws SlotException when wrong business logic.
   */
  private Period obtainPeriod(LocalTime from, LocalTime to) throws SlotException {
    periodValidator.validate(from, to);

    Optional<Period> periodOptional = periodRepository.findPeriodByFromAndTo(from, to);

    return periodOptional.orElseGet(() -> periodRepository.save(
        new Period(0L, from, to, new HashSet<>(), new HashSet<>(), new HashSet<>())));
  }

  /**
   * Tell if times of periods cross. Boundaries are inclusive.
   *
   * @return true if periods are overlapping.
   */
  public boolean areOverlapping(Period period1, Period period2) {
    LocalTime from1 = period1.getFrom();
    LocalTime from2 = period2.getFrom();

    return isTimeInPeriod(from1, period2)
        || isTimeInPeriod(from2, period1);
  }

  /**
   * Boundaries are inclusive.
   *
   * @return true if first period is inside the second period.
   */
  public boolean isFirstInsideSecond(Period first, Period second) {
    return !first.getFrom().isBefore(second.getFrom())
        && !first.getTo().isAfter(second.getTo());
  }

  /**
   * Tell if given time isn't smaller than "from" and smaller than "to".
   */
  private boolean isTimeInPeriod(LocalTime time, Period period) {
    return !time.isBefore(period.getFrom())
        && time.isBefore(period.getTo());
  }
}
