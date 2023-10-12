package intellistart.interviewplanning.model.booking

import intellistart.interviewplanning.exceptions.BookingException
import intellistart.interviewplanning.exceptions.BookingException.BookingExceptionProfile
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class BookingService(
    private val bookingRepository: BookingRepository
) {

    fun getById(id: ObjectId): Booking = bookingRepository.findById(id)
        ?: throw BookingException(BookingExceptionProfile.BOOKING_NOT_FOUND)

    fun create(booking: Booking): Booking {
        return bookingRepository.save(booking)
    }

    fun update(booking: Booking): Booking {
        return bookingRepository.update(booking)
    }

    fun delete(booking: Booking) = bookingRepository.delete(booking)
}
