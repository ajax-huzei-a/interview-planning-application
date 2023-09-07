package intellistart.interviewplanning.controllers.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for list of CandidateSlotsDto.
 */
@Getter
@Setter
@NoArgsConstructor
public class CandidateSlotsDto {
  private List<CandidateSlotDto> candidateSlotDtoList;
}
