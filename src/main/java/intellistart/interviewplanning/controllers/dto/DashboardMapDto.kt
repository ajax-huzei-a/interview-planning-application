package intellistart.interviewplanning.controllers.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import intellistart.interviewplanning.model.candidateslot.CandidateSlot
import intellistart.interviewplanning.model.dayofweek.DayOfWeek.FRI
import intellistart.interviewplanning.model.dayofweek.DayOfWeek.MON
import intellistart.interviewplanning.model.dayofweek.DayOfWeek.SAT
import intellistart.interviewplanning.model.dayofweek.DayOfWeek.SUN
import intellistart.interviewplanning.model.dayofweek.DayOfWeek.THU
import intellistart.interviewplanning.model.dayofweek.DayOfWeek.TUE
import intellistart.interviewplanning.model.dayofweek.DayOfWeek.WED
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlot
import intellistart.interviewplanning.model.week.WeekService
import java.time.LocalDate

/**
 * Dto object for representation all candidate, interviewer slots
 * and bookings for a certain week.
 */
data class DashboardMapDto(
    @JsonIgnore
    val weekService: WeekService,
    val weekNum: Long,
    val dashboard: Map<LocalDate, DashboardDto>
) {
    /**
     * Constructor to initialize object with given weekNum.
     * Needs [WeekService] instance to access weeks logic
     * (gaining date from week number and day, etc).
     *
     * @param weekNum number of week
     * @param weekService instance of service object to access logic from
     */
    constructor(weekNum: Long, weekService: WeekService) : this(
        weekService,
        weekNum,
        mapOf<LocalDate, DashboardDto>(
            weekService.convertToLocalDate(weekNum, MON) to DashboardDto(HashSet(), HashSet(), HashMap()),
            weekService.convertToLocalDate(weekNum, TUE) to DashboardDto(HashSet(), HashSet(), HashMap()),
            weekService.convertToLocalDate(weekNum, WED) to DashboardDto(HashSet(), HashSet(), HashMap()),
            weekService.convertToLocalDate(weekNum, THU) to DashboardDto(HashSet(), HashSet(), HashMap()),
            weekService.convertToLocalDate(weekNum, FRI) to DashboardDto(HashSet(), HashSet(), HashMap()),
            weekService.convertToLocalDate(weekNum, SAT) to DashboardDto(HashSet(), HashSet(), HashMap()),
            weekService.convertToLocalDate(weekNum, SUN) to DashboardDto(HashSet(), HashSet(), HashMap())
        )
    )

    /**
     * Mapping all given interviewer slots to inner map
     * within all bookings from them.
     *
     * @param interviewerSlots set of InterviewerSlot objects to map information from
     */
    fun addInterviewerSlots(interviewerSlots: Set<InterviewerSlot>) {
        interviewerSlots.forEach { interviewerSlot ->
            val date = weekService.convertToLocalDate(weekNum, interviewerSlot.dayOfWeek)
            dashboard[date]!!
                .interviewerSlots.add(interviewerSlot.toDtoForDashboard())
            val bookingDtoMap: Map<Long, DashboardBookingDto> = interviewerSlot.bookings
                .associate { it.id to it.toDtoForDashboard() }
            dashboard[date]!!
                .bookings.putAll(bookingDtoMap)
        }
    }

    /**
     * Mapping all given candidate slots to inner map
     * within all bookings from them.
     *
     * @param candidateSlots set of CandidateSlot objects to map information from
     */
    fun addCandidateSlots(candidateSlots: Set<CandidateSlot>) {
        candidateSlots.forEach { candidateSlot ->
            val date = candidateSlot.date
            dashboard[date]!!.candidateSlots.add(candidateSlot.toDtoForDashboard())
            val bookingDtoMap = candidateSlot.bookings.associate { it.id to it.toDtoForDashboard() }
            dashboard[date]!!
                .bookings.putAll(bookingDtoMap)
        }
    }
}
