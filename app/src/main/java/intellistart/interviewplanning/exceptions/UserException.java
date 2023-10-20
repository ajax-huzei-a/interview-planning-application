package intellistart.interviewplanning.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserException extends Exception {

  @AllArgsConstructor
  public enum UserExceptionProfile {

      INVALID_INTERVIEWER("interviewer_not_found",
              "Invalid interviewer's id in the path.", HttpStatus.NOT_FOUND),

      SELF_REVOKING("self_revoking",
              "The user cannot change or delete himself.", HttpStatus.BAD_REQUEST),

      USER_ALREADY_HAS_ROLE("user_already_has_role",
              "This user already has another role.", HttpStatus.BAD_REQUEST),

      USER_NOT_FOUND("user_not_found",
              "User not found.", HttpStatus.NOT_FOUND),

      NOT_INTERVIEWER("not_interviewer",
              "Provided user is not interviewer.", HttpStatus.BAD_REQUEST),

      NOT_COORDINATOR("not_coordinator",
          "Provided user is not coordinator.", HttpStatus.BAD_REQUEST);

    private final String exceptionName;
    private final String exceptionMessage;
    private final HttpStatus responseStatus;
  }

  private final UserException.UserExceptionProfile userExceptionProfile;

  public UserException(UserExceptionProfile userExceptionProfile) {
    super(userExceptionProfile.exceptionMessage);
    this.userExceptionProfile = userExceptionProfile;
  }

  public String getName() {
    return userExceptionProfile.exceptionName;
  }

  public HttpStatus getResponseStatus() {
    return userExceptionProfile.responseStatus;
  }
}
