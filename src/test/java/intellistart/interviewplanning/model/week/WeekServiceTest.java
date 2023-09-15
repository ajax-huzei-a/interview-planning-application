package intellistart.interviewplanning.model.week;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import intellistart.interviewplanning.model.dayofweek.DayOfWeek;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

class WeekServiceTest {

  private static WeekRepository weekRepository ;
  private static WeekService cut;

  @BeforeEach
  void initialize(){
    weekRepository = Mockito.mock(WeekRepository.class);
    cut = new WeekService(weekRepository);
  }

  @ParameterizedTest
  @CsvSource({"2022-01-01,0","2022-10-13,41","2023-01-01,52",
              "2023-01-02,53","2023-12-31,104","2024-01-01,105","2025-01-01,157"})
  void getNumberOfWeekTest(LocalDate date,long expected){
    long actual = cut.getNumberOfWeek(date);
    assertEquals(expected,actual);
  }


  @ParameterizedTest
  @CsvSource({"2023-01-02,MON","2024-02-13,TUE","2023-06-14,WED","2022-10-13,THU",
              "2024-03-15,FRI","2022-01-01,SAT","2023-01-01,SUN"})
  void getDayOfWeekTest(LocalDate date, DayOfWeek expected){
    DayOfWeek actual = cut.getDayOfWeek(date);
    assertEquals(expected,actual);
  }

  @ParameterizedTest
  @CsvSource({"53,MON,2023-01-02","1,TUE,2022-01-04","41,WED,2022-10-12","173,THU,2025-04-17",
              "62,FRI,2023-03-10","273,SAT,2027-03-13","104,SUN,2023-12-31"})
  void convertToLocalDateTest(long numberOfWeek,DayOfWeek dayOfWeek,LocalDate expected){
    LocalDate actual = cut.convertToLocalDate(numberOfWeek,dayOfWeek);
    assertEquals(expected,actual);
  }

  @Test
  void convertToLocalDateWhenIncorrectDataTest(){
    assertThrows(DateTimeException.class,() -> cut.convertToLocalDate(-56,DayOfWeek.THU));
  }

 @Test
  void getWeekByWeekNumIfNotExistTest(){
    Week expected = new Week(153L,new HashSet<>());
    given(weekRepository.save(expected)).willReturn(expected);
    Week actual = cut.getWeekByWeekNum(153L);
    verify(weekRepository).save(expected);
    assertEquals(expected,actual);
 }
  @Test
  void getWeekByWeekNumIfExistTest(){
    Week expected = new Week(153L,new HashSet<>());
    given(weekRepository.findById(153L)).willReturn(Optional.of(expected));
    Week actual = cut.getWeekByWeekNum(153L);
    verify(weekRepository,never()).save(any());
    assertEquals(expected,actual);
  }

}
