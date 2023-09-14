package intellistart.interviewplanning.model.period.validation;

import static org.junit.jupiter.api.Assertions.*;

import intellistart.interviewplanning.exceptions.SlotException;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PeriodValidatorTest {

  private static PeriodValidator cut;

  @BeforeAll
  public static void initialize(){
    cut = new PeriodValidator();
  }


  @Test
  void correct(){
    LocalTime from = LocalTime.of(18, 0);
    LocalTime to = LocalTime.of(19, 30);

    assertDoesNotThrow(() -> cut.validate(from, to));
  }

  @Test
  void incorrectRounding(){
    LocalTime from = LocalTime.of(9, 1);
    LocalTime to = LocalTime.of(19, 30);

    assertThrows(SlotException.class, () ->
      cut.validate(from, to));
  }

  @Test
  void incorrectDuration(){
    LocalTime from = LocalTime.of(9, 0);
    LocalTime to = LocalTime.of(10, 0);

    assertThrows(SlotException.class, () ->
      cut.validate(from, to));
  }

  @Test
  void incorrectLimits(){
    LocalTime from = LocalTime.of(9, 30);
    LocalTime to = LocalTime.of(23, 0);

    assertThrows(SlotException.class, () ->
      cut.validate(from, to));
  }

  @Test
  void incorrectEverything(){
    LocalTime from = LocalTime.of(9, 31);
    LocalTime to = LocalTime.of(3, 0);

    assertThrows(SlotException.class, () ->
      cut.validate(from, to));
  }
}