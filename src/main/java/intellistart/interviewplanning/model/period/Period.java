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
import lombok.NoArgsConstructor;

/**
 * Entity for period of time.
 */
@Entity
@Table(name = "periods")
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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public LocalTime getFrom() {
    return from;
  }

  public void setFrom(LocalTime from) {
    this.from = from;
  }

  public LocalTime getTo() {
    return to;
  }

  public void setTo(LocalTime to) {
    this.to = to;
  }

  public Set<InterviewerSlot> getInterviewerSlots() {
    return interviewerSlots;
  }

  public void setInterviewerSlots(Set<InterviewerSlot> interviewerSlots) {
    this.interviewerSlots = interviewerSlots;
  }

  public Set<CandidateSlot> getCandidateSlots() {
    return candidateSlots;
  }

  public void setCandidateSlots(Set<CandidateSlot> candidateSlots) {
    this.candidateSlots = candidateSlots;
  }

  public Set<Booking> getBookings() {
    return bookings;
  }

  public void setBookings(Set<Booking> bookings) {
    this.bookings = bookings;
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
