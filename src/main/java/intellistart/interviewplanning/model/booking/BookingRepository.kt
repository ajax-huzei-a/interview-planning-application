package intellistart.interviewplanning.model.booking

import org.springframework.stereotype.Repository

@Repository
class BookingRepository {

    fun findById(id: Long): Booking? = TODO()

    fun delete(booking: Booking): Unit = TODO()

    fun save(booking: Booking): Booking = TODO()
    fun update(booking: Booking): Booking {
        TODO("Not yet implemented")
    }
}
