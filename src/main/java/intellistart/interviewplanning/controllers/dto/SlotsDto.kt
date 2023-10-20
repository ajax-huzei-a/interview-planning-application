package intellistart.interviewplanning.controllers.dto

import intellistart.interviewplanning.model.slot.Slot

data class SlotsDto(
    val slots: List<SlotDto> = listOf()
)

fun List<Slot>.toDtoList(): SlotsDto = SlotsDto(map { it.toDto() })
