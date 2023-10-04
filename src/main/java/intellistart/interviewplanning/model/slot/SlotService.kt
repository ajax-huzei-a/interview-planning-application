package intellistart.interviewplanning.model.slot

import intellistart.interviewplanning.exceptions.SlotException
import intellistart.interviewplanning.exceptions.SlotException.SlotExceptionProfile
import intellistart.interviewplanning.model.slot.validation.SlotValidator
import intellistart.interviewplanning.security.JwtUserDetails
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class SlotService(
    private val slotRepository: SlotRepository,
    private val slotValidator: SlotValidator
) {

    fun create(slot: Slot, authentication: Authentication): Slot {
        slotValidator.validateCreating(slot, authentication)
        val jwtUserDetails = authentication.principal as JwtUserDetails
        return slotRepository.save(slot, jwtUserDetails.email)
    }

    fun update(slot: Slot, authentication: Authentication): Slot {
        slotValidator.validateUpdating(slot, authentication)
        val jwtUserDetails = authentication.principal as JwtUserDetails
        return slotRepository.update(slot, jwtUserDetails.email)
    }

    fun getAllSlotsByEmail(email: String): List<Slot> = slotRepository.findByEmail(email)

    fun getSlotsByEmailAndDate(email: String, date: LocalDate): List<Slot> =
        slotRepository.findByEmailAndDate(email, date)

    fun getById(id: Long): Slot = slotRepository.findById(id)
        ?: (throw SlotException(SlotExceptionProfile.CANDIDATE_SLOT_NOT_FOUND))
}
