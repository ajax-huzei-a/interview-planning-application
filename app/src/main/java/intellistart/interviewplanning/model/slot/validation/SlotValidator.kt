package intellistart.interviewplanning.model.slot.validation

import intellistart.interviewplanning.exceptions.SlotException
import intellistart.interviewplanning.exceptions.SlotException.SlotExceptionProfile
import intellistart.interviewplanning.model.period.PeriodService
import intellistart.interviewplanning.model.slot.Slot
import intellistart.interviewplanning.model.slot.SlotService
import org.bson.types.ObjectId
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
@Lazy
class SlotValidator(
    private val slotService: SlotService,
    private val periodService: PeriodService
) {

    fun validateCreating(slot: Slot, email: String) {
        validateSlotInFuture(slot)
        validateOverlapping(slot, email)
    }

    fun validateUpdating(slot: Slot, email: String) {
        validateSlotIsBookingAndTheSlotExists(slot.id)
        validateSlotInFuture(slot)
        validateOverlapping(slot, email)
    }

    private fun validateSlotInFuture(slot: Slot) {
        if (LocalDate.now() > slot.period.date) {
            throw SlotException(SlotExceptionProfile.INVALID_BOUNDARIES)
        }
    }

    private fun validateOverlapping(slot: Slot, email: String) {
        val slotList = slotService.getSlotsByEmailAndDate(
            email,
            slot.period.date
        )

        if (slotList.isNotEmpty()) {
            for (item in slotList) {
                if (slot.id != item.id &&
                    periodService.areOverlapping(slot.period, item.period)
                ) {
                    throw SlotException(SlotExceptionProfile.SLOT_IS_OVERLAPPING)
                }
            }
        }
    }

    private fun validateSlotIsBookingAndTheSlotExists(id: ObjectId) {
        val slot = slotService.getById(id)

        if (slot.bookings.isNotEmpty()) {
            throw SlotException(SlotExceptionProfile.SLOT_IS_BOOKED)
        }
    }
}
