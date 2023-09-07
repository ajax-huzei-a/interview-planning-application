package intellistart.interviewplanning.model.week;

import intellistart.interviewplanning.model.dayofweek.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.HashSet;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for Week entity.
 */
@Service
public class WeekService {

  private final WeekRepository weekRepository;


  @Autowired
  public WeekService(WeekRepository weekRepository) {
    this.weekRepository = weekRepository;
  }

  /**
   * Get date and convert it to number of week.
   *
   * @param date any date
   * @return number of week
   */
  public long getNumberOfWeek(LocalDate date) {
    long sumOfWeeks = 0;
    if (date.getYear() != 2022) {
      for (int i = 2022; i < date.getYear(); i++) {
        sumOfWeeks += date.withYear(i).range(WeekFields.ISO.weekOfYear()).getMaximum();
      }
    }
    long weeksOfCurrentYear = date.get(WeekFields.ISO.weekOfYear());
    if (checkBeginOfYear(date.getYear())) {
      weeksOfCurrentYear = weeksOfCurrentYear - 1;
    }
    return sumOfWeeks + weeksOfCurrentYear;
  }

  /**
   * Method checks if the first day of year is tuesday,wednesday or thursday
   * for right calculating of number of week.
   *
   * @param year current year
   * @return true if year begins from tuesday,wednesday or thursday
   */
  private boolean checkBeginOfYear(int year) {
    LocalDate date = LocalDate.of(year, 1, 1);
    return date.getDayOfWeek().equals(java.time.DayOfWeek.TUESDAY)
        || date.getDayOfWeek().equals(java.time.DayOfWeek.WEDNESDAY)
        || date.getDayOfWeek().equals(java.time.DayOfWeek.THURSDAY);
  }

  /**
   * Get date and convert it to day of week.
   *
   * @param date any date
   * @return day of week
   */
  public DayOfWeek getDayOfWeek(LocalDate date) {
    String dayOfWeek = date.getDayOfWeek().toString().substring(0, 3);
    return DayOfWeek.valueOf(dayOfWeek);
  }

  /**
   * Get number of week and day of week
   * and convert them to date (LocalDate).
   *
   * @param weekNum number of week
   *
   * @param dayOfWeek day of week
   * @return date
   */
  public LocalDate convertToLocalDate(long weekNum, DayOfWeek dayOfWeek) {
    return LocalDate.now()
        .with(WeekFields.ISO.weekBasedYear(), getYear(weekNum))
        .with(WeekFields.ISO.weekOfYear(), getWeek(weekNum))
        .with(WeekFields.ISO.dayOfWeek(), dayOfWeek.ordinal() + 1L);
  }

  /**
   * Get number of week and return current year.
   *
   * @param weekNum number of week from 2022
   * @return current year
   */
  private long getYear(long weekNum) {
    LocalDate date = LocalDate.parse("2022-01-01");
    LocalDate currentDate = date.plusDays(weekNum * 7);
    return currentDate.getYear();
  }

  /**
   * Get number of week and calculate number of week from current year.
   *
   * @param weekNum number of week from 2022
   * @return number of week from current year
   */
  private long getWeek(long weekNum) {
    LocalDate date = LocalDate.parse("2022-01-01");
    long year = getYear(weekNum);
    for (int i = 2022; i < year; i++) {
      weekNum -=  date.withYear(i).range(WeekFields.ISO.weekOfYear()).getMaximum();
    }
    return weekNum;
  }

  /**
   * Return object Week for request for getting number of current week.
   *
   * @return Week object
   */
  public Week getCurrentWeek() {
    LocalDate date = LocalDate.now();
    return getWeekByWeekNum(getNumberOfWeek(date));
  }

  /**
   * Return object Week for request for getting number of next week.
   *
   * @return object Week
   */
  public Week getNextWeek() {
    LocalDate date = LocalDate.now();
    return getWeekByWeekNum(getNumberOfWeek(date) + 1L);
  }

  /**
   * Get number of week and check if object Week with id weekNum exists.
   * If it exists return this object if not object with such id is created and
   * also returned.
   *
   * @param weekNum number of week
   * @return object Week
   */
  public Week getWeekByWeekNum(Long weekNum) {
    Optional<Week> week = weekRepository.findById(weekNum);
    return week.orElseGet(() -> createWeek(weekNum));
  }

  /**
   * Create Week with id weekNum and return it.
   *
   * @param weekNum number of week
   * @return object Week
   */
  public Week createWeek(Long weekNum) {
    Week newWeek = new Week(weekNum, new HashSet<>());
    return weekRepository.save(newWeek);
  }
}
