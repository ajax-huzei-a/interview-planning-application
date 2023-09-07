package intellistart.interviewplanning.exceptions.handlers;

import intellistart.interviewplanning.exceptions.BookingLimitException;
import intellistart.interviewplanning.exceptions.handlers.dto.ExceptionResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Handler for booking limit's exceptions.
 */
@ControllerAdvice
public class BookingLimitExceptionHandler extends ResponseEntityExceptionHandler {


  /**
   * Method for handling BookingLimitException.
   */
  @ExceptionHandler(value = BookingLimitException.class)
  public ResponseEntity<Object> handleBookingLimitException(
      BookingLimitException exception, WebRequest webRequest) {

    var exceptionBody = new ExceptionResponse(exception.getName(), exception.getMessage());

    return handleExceptionInternal(exception, exceptionBody, new HttpHeaders(),
        exception.getResponseStatus(), webRequest);
  }
}
