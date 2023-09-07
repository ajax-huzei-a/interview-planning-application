package intellistart.interviewplanning.model.period;

import com.fasterxml.jackson.annotation.JsonIgnore;
import intellistart.interviewplanning.model.booking.Booking;
import intellistart.interviewplanning.model.candidateslot.CandidateSlot;
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlot;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity for period of time.
 */
@Entity
@Table(name = "periods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Period {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "period_id")
  private Long id;

  @Column(name = "period_from")
  private LocalTime from;

  @Column(name = "period_to")
  private LocalTime to;

  @OneToMany(mappedBy = "period")
  @JsonIgnore
  private Set<InterviewerSlot> interviewerSlots = new HashSet<>();

  @OneToMany(mappedBy = "period")
  @JsonIgnore
  private Set<CandidateSlot> candidateSlots = new HashSet<>();

  @OneToMany(mappedBy = "period")
  @JsonIgnore
  private Set<Booking> bookings = new HashSet<>();


  public void addInterviewerSlot(InterviewerSlot interviewerSlot) {
    interviewerSlots.add(interviewerSlot);
  }

  public void addCandidateSlot(CandidateSlot candidateSlot) {
    candidateSlots.add(candidateSlot);
  }

  public void addBooking(Booking booking) {
    bookings.add(booking);
  }

  @Override
  public String toString() {
    return "Period{"
        + "id=" + id
        + ", from=" + from
        + ", to=" + to
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
    Period period = (Period) o;
    return Objects.equals(id, period.id);

  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
