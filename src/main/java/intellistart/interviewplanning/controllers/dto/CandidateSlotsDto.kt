package intellistart.interviewplanning.controllers.dto

import intellistart.interviewplanning.model.candidateslot.CandidateSlot

/**
 * DTO for list of CandidateSlotsDto.
 */
data class CandidateSlotsDto(val candidateSlotDtoList: List<CandidateSlotDto>)

fun List<CandidateSlot>.toDTOList():CandidateSlotsDto = CandidateSlotsDto(map { it.toDTO() })

