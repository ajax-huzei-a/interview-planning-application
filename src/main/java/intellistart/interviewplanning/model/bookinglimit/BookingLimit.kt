package intellistart.interviewplanning.model.bookinglimit;

import intellistart.interviewplanning.model.user.User;
import intellistart.interviewplanning.model.week.Week;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

/**
 * Entity for storing limit of booking per week for users with Interviewer role.
 */
@Entity
@Table(name = "booking_limits")
public class BookingLimit {

  @EmbeddedId
  private BookingLimitKey id;

  @Column(name = "booking_limit")
  private Integer bookingLimit;

  @ManyToOne
  @MapsId("userId")
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @MapsId("weekId")
  @JoinColumn(name = "week_id")
  private Week week;

  /**
   * Constructor.
   *
   * @param id - id
   * @param bookingLimit - bookingLimit
   * @param user - user
   * @param week - week
   */
  public BookingLimit(BookingLimitKey id, Integer bookingLimit, User user, Week week) {
    this.id = id;
    this.bookingLimit = bookingLimit;
    this.user = user;
    this.week = week;
  }

  public BookingLimit() {
  }

  @Override
  public String toString() {
    return "BookingLimit{"
            + "id=" + id
            + ", bookingLimit=" + bookingLimit
            + '}';
  }

  public BookingLimitKey getId() {
    return id;
  }

  public void setId(BookingLimitKey id) {
    this.id = id;
  }

  public Integer getBookingLimit() {
    return bookingLimit;
  }

  public void setBookingLimit(Integer bookingLimit) {
    this.bookingLimit = bookingLimit;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Week getWeek() {
    return week;
  }

  public void setWeek(Week week) {
    this.week = week;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BookingLimit that = (BookingLimit) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}