package intellistart.interviewplanning.controllers.dto;

import intellistart.interviewplanning.model.booking.Booking;
import intellistart.interviewplanning.model.candidateslot.CandidateSlot;
import intellistart.interviewplanning.model.period.Period;
import java.time.LocalTime;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class DashboardCandidateSlotDtoTest {

  private static Arguments[] createTestArgs() {
    return new Arguments[]{
        Arguments.of(new CandidateSlot(
              1L, null,
              new Period(2L, LocalTime.of(10, 0), LocalTime.of(20, 0), null, null, null),
              Set.of(),
              "email@test.com", "candidate name"
            ),
            new DashboardCandidateSlotDto(1L, LocalTime.of(10, 0).toString(), LocalTime.of(20, 0).toString(),
                "email@test.com", "candidate name", Set.of())
        ),

        Arguments.of(new CandidateSlot(
                1L, null,
                new Period(2L, LocalTime.of(21, 30), LocalTime.of(23, 0), null, null, null),
                Set.of(
                    new Booking(1L, null, null, null, null, null),
                    new Booking(2L, null, null, null, null, null),
                    new Booking(3L, null, null, null, null, null),
                    new Booking(4L, null, null, null, null, null),
                    new Booking(5L, null, null, null, null, null)
                ),
                "candEmail@slot.test", "name name name"
            ),
            new DashboardCandidateSlotDto(1L, LocalTime.of(21, 30).toString(), LocalTime.of(23, 0).toString(),
                "candEmail@slot.test", "name name name", Set.of(1L, 2L, 3L, 4L, 5L))
        )
    };
  }

  @ParameterizedTest
  @MethodSource("createTestArgs")
  void initializeCandidateSlotDto(CandidateSlot rawSlot, DashboardCandidateSlotDto expected) {

    DashboardCandidateSlotDto actual = DashboardCandidateSlotDtoKt.toDTOForDashboard(rawSlot);
    Assertions.assertEquals(expected, actual);
  }
}
