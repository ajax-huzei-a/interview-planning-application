package com.intellistart.interviewplanning.exceptions.handlers;

import com.intellistart.interviewplanning.exceptions.handlers.dto.ExceptionResponse;
import com.intellistart.interviewplanning.exceptions.UserException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class UserExceptionHandler {

  @ExceptionHandler(value = UserException.class)
  public Mono<ResponseEntity<Object>> handleUserException(UserException exception) {

    var exceptionBody = new ExceptionResponse(exception.getName(), exception.getMessage());

    return Mono.just(ResponseEntity.status(exception.getResponseStatus()).body(exceptionBody));
  }
}
