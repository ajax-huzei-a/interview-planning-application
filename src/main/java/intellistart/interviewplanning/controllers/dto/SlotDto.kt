package intellistart.interviewplanning.controllers.dto

import com.fasterxml.jackson.annotation.JsonFormat
import intellistart.interviewplanning.model.slot.Slot
import java.time.LocalDate

data class SlotDto(

    val id: String,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val date: LocalDate,

    val from: String,

    val to: String
)

fun Slot.toDto(): SlotDto = SlotDto(
    id = id.toHexString(),
    date = period.date,
    from = period.from.toString(),
    to = period.to.toString()
)
