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
import lombok.NoArgsConstructor;

/**
 * Booking entity.
 */
@Entity
@Table(name = "bookings")
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

  public void setId(Long id) {
    this.id = id;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setInterviewerSlot(InterviewerSlot interviewerSlot) {
    this.interviewerSlot = interviewerSlot;
  }

  public void setCandidateSlot(CandidateSlot candidateSlot) {
    this.candidateSlot = candidateSlot;
  }

  public void setPeriod(Period period) {
    this.period = period;
  }

  public Long getId() {
    return id;
  }

  public String getSubject() {
    return subject;
  }

  public String getDescription() {
    return description;
  }

  public InterviewerSlot getInterviewerSlot() {
    return interviewerSlot;
  }

  public CandidateSlot getCandidateSlot() {
    return candidateSlot;
  }

  public Period getPeriod() {
    return period;
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