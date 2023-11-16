package com.intellistart.interviewplanning.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Exception class for all slot logic exceptions.
 */
@Getter
public class PeriodException extends Exception {

  @AllArgsConstructor
  public enum PeriodExceptionProfile {

    INVALID_BOUNDARIES("invalid_boundaries",
        "Time boundaries of period are invalid.", HttpStatus.BAD_REQUEST),

    PERIOD_IS_IN_THE_PAST("period_is_in_the_past",
        "New date for this period is in the past.", HttpStatus.BAD_REQUEST);

    private final String exceptionName;
    private final String exceptionMessage;
    private final HttpStatus responseStatus;
  }

  private final PeriodExceptionProfile periodExceptionProfile;

  public PeriodException(PeriodExceptionProfile periodExceptionProfile) {
    super(periodExceptionProfile.exceptionMessage);
    this.periodExceptionProfile = periodExceptionProfile;
  }

  public String getName() {
    return periodExceptionProfile.exceptionName;
  }

  public HttpStatus getResponseStatus() {
    return periodExceptionProfile.responseStatus;
  }
}
