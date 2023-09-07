package intellistart.interviewplanning.model.period;

import static org.junit.jupiter.api.Assertions.*;

import intellistart.interviewplanning.exceptions.SlotException;
import intellistart.interviewplanning.model.period.services.TimeService;
import intellistart.interviewplanning.model.period.services.validation.PeriodValidator;

import java.time.LocalTime;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

class PeriodServiceTest {

  private static PeriodRepository repository;
  private static TimeService converter;
  private static PeriodValidator validator;
  private static Period tenToTwoPeriod;
  private static PeriodService cut;

  static Period createPeriod(LocalTime from, LocalTime to){
    Period period = new Period();
    period.setFrom(from);
    period.setTo(to);

    return period;
  }

  @BeforeAll
  static void initialize(){
    repository = Mockito.mock(PeriodRepository.class);
    converter = Mockito.mock(TimeService.class);
    validator = Mockito.mock(PeriodValidator.class);

    cut = new PeriodService(
        repository,
        validator,
        converter
    );

    tenToTwoPeriod = createPeriod(LocalTime.of(10, 0), LocalTime.of(14,0));
  }

  @Test
  void exceptionWhenIncorrectValidation() throws SlotException {
    String fromStr = "19:00";
    String toStr = "23:00";
    LocalTime from = LocalTime.of(19, 0);
    LocalTime to = LocalTime.of(23, 0);

    Mockito.doThrow(SlotException.class).when(validator).validate(from, to);

    Mockito.when(converter.convert(fromStr)).thenReturn(from);
    Mockito.when(converter.convert(toStr)).thenReturn(to);

    assertThrows(SlotException .class, () ->
        cut.obtainPeriod(fromStr, toStr));
  }

  static Stream<Arguments> provideObtainPeriodArguments(){
    return Stream.of(
        Arguments.of(
            "19:30",
            "20:00",
            LocalTime.of(19, 30),
            LocalTime.of(20,0)),
        Arguments.of(
            "8:00",
            "16:00",
            LocalTime.of(8, 0),
            LocalTime.of(16,0)),
        Arguments.of(
            "12:30",
            "21:00",
            LocalTime.of(12, 30),
            LocalTime.of(21,0)));
  }

  @ParameterizedTest
  @MethodSource("provideObtainPeriodArguments")
  void returnPeriodWhenPeriodNotExists(String fromStr, String toStr,
      LocalTime from, LocalTime to) throws SlotException {

    Period expected = createPeriod(from, to);

    Mockito.when(converter.convert(fromStr)).thenReturn(from);
    Mockito.when(converter.convert(toStr)).thenReturn(to);

    Mockito.when(repository.findPeriodByFromAndTo(from, to)).thenReturn(Optional.empty());

    Period createdPeriod = createPeriod(from, to);
    Mockito.when(repository
        .save(new Period(null, from, to, null, null, null)))
        .thenReturn(createdPeriod);

    Period actual = cut.obtainPeriod(fromStr, toStr);

    assertEquals(actual, expected);
  }

  @ParameterizedTest
  @MethodSource("provideObtainPeriodArguments")
  void returnPeriodWhenPeriodExists(String fromStr, String toStr,
  LocalTime from, LocalTime to) throws SlotException {

    Period expected = createPeriod(from, to);

    Mockito.when(converter.convert(fromStr)).thenReturn(from);
    Mockito.when(converter.convert(toStr)).thenReturn(to);

    Period foundPeriod = createPeriod(from, to);
    Mockito.when(repository.findPeriodByFromAndTo(from, to)).thenReturn(Optional.of(foundPeriod));

    Period actual = cut.obtainPeriod(fromStr, toStr);

    assertEquals(actual, expected);
  }

  static Stream<Arguments> provideOverlappingArguments(){
    Period crossingSecondBefore = createPeriod(
        LocalTime.of(8, 30),
        LocalTime.of(11, 30));

    Period crossingFirstBefore = createPeriod(
        LocalTime.of(13, 30),
        LocalTime.of(18, 30));

    Period inner = createPeriod(
        LocalTime.of(8, 0),
        LocalTime.of(16, 30));

    Period sameFrom = createPeriod(
        LocalTime.of(10, 0),
        LocalTime.of(12, 0));

    Period sameTo = createPeriod(
        LocalTime.of(12, 0),
        LocalTime.of(14, 0));

    Period same = tenToTwoPeriod;

    return Stream.of(Arguments.of(
        crossingSecondBefore,
        crossingFirstBefore,
        inner,
        sameFrom,
        sameTo,
        same));
  }

  @ParameterizedTest
  @MethodSource("provideOverlappingArguments")
  void overlapWhenShould(Period period){
    System.out.println(period);
    System.out.println(tenToTwoPeriod);
    assertTrue(cut.areOverlapping(tenToTwoPeriod, period));
  }

  static Stream<Arguments> provideNonOverlappingArguments(){
    Period outerBorders = createPeriod(
        LocalTime.of(14, 0),
        LocalTime.of(17, 30));

    Period completelyDifferent = createPeriod(
        LocalTime.of(15, 30),
        LocalTime.of(18, 30));

    return Stream.of(Arguments.of(
        outerBorders,
        completelyDifferent));
  }

  @ParameterizedTest
  @MethodSource("provideNonOverlappingArguments")
  void notOverlappingWhenShouldNot(Period period){
    assertFalse(cut.areOverlapping(tenToTwoPeriod, period));
  }

  static Stream<Arguments> provideIsInsideArguments(){
    Period sameFrom = createPeriod(
        LocalTime.of(10, 0),
        LocalTime.of(11, 30));

    Period sameTo = createPeriod(
        LocalTime.of(10, 30),
        LocalTime.of(14, 0));

    Period inner = createPeriod(
        LocalTime.of(10, 30),
        LocalTime.of(13, 30));

    Period same = tenToTwoPeriod;

    return Stream.of(Arguments.of(
        sameFrom,
        sameTo,
        inner,
        same));
  }

  @ParameterizedTest
  @MethodSource("provideIsInsideArguments")
  void isInsideFirstWhenShould(Period period){
    assertTrue(cut.isFirstInsideSecond(period, tenToTwoPeriod));
  }

  static Stream<Arguments> provideNotInsideArguments(){
    Period bordersAbove = createPeriod(
        LocalTime.of(14, 0),
        LocalTime.of(16, 30));

    Period bordersBelow = createPeriod(
        LocalTime.of(8, 30),
        LocalTime.of(10, 0));

    Period overlapsBelow = createPeriod(
        LocalTime.of(8, 30),
        LocalTime.of(13, 30));

    Period overlapsAbove = createPeriod(
        LocalTime.of(13, 30),
        LocalTime.of(20, 30));

    Period completelyDifferent = createPeriod(
        LocalTime.of(18, 30),
        LocalTime.of(21, 30));

    return Stream.of(Arguments.of(
        bordersAbove,
        bordersBelow,
        overlapsBelow,
        overlapsAbove,
        completelyDifferent));
  }

  @ParameterizedTest
  @MethodSource("provideNotInsideArguments")
  void notInsideFirstWhenShouldNot(Period period){
    assertFalse(cut.isFirstInsideSecond(period, tenToTwoPeriod));
  }
}