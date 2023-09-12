package intellistart.interviewplanning.controllers.dto

import intellistart.interviewplanning.model.candidateslot.CandidateSlot

/**
 * Dto object for mapping [CandidateSlot] into a part of Dashboard.
 */
data class DashboardCandidateSlotDto(
    val candidateSlotId: Long,
    val from: String,
    val to: String,
    val candidateEmail: String,
    val candidateName: String,
    val bookings: Set<Long>
)

fun CandidateSlot.toDtoForDashboard(): DashboardCandidateSlotDto = DashboardCandidateSlotDto(
    candidateSlotId = id,
    from = period.from.toString(),
    to = period.to.toString(),
    candidateEmail = email,
    candidateName = name,
    bookings = bookings.map { it.id }.toSet()
)
