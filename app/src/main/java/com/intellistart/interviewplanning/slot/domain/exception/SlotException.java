package com.intellistart.interviewplanning.slot.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Exception class for all slot logic exceptions.
 */
@Getter
public class SlotException extends Exception {

  @AllArgsConstructor
  public enum SlotExceptionProfile {

    INVALID_BOUNDARIES("invalid_boundaries",
        "Time boundaries of slot or booking are invalid.", HttpStatus.BAD_REQUEST),


    SLOT_NOT_FOUND("slot_not_found",
        "Slot by given id was not found.", HttpStatus.NOT_FOUND),

    SLOT_IS_BOOKED("slot_is_booked",
        "Slot you are trying to occur is booked.", HttpStatus.BAD_REQUEST),

    SLOT_IS_OVERLAPPING("slot_is_overlapping",
        "Slot overlaps already existed one.", HttpStatus.BAD_REQUEST),

    SLOT_IS_IN_THE_PAST("slot_is_in_the_past",
        "New date for this slot is in the past.", HttpStatus.BAD_REQUEST);

    private final String exceptionName;
    private final String exceptionMessage;
    private final HttpStatus responseStatus;
  }

  private final SlotExceptionProfile slotExceptionProfile;

  public SlotException(SlotException.SlotExceptionProfile slotExceptionProfile) {
    super(slotExceptionProfile.exceptionMessage);
    this.slotExceptionProfile = slotExceptionProfile;
  }

  public String getName() {
    return slotExceptionProfile.exceptionName;
  }

  public HttpStatus getResponseStatus() {
    return slotExceptionProfile.responseStatus;
  }
}
