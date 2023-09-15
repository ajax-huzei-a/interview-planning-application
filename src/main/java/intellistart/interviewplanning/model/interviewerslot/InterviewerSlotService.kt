package intellistart.interviewplanning.model.interviewerslot

import intellistart.interviewplanning.exceptions.SlotException
import intellistart.interviewplanning.exceptions.SlotException.SlotExceptionProfile
import intellistart.interviewplanning.model.booking.BookingService
import intellistart.interviewplanning.model.dayofweek.DayOfWeek
import intellistart.interviewplanning.model.user.User
import intellistart.interviewplanning.model.week.Week
import org.springframework.stereotype.Service

/**
 * Service for InterviewSlot entity.
 */
@Service
class InterviewerSlotService(
    private val interviewerSlotRepository: InterviewerSlotRepository,
    private val bookingService: BookingService
) {

    /**
     * Find instance of InterviewerSlot by id in database.
     *
     * @throws SlotException if no instance with given id
     */
    fun getById(id: Long): InterviewerSlot = interviewerSlotRepository.findById(id)
        .orElseThrow { SlotException(SlotExceptionProfile.INTERVIEWER_SLOT_NOT_FOUND) }

    /**
     * Get InterviewerSlot and save it in the DB.
     *
     * @param interviewerSlot - interviewerSlot
     * @return InterviewerSlot
     */
    fun create(interviewerSlot: InterviewerSlot): InterviewerSlot = interviewerSlotRepository.save(interviewerSlot)

    /**
     * Get InterviewerSlot and update it in the DB.
     *
     * @param interviewerSlot - interviewerSlot
     * @return InterviewerSlot
     */
    fun update(interviewerSlot: InterviewerSlot): InterviewerSlot = create(interviewerSlot)

    /**
     * Method deletes all slots of the given user,
     * before deleting a slot it deletes all bookings in the slots being deleted.
     *
     * @param user - user by which slots are deleted.
     */
    fun deleteSlotsByUser(user: User) {
        val interviewerSlots = interviewerSlotRepository.findInterviewerSlotsByUser(user)

        interviewerSlots.forEach { interviewerSlot ->
            if (interviewerSlot.bookings.isNotEmpty()) {
                bookingService.deleteBookings(interviewerSlot.bookings)
            }
        }

        interviewerSlotRepository.deleteAll(interviewerSlots)
    }

    /**
     * Get slots of user by [Week].
     *
     * @param week object to get slots with
     * @return [Set] of [InterviewerSlot]
     */
    fun getSlotsByWeek(week: Week): Set<InterviewerSlot> = interviewerSlotRepository.findByWeek(week)

    /**
     * Get slots of user by weekId.
     *
     * @param userEmail - userEmail
     * @param weekId - weekId
     * @return [List] of [InterviewerSlot]
     */
    fun getSlotsByWeek(userEmail: String, weekId: Long): List<InterviewerSlot> =
        interviewerSlotRepository.getInterviewerSlotsByUserEmailAndWeekId(userEmail, weekId)

    /**
     * Alias for [InterviewerSlotRepository] method.
     */
    fun getInterviewerSlotsByUserAndWeekAndDayOfWeek(
        user: User,
        week: Week,
        dayOfWeek: DayOfWeek
    ): List<InterviewerSlot> =
        interviewerSlotRepository.getInterviewerSlotsByUserIdAndWeekIdAndDayOfWeek(user.id, week.id, dayOfWeek)

    /**
     * Alias for [InterviewerSlotRepository] method.
     */
    fun getInterviewerSlotsByUserAndWeek(user: User, week: Week): List<InterviewerSlot> =
        interviewerSlotRepository.getInterviewerSlotsByUserIdAndWeekId(user.id, week.id)
}
