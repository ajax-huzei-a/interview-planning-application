package intellistart.interviewplanning.controllers.dto

/**
 * Dto object for representation of all slots and bookings per certain time period.
 */
data class DashboardDto(
    val interviewerSlots: MutableSet<DashboardInterviewerSlotDto>,
    val candidateSlots: MutableSet<DashboardCandidateSlotDto>,
    val bookings: MutableMap<Long, DashboardBookingDto>
)
