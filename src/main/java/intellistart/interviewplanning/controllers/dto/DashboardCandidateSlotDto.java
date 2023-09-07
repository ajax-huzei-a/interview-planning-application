package intellistart.interviewplanning.controllers.dto;

import intellistart.interviewplanning.model.booking.Booking;
import intellistart.interviewplanning.model.candidateslot.CandidateSlot;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dto object for mapping {@link CandidateSlot} into a part of Dashboard.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DashboardCandidateSlotDto {

  private Long candidateSlotId;
  private String from;
  private String to;
  private String candidateEmail;
  private String candidateName;
  private Set<Long> bookings;

  /**
   * Constructor for DashboardBookingDto initialization from
   * CandidateSlot object.
   *
   * @param candidateSlot object to initialize from
   */
  public DashboardCandidateSlotDto(CandidateSlot candidateSlot) {
    this.candidateSlotId = candidateSlot.getId();
    this.from = candidateSlot.getPeriod().getFrom().toString();
    this.to = candidateSlot.getPeriod().getTo().toString();
    this.candidateEmail = candidateSlot.getEmail();
    this.candidateName = candidateSlot.getName();

    this.bookings = candidateSlot.getBookings().stream()
        .map(Booking::getId)
        .collect(Collectors.toSet());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DashboardCandidateSlotDto that = (DashboardCandidateSlotDto) o;
    return Objects.equals(from, that.from)
        && Objects.equals(to, that.to) && Objects.equals(candidateEmail,
        that.candidateEmail) && Objects.equals(candidateName, that.candidateName)
        && Objects.equals(bookings, that.bookings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(from, to, candidateEmail, candidateName, bookings);
  }
}
