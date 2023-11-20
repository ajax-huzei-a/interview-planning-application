package com.intellistart.interviewplanning.slot.application.port

import com.intellistart.interviewplanning.slot.domain.model.Slot
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

interface SlotOperationsInPort {

    fun create(slot: Slot, email: String): Mono<Slot>

    fun update(slot: Slot, email: String): Mono<Slot>

    fun getSlotsByEmailAndDate(email: String, date: LocalDate): Flux<Slot>

    fun getAllSlotsByEmail(email: String): Flux<Slot>

    fun getById(id: String): Mono<Slot>
}
