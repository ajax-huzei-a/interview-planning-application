package intellistart.interviewplanning.controllers.dto;

import intellistart.interviewplanning.model.bookinglimit.BookingLimit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for BookingLimit.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class BookingLimitDto {

  private Long userId;
  private Long weekNum;
  private Integer bookingLimit;

  /**
   * Constructor for BookingLimitDto.
   *
   * @param bookingLimit - booking limit
   */
  public BookingLimitDto(BookingLimit bookingLimit) {
    this.setUserId(bookingLimit.getUser().getId());
    this.setWeekNum(bookingLimit.getWeek().getId());
    this.setBookingLimit(bookingLimit.getBookingLimit());
  }
}
