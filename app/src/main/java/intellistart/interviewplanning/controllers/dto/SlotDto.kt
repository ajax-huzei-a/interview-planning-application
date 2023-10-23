package intellistart.interviewplanning.controllers.dto

import com.fasterxml.jackson.annotation.JsonFormat
import intellistart.interviewplanning.commonmodels.slot.SlotProto
import intellistart.interviewplanning.model.slot.Slot
import java.time.LocalDate

data class SlotDto(

    val id: String = "",

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val date: LocalDate = LocalDate.now(),

    val from: String = "",

    val to: String = ""
)

fun Slot.toDto(): SlotDto = SlotDto(
    id = id.toHexString(),
    date = period.date,
    from = period.from.toString(),
    to = period.to.toString()
)

fun Slot.toProto(): SlotProto = SlotProto.newBuilder()
    .setId(id.toHexString())
    .setDate(period.date.toString())
    .setFrom(period.from.toString())
    .setTo(period.to.toString())
    .build()
