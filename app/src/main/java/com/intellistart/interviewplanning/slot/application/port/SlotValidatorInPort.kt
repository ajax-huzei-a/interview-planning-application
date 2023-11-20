package com.intellistart.interviewplanning.slot.application.port

import com.intellistart.interviewplanning.slot.domain.model.Slot
import reactor.core.publisher.Mono

interface SlotValidatorInPort {

    fun validateCreating(slot: Slot, email: String): Mono<Unit>

    fun validateUpdating(slot: Slot, email: String): Mono<Unit>
}
