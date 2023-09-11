package intellistart.interviewplanning.controllers.dto;

import intellistart.interviewplanning.model.booking.Booking;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dto object for mapping {@link Booking} into a part of Dashboard.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DashboardBookingDto {

  private Long bookingId;
  private String subject;
  private String description;
  private Long interviewerSlotId;
  private Long candidateSlotId;
  private String from;
  private String to;

  /**
   * Constructor for DashboardBookingDto initialization from
   * Booking object.
   *
   * @param booking object to initialize from
   */
  public DashboardBookingDto(Booking booking) {
    this.bookingId = booking.getId();
    this.subject = booking.getSubject();
    this.description = booking.getDescription();
    this.interviewerSlotId = booking.getInterviewerSlot().getId();
    this.candidateSlotId = booking.getCandidateSlot().getId();
    this.from = booking.getPeriod().getFrom().toString();
    this.to = booking.getPeriod().getTo().toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DashboardBookingDto that = (DashboardBookingDto) o;
    return Objects.equals(bookingId, that.bookingId) && Objects.equals(subject,
        that.subject) && Objects.equals(description, that.description)
        && Objects.equals(interviewerSlotId, that.interviewerSlotId)
        && Objects.equals(candidateSlotId, that.candidateSlotId)
        && Objects.equals(from, that.from) && Objects.equals(to, that.to);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingId, subject, description, interviewerSlotId, candidateSlotId, from,
        to);
  }
}
