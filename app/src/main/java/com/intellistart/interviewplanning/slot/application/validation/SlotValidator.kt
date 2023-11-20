package com.intellistart.interviewplanning.slot.application.validation

import com.intellistart.interviewplanning.appltication.port.PeriodOperationsInPort
import com.intellistart.interviewplanning.slot.port.SlotOperationsInPort
import com.intellistart.interviewplanning.slot.port.SlotValidatorInPort
import com.intellistart.interviewplanning.slot.domain.exception.SlotException
import com.intellistart.interviewplanning.slot.domain.model.Slot
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDate

@Service
class SlotValidator(
    private val slotService: SlotOperationsInPort,
    private val periodService: PeriodOperationsInPort
) : SlotValidatorInPort {

    override fun validateCreating(slot: Slot, email: String): Mono<Unit> =
        Mono.`when`(
            Mono.fromCallable {
                validateSlotInFuture(slot)
            },
            validateOverlapping(slot, email)
        ).thenReturn(Unit)

    override fun validateUpdating(slot: Slot, email: String): Mono<Unit> =
        Mono.`when`(
            Mono.fromCallable {
                validateSlotInFuture(slot)
            },
            validateSlotIsBookingAndTheSlotExists(slot.id),
            validateOverlapping(slot, email)
        ).thenReturn(Unit)

    private fun validateSlotInFuture(slot: Slot) {
        if (LocalDate.now() > slot.period.date) {
            throw SlotException(SlotException.SlotExceptionProfile.INVALID_BOUNDARIES)
        }
    }

    private fun validateOverlapping(slot: Slot, email: String): Mono<Unit> =
        slotService.getSlotsByEmailAndDate(email, slot.period.date)
            .filter {
                slot.id != it.id && periodService.areOverlapping(slot.period, it.period)
            }.hasElements().handle { overlaps, sink ->
                if (overlaps) {
                    sink.error(SlotException(SlotException.SlotExceptionProfile.SLOT_IS_OVERLAPPING))
                } else {
                    sink.next(Unit)
                }
            }

    private fun validateSlotIsBookingAndTheSlotExists(id: String): Mono<Unit> =
        slotService.getById(id).handle<Unit> { slot, sink ->
            if (slot.bookings.isNotEmpty()) {
                sink.error(SlotException(SlotException.SlotExceptionProfile.SLOT_IS_BOOKED))
            }
        }.thenReturn(Unit)
}
