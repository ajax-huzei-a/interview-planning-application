package intellistart.interviewplanning.model.interviewerslot;


import com.fasterxml.jackson.annotation.JsonIgnore;
import intellistart.interviewplanning.model.booking.Booking;
import intellistart.interviewplanning.model.dayofweek.DayOfWeek;
import intellistart.interviewplanning.model.period.Period;
import intellistart.interviewplanning.model.user.User;
import intellistart.interviewplanning.model.week.Week;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
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
 * InterviewerSlot entity.
 */
@Entity
@Table(name = "interviewer_slots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterviewerSlot {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "interviewer_slot_id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "week_id")
  private Week week;

  @Enumerated
  private DayOfWeek dayOfWeek;

  @ManyToOne
  @JoinColumn(name = "period_id")
  private Period period;

  @OneToMany(mappedBy = "interviewerSlot")
  @JsonIgnore
  private Set<Booking> bookings = new HashSet<>();

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  public void addBooking(Booking booking) {
    bookings.add(booking);
  }

  @Override
  public String toString() {
    return "InterviewerSlot{"
        + "id=" + id
        + ", week=" + week.getId()
        + ", dayOfWeek=" + dayOfWeek
        + ", period=" + period
        + ", user=" + user.getId()
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
    InterviewerSlot that = (InterviewerSlot) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
