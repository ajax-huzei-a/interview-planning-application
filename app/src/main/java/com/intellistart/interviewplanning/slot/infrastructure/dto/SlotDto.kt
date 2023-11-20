package com.intellistart.interviewplanning.slot.infrastructure.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.google.protobuf.Duration
import com.google.type.Date
import com.intellistart.interviewplanning.slot.domain.model.Slot
import java.time.LocalDate
import com.intellistart.interviewplanning.commonmodels.slot.Slot as ProtobufSlot

data class SlotDto(

    val id: String = "",

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val date: LocalDate = LocalDate.now(),

    val from: String = "",

    val to: String = ""
)

fun Slot.toDto(): SlotDto = SlotDto(
    id = id,
    date = period.date,
    from = period.from.toString(),
    to = period.to.toString()
)

fun Slot.toProto() = ProtobufSlot.newBuilder()
    .setId(id)
    .setDate(
        Date.newBuilder()
            .setYear(period.date.year)
            .setMonth(period.date.monthValue)
            .setDay(period.date.dayOfMonth)
    )
    .setFrom(Duration.newBuilder().setSeconds(period.from.toSecondOfDay().toLong()).setNanos(0))
    .setTo(Duration.newBuilder().setSeconds(period.to.toSecondOfDay().toLong()).setNanos(0))
    .build()