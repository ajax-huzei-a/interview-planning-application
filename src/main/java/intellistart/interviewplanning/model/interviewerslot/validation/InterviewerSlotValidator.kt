package intellistart.interviewplanning.model.interviewerslot.validation

import intellistart.interviewplanning.exceptions.SlotException
import intellistart.interviewplanning.exceptions.UserException
import intellistart.interviewplanning.model.dayofweek.DayOfWeek
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlot
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlotService
import intellistart.interviewplanning.model.period.PeriodService
import intellistart.interviewplanning.model.user.Role
import intellistart.interviewplanning.model.user.User
import intellistart.interviewplanning.model.week.Week
import intellistart.interviewplanning.model.week.WeekService
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class InterviewerSlotValidator(
    private val periodService: PeriodService,
    private val interviewerSlotService: InterviewerSlotService,
    private val weekService: WeekService
) {

    fun validateCreating(interviewerSlot: InterviewerSlot) {
        validateIfCorrectDay(interviewerSlot.dayOfWeek.toString())

        validateIfCanEditThisWeek(interviewerSlot.week)

        validateIfCorrectTime(interviewerSlot)

        validateIfInterviewerRoleInterviewer(interviewerSlot.user)

        validateIfPeriodIsOverlapping(interviewerSlot)

        interviewerSlot.week.interviewerSlots.add(interviewerSlot)
    }

    fun validateUpdating(
        interviewerSlot: InterviewerSlot,
        slotId: Long
    ) {
        interviewerSlot.id = slotId

        validateIfCorrectDay(interviewerSlot.dayOfWeek.toString())

        validateIfCanEditThisWeek(interviewerSlot.week)

        validateIfCorrectTime(interviewerSlot)

        validateIfSlotExist(slotId)

        validateIfInterviewerRoleInterviewer(interviewerSlot.user)

        validateIfPeriodIsOverlapping(interviewerSlot)

        validateIfSlotIsBooked(interviewerSlotService.getById(slotId))

        interviewerSlot.week.interviewerSlots.add(interviewerSlot)
    }

    private fun validateIfSlotExist(slotId: Long) {
        interviewerSlotService.getById(slotId)
    }

    private fun validateIfSlotIsBooked(interviewerSlot: InterviewerSlot) {
        if (interviewerSlot.bookings.isNotEmpty()) {
            throw SlotException(SlotException.SlotExceptionProfile.SLOT_IS_BOOKED)
        }
    }

    private fun validateIfInterviewerRoleInterviewer(user: User) {
        if (user.role != Role.INTERVIEWER) {
            throw UserException(UserException.UserExceptionProfile.INVALID_INTERVIEWER)
        }
    }

    private fun validateIfCorrectDay(dayOfWeek: String) {
        if (
            dayOfWeek == DayOfWeek.SUN.name ||
            dayOfWeek == DayOfWeek.SAT.name ||
            !DayOfWeek.entries.any { it.name == dayOfWeek }
        ) {
            throw SlotException(SlotException.SlotExceptionProfile.INVALID_DAY_OF_WEEK)
        }
    }

    private fun validateIfCorrectTime(interviewerSlot: InterviewerSlot) {
        val date = weekService.convertToLocalDate(interviewerSlot.week.id, interviewerSlot.dayOfWeek)
        if (LocalDate.now().isAfter(LocalDate.from(date))) {
            throw SlotException(SlotException.SlotExceptionProfile.SLOT_IS_IN_THE_PAST)
        }
    }

    private fun validateIfCanEditThisWeek(week: Week) {
        val currentWeek = weekService.getCurrentWeek()
        if (week.id <= currentWeek.id) {
            throw SlotException(SlotException.SlotExceptionProfile.CANNOT_EDIT_THIS_WEEK)
        }
    }

    private fun validateIfPeriodIsOverlapping(interviewerSlot: InterviewerSlot) {
        var interviewerSlotsList = interviewerSlotService.getInterviewerSlotsByUserAndWeekAndDayOfWeek(
            interviewerSlot.user,
            interviewerSlot.week,
            interviewerSlot.dayOfWeek
        )

        if (interviewerSlotsList.isNotEmpty()) {
            if (interviewerSlot.id != 0L) {
                interviewerSlotsList = interviewerSlotsList
                    .filter { it.id != interviewerSlot.id }
            }

            for (temp in interviewerSlotsList) {
                if (periodService.areOverlapping(temp.period, interviewerSlot.period)) {
                    throw SlotException(SlotException.SlotExceptionProfile.SLOT_IS_OVERLAPPING)
                }
            }
        }
    }
}
