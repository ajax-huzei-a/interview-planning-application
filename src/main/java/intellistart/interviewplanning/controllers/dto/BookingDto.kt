package intellistart.interviewplanning.controllers.dto

import com.fasterxml.jackson.annotation.JsonFormat
import intellistart.interviewplanning.model.booking.Booking
import java.time.LocalDate

data class BookingDto(

    var id: String = "",

    var interviewerSlotId: String = "",

    var candidateSlotId: String = "",

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    var date: LocalDate = LocalDate.now(),

    var from: String = "",

    var to: String = "",

    var subject: String = "",

    var description: String = ""
)

fun Booking.toDto(): BookingDto = BookingDto(
    id = id.toHexString(),
    interviewerSlotId = interviewerSlotId.toHexString(),
    candidateSlotId = candidateSlotId.toHexString(),
    date = period.date,
    from = period.from.toString(),
    to = period.to.toString(),
    subject = subject,
    description = description
)
