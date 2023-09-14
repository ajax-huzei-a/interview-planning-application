package intellistart.interviewplanning.model.interviewerSlot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import intellistart.interviewplanning.exceptions.SlotException;
import intellistart.interviewplanning.model.booking.Booking;
import intellistart.interviewplanning.model.booking.BookingService;
import intellistart.interviewplanning.model.dayofweek.DayOfWeek;
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlot;
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlotRepository;
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlotService;
import intellistart.interviewplanning.model.period.Period;
import intellistart.interviewplanning.model.user.Role;
import intellistart.interviewplanning.model.user.User;
import intellistart.interviewplanning.model.week.Week;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class InterviewerSlotServiceTest {


  static InterviewerSlotRepository interviewerSlotRepository = Mockito.mock(
      InterviewerSlotRepository.class);
  static BookingService bookingService = Mockito.mock(BookingService.class);

  static User u1 = new User(1L, "interviewer@gmail.com", Role.INTERVIEWER);
  static User u2 = new User(2L, "interviewer@gmail2.com", Role.INTERVIEWER);

  static Week w1 = new Week(50L, new HashSet<>());
  static Week w2 = new Week(100L, new HashSet<>());
  static Week w3 = new Week(103L, new HashSet<>());

  static Period p1 =
      new Period(null, LocalTime.of(10, 0), LocalTime.of(20, 0),
      new HashSet<>(), new HashSet<>(), new HashSet<>());
  static Period p2 =
      new Period(null, LocalTime.of(12, 0), LocalTime.of(18, 0),
      new HashSet<>(), new HashSet<>(), new HashSet<>());
  static Period p3 = new Period(null, LocalTime.of(11, 0),
      LocalTime.of(17, 30),
      new HashSet<>(), new HashSet<>(), new HashSet<>());

  static InterviewerSlot is1 =
      new InterviewerSlot(null, w1, DayOfWeek.TUE, p1, null, u1);
  static InterviewerSlot is2 =
      new InterviewerSlot(null, w2, DayOfWeek.TUE, p2, null, u1);
  static InterviewerSlot is3 =
      new InterviewerSlot(null, w3, DayOfWeek.TUE, p3, Set.of(new Booking()), u2);

  InterviewerSlotService cut = new InterviewerSlotService(
      interviewerSlotRepository, bookingService
  );

  @Test
  void getSlotsByWeekTest() throws SlotException {
    when(interviewerSlotRepository.findById(1L)).thenReturn(Optional.of(is1));
    InterviewerSlot actual = cut.getById(1L);
    InterviewerSlot expected = is1;
    assertEquals(expected, actual);
  }

  @Test
  void createInterviewerSlotsTest() {
    InterviewerSlot expected = new InterviewerSlot(null, w1, DayOfWeek.TUE, p1, null, u1);
    cut.create(expected);
    ArgumentCaptor<InterviewerSlot> slotArgumentCaptor = ArgumentCaptor.forClass(
        InterviewerSlot.class);
    verify(interviewerSlotRepository).save(slotArgumentCaptor.capture());
    InterviewerSlot actual = slotArgumentCaptor.getValue();
    assertEquals(expected, actual);
  }

  static Arguments[] getUserByEmailTestArgs(){
    return new Arguments[]{
        Arguments.arguments(u2, List.of(is1, is2)),
        Arguments.arguments(u1, List.of(is3))
    };
  }
  @ParameterizedTest
  @MethodSource("getUserByEmailTestArgs")
  void getUserByEmailTest(User user, List<InterviewerSlot> interviewerSlots) {
    Mockito.when(interviewerSlotRepository.findInterviewerSlotsByUser(user))
        .thenReturn(interviewerSlots);

    cut.deleteSlotsByUser(user);
    Mockito.verify(interviewerSlotRepository).deleteAll(interviewerSlots);
  }
}
