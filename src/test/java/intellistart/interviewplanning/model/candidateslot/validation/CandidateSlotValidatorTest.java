package intellistart.interviewplanning.model.candidateslot.validation;

import intellistart.interviewplanning.exceptions.SlotException;
import intellistart.interviewplanning.model.booking.Booking;
import intellistart.interviewplanning.model.candidateslot.CandidateSlot;
import intellistart.interviewplanning.model.candidateslot.CandidateSlotService;
import intellistart.interviewplanning.model.period.Period;
import intellistart.interviewplanning.model.period.PeriodService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

public class CandidateSlotValidatorTest {

  private static CandidateSlotService candidateSlotService;
  private static CandidateSlotValidator cut;
  private static PeriodService periodService;
  private static CandidateSlot candidateSlot1;
  private static CandidateSlot candidateSlot2;
  private static CandidateSlot candidateSlot3;
  private static CandidateSlot candidateSlot4;
  private static CandidateSlot candidateSlot5;
  private static CandidateSlot candidateSlot6;
  private static CandidateSlot candidateSlot7;
  private static CandidateSlot candidateSlot8;
  private static CandidateSlot candidateSlot9;
  private static CandidateSlot candidateSlot10;

  @BeforeAll
  static void initialize() {
    candidateSlotService = Mockito.mock(CandidateSlotService.class);
    periodService = Mockito.mock(PeriodService.class);
    cut = new CandidateSlotValidator(candidateSlotService,
        periodService);

    Period period1 = new Period();
    period1.setFrom(LocalTime.of(9,0));
    period1.setTo(LocalTime.of(10,30));

    Period period2 = new Period();
    period2.setFrom(LocalTime.of(14,0));
    period2.setTo(LocalTime.of(20,30));
    

    candidateSlot1 = new CandidateSlot();
    candidateSlot1.setId(1L);
    candidateSlot1.setDate(LocalDate.of(1990,1,1));
    candidateSlot1.setPeriod(period1);
    candidateSlot1.setEmail("test@unit.com");
    candidateSlot1.setName("AAA");

    candidateSlot2 = new CandidateSlot();
    candidateSlot2.setId(2L);
    candidateSlot2.setDate(LocalDate.of(2021, 4, 24));
    candidateSlot2.setEmail("test@unit.com");
    candidateSlot2.setName("AAA");

    candidateSlot3 = new CandidateSlot();
    candidateSlot3.setId(3L);
    candidateSlot3.setDate(LocalDate.of(LocalDate.now().getYear()+1,1,1));
    candidateSlot3.setPeriod(period1);
    candidateSlot3.setEmail("test@unit.com");
    candidateSlot3.setName("AAA");

    candidateSlot4 = new CandidateSlot();
    candidateSlot4.setId(4L);
    candidateSlot4.setDate(LocalDate.of(LocalDate.now().getYear()+1,2,2));
    candidateSlot4.setPeriod(period2);
    candidateSlot4.setEmail("test@unit.com");
    candidateSlot4.setName("AAA");

    candidateSlot5 = new CandidateSlot();
    candidateSlot5.setId(5L);
    candidateSlot5.setDate(LocalDate.of(LocalDate.now().getYear()+1,1,1));
    candidateSlot5.setPeriod(period2);
    candidateSlot5.setEmail("test@unit.com");
    candidateSlot5.setName("AAA");
    candidateSlot5.addBooking(new Booking());

    candidateSlot6 = new CandidateSlot();
    candidateSlot6.setId(6L);
    candidateSlot6.setDate(LocalDate.of(LocalDate.now().getYear()+1,2,2));
    candidateSlot6.setPeriod(period1);
    candidateSlot6.setEmail("test@mail.com");
    candidateSlot6.setName("AAA");
    candidateSlot6.addBooking(new Booking());

    candidateSlot7 = new CandidateSlot();
    candidateSlot7.setId(7L);
    candidateSlot7.setDate(LocalDate.of(LocalDate.now().getYear()+1,12,1));
    candidateSlot7.setPeriod(period1);
    candidateSlot7.setEmail("test@mail.com");
    candidateSlot7.setName("AAA");

    candidateSlot8 = new CandidateSlot();
    candidateSlot8.setId(8L);
    candidateSlot8.setDate(LocalDate.of(LocalDate.now().getYear()+1,9,2));
    candidateSlot8.setPeriod(period2);
    candidateSlot8.setEmail("test@mail.com");
    candidateSlot8.setName("AAA");

    candidateSlot9 = new CandidateSlot();
    candidateSlot9.setId(9L);
    candidateSlot9.setDate(LocalDate.of(LocalDate.now().getYear()+1,8,30));
    candidateSlot9.setPeriod(period2);
    candidateSlot9.setEmail("test@mail.com");
    candidateSlot9.setName("AAA");
    candidateSlot9.addBooking(new Booking());

    candidateSlot10 = new CandidateSlot();
    candidateSlot10.setId(10L);
    candidateSlot10.setDate(LocalDate.of(LocalDate.now().getYear()+1,1,20));
    candidateSlot10.setPeriod(period2);
    candidateSlot10.setEmail("test@mail.com");
    candidateSlot10.setName("AAA");
    candidateSlot10.addBooking(new Booking());
  }

