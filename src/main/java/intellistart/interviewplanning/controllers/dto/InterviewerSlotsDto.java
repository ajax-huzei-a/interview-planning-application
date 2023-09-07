package intellistart.interviewplanning.controllers.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for list of InterviewerSlotDto.
 */
@Getter
@Setter
@AllArgsConstructor
public class InterviewerSlotsDto {

  private List<InterviewerSlotDto> interviewerSlotDtoList;
}
