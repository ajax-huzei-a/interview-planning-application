package intellistart.interviewplanning.exceptions.handlers;

import intellistart.interviewplanning.exceptions.SlotException;
import intellistart.interviewplanning.exceptions.handlers.dto.ExceptionResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Handler for slot's exceptions.
 */
@ControllerAdvice
public class SlotExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * Method for handling SecurityException.
   */
  @ExceptionHandler(value = SlotException.class)
  public ResponseEntity<Object> handleSlotException(SlotException exception,
      WebRequest webRequest) {

    var exceptionBody = new ExceptionResponse(exception.getName(), exception.getMessage());

    return handleExceptionInternal(exception, exceptionBody, new HttpHeaders(),
        exception.getResponseStatus(), webRequest);
  }
}
