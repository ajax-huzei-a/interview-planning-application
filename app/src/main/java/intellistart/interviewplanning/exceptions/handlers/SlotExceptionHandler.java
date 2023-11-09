package intellistart.interviewplanning.exceptions.handlers;

import intellistart.interviewplanning.exceptions.SlotException;
import intellistart.interviewplanning.exceptions.handlers.dto.ExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class SlotExceptionHandler {

  @ExceptionHandler(value = SlotException.class)
  public Mono<ResponseEntity<Object>> handleSlotException(SlotException exception) {

    var exceptionBody = new ExceptionResponse(exception.getName(), exception.getMessage());

    return Mono.just(ResponseEntity.status(exception.getResponseStatus()).body(exceptionBody));
  }
}
