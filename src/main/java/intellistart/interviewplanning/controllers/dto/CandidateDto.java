package intellistart.interviewplanning.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for user with CANDIDATE role.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CandidateDto {

  private String email;
  private String role;

  public CandidateDto(String email) {
    this(email, "CANDIDATE");
  }
}
