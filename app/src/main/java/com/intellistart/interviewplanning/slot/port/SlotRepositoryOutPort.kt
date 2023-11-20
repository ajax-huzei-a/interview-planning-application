package com.intellistart.interviewplanning.slot.port

import com.intellistart.interviewplanning.slot.domain.model.Slot
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

interface SlotRepositoryOutPort {

    fun findByEmail(email: String): Flux<Slot>

    fun findById(id: String): Mono<Slot>

    fun findByEmailAndDate(email: String, date: LocalDate): Flux<Slot>

    fun save(slot: Slot, email: String): Mono<Slot>

    fun update(slot: Slot, email: String): Mono<Slot>
}
