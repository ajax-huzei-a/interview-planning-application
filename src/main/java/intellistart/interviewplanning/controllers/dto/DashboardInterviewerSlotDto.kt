package intellistart.interviewplanning.controllers.dto

import intellistart.interviewplanning.model.interviewerslot.InterviewerSlot

/**
 * Dto object for mapping [InterviewerSlot] into a part of Dashboard.
 */
data class DashboardInterviewerSlotDto(
    val from: String,
    val to: String,
    val interviewerSlotId: Long,
    val interviewerId: Long,
    val bookings: Set<Long>
)

fun InterviewerSlot.toDTOForDashboard():DashboardInterviewerSlotDto = DashboardInterviewerSlotDto(
    interviewerSlotId = id,
    interviewerId = user.id,
    from = period.from.toString(),
    to = period.to.toString(),
    bookings = bookings.map { it.id }.toSet()
)
