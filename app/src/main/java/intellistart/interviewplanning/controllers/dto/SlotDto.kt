package intellistart.interviewplanning.controllers.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.google.protobuf.Duration
import com.google.type.Date
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

fun Slot.toProto() = intellistart.interviewplanning.commonmodels.slot.Slot.newBuilder()
    .setId(id.toHexString())
    .setDate(
        Date.newBuilder()
            .setYear(period.date.year)
            .setMonth(period.date.monthValue)
            .setDay(period.date.dayOfMonth)
    )
    .setFrom(Duration.newBuilder().setSeconds(period.from.toSecondOfDay().toLong()).setNanos(0))
    .setTo(Duration.newBuilder().setSeconds(period.to.toSecondOfDay().toLong()).setNanos(0))
    .build()
