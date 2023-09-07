package intellistart.interviewplanning.model.bookinglimit;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Embedded entity (complex PK) for BookingLimit entity.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookingLimitKey implements Serializable {

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "week_id")
  private Long weekId;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BookingLimitKey that = (BookingLimitKey) o;
    return Objects.equals(userId, that.userId) && Objects.equals(weekId,
        that.weekId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, weekId);
  }
}
