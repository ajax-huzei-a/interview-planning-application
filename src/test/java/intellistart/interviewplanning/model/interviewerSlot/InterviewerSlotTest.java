package intellistart.interviewplanning.model.interviewerSlot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import intellistart.interviewplanning.model.dayofweek.DayOfWeek;
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlot;
import intellistart.interviewplanning.model.period.Period;
import intellistart.interviewplanning.model.user.Role;
import intellistart.interviewplanning.model.user.User;
import intellistart.interviewplanning.model.week.Week;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InterviewerSlotTest {

  @Test
  void toStringTest() {
    String actual = is1.toString();
    String expect = "InterviewerSlot{id=1, week=45, "
        + "dayOfWeek=THU, period=Period{id=1, from=10:00, "
        + "to=20:00}, user=1}";
    assertEquals(expect, actual);
  }

  @Test
  void equalsTest() {
    Assertions.assertTrue(is1.equals(is2));
  }

  @Test
  void hashCodeTest() {
    int expect = is1.hashCode();
    int actual = Objects.hash(1);
    assertEquals(expect, actual);

  }

  static User u1 = new User(1L, "interviewer@gmail.com", Role.INTERVIEWER);
  static Period p1 = new Period(1L, LocalTime.of(10, 0), LocalTime.of(20, 0),
      new HashSet<>(), new HashSet<>(), new HashSet<>());
  static Week w1 = new Week(45L, new HashSet<>());
  static InterviewerSlot is1 = new InterviewerSlot(1L, w1, DayOfWeek.THU, p1, new HashSet<>(), u1);
  static InterviewerSlot is2 = new InterviewerSlot(1L, w1, DayOfWeek.THU, p1, new HashSet<>(), u1);


}
