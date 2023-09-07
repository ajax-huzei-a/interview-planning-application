package intellistart.interviewplanning.controllers.dto;

import intellistart.interviewplanning.model.booking.Booking;
import intellistart.interviewplanning.model.candidateslot.CandidateSlot;
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlot;
import intellistart.interviewplanning.model.period.Period;
import java.time.LocalTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class DashboardBookingDtoTest {

  static Arguments[] createTestArgs() {
    return new Arguments[]{
        Arguments.arguments(new Booking(11L, "Test", "Test test",
            new InterviewerSlot(12L, null, null, null, null, null),
            new CandidateSlot(13L, null, null, null, null, null),
            new Period(1L, LocalTime.of(10, 0), LocalTime.of(20, 30), null, null, null)
            ),

            new DashboardBookingDto(11L, "Test", "Test test", 12L, 13L,
                LocalTime.of(10, 0).toString(), LocalTime.of(20, 30).toString())
        ),

        Arguments.arguments(new Booking(12345L, "test sub", "test desc",
                new InterviewerSlot(78L, null, null, null, null, null),
                new CandidateSlot(90L, null, null, null, null, null),
                new Period(1L, LocalTime.of(16, 30), LocalTime.of(22, 30), null, null, null)
            ),

            new DashboardBookingDto(12345L, "test sub", "test desc", 78L, 90L,
                LocalTime.of(16, 30).toString(), LocalTime.of(22, 30).toString())
        )
    };
  }

  @ParameterizedTest
  @MethodSource("createTestArgs")
  void initializeBookingDto(Booking rawBooking, DashboardBookingDto expected) {

    DashboardBookingDto actual = new DashboardBookingDto(rawBooking);
    Assertions.assertEquals(expected, actual);
  }
}
