package intellistart.interviewplanning.model.candidateslot;

import intellistart.interviewplanning.model.booking.Booking;
import intellistart.interviewplanning.model.period.Period;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * CandidateSlot entity.
 */
@Entity
@Table(name = "candidate_slots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandidateSlot {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "candidate_slot_id")
  private Long id;

  private LocalDate date;

  @ManyToOne
  @JoinColumn(name = "period_id")
  private Period period;

  @OneToMany(mappedBy = "candidateSlot")
  private Set<Booking> bookings = new HashSet<>();

  private String email;
  private String name;

  public void addBooking(Booking booking) {
    bookings.add(booking);
  }

  @Override
  public String toString() {
    return "CandidateSlot{"
        + "id=" + id
        + ", date=" + date
        + ", period=" + period
        + ", email='" + email
        + '\'' + ", name='"
        + name + '\'' + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CandidateSlot that = (CandidateSlot) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
