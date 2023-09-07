package intellistart.interviewplanning.controllers.dto;

import intellistart.interviewplanning.model.booking.Booking;
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlot;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dto object for mapping {@link InterviewerSlot} into a part of Dashboard.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DashboardInterviewerSlotDto {

  private String from;
  private String to;
  private Long interviewerSlotId;
  private Long interviewerId;
  private Set<Long> bookings;

  /**
   * Constructor for DashboardBookingDto initialization from
   * InterviewerSlot object.
   *
   * @param interviewerSlot object to initialize from
   */
  public DashboardInterviewerSlotDto(InterviewerSlot interviewerSlot) {
    this.interviewerSlotId = interviewerSlot.getId();
    this.interviewerId = interviewerSlot.getUser().getId();
    this.from = interviewerSlot.getPeriod().getFrom().toString();
    this.to = interviewerSlot.getPeriod().getTo().toString();

    this.bookings = interviewerSlot.getBookings().stream()
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
    DashboardInterviewerSlotDto that = (DashboardInterviewerSlotDto) o;
    return Objects.equals(interviewerSlotId, that.interviewerSlotId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(interviewerSlotId);
  }
}
