package intellistart.interviewplanning.exceptions.handlers;

import intellistart.interviewplanning.exceptions.UserException;
import intellistart.interviewplanning.exceptions.handlers.dto.ExceptionResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Handler for user's exceptions.
 */
@ControllerAdvice
public class UserExceptionHandler extends ResponseEntityExceptionHandler {

  /**
  * Method for handling UserException.
  */
  @ExceptionHandler(value = UserException.class)
  public ResponseEntity<Object> handleUserException(UserException exception,
      WebRequest webRequest) {

    var exceptionBody = new ExceptionResponse(exception.getName(), exception.getMessage());

    return handleExceptionInternal(exception, exceptionBody, new HttpHeaders(),
            exception.getResponseStatus(), webRequest);
  }
}
