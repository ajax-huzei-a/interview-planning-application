package intellistart.interviewplanning.model.period.validation.chain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RoundingMinutesValidatorTest {

  private static RoundingMinutesValidator cut;

  @BeforeAll
  public static void initialize(){
    cut = new RoundingMinutesValidator();
  }

  @Test
  void trueWhenOkay(){
    LocalTime lower = LocalTime.of(19, 0);
    LocalTime upper = LocalTime.of(21, 30);

    assertTrue(cut.isCorrect(lower, upper));
  }

  @Test
  void falseWhenUpperIncorrect(){
    LocalTime lower = LocalTime.of(19, 0);
    LocalTime upper = LocalTime.of(21, 34);

    assertFalse(cut.isCorrect(lower, upper));
  }

  @Test
  void falseWhenLowerIncorrect(){
    LocalTime lower = LocalTime.of(15, 17);
    LocalTime upper = LocalTime.of(21, 0);

    assertFalse(cut.isCorrect(lower, upper));
  }

  @Test
  void trueWhenAnotherLogicIncorrect(){
    LocalTime lower = LocalTime.of(15, 0);
    LocalTime upper = LocalTime.of(16, 0);

    assertTrue(cut.isCorrect(lower, upper));
  }
}