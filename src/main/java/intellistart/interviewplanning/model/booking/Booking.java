package intellistart.interviewplanning.model.booking;

import intellistart.interviewplanning.model.candidateslot.CandidateSlot;
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlot;
import intellistart.interviewplanning.model.period.Period;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Booking entity.
 */
@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "booking_id")
  private Long id;

  private String subject;

  private String description;

  @ManyToOne
  @JoinColumn(name = "interviewer_slot_id")
  private InterviewerSlot interviewerSlot;

  @ManyToOne
  @JoinColumn(name = "candidate_slot_id")
  private CandidateSlot candidateSlot;

  @ManyToOne
  @JoinColumn(name = "period_id")
  private Period period;


  @Override
  public String toString() {
    return "Booking{"
        + "id=" + id
        + ", period=" + period
        + ", subject='" + subject + '\''
        + ", description='" + description + '\''
        + ", interviewerSlot=" + interviewerSlot.getId()
        + ", candidateSlot=" + candidateSlot.getId()
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Booking booking = (Booking) o;
    return Objects.equals(id, booking.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}