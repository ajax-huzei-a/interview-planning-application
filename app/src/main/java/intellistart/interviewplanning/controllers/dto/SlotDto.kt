package intellistart.interviewplanning.controllers.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.google.protobuf.Duration
import com.google.protobuf.Timestamp
import intellistart.interviewplanning.commonmodels.slot.SlotProto
import intellistart.interviewplanning.model.slot.Slot
import java.time.LocalDate
import java.time.ZoneOffset

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
    .setDate(
        Timestamp.newBuilder().setSeconds(
            period.date.atStartOfDay(ZoneOffset.UTC).toInstant().epochSecond
        ).setNanos(period.date.atStartOfDay(ZoneOffset.UTC).toInstant().nano)
    )
    .setFrom(Duration.newBuilder().setSeconds(period.from.toSecondOfDay().toLong()).setNanos(0))
    .setTo(Duration.newBuilder().setSeconds(period.to.toSecondOfDay().toLong()).setNanos(0))
    .build()
