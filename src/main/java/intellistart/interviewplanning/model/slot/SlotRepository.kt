package intellistart.interviewplanning.model.slot

import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class SlotRepository {
    fun findByEmail(email: String): List<Slot> = TODO()

    fun findByEmailAndDate(email: String, date: LocalDate): List<Slot> = TODO()

    fun findById(id: Long): Slot? = TODO()

    fun save(slot: Slot, email: String): Slot {
        TODO("Not yet implemented")
    }

    fun update(slot: Slot, email: String): Slot {
        TODO("Not yet implemented")
    }
}
