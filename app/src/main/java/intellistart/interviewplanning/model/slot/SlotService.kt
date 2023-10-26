package intellistart.interviewplanning.model.slot

import intellistart.interviewplanning.exceptions.SlotException
import intellistart.interviewplanning.exceptions.SlotException.SlotExceptionProfile
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class SlotService(
    private val slotRepository: SlotRepository
) {

    fun create(slot: Slot, email: String): Slot = slotRepository.save(slot, email)

    fun update(slot: Slot, email: String): Slot = slotRepository.update(slot, email)

    fun getSlotsByEmailAndDate(email: String, date: LocalDate): List<Slot> =
        slotRepository.findByEmailAndDate(email, date)

    fun getAllSlotsByEmail(email: String): List<Slot> = slotRepository.findByEmail(email)

    fun getById(id: ObjectId): Slot = slotRepository.findById(id)
        ?: (throw SlotException(SlotExceptionProfile.SLOT_NOT_FOUND))
}
