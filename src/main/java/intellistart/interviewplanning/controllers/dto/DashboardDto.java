package intellistart.interviewplanning.controllers.dto;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dto object for representation of all slots and bookings per certain time period.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DashboardDto {

  private Set<DashboardInterviewerSlotDto> interviewerSlots = new HashSet<>();
  private Set<DashboardCandidateSlotDto> candidateSlots = new HashSet<>();
  private Map<Long, DashboardBookingDto> bookings = new HashMap<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DashboardDto that = (DashboardDto) o;
    return Objects.equals(interviewerSlots, that.interviewerSlots)
        && Objects.equals(candidateSlots, that.candidateSlots) && Objects.equals(
        bookings, that.bookings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(interviewerSlots, candidateSlots, bookings);
  }
}

