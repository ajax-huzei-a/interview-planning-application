package intellistart.interviewplanning.exceptions.handlers.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExceptionResponse {

  private String errorCode;

  private String errorMessage;
}
