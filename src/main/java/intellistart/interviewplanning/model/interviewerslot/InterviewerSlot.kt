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
import lombok.NoArgsConstructor;

/**
 * InterviewerSlot entity.
 */
@Entity
@Table(name = "interviewer_slots")
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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Week getWeek() {
    return week;
  }

  public void setWeek(Week week) {
    this.week = week;
  }

  public DayOfWeek getDayOfWeek() {
    return dayOfWeek;
  }

  public void setDayOfWeek(DayOfWeek dayOfWeek) {
    this.dayOfWeek = dayOfWeek;
  }

  public Period getPeriod() {
    return period;
  }

  public void setPeriod(Period period) {
    this.period = period;
  }

  public Set<Booking> getBookings() {
    return bookings;
  }

  public void setBookings(Set<Booking> bookings) {
    this.bookings = bookings;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
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
