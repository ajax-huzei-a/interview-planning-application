package intellistart.interviewplanning.model.booking.validation;

import static org.junit.jupiter.api.Assertions.*;

import intellistart.interviewplanning.exceptions.BookingException;
import intellistart.interviewplanning.exceptions.SlotException;
import intellistart.interviewplanning.model.booking.Booking;
import intellistart.interviewplanning.model.bookinglimit.BookingLimitService;
import intellistart.interviewplanning.model.candidateslot.CandidateSlot;
import intellistart.interviewplanning.model.dayofweek.DayOfWeek;
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlot;
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlotService;
import intellistart.interviewplanning.model.period.Period;
import intellistart.interviewplanning.model.period.PeriodService;
import intellistart.interviewplanning.model.period.services.TimeService;
import intellistart.interviewplanning.model.user.Role;
import intellistart.interviewplanning.model.user.User;
import intellistart.interviewplanning.model.week.Week;
import intellistart.interviewplanning.model.week.WeekService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

class BookingValidatorTest {
  private static PeriodService periodService;
  private static TimeService timeService;
  private static WeekService weekService;
  private static BookingLimitService bookingLimitService;
  private static InterviewerSlotService interviewerSlotService;
  private static BookingValidator cut;
  private static Period wrongPeriod;
  private static Week week1;
  private static LocalDate week1TuesdayDate;
  private static InterviewerSlot interviewerSlot1;
  private static CandidateSlot candidateSlot1;
  private static CandidateSlot candidateSlot2;
  private static CandidateSlot candidateSlot3;
  private static Period interviewerSlotPeriod1;
  private static Period candidateSlotPeriod1;
  private static Period slotPeriod3;
  private static Period bookingPeriod1;
  private static Period bookingPeriod2;
  private static Period bookingPeriod3;
  private static Booking booking1;
  private static Booking booking2;


  @BeforeAll
  static void initialize(){
    periodService = Mockito.mock(PeriodService.class);
    timeService = Mockito.mock(TimeService.class);
    weekService = Mockito.mock(WeekService.class);
    bookingLimitService = Mockito.mock(BookingLimitService.class);
    interviewerSlotService = Mockito.mock(InterviewerSlotService.class);

    cut = new BookingValidator(periodService, timeService, weekService, bookingLimitService,
        interviewerSlotService);

    wrongPeriod = new Period();
    wrongPeriod.setFrom(LocalTime.of(10, 0));
    wrongPeriod.setTo(LocalTime.of(12, 0));

    week1 = new Week(43L, null);
    week1TuesdayDate = LocalDate.of(2022, 1, 1);

    interviewerSlotPeriod1 = new Period();
    interviewerSlotPeriod1.setFrom(LocalTime.of(8, 0));
    interviewerSlotPeriod1.setTo(LocalTime.of(17, 30));

    candidateSlotPeriod1 = new Period();
    candidateSlotPeriod1.setFrom(LocalTime.of(8, 30));
    candidateSlotPeriod1.setTo(LocalTime.of(20, 0));

    slotPeriod3 = new Period();
    slotPeriod3.setFrom(LocalTime.of(18, 0));
    slotPeriod3.setTo(LocalTime.of(21, 30));

    bookingPeriod1 = new Period();
    bookingPeriod1.setFrom(LocalTime.of(10, 30));
    bookingPeriod1.setTo(LocalTime.of(12, 0));

    bookingPeriod2 = new Period();
    bookingPeriod2.setFrom(LocalTime.of(9, 0));
    bookingPeriod2.setTo(LocalTime.of(10, 30));

    bookingPeriod3 = new Period();
    bookingPeriod3.setFrom(LocalTime.of(13, 0));
    bookingPeriod3.setTo(LocalTime.of(14, 30));

    interviewerSlot1 = new InterviewerSlot(
        1L, week1, DayOfWeek.TUE, interviewerSlotPeriod1,
        new LinkedHashSet<>(), new User(null, "inierviewer@gmail.com", Role.INTERVIEWER));
    //Arrays.asList(booking1, booking2)

    candidateSlot1 = new CandidateSlot(
        1L, week1TuesdayDate, candidateSlotPeriod1,
        new LinkedHashSet<>(), null, null); //Arrays.asList(booking1)

    candidateSlot2 = new CandidateSlot(
        2L, week1TuesdayDate, candidateSlotPeriod1,
        new LinkedHashSet<>(), null, null); //Arrays.asList(booking2)

    candidateSlot3 = new CandidateSlot(
        3L, week1TuesdayDate, candidateSlotPeriod1,
        new LinkedHashSet<>(), null, null);

    booking1 = new Booking(
        1L, "interview", "Maks Kostyshen",
        interviewerSlot1, candidateSlot1, bookingPeriod1);

    interviewerSlot1.getBookings().add(booking1);
    candidateSlot1.getBookings().add(booking1);

    booking2 = new Booking(
        2L, "interview", "Daria Pavliuk",
        interviewerSlot1, candidateSlot2, bookingPeriod2);

    interviewerSlot1.getBookings().add(booking2);
    candidateSlot2.getBookings().add(booking2);
  }

