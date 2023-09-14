package intellistart.interviewplanning.controllers;

import static org.junit.jupiter.api.Assertions.*;

import intellistart.interviewplanning.controllers.dto.BookingDto;
import intellistart.interviewplanning.exceptions.SlotException;
import intellistart.interviewplanning.model.booking.Booking;
import intellistart.interviewplanning.model.booking.BookingService;
import intellistart.interviewplanning.model.booking.validation.BookingValidator;
import intellistart.interviewplanning.model.candidateslot.CandidateSlot;
import intellistart.interviewplanning.model.candidateslot.CandidateSlotService;
import intellistart.interviewplanning.model.dayofweek.DayOfWeek;
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlot;
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlotService;
import intellistart.interviewplanning.model.period.Period;
import intellistart.interviewplanning.model.period.PeriodService;
import intellistart.interviewplanning.model.user.UserService;
import intellistart.interviewplanning.model.week.Week;
import intellistart.interviewplanning.model.week.WeekService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

class CoordinatorControllerTest {
  private static BookingService bookingService;
  private static BookingValidator bookingValidator;
  private static InterviewerSlotService interviewerSlotService;
  private static CandidateSlotService candidateSlotService;
  private static PeriodService periodService;
  private static UserService userService;
  private static CoordinatorController cut;
  private static Period slotPeriod;
  private static CandidateSlot candidateSlot;
  private static InterviewerSlot interviewerSlot;
  private static Period bookingPeriod;
  private static WeekService weekService;

  @BeforeAll
  static void initialize(){
    bookingService = Mockito.mock(BookingService.class);
    bookingValidator = Mockito.mock(BookingValidator.class);
    interviewerSlotService = Mockito.mock(InterviewerSlotService.class);
    candidateSlotService = Mockito.mock(CandidateSlotService.class);
    periodService = Mockito.mock(PeriodService.class);
    userService = Mockito.mock(UserService.class);
    weekService = Mockito.mock(WeekService.class);


    cut = new CoordinatorController(
        bookingService,
        bookingValidator,
        interviewerSlotService,
        candidateSlotService,
        periodService,
        userService,
        weekService);

    slotPeriod = new Period();
    slotPeriod.setFrom(LocalTime.of(18, 0));
    slotPeriod.setTo(LocalTime.of(21, 30));

    bookingPeriod = new Period();
    bookingPeriod.setFrom(LocalTime.of(19, 30));
    bookingPeriod.setTo(LocalTime.of(21, 0));

    interviewerSlot = new InterviewerSlot(
        1L, new Week(1L, null), DayOfWeek.TUE, slotPeriod,
        new LinkedHashSet<>(), null);

    candidateSlot = new CandidateSlot(
        1L, LocalDate.of(2022, 1, 1), slotPeriod,
        new LinkedHashSet<>(), null, null);
  }

  static Stream<Arguments> provideCorrectParameters(){
    return Stream.of(
        Arguments.arguments(
            new BookingDto( 1L, 1L, "19:30",
                "21:00", "subj", "desc"),
            new Booking(null, "subj", "desc",
                interviewerSlot, candidateSlot, bookingPeriod)));
  }
  @ParameterizedTest
  @MethodSource("provideCorrectParameters")
  void okayWhenOkay(BookingDto bookingDto, Booking expected)
          throws SlotException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Mockito
        .when(candidateSlotService.getById(bookingDto.getCandidateSlotId()))
        .thenReturn(candidateSlot);
    Mockito
        .when(interviewerSlotService.getById(bookingDto.getInterviewerSlotId()))
        .thenReturn(interviewerSlot);

    Mockito
        .when(periodService.obtainPeriod(bookingDto.getFrom(), bookingDto.getTo()))
        .thenReturn(expected.getPeriod());

    Method privateMethod = CoordinatorController.class.getDeclaredMethod("getFromDto", BookingDto.class);
    privateMethod.setAccessible(true);
    Booking actual = (Booking) privateMethod.invoke(cut, bookingDto);

    assertEquals(expected, actual);
  }

}