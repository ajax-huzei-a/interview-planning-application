package intellistart.interviewplanning.model.candidateslot;

import intellistart.interviewplanning.exceptions.SlotException;
import intellistart.interviewplanning.model.period.Period;
import intellistart.interviewplanning.model.period.PeriodService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

public class CandidateSlotServiceTest {

  private static CandidateSlotRepository candidateSlotRepository;
  private static PeriodService periodService;
  private static CandidateSlotService cut;
  private static CandidateSlot candidateSlot1;
  private static CandidateSlot candidateSlot2;
  private static Period period1;
  private static Period period2;

  @BeforeAll
  static void initialize() {
    candidateSlotRepository = Mockito.mock(CandidateSlotRepository.class);
    periodService = Mockito.mock(PeriodService.class);
    cut = new CandidateSlotService(candidateSlotRepository,
        periodService);

    period1 = new Period();
    period1.setFrom(LocalTime.of(9,0));
    period1.setTo(LocalTime.of(10,30));

    period2 = new Period();
    period2.setFrom(LocalTime.of(14,0));
    period2.setTo(LocalTime.of(20,30));


    candidateSlot1 = new CandidateSlot();
    candidateSlot1.setDate(LocalDate.of(2023, 1, 1));
    candidateSlot1.setPeriod(period1);
    candidateSlot1.setEmail("test@mail.com");
    candidateSlot1.setName("AAA");

    candidateSlot2 = new CandidateSlot();
    candidateSlot2.setDate(LocalDate.of(2023, 4, 24));
    candidateSlot2.setPeriod(period2);
    candidateSlot2.setEmail("test@mail.com");
    candidateSlot2.setName("AAA");
  }

  static Arguments[] createTestArgs(){
    return new Arguments[]{
        Arguments.arguments(candidateSlot1),
        Arguments.arguments(candidateSlot2)
    };
  }
  @ParameterizedTest
  @MethodSource("createTestArgs")
  void createTest(CandidateSlot expected) {
    Mockito.when(candidateSlotRepository.save(expected)).thenReturn(expected);

    CandidateSlot actual = cut.create(expected);
    Assertions.assertEquals(actual, expected);
  }

  static Arguments[] updateTestArgs(){
    return new Arguments[]{
        Arguments.arguments(candidateSlot1),
        Arguments.arguments(candidateSlot2)
    };
  }
  @ParameterizedTest
  @MethodSource("updateTestArgs")
  void updateTest(CandidateSlot expected) {
    Mockito.when(candidateSlotRepository.save(expected)).thenReturn(expected);

    CandidateSlot actual = cut.update(expected);
    Assertions.assertEquals(actual, expected);
  }

  static Arguments[] getAllSlotsOfCandidateArgs(){
    return new Arguments[]{
        Arguments.arguments("test@unit.com", List.of()),
        Arguments.arguments("test@unit.com", List.of(candidateSlot1, candidateSlot2))
    };
  }
  @ParameterizedTest
  @MethodSource("getAllSlotsOfCandidateArgs")
  void getAllSlotsOfCandidateTest(String email, List<CandidateSlot> expected) {
    Mockito.when(candidateSlotRepository.findByEmail(email))
        .thenReturn(expected);

    List<CandidateSlot> actual = cut.getAllSlotsByEmail(email);
    Assertions.assertEquals(actual, expected);
  }

  static Arguments[] getCandidateSlotsByEmailAndDateTestArgs(){
    return new Arguments[]{
        Arguments.arguments(List.of(), LocalDate.of(2022,1,1)),
        Arguments.arguments(List.of(candidateSlot1), LocalDate.of(2023, 1, 1)),
        Arguments.arguments(List.of(candidateSlot2), LocalDate.of(2023, 4, 24))
    };
  }
  @ParameterizedTest
  @MethodSource("getCandidateSlotsByEmailAndDateTestArgs")
  void getCandidateSlotsByEmailAndDateTest(List<CandidateSlot> expected, LocalDate date) {
    Mockito.when(candidateSlotRepository.findByEmailAndDate("test@mail.com", date))
        .thenReturn(expected);

    List<CandidateSlot> actual = cut.getCandidateSlotsByEmailAndDate("test@mail.com", date);
    Assertions.assertEquals(actual, expected);
  }

  static Arguments[] getCandidateSlotByIdArgs(){
    return new Arguments[]{
        Arguments.arguments(candidateSlot1, 1L),
        Arguments.arguments(candidateSlot2, 2L),
    };
  }
  @ParameterizedTest
  @MethodSource("getCandidateSlotByIdArgs")
  void getCandidateSlotByIdTest(CandidateSlot expected, Long id) throws SlotException {
//    Mockito.when(candidateSlotRepository.findById(id)).thenReturn(Optional.of(expected));
    Mockito.when(candidateSlotRepository.findById(id)).thenReturn(Optional.of(expected));

    CandidateSlot actual = cut.getById(id);
//    CandidateSlot actual = candidateSlot.orElse(null);
    Assertions.assertEquals(actual, expected);
  }

  static Arguments[] createCandidateSlotArgs(){
    return new Arguments[]{
        Arguments.arguments(LocalDate.of(2023, 1, 1),
            "9:00","10:30", "test@mail.com", "AAA", candidateSlot1, period1),
        Arguments.arguments(LocalDate.of(2023, 4, 24),
            "14:00","20:30", "test@mail.com", "AAA",  candidateSlot1, period2),
    };
  }
  @ParameterizedTest
  @MethodSource("createCandidateSlotArgs")
  void createCandidateSlotTest(LocalDate date, String from, String to, String email, String name,
      CandidateSlot expected, Period period) throws SlotException {
      
    Mockito.when(periodService.obtainPeriod(from, to)).thenReturn(period);

    CandidateSlot actual = cut.createCandidateSlot(date, from, to, email, name);
    Assertions.assertEquals(actual, expected);
  }
}
