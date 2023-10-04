package intellistart.interviewplanning.model.booking

import intellistart.interviewplanning.exceptions.BookingException
import intellistart.interviewplanning.exceptions.BookingException.BookingExceptionProfile
import intellistart.interviewplanning.model.booking.validation.BookingValidator
import org.springframework.stereotype.Service

@Service
class BookingService(
    private val bookingRepository: BookingRepository,
    private val bookingValidator: BookingValidator
) {

    fun getById(id: Long): Booking = bookingRepository.findById(id)
        ?: throw BookingException(BookingExceptionProfile.BOOKING_NOT_FOUND)

    fun create(booking: Booking): Booking {
        bookingValidator.validateCreating(booking)
        return bookingRepository.save(booking)
    }

    fun update(booking: Booking): Booking {
        bookingValidator.validateUpdating(booking)
        return bookingRepository.update(booking)
    }

    fun delete(booking: Booking) = bookingRepository.delete(booking)
}
