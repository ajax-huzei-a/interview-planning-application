package intellistart.interviewplanning.controllers.dto;

import intellistart.interviewplanning.model.booking.Booking;
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlot;
import intellistart.interviewplanning.model.period.Period;
import intellistart.interviewplanning.model.user.User;
import java.time.LocalTime;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class DashboardInterviewerSlotDtoTest {

  private static Arguments[] createTestArgs() {
    return new Arguments[]{
        Arguments.of(new InterviewerSlot(15L, null, null,
                new Period(null, LocalTime.of(10, 30), LocalTime.of(20, 30), null, null, null),
                Set.of(), new User(10L, null, null)),

            new DashboardInterviewerSlotDto(LocalTime.of(10, 30).toString(),
                LocalTime.of(20, 30).toString(),
                15L, 10L, Set.of())
        ),

        Arguments.of(new InterviewerSlot(15L, null, null,
                new Period(null, LocalTime.of(10, 30), LocalTime.of(20, 30), null, null, null),
                Set.of(
                    new Booking(6L, null, null, null, null, null),
                    new Booking(7L, null, null, null, null, null),
                    new Booking(8L, null, null, null, null, null),
                    new Booking(9L, null, null, null, null, null),
                    new Booking(0L, null, null, null, null, null)
                ),
                new User(10L, null, null)),

            new DashboardInterviewerSlotDto(LocalTime.of(10, 30).toString(),
                LocalTime.of(20, 30).toString(),
                15L, 10L, Set.of(6L, 7L, 8L, 9L, 0L))
        )

    };
  }

  @ParameterizedTest
  @MethodSource("createTestArgs")
  void initializeInterviewerSlotDto(InterviewerSlot rawSlot, DashboardInterviewerSlotDto expected) {

    DashboardInterviewerSlotDto actual = DashboardInterviewerSlotDtoKt.toDTOForDashboard(rawSlot);
    Assertions.assertEquals(expected, actual);
  }
}
