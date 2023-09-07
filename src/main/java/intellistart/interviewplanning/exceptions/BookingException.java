package intellistart.interviewplanning.exceptions;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Exception class for all bookings exceptions.
 */
public class BookingException extends Exception {

  /**
   * Enum that describes type of exception.
   */
  @AllArgsConstructor
  public enum BookingExceptionProfile {

    BOOKING_NOT_FOUND("booking_not_found",
        "Booking by given id was not found.", HttpStatus.NOT_FOUND),

    INVALID_SUBJECT("invalid_subject",
        "Provided subject is invalid", HttpStatus.BAD_REQUEST),

    INVALID_DESCRIPTION("invalid_description",
        "Provided description is invalid.", HttpStatus.BAD_REQUEST),

    SLOTS_NOT_INTERSECTING("slots_not_intersecting",
        "Provided slots have not free joint time period.", HttpStatus.BAD_REQUEST);

    private final String exceptionName;
    private final String exceptionMessage;
    private final HttpStatus responseStatus;
  }

  private final BookingExceptionProfile bookingExceptionProfile;

  public BookingException(BookingExceptionProfile bookingExceptionProfile) {
    super(bookingExceptionProfile.exceptionMessage);
    this.bookingExceptionProfile = bookingExceptionProfile;
  }

  public String getName() {
    return bookingExceptionProfile.exceptionName;
  }

  public HttpStatus getResponseStatus() {
    return bookingExceptionProfile.responseStatus;
  }
}
