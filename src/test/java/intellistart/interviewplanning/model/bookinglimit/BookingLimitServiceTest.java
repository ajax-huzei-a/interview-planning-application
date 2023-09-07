package intellistart.interviewplanning.model.bookinglimit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import intellistart.interviewplanning.exceptions.BookingLimitException;
import intellistart.interviewplanning.exceptions.UserException;
import intellistart.interviewplanning.model.user.Role;
import intellistart.interviewplanning.model.user.User;
import intellistart.interviewplanning.model.week.Week;
import intellistart.interviewplanning.model.week.WeekService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

class BookingLimitServiceTest {

  private static BookingLimitRepository bookingLimitRepository;
  private static WeekService weekService;
  private static BookingLimitService cut;
  private static Week week1;
  private static Week week2;
  private static Week week3;
  private static User user1;
  private static User user2;
  private static User user3;
  private static User user4;
  private static BookingLimit bookingLimit1;
  private static BookingLimit bookingLimit2;
  private static BookingLimit bookingLimit3;
  private static BookingLimit bookingLimit4;

  @BeforeEach
  void initialize(){
    bookingLimitRepository = Mockito.mock(BookingLimitRepository.class);
    weekService = Mockito.mock(WeekService.class);
    cut = new BookingLimitService(bookingLimitRepository, weekService);
  }

  @BeforeAll
  static void initialize2() {

    user1 = new User();
    user1.setId(1L);
    user1.setRole(Role.INTERVIEWER);

    user2 = new User();
    user2.setId(2L);
    user2.setRole(Role.INTERVIEWER);

    user3 = new User();
    user3.setId(3L);
    user3.setRole(Role.COORDINATOR);

    user4 = new User();
    user4.setId(4L);
    user4.setRole(Role.COORDINATOR);

    week1 = new Week(40L,null);
    week2 = new Week(20L,null);
    week3 = new Week(13L,null);

    bookingLimit1 = new BookingLimit(new BookingLimitKey(
        user1.getId(),week1.getId()), 120, user1, week1);

    bookingLimit2 = new BookingLimit(new BookingLimitKey(
        user2.getId(),week2.getId()), 35, user2, week2);

    bookingLimit3 = new BookingLimit(new BookingLimitKey(
        user1.getId(),week3.getId()), 0, user1, week3);

    bookingLimit4 = new BookingLimit(new BookingLimitKey(
        user2.getId(),week3.getId()), 0, user2, week3);
  }

  static Arguments[] getBookingLimitTestArgs(){
    return new Arguments[]{
        Arguments.arguments(user1,week1,bookingLimit1),
        Arguments.arguments(user2,week2,bookingLimit2)
    };
  }

  @ParameterizedTest
  @MethodSource("getBookingLimitTestArgs")
  void getBookingLimitByInterviewerTest(User user, Week week, BookingLimit expected)
      throws UserException {
    given(bookingLimitRepository.findById(expected.getId())).willReturn(Optional.of(expected));

    BookingLimit actual = cut.getBookingLimitByInterviewer(user,week);

    verify(bookingLimitRepository,never()).save(any());
    assertEquals(expected,actual);
  }

  static Arguments[] getBookingLimitIfNotExistTestArgs(){
    return new Arguments[]{
        Arguments.arguments(user1,week3,bookingLimit3),
        Arguments.arguments(user2,week3,bookingLimit4)
    };
  }

  @ParameterizedTest
  @MethodSource("getBookingLimitIfNotExistTestArgs")
  void getBookingLimitByInterviewerIfNotExistTest(User user, Week week,
      BookingLimit expected) throws UserException {
    given(bookingLimitRepository.findById(expected.getId())).willReturn(Optional.empty());
    given(bookingLimitRepository.save(expected)).willReturn(expected);

    BookingLimit actual = cut.getBookingLimitByInterviewer(user,week);

    assertEquals(expected,actual);
  }


  static Arguments[] getBookingLimitNotInterviewerExceptionTestArgs(){
    return new Arguments[]{
        Arguments.arguments(user3,week3),
        Arguments.arguments(user4,week3)
    };
  }

  @ParameterizedTest
  @MethodSource("getBookingLimitNotInterviewerExceptionTestArgs")
  void getBookingLimitByInterviewerNotInterviewerExceptionTest(User user, Week week) {
    assertThrows(UserException.class,
        () -> cut.getBookingLimitByInterviewer(user,week));
  }

  static Arguments[] createBookingLimitNotInterviewerExceptionTestArgs(){
    return new Arguments[]{
        Arguments.arguments(user3,113),
        Arguments.arguments(user4,17)
    };
  }

  @ParameterizedTest
  @MethodSource("createBookingLimitNotInterviewerExceptionTestArgs")
  void createBookingLimitNotInterviewerExceptionTest(User user, Integer bookingLimit){
    assertThrows(UserException.class,
        () -> cut.createBookingLimit(user,bookingLimit));
  }

  static Arguments[] createBookingLimitExceptionTestArgs(){
    return new Arguments[]{
        Arguments.arguments(user1,0),
        Arguments.arguments(user2,-13)
    };
  }

  @ParameterizedTest
  @MethodSource("createBookingLimitExceptionTestArgs")
  void createBookingLimitInvalidBookingLimitExceptionTest(User user, Integer bookingLimit){
    assertThrows(BookingLimitException.class,
        () -> cut.createBookingLimit(user,bookingLimit));
  }

  static Arguments[] createBookingLimitTestArgs(){
    return new Arguments[]{
        Arguments.arguments(user1,120,bookingLimit1),
        Arguments.arguments(user2,35,bookingLimit2)
    };
  }

  @ParameterizedTest
  @MethodSource("createBookingLimitTestArgs")
  void createBookingLimitTest(User user, Integer bookingLimit, BookingLimit expected)
      throws BookingLimitException, UserException {
    given(weekService.getNextWeek()).willReturn(expected.getWeek());
    given(bookingLimitRepository.save(expected)).willReturn(expected);

    BookingLimit actual = cut.createBookingLimit(user,bookingLimit);

    verify(bookingLimitRepository).save(any());
    assertEquals(expected,actual);
  }

}
