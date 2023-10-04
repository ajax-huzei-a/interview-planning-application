package intellistart.interviewplanning.model.slot.validation

import intellistart.interviewplanning.exceptions.SlotException
import intellistart.interviewplanning.exceptions.SlotException.SlotExceptionProfile
import intellistart.interviewplanning.model.period.PeriodService
import intellistart.interviewplanning.model.slot.Slot
import intellistart.interviewplanning.model.slot.SlotService
import intellistart.interviewplanning.security.JwtUserDetails
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class SlotValidator(
    private val slotService: SlotService,
    private val periodService: PeriodService
) {

    fun validateCreating(slot: Slot, authentication: Authentication) {
        validateSlotInFuture(slot)
        validateOverlapping(slot, authentication)
    }

    fun validateUpdating(slot: Slot, authentication: Authentication) {
        validateSlotIsBookingAndTheSlotExists(slot.id)
        validateSlotInFuture(slot)
        validateOverlapping(slot, authentication)
    }

    private fun validateSlotInFuture(slot: Slot) {
        if (LocalDate.now() > slot.period.date) {
            throw SlotException(SlotExceptionProfile.INVALID_BOUNDARIES)
        }
    }

    private fun validateOverlapping(slot: Slot, authentication: Authentication) {
        val jwtUserDetails = authentication.principal as JwtUserDetails

        val candidateSlotList = slotService.getSlotsByEmailAndDate(
            jwtUserDetails.email,
            slot.period.date
        )

        if (candidateSlotList.isNotEmpty()) {
            for (item in candidateSlotList) {
                if (slot.id != item.id &&
                    periodService.areOverlapping(slot.period, item.period)
                ) {
                    throw SlotException(SlotExceptionProfile.SLOT_IS_OVERLAPPING)
                }
            }
        }
    }

    private fun validateSlotIsBookingAndTheSlotExists(id: Long) {
        val candidateSlot = slotService.getById(id)

        if (candidateSlot.bookings.isNotEmpty()) {
            throw SlotException(SlotExceptionProfile.SLOT_IS_BOOKED)
        }
    }
}
