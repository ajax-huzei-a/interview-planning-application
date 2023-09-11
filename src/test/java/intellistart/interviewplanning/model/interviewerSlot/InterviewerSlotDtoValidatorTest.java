package intellistart.interviewplanning.model.interviewerSlot;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import intellistart.interviewplanning.controllers.dto.InterviewerSlotDtoRequest;
import intellistart.interviewplanning.exceptions.SlotException;
import intellistart.interviewplanning.exceptions.UserException;
import intellistart.interviewplanning.model.dayofweek.DayOfWeek;
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlot;
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlotDtoValidator;
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlotService;
import intellistart.interviewplanning.model.period.Period;
import intellistart.interviewplanning.model.period.PeriodRepository;
import intellistart.interviewplanning.model.period.PeriodService;
import intellistart.interviewplanning.model.period.services.TimeService;
import intellistart.interviewplanning.model.period.services.validation.PeriodValidator;
import intellistart.interviewplanning.model.user.Role;
import intellistart.interviewplanning.model.user.User;
import intellistart.interviewplanning.model.user.UserRepository;
import intellistart.interviewplanning.model.user.UserService;
import intellistart.interviewplanning.model.week.Week;
import intellistart.interviewplanning.model.week.WeekRepository;
import intellistart.interviewplanning.model.week.WeekService;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;


public class InterviewerSlotDtoValidatorTest {

  static UserRepository userRepository = Mockito.mock(UserRepository.class);
  static InterviewerSlotService interviewerSlotService = Mockito.mock(InterviewerSlotService.class);

  @MockBean
  static UserService userService = new UserService(userRepository, interviewerSlotService);
  static PeriodRepository periodRepository = Mockito.mock(PeriodRepository.class);
  static TimeService timeConverter = new TimeService();
  static PeriodValidator periodValidator = new PeriodValidator();

  @MockBean
  static PeriodService periodService = new PeriodService(
          periodRepository,
          periodValidator,
          timeConverter);
  static WeekRepository weekRepository = Mockito.mock(WeekRepository.class);
  @MockBean
  static WeekService weekService = new WeekService(weekRepository);

  InterviewerSlotDtoValidator cut = new InterviewerSlotDtoValidator(
      periodService, userService, interviewerSlotService, weekService
  );

  @Test
  void validationForCoordinatorTest(){
    assertThrows(SlotException.class, () -> cut.validationForCoordinator(dto1, 1L, 1L));
  }

  @Test
  void isCorrectDayTest() {
    assertThrows(SlotException.class, () -> cut.validateIfCorrectDay("friday"));
    assertThrows(SlotException.class, () -> cut.validateIfCorrectDay("february"));
    assertDoesNotThrow(() -> cut.validateIfCorrectDay("TUE"));
  }

  @Test
  void isInterviewerRoleINTERVIEWERTest() {
    assertThrows(UserException.class, () -> cut.validateIfInterviewerRoleInterviewer(u3));
    assertThrows(UserException.class, () -> cut.validateIfInterviewerRoleInterviewer(u2));
    assertDoesNotThrow(() -> cut.validateIfInterviewerRoleInterviewer(u1));
  }

  @Test
  void canEditThisWeekTest() {
    when(weekService.getCurrentWeek()).thenReturn(new Week(43L,new HashSet<>()));
    assertThrows(SlotException.class, () -> cut.validateIfCanEditThisWeek(w2));
    assertDoesNotThrow(() -> cut.validateIfCanEditThisWeek(w1));
  }

  @Test
  void isSlotOverlapTest() {
    List<InterviewerSlot> list = new ArrayList<>();
    when(interviewerSlotService
        .getInterviewerSlotsByUserAndWeekAndDayOfWeek(u1,
            w1, DayOfWeek.TUE)).thenReturn(list);
    assertDoesNotThrow(() -> cut.validateIfPeriodIsOverlapping(is3));
  }
  @Test
  void isSlotOverlapTestThrow() {
    List<InterviewerSlot> list = new ArrayList<>();
    list.add(is3);
    when(interviewerSlotService
        .getInterviewerSlotsByUserAndWeekAndDayOfWeek(u1,
            w1, DayOfWeek.TUE)).thenReturn(list);
    assertThrows(SlotException.class, () -> cut.validateIfPeriodIsOverlapping(is3));
  }

  static User u1 = new User(1L, "interviewer@gmail.com", Role.INTERVIEWER);
  static User u2 = new User(null, "interviewer2@gmail.com", Role.COORDINATOR);
  static User u3 = new User(null, "interviewer3@gmail.com", Role.COORDINATOR);


  static Week w1 = new Week(100L, new HashSet<>());
  static Week w2 = new Week(35L, new HashSet<>());
static Period p1 = new Period(null, LocalTime.of(12, 0), LocalTime.of(18, 0),
    new HashSet<>(), new HashSet<>(), new HashSet<>());
  static Period p2 = new Period(null, LocalTime.of(11, 0), LocalTime.of(17, 30),
      new HashSet<>(), new HashSet<>(), new HashSet<>());

  static InterviewerSlotDtoRequest dto1 = new InterviewerSlotDtoRequest( 40L,
      "TUE", "12:00", "18:00");
  static InterviewerSlotDtoRequest dto1b = new InterviewerSlotDtoRequest(400L,
      "TUEyy", "12:00", "18:00");
  static InterviewerSlotDtoRequest dto2 = new InterviewerSlotDtoRequest(100L,
      "TUE", "12:00", "18:00");
  static InterviewerSlotDtoRequest dto3 = new InterviewerSlotDtoRequest(103L,
      "THU", "11:00", "17:30");
  static User u4 = new User(2L, "interviewer@gmail2.com", Role.INTERVIEWER);
  static Week w4 = new Week(103L, new HashSet<>());

  static InterviewerSlot is3 = new InterviewerSlot(null, w1, DayOfWeek.TUE, p1, null, u1);
  static InterviewerSlot is4 = new InterviewerSlot(null, w4, DayOfWeek.THU, p2, null, u4);


  static class DtoSlotArgumentsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
      return Stream.of(
          Arguments.of(dto2, is3),
          Arguments.of(dto3, is4)
      );
    }
  }
}