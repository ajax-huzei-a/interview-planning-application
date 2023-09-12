package intellistart.interviewplanning.controllers.dto

import intellistart.interviewplanning.model.booking.Booking

/**
 * Dto object for mapping [Booking] into a part of Dashboard.
 */
data class DashboardBookingDto (
    var bookingId: Long,
    var subject: String,
    var description: String,
    var interviewerSlotId: Long,
    var candidateSlotId: Long,
    var from: String,
    var to: String,
)

fun Booking.toDtoForDashboard() = DashboardBookingDto(
    bookingId = id,
    subject = subject,
    description = description,
    interviewerSlotId = interviewerSlot.id,
    candidateSlotId = candidateSlot.id,
    from = period.from.toString(),
    to = period.to.toString()
)
