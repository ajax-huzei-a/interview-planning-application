package intellistart.interviewplanning.exceptions;

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

    CANNOT_EDIT_THIS_WEEK("cannot_edit_this_week",
        "This week can't be edited.", HttpStatus.BAD_REQUEST),

    INVALID_BOUNDARIES("invalid_boundaries",
        "Time boundaries of slot or booking are invalid.", HttpStatus.BAD_REQUEST),

    INVALID_DAY_OF_WEEK("invalid_day_of_week",
        "Cannot arrange booking on this day.", HttpStatus.BAD_REQUEST),

    CANDIDATE_SLOT_NOT_FOUND("candidate_slot_not_found",
        "Candidate slot by given id was not found.", HttpStatus.NOT_FOUND),

    INTERVIEWER_SLOT_NOT_FOUND("interviewer_slot_not_found",
        "Interviewer slot by given id was not found.", HttpStatus.NOT_FOUND),

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

  private final SlotException.SlotExceptionProfile slotExceptionProfile;

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