  static Stream<Arguments> provideInvalidPeriodArguments(){
    return Stream.of(
        Arguments.arguments(
            booking1,
            new Booking(null, null, null,
                null,null, wrongPeriod)));
  }

  @ParameterizedTest
  @MethodSource("provideInvalidPeriodArguments")
  void failWhenInvalidPeriodDuration(Booking updatingBooking, Booking newDataBooking){
    Mockito
        .when(timeService.calculateDurationMinutes(
            newDataBooking.getPeriod().getFrom(), newDataBooking.getPeriod().getTo()))
        .thenReturn(120);

    assertThrows(SlotException.class,
        () -> cut.validateUpdating(updatingBooking, newDataBooking));
  }


  static Stream<Arguments> provideNotIntersectingSlotsArguments(){
    Period period1 = new Period();
    period1.setFrom(LocalTime.of(9, 30));
    period1.setTo(LocalTime.of(11, 0));

    return Stream.of(
        Arguments.arguments(
            booking1,
            new Booking(null, "s", "d", interviewerSlot1, candidateSlot1, period1)),
        Arguments.arguments(
            booking2,
            new Booking(null, "s", "d", interviewerSlot1, candidateSlot2, period1))
        );
  }

  @ParameterizedTest
  @MethodSource("provideNotIntersectingSlotsArguments")
  void failWhenSlotsDoNotIntersect(Booking updatingBooking, Booking newDataBooking){
    Mockito
        .when(timeService.calculateDurationMinutes(
            newDataBooking.getPeriod().getFrom(), newDataBooking.getPeriod().getTo()))
        .thenReturn(90);

    Mockito
        .when(weekService.convertToLocalDate(newDataBooking.getInterviewerSlot().getWeek().getId(),
            newDataBooking.getInterviewerSlot().getDayOfWeek()))
        .thenReturn(newDataBooking.getCandidateSlot().getDate());

    Mockito
        .when(periodService.isFirstInsideSecond(newDataBooking.getPeriod(),
            newDataBooking.getInterviewerSlot().getPeriod()))
        .thenReturn(true);

    Mockito
        .when(periodService.isFirstInsideSecond(newDataBooking.getPeriod(),
            newDataBooking.getCandidateSlot().getPeriod()))
        .thenReturn(true);

    for (Booking candidateBooking : newDataBooking.getCandidateSlot().getBookings()) {
      Mockito
          .when(periodService.areOverlapping(newDataBooking.getPeriod(), candidateBooking.getPeriod()))
          .thenReturn(true);
    }

    assertThrows(BookingException.class,
        () -> cut.validateUpdating(updatingBooking, newDataBooking));
  }

  static Stream<Arguments> provideInvalidTextArguments(){
    return Stream.of(
        Arguments.arguments(
            new Booking(null, "s", "desc",
                interviewerSlot1, candidateSlot2, bookingPeriod1),
            new Booking(null, "s".repeat(256), "desc",
                interviewerSlot1, candidateSlot2, bookingPeriod1)),
        Arguments.arguments(
            new Booking(null, "subject", "d",
                interviewerSlot1, candidateSlot2, bookingPeriod1),
            new Booking(null, "subject", "d".repeat(4001),
                interviewerSlot1, candidateSlot2, bookingPeriod1)));
  }

  @ParameterizedTest
  @MethodSource("provideInvalidTextArguments")
  void failWhenUpdateInvalidTestArguments(Booking updatingBooking, Booking newBookingData){
    assertThrows(SlotException.class,
        () -> cut.validateUpdating(updatingBooking, newBookingData));
  }

  static Stream<Arguments> provideCorrectArgumentsUpdating(){
    Period period1 = new Period();
    period1.setFrom(LocalTime.of(11, 30));
    period1.setTo(LocalTime.of(13, 0));

    Period period2 = new Period();
    period2.setFrom(LocalTime.of(8, 30));
    period2.setTo(LocalTime.of(10, 0));

    return Stream.of(
        Arguments.arguments(
            booking1,
            new Booking(null, "s", "d",
                interviewerSlot1, candidateSlot1, period1)),
        Arguments.arguments(
            booking2,
            new Booking(
                null, "s", "d",
                interviewerSlot1, candidateSlot2, period2)));
  }

