package intellistart.interviewplanning.exceptions;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Exception class for all booking limit exceptions.
 */
public class BookingLimitException extends Exception {

  /**
   * Enum that describes type of exception.
   */
  @AllArgsConstructor
  public enum BookingLimitExceptionProfile {

    INVALID_BOOKING_LIMIT("invalid_booking_limit",
        "Value of booking limit is not correct.", HttpStatus.BAD_REQUEST),
    BOOKING_LIMIT_IS_EXCEEDED("booking_limit_is_exceeded",
        "Interviewer isn't allowed to have more bookings", HttpStatus.BAD_REQUEST);

    private final String exceptionName;
    private final String exceptionMessage;
    private final HttpStatus responseStatus;
  }

  private final BookingLimitException.BookingLimitExceptionProfile bookingLimitExceptionProfile;

  public BookingLimitException(BookingLimitExceptionProfile bookingLimitExceptionProfile) {
    super(bookingLimitExceptionProfile.exceptionMessage);
    this.bookingLimitExceptionProfile = bookingLimitExceptionProfile;
  }

  public String getName() {
    return bookingLimitExceptionProfile.exceptionName;
  }

  public HttpStatus getResponseStatus() {
    return bookingLimitExceptionProfile.responseStatus;
  }
}
