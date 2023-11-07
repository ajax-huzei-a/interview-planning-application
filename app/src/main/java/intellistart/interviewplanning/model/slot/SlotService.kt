package intellistart.interviewplanning.model.slot

import intellistart.interviewplanning.exceptions.SlotException
import intellistart.interviewplanning.exceptions.SlotException.SlotExceptionProfile
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

@Service
class SlotService(
    private val slotRepository: SlotRepository
) {

    fun create(slot: Slot, email: String): Mono<Slot> = slotRepository.save(slot, email)

    fun update(slot: Slot, email: String): Mono<Slot> = slotRepository.update(slot, email)

    fun getSlotsByEmailAndDate(email: String, date: LocalDate): Flux<Slot> =
        slotRepository.findByEmailAndDate(email, date)

    fun getAllSlotsByEmail(email: String): Flux<Slot> = slotRepository.findByEmail(email)

    fun getById(id: ObjectId): Mono<Slot> = slotRepository.findById(id)
        .switchIfEmpty(Mono.error(SlotException(SlotExceptionProfile.SLOT_NOT_FOUND)))
}
