package intellistart.interviewplanning.controllers.dto;

import intellistart.interviewplanning.model.booking.Booking;
import intellistart.interviewplanning.model.candidateslot.CandidateSlot;
import intellistart.interviewplanning.model.dayofweek.DayOfWeek;
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlot;
import intellistart.interviewplanning.model.period.Period;
import intellistart.interviewplanning.model.user.Role;
import intellistart.interviewplanning.model.user.User;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import intellistart.interviewplanning.model.week.Week;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class DashboardInterviewerSlotDtoTest {

  private static Arguments[] createTestArgs() {
    return new Arguments[]{
        Arguments.of(new InterviewerSlot(15L, new Week(), DayOfWeek.MON,
                new Period(0L, LocalTime.of(10, 30), LocalTime.of(20, 30), new HashSet<>(), new HashSet<>(), new HashSet<>()),
                Set.of(), new User(10L, "null", Role.COORDINATOR)),

            new DashboardInterviewerSlotDto(LocalTime.of(10, 30).toString(),
                LocalTime.of(20, 30).toString(),
                15L, 10L, Set.of())
        ),

        Arguments.of(new InterviewerSlot(15L, new Week(), DayOfWeek.MON,
                new Period(0L, LocalTime.of(10, 30), LocalTime.of(20, 30), new HashSet<>(), new HashSet<>(), new HashSet<>()),
                Set.of(
                    new Booking(6L, "null", "null", new InterviewerSlot(), new CandidateSlot(), new Period()),
                    new Booking(7L, "null", "null", new InterviewerSlot(), new CandidateSlot(), new Period()),
                    new Booking(8L, "null", "null", new InterviewerSlot(), new CandidateSlot(), new Period()),
                    new Booking(9L, "null", "null", new InterviewerSlot(), new CandidateSlot(), new Period()),
                    new Booking(0L, "null", "null", new InterviewerSlot(), new CandidateSlot(), new Period())
                ),
                new User(10L, "null", Role.COORDINATOR)),

            new DashboardInterviewerSlotDto(LocalTime.of(10, 30).toString(),
                LocalTime.of(20, 30).toString(),
                15L, 10L, Set.of(6L, 7L, 8L, 9L, 0L))
        )

    };
  }

  @ParameterizedTest
  @MethodSource("createTestArgs")
  void initializeInterviewerSlotDto(InterviewerSlot rawSlot, DashboardInterviewerSlotDto expected) {

    DashboardInterviewerSlotDto actual = DashboardInterviewerSlotDtoKt.toDtoForDashboard(rawSlot);
    Assertions.assertEquals(expected, actual);
  }
}
