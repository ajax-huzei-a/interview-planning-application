package intellistart.interviewplanning.controllers.dto;

import intellistart.interviewplanning.model.booking.Booking;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Booking data transfer object from RestController.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BookingDto {

  private Long bookingId;
  private Long interviewerSlotId;
  private Long candidateSlotId;
  private String from;
  private String to;
  private String subject;
  private String description;

  /**
   * Constructor.
   */
  public BookingDto(Booking booking) {
    this.bookingId = booking.getId();
    this.candidateSlotId = booking.getCandidateSlot().getId();
    this.interviewerSlotId = booking.getInterviewerSlot().getId();

    this.from = booking.getPeriod().getFrom().toString();
    this.to = booking.getPeriod().getTo().toString();

    this.subject = booking.getSubject();
    this.description = booking.getDescription();
  }

  @Override
  public String toString() {
    return "BookingDto{"
        + "interviewerSlotId=" + interviewerSlotId
        + ", candidateSlotId=" + candidateSlotId
        + ", from='" + from + '\''
        + ", to='" + to + '\''
        + ", subject='" + subject + '\''
        + ", description='" + description + '\''
        + '}';
  }
}
