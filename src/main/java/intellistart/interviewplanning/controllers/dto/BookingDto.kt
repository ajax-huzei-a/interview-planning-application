package intellistart.interviewplanning.controllers.dto

import intellistart.interviewplanning.model.booking.Booking

/**
 * Booking data transfer object from RestController.
 */
data class BookingDto(
    var interviewerSlotId: Long = 0,
    var candidateSlotId: Long = 0,
    var from: String = "",
    var to: String = "",
    var subject: String = "",
    var description: String = ""
)

fun Booking.toDto(): BookingDto = BookingDto(
    interviewerSlotId = interviewerSlot.id,
    candidateSlotId = candidateSlot.id,
    from = period.from.toString(),
    to = period.to.toString(),
    subject = subject,
    description = description
)