  @ParameterizedTest
  @MethodSource("provideCorrectArgumentsUpdating")
  void notFailWhenCorrectUpdating(Booking updatingBooking, Booking newDataBooking) {
//    Mockito
//        .when(timeService.calculateDurationMinutes(
//            newDataBooking.getPeriod().getFrom(), newDataBooking.getPeriod().getTo()))
//        .thenReturn(90);
//
//    Week week = newDataBooking.getInterviewerSlot().getWeek();
//
//    Mockito
//        .when(weekService.convertToLocalDate(week.getId(),
//            newDataBooking.getInterviewerSlot().getDayOfWeek()))
//        .thenReturn(newDataBooking.getCandidateSlot().getDate());
//
//    Mockito
//        .when(periodService.isFirstInsideSecond(newDataBooking.getPeriod(),
//            newDataBooking.getInterviewerSlot().getPeriod()))
//        .thenReturn(true);
//
//    Mockito
//        .when(periodService.isFirstInsideSecond(newDataBooking.getPeriod(),
//            newDataBooking.getCandidateSlot().getPeriod()))
//        .thenReturn(true);
//
//    for (Booking interviewerBooking : newDataBooking.getInterviewerSlot().getBookings()) {
//      Mockito
//          .when(periodService.areOverlapping(newDataBooking.getPeriod(), interviewerBooking.getPeriod()))
//          .thenReturn(false);
//    }
//
//    for (Booking candidateBooking : newDataBooking.getCandidateSlot().getBookings()) {
//      Mockito
//          .when(periodService.areOverlapping(newDataBooking.getPeriod(), candidateBooking.getPeriod()))
//          .thenReturn(false);
//    }
//
//    User newInterviewer = newDataBooking.getInterviewerSlot().getUser();
////    List<InterviewerSlot> interviewerSlotsNewInterviewer = interviewerSlotService
////        .getInterviewerSlotsByUserAndWeekAndDayOfWeek(
////            newInterviewer, newDataBooking.getInterviewerSlot().getWeek(),
////            newDataBooking.getInterviewerSlot().getDayOfWeek());
//
//    List<InterviewerSlot> interviewerSlotsByUser =
//        new ArrayList<>(Arrays.asList(newDataBooking.getInterviewerSlot()));
//
//    DayOfWeek dayOfWeek = newDataBooking.getInterviewerSlot().getDayOfWeek();
//    Mockito.
//        when(
//            interviewerSlotService.getInterviewerSlotsByUserAndWeekAndDayOfWeek(
//            new User(), new Week(), DayOfWeek.TUE))
//        .thenReturn(null);
//
//    int bookingLimit = interviewerSlotsByUser.size();
//
//    Mockito.when(bookingLimitService.getBookingLimitByInterviewer(
//        newDataBooking.getInterviewerSlot().getUser(), week).getBookingLimit()).thenReturn(
//        (bookingLimit + 1));
//
//    assertDoesNotThrow(() -> cut.validateUpdating(updatingBooking, newDataBooking));
  }

  static Stream<Arguments> provideCorrectArgumentsCreating(){
    return Stream.of(Arguments.arguments(
        new Booking(
            4L, "interview", "Mali Sari",
            interviewerSlot1, candidateSlot2, bookingPeriod3),
        new Booking(
            3L, "interview", "Anisimov Serhiy",
            interviewerSlot1, candidateSlot3, bookingPeriod3)));
  }

  @ParameterizedTest
  @MethodSource("provideCorrectArgumentsCreating")
  void notFailWhenCorrectCreating(Booking booking){
//
//    Mockito
//        .when(timeService.calculateDurationMinutes(
//            booking.getPeriod().getFrom(), booking.getPeriod().getTo()))
//        .thenReturn(90);
//
//    Mockito
//        .when(weekService.convertToLocalDate(booking.getInterviewerSlot().getWeek().getId(),
//            booking.getInterviewerSlot().getDayOfWeek()))
//        .thenReturn(booking.getCandidateSlot().getDate());
//
//    Mockito
//        .when(periodService.isFirstInsideSecond(booking.getPeriod(),
//            booking.getInterviewerSlot().getPeriod()))
//        .thenReturn(true);
//
//    Mockito
//        .when(periodService.isFirstInsideSecond(booking.getPeriod(),
//            booking.getCandidateSlot().getPeriod()))
//        .thenReturn(true);
//
//    for (Booking interviewerBooking : booking.getInterviewerSlot().getBookings()) {
//      Mockito
//          .when(periodService.areOverlapping(booking.getPeriod(), interviewerBooking.getPeriod()))
//          .thenReturn(false);
//    }
//
//    for (Booking candidateBooking : booking.getCandidateSlot().getBookings()) {
//      Mockito
//          .when(periodService.areOverlapping(booking.getPeriod(), candidateBooking.getPeriod()))
//          .thenReturn(false);
//    }
//
//    assertDoesNotThrow(() -> cut.validateCreating(booking));
  }
}