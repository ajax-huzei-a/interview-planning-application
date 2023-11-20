package com.intellistart.interviewplanning.slot.application.service

import com.intellistart.interviewplanning.slot.domain.exception.SlotException
import com.intellistart.interviewplanning.slot.domain.model.Slot
import com.intellistart.interviewplanning.slot.port.ProducerOutPort
import com.intellistart.interviewplanning.slot.port.SlotOperationsInPort
import com.intellistart.interviewplanning.slot.port.SlotRepositoryOutPort
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDate

@Service
class SlotOperations(
    private val slotRepository: SlotRepositoryOutPort,
    private val slotKafkaProducer: ProducerOutPort
) : SlotOperationsInPort {

    override fun create(slot: Slot, email: String): Mono<Slot> = slotRepository.save(slot, email)

    override fun update(slot: Slot, email: String): Mono<Slot> =
        slotRepository.update(slot, email)
            .doOnNext {
                slotKafkaProducer.produceSlotNotificationToKafka(it, it.id)
            }

    override fun getSlotsByEmailAndDate(email: String, date: LocalDate): Flux<Slot> =
        slotRepository.findByEmailAndDate(email, date)

    override fun getAllSlotsByEmail(email: String): Flux<Slot> = slotRepository.findByEmail(email)

    override fun getById(id: String): Mono<Slot> = slotRepository.findById(id)
        .switchIfEmpty {
            SlotException(SlotException.SlotExceptionProfile.SLOT_NOT_FOUND)
                .toMono()
        }
}
