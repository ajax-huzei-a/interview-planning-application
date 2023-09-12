package intellistart.interviewplanning.controllers.dto

import intellistart.interviewplanning.model.interviewerslot.InterviewerSlot

/**
 * DTO for list of InterviewerSlotDto.
 */
data class InterviewerSlotsDto(val interviewerSlotDtoList: List<InterviewerSlotDtoResponse>)

fun List<InterviewerSlot>.toDtoList(): InterviewerSlotsDto = InterviewerSlotsDto(map { it.toDtoResponse() })
