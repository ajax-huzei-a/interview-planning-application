package com.intellistart.interviewplanning.exceptions.handlers;

import com.intellistart.interviewplanning.exceptions.handlers.dto.ExceptionResponse;
import com.intellistart.interviewplanning.exceptions.SecurityException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class SecurityExceptionHandler {

  @ExceptionHandler(value = SecurityException.class)
  public Mono<ResponseEntity<Object>> handleSecurityException(SecurityException exception) {

    var exceptionBody = new ExceptionResponse(exception.getName(), exception.getMessage());

    return Mono.just(ResponseEntity.status(exception.getResponseStatus()).body(exceptionBody));
  }
}
