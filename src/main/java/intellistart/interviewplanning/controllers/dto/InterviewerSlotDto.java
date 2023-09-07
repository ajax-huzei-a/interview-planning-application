package intellistart.interviewplanning.controllers.dto;

import intellistart.interviewplanning.model.interviewerslot.InterviewerSlot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

/**
 * Class for Interviewer Slot DTO.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class InterviewerSlotDto {

  private Long interviewerId;
  private Long interviewerSlotId;
  private Long week;
  @NonNull
  private String dayOfWeek;
  @NonNull
  private String from;
  @NonNull
  private String to;

  /**
   * Constructor.
   * Constructs the InterviewerSlotDto from InterviewerSlot object.
   */
  public InterviewerSlotDto(InterviewerSlot interviewerSlot) {
    this.interviewerId = interviewerSlot.getUser().getId();
    this.interviewerSlotId = interviewerSlot.getId();
    this.week = interviewerSlot.getWeek().getId();
    this.dayOfWeek = interviewerSlot.getDayOfWeek().toString();
    this.from = interviewerSlot.getPeriod().getFrom().toString();
    this.to = interviewerSlot.getPeriod().getTo().toString();
  }
}
