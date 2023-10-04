package intellistart.interviewplanning.controllers.dto

import com.fasterxml.jackson.annotation.JsonFormat
import intellistart.interviewplanning.model.booking.Booking
import java.time.LocalDate

data class BookingDto(

    var interviewerSlotId: Long = 0,

    var candidateSlotId: Long = 0,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    var date: LocalDate = LocalDate.now(),

    var from: String = "",

    var to: String = "",

    var subject: String = "",

    var description: String = ""
)

fun Booking.toDto(): BookingDto = BookingDto(
    interviewerSlotId = interviewerSlotId,
    candidateSlotId = candidateSlotId,
    date = period.date,
    from = period.from.toString(),
    to = period.to.toString(),
    subject = subject,
    description = description
)
