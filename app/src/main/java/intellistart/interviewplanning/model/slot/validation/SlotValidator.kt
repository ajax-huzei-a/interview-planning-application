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
        Mono.`when`(
            Mono.fromCallable {
                validateSlotInFuture(slot)
            },
            validateOverlapping(slot, email)
        ).thenReturn(Unit)

    fun validateUpdating(slot: Slot, email: String): Mono<Unit> =
        Mono.`when`(
            Mono.fromCallable {
                validateSlotInFuture(slot)
            },
            validateSlotIsBookingAndTheSlotExists(slot.id),
            validateOverlapping(slot, email)
        ).thenReturn(Unit)

    private fun validateSlotInFuture(slot: Slot) {
        if (LocalDate.now() > slot.period.date) {
            throw SlotException(SlotExceptionProfile.INVALID_BOUNDARIES)
        }
    }

    private fun validateOverlapping(slot: Slot, email: String): Mono<Unit> =
        slotService.getSlotsByEmailAndDate(email, slot.period.date)
            .filter {
                slot.id != it.id && periodService.areOverlapping(slot.period, it.period)
            }.hasElements().flatMap {
                Unit.toMono()
            }.switchIfEmpty { SlotException(SlotExceptionProfile.SLOT_IS_OVERLAPPING).toMono() }// TODO fix this method

    private fun validateSlotIsBookingAndTheSlotExists(id: ObjectId): Mono<Unit> =
        slotService.getById(id).handle<Unit> { slot, sink ->
            if (slot.bookings.isNotEmpty()) {
                sink.error(SlotException(SlotExceptionProfile.SLOT_IS_BOOKED))
            }
        }.thenReturn(Unit)
}