  static Arguments[] validateCreateCandidateSlotArgs(){
    return new Arguments[]{
        Arguments.arguments(candidateSlot4),
        Arguments.arguments(candidateSlot3)
    };
  }
  @ParameterizedTest
  @MethodSource("validateCreateCandidateSlotArgs")
  void validateCreateCandidateSlotTest(CandidateSlot candidateSlot)
      throws SlotException {
    Mockito.when(candidateSlotService.getCandidateSlotsByEmailAndDate(candidateSlot.getEmail(),
        candidateSlot.getDate())).thenReturn(List.of());

    cut.validateCreating(candidateSlot);

    Mockito.verify(candidateSlotService).getCandidateSlotsByEmailAndDate(candidateSlot.getEmail(),
        candidateSlot.getDate());
  }

  static Arguments[] validateCreateCandidateSlotExc1Args(){
    return new Arguments[]{
        Arguments.arguments(candidateSlot1, SlotException.class),
        Arguments.arguments(candidateSlot2, SlotException.class)
    };
  }
  @ParameterizedTest
  @MethodSource("validateCreateCandidateSlotExc1Args")
  void validateCreateCandidateSlotInvalidBoundariesExceptionTest(CandidateSlot candidateSlot,
      Class<Exception> actual) {
    Assertions.assertThrows(actual,
        ()-> cut.validateCreating(candidateSlot));
  }

  static Arguments[] validateCreateCandidateSlotExc2Args(){
    return new Arguments[]{
        Arguments.arguments(candidateSlot3, SlotException.class,
            List.of(candidateSlot5, candidateSlot6)),
        Arguments.arguments(candidateSlot4, SlotException.class,
            List.of(candidateSlot6, candidateSlot5))
    };
  }
  @ParameterizedTest
  @MethodSource("validateCreateCandidateSlotExc2Args")
  void validateCreateCandidateSlotSlotIsOverlappingExceptionTest(CandidateSlot candidateSlot,
      Class<Exception> actual, List<CandidateSlot> candidateSlotList) {
    Mockito.when(candidateSlotService.getCandidateSlotsByEmailAndDate(candidateSlot.getEmail(),
            candidateSlot.getDate()))
            .thenReturn(candidateSlotList);

    Period period = candidateSlot.getPeriod();
    Mockito.when(periodService.areOverlapping(period, candidateSlotList.get(1).getPeriod()))
            .thenReturn(true);

    Assertions.assertThrows(actual,
        ()-> cut.validateCreating(candidateSlot));
  }

  static Arguments[] validateUpdateCandidateSlotArgs(){
    return new Arguments[]{
        Arguments.arguments(candidateSlot7, 3),
        Arguments.arguments(candidateSlot8, 2)
    };
  }
  @ParameterizedTest
  @MethodSource("validateUpdateCandidateSlotArgs")
  void validateUpdateCandidateSlotTest(CandidateSlot candidateSlot)
      throws SlotException {
    Mockito.when(candidateSlotService.getCandidateSlotsByEmailAndDate(candidateSlot.getEmail(),
        candidateSlot.getDate())).thenReturn(List.of());
    Mockito.when(candidateSlotService.findById(candidateSlot.getId()))
        .thenReturn(candidateSlot);

    cut.validateUpdating(candidateSlot);

    Mockito.verify(candidateSlotService).getCandidateSlotsByEmailAndDate(candidateSlot.getEmail(),
        candidateSlot.getDate());
    Mockito.verify(candidateSlotService).findById(candidateSlot.getId());
  }

  static Arguments[] validateUpdateCandidateSlotExc1Args(){
    return new Arguments[]{
        Arguments.arguments(candidateSlot5, SlotException.class),
        Arguments.arguments(candidateSlot6, SlotException.class)
    };
  }
  @ParameterizedTest
  @MethodSource("validateUpdateCandidateSlotExc1Args")
  void validateUpdateCandidateSlotSlotNotFoundExceptionTest(CandidateSlot candidateSlot,
      Class<Exception> actual) throws SlotException {
    Mockito.when(candidateSlotService.findById(candidateSlot.getId())).thenThrow(SlotException.class);

    Assertions.assertThrows(actual,
        ()-> cut.validateUpdating(candidateSlot));
  }

  static Arguments[] validateUpdateCandidateSlotExc2Args(){
    return new Arguments[]{
        Arguments.arguments(candidateSlot9, SlotException.class),
        Arguments.arguments(candidateSlot10, SlotException.class)
    };
  }
  @ParameterizedTest
  @MethodSource("validateUpdateCandidateSlotExc2Args")
  void validateUpdateCandidateSlotSlotIsBookedExceptionTest(CandidateSlot candidateSlot,
      Class<Exception> actual) throws SlotException {
    Mockito.when(candidateSlotService.findById(candidateSlot.getId()))
        .thenReturn(candidateSlot);

    Assertions.assertThrows(actual,
        ()-> cut.validateUpdating(candidateSlot));
  }
}

