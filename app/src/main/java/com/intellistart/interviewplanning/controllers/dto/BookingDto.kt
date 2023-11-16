package com.intellistart.interviewplanning.controllers.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.intellistart.interviewplanning.model.booking.Booking
import java.time.LocalDate

data class BookingDto(

    val id: String = "",

    val interviewerSlotId: String = "",

    val candidateSlotId: String = "",

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val date: LocalDate = LocalDate.now(),

    val from: String = "",

    val to: String = "",

    val subject: String = "",

    val description: String = ""
)

fun Booking.toDto(): BookingDto =
    BookingDto(
        id = id.toHexString(),
        interviewerSlotId = interviewerSlotId.toHexString(),
        candidateSlotId = candidateSlotId.toHexString(),
        date = period.date,
        from = period.from.toString(),
        to = period.to.toString(),
        subject = subject,
        description = description
    )
