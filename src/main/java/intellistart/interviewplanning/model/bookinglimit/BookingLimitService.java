package intellistart.interviewplanning.model.bookinglimit;

import intellistart.interviewplanning.exceptions.BookingLimitException;
import intellistart.interviewplanning.exceptions.BookingLimitException.BookingLimitExceptionProfile;
import intellistart.interviewplanning.exceptions.UserException;
import intellistart.interviewplanning.model.user.Role;
import intellistart.interviewplanning.model.user.User;
import intellistart.interviewplanning.model.week.Week;
import intellistart.interviewplanning.model.week.WeekService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for BookingLimit entity.
 */
@Service
public class BookingLimitService {

  private final BookingLimitRepository bookingLimitRepository;
  private final WeekService weekService;
  private static final Integer INFINITE_BOOKING_LIMITS_NUMBER = 1000;

  /**
   * Constructor for BookingLimitService.
   *
   * @param bookingLimitRepository - bookingLimitRepository
   * @param weekService - weekService
   */
  @Autowired
  public BookingLimitService(BookingLimitRepository bookingLimitRepository,
      WeekService weekService) {
    this.bookingLimitRepository = bookingLimitRepository;
    this.weekService = weekService;
  }

  public BookingLimit getBookingLimitForNextWeek(User user)
      throws UserException {
    return getBookingLimitByInterviewer(user, weekService.getNextWeek());
  }

  public BookingLimit getBookingLimitForCurrentWeek(User user)
      throws UserException {
    return getBookingLimitByInterviewer(user, weekService.getCurrentWeek());
  }

  /**
   * Return booking limit of certain interviewer for certain week,
   * or create infinite booking limit.
   *
   * @param user - interviewer
   * @param week - certain week
   * @return BookingLimit
   * @throws UserException - not interviewer id
   */
  public BookingLimit getBookingLimitByInterviewer(User user, Week week)
      throws UserException {
    if (user.getRole() != Role.INTERVIEWER) {
      throw new UserException(UserException.UserExceptionProfile.NOT_INTERVIEWER);
    }

    long weekNum = week.getId();

    while (weekNum > 0) {
      Optional<BookingLimit> bookingLimit = bookingLimitRepository
          .findById(new BookingLimitKey(user.getId(), weekNum));
      if (bookingLimit.isPresent()) {
        return bookingLimit.get();
      }
      weekNum--;
    }

    return createInfiniteBookingLimit(user, week);
  }

  private BookingLimit createInfiniteBookingLimit(User user, Week week) {
    return new BookingLimit(new BookingLimitKey(user.getId(),
        week.getId()), INFINITE_BOOKING_LIMITS_NUMBER, user, week);
  }

  /**
   * Create BookingLimit for next week.
   *
   * @param user - interviewer
   * @param bookingLimit - booking limit
   * @return BookingLimit
   * @throws BookingLimitException - invalid bookingLimit exception
   * @throws UserException - not interviewer id
   */
  public BookingLimit createBookingLimit(User user, Integer bookingLimit)
      throws BookingLimitException, UserException {

    if (user.getRole() != Role.INTERVIEWER) {
      throw new UserException(UserException.UserExceptionProfile.NOT_INTERVIEWER);
    }

    if (bookingLimit <= 0) {
      throw new BookingLimitException(BookingLimitExceptionProfile.INVALID_BOOKING_LIMIT);
    }

    Week nextWeek = weekService.getNextWeek();

    BookingLimit newBookingLimit = new BookingLimit(
        new BookingLimitKey(user.getId(), nextWeek.getId()),
        bookingLimit, user, nextWeek);

    return bookingLimitRepository.save(newBookingLimit);
  }
}