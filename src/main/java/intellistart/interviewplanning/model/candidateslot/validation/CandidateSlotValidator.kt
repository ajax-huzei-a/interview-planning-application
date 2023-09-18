package intellistart.interviewplanning.model.candidateslot.validation

import intellistart.interviewplanning.exceptions.SlotException
import intellistart.interviewplanning.exceptions.SlotException.SlotExceptionProfile
import intellistart.interviewplanning.model.candidateslot.CandidateSlot
import intellistart.interviewplanning.model.candidateslot.CandidateSlotService
import intellistart.interviewplanning.model.period.PeriodService
import org.springframework.stereotype.Service
import java.time.LocalDate

/**
 * Validator for CandidateSlot.
 */
@Service
class CandidateSlotValidator(
    private val candidateSlotService: CandidateSlotService,
    private val periodService: PeriodService
) {

    /**
     * Validate CandidateSlot object for what the slot should be in the future,
     * whether the slot is not overlapping.
     *
     * @param candidateSlot - the slot that we will validate.
     *
     * @throws SlotException - when parameters are incorrect or slot is overlapping.
     */
    fun validateCreating(candidateSlot: CandidateSlot) {
        validateSlotInFuture(candidateSlot)
        validateOverlapping(candidateSlot)
    }

    /**
     * Validate CandidateSlot object for all slot creation checks,
     * whether the slot exists, whether the slot is not booking.
     *
     * @param candidateSlot - the updated slot that we will validate.
     *
     * @throws SlotException - when parameters are incorrect or updated slot is booked
     *     slot is overlapping.
     */
    fun validateUpdating(candidateSlot: CandidateSlot) {
        validateSlotIsBookingAndTheSlotExists(candidateSlot.id)
        validateSlotInFuture(candidateSlot)
        validateOverlapping(candidateSlot)
    }

    private fun validateSlotInFuture(candidateSlot: CandidateSlot) {
        if (LocalDate.now() > candidateSlot.date) {
            throw SlotException(SlotExceptionProfile.INVALID_BOUNDARIES)
        }
    }

    private fun validateOverlapping(candidateSlot: CandidateSlot) {
        val candidateSlotList = candidateSlotService.getCandidateSlotsByEmailAndDate(
            candidateSlot.email,
            candidateSlot.date
        )

        if (candidateSlotList.isNotEmpty()) {
            for (item in candidateSlotList) {
                if (candidateSlot.id != item.id &&
                    periodService.areOverlapping(candidateSlot.period, item.period)
                ) {
                    throw SlotException(SlotExceptionProfile.SLOT_IS_OVERLAPPING)
                }
            }
        }
    }

    private fun validateSlotIsBookingAndTheSlotExists(id: Long) {
        val candidateSlot = candidateSlotService.getById(id)

        if (candidateSlot.bookings.isNotEmpty()) {
            throw SlotException(SlotExceptionProfile.SLOT_IS_BOOKED)
        }
    }
}
