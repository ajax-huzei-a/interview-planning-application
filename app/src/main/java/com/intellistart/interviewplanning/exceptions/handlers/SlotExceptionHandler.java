package com.intellistart.interviewplanning.exceptions.handlers;

import com.intellistart.interviewplanning.exceptions.handlers.dto.ExceptionResponse;
import com.intellistart.interviewplanning.slot.domain.exception.SlotException;
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
