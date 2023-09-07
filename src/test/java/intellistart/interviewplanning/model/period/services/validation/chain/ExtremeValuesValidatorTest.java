package intellistart.interviewplanning.model.period.services.validation.chain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ExtremeValuesValidatorTest {

  private static ExtremeValuesValidator cut;

  @BeforeAll
  public static void initialize(){
    cut = new ExtremeValuesValidator();
  }

  @Test
  void trueWhenOkay(){
    LocalTime lower = LocalTime.of(19, 0);
    LocalTime upper = LocalTime.of(8, 30);

    assertTrue(cut.isCorrect(lower, upper));
  }

  @Test
  void trueWhenBorderTime(){
    LocalTime lower = LocalTime.of(8, 0);
    LocalTime upper = LocalTime.of(22, 0);

    assertTrue(cut.isCorrect(lower, upper));
  }

  @Test
  void falseWhenUpperIncorrect(){
    LocalTime lower = LocalTime.of(9, 0);
    LocalTime upper = LocalTime.of(22, 30);

    assertFalse(cut.isCorrect(lower, upper));
  }

  @Test
  void falseWhenLowerIncorrect(){
    LocalTime lower = LocalTime.of(3, 0);
    LocalTime upper = LocalTime.of(21, 30);

    assertFalse(cut.isCorrect(lower, upper));
  }

  @Test
  void trueWhenAnotherLogicIncorrect(){
    LocalTime lower = LocalTime.of(15, 44);
    LocalTime upper = LocalTime.of(16, 7);

    assertTrue(cut.isCorrect(lower, upper));
  }
}