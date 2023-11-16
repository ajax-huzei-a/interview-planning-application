package com.intellistart.interviewplanning.exceptions.handlers;

import com.intellistart.interviewplanning.domain.exception.PeriodException;
import com.intellistart.interviewplanning.exceptions.handlers.dto.ExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class PeriodExceptionHandler {

  @ExceptionHandler(value = PeriodException.class)
  public Mono<ResponseEntity<Object>> handleSlotException(PeriodException exception) {

    var exceptionBody = new ExceptionResponse(exception.getName(), exception.getMessage());

    return Mono.just(ResponseEntity.status(exception.getResponseStatus()).body(exceptionBody));
  }
}
