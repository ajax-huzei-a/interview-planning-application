package intellistart.interviewplanning.controllers.dto

import com.fasterxml.jackson.annotation.JsonFormat
import intellistart.interviewplanning.model.slot.Slot
import java.time.LocalDate

data class SlotDto(

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    var date: LocalDate = LocalDate.now(),

    var from: String = "",

    var to: String = ""
)

fun Slot.toDto(): SlotDto = SlotDto(
    date = period.date,
    from = period.from.toString(),
    to = period.to.toString()
)