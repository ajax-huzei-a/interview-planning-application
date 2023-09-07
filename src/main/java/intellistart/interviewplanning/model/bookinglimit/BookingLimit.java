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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity for storing limit of booking per week for users with Interviewer role.
 */
@Entity
@Table(name = "booking_limits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

  @Override
  public String toString() {
    return "BookingLimit{"
        + "id=" + id
        + ", bookingLimit=" + bookingLimit
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
    BookingLimit that = (BookingLimit) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}