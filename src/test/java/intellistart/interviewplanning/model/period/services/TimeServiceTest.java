package intellistart.interviewplanning.model.period.services;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TimeServiceTest {

  private static TimeService cut;

  @BeforeAll
  public static void initialize(){
    cut = new TimeService();
  }
  @Test
  void convertWhenCorrectFormat() {
    LocalTime actual = cut.convert("19:00");
    LocalTime expected = LocalTime.of(19, 0);
    assertEquals(actual, expected);
  }

  @Test
  void notConvertWhenSecondsGiven() {
    assertThrows(IllegalArgumentException.class, () ->
        cut.convert("19:00:33"));
  }

  @Test
  void notConvertWhenInvalidHourOrMinutes(){
    assertThrows(IllegalArgumentException.class, () ->
        cut.convert("34:00"));
    assertThrows(IllegalArgumentException.class, () ->
        cut.convert("11:77"));
  }

  @Test
  void convertWhenIncorrectLogic(){
    Assertions.assertDoesNotThrow(() -> cut.convert("19:23"));
    Assertions.assertDoesNotThrow(() -> cut.convert("23:00"));
  }
}