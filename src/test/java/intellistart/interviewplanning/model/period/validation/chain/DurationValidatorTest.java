package intellistart.interviewplanning.model.period.validation.chain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DurationValidatorTest {

  private static DurationValidator cut;

  @BeforeAll
  public static void initialize(){
    cut = new DurationValidator();
  }

  @Test
  void trueWhenCorrect(){
    LocalTime lower = LocalTime.of(8, 0);
    LocalTime upper = LocalTime.of(10, 0);

    assertTrue(cut.isCorrect(lower, upper));
  }

  @Test
  void falseWhenIncorrectDifference(){
    LocalTime lower = LocalTime.of(8, 0);
    LocalTime upper = LocalTime.of(9, 0);

    assertFalse(cut.isCorrect(lower, upper));
  }

  @Test
  void trueWhenIncorrectLogic(){
    LocalTime lower = LocalTime.of(3, 34, 8);
    LocalTime upper = LocalTime.of(22, 33, 0);

    assertTrue(cut.isCorrect(lower, upper));
  }

  @Test
  void falseWhenNegativeDuration(){
    LocalTime lower = LocalTime.of(18, 0);
    LocalTime upper = LocalTime.of(15, 0);

    assertFalse(cut.isCorrect(lower, upper));
  }
}