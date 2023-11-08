package intellistart.interviewplanning.model.slot.validation

import intellistart.interviewplanning.exceptions.SlotException
import intellistart.interviewplanning.exceptions.SlotException.SlotExceptionProfile
import intellistart.interviewplanning.model.period.PeriodService
import intellistart.interviewplanning.model.slot.Slot
import intellistart.interviewplanning.model.slot.SlotService
import org.bson.types.ObjectId
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDate

@Service
@Lazy
class SlotValidator(
    private val slotService: SlotService,
    private val periodService: PeriodService
) {

    fun validateCreating(slot: Slot, email: String): Mono<Unit> =
        validateSlotInFuture(slot)
            .then(validateOverlapping(slot, email))

    fun validateUpdating(slot: Slot, email: String) =
        validateSlotIsBookingAndTheSlotExists(slot.id)
            .then(validateSlotInFuture(slot))
            .then(validateOverlapping(slot, email))

    private fun validateSlotInFuture(slot: Slot): Mono<Unit> =
        if (LocalDate.now() > slot.period.date) {
            Mono.error(SlotException(SlotExceptionProfile.INVALID_BOUNDARIES))
        } else {
            Unit.toMono()
        }

    private fun validateOverlapping(slot: Slot, email: String): Mono<Unit> =
        slotService.getSlotsByEmailAndDate(email, slot.period.date)
            .filter {
                slot.id != it.id && periodService.areOverlapping(slot.period, it.period)
            }.hasElements().flatMap {
                Unit.toMono()
            }.switchIfEmpty { SlotException(SlotExceptionProfile.SLOT_IS_OVERLAPPING).toMono() }

    private fun validateSlotIsBookingAndTheSlotExists(id: ObjectId): Mono<Unit> =
        slotService.getById(id).flatMap { slot ->
            if (slot.bookings.isNotEmpty()) {
                Mono.error(SlotException(SlotExceptionProfile.SLOT_IS_BOOKED))
            } else {
                Unit.toMono()
            }
        }
}
