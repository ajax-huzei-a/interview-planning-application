package intellistart.interviewplanning.model.booking

import intellistart.interviewplanning.exceptions.BookingException
import intellistart.interviewplanning.exceptions.BookingException.BookingExceptionProfile
import org.springframework.stereotype.Service

/**
 * Service for Booking entity.
 */
@Service
class BookingService(private val bookingRepository: BookingRepository) {

    /**
     * Find Booking by id from repository.
     *
     * @throws BookingException if no booking with given id
     */
    fun getById(id: Long): Booking = bookingRepository.findById(id)
        .orElseThrow { BookingException(BookingExceptionProfile.BOOKING_NOT_FOUND) }

    /**
     * Alias for method in [BookingRepository].
     */
    fun save(booking: Booking): Booking = bookingRepository.save(booking)

    /**
     * Delete the given bookings from DB.
     *
     * @param bookings - bookings that need to be removed from the database.
     */
    fun deleteBookings(bookings: Set<Booking>) = bookingRepository.deleteAll(bookings)

    /**
     * Delete the given booking from DB.
     *
     * @param booking - booking that needed to be removed from the database.
     */
    fun deleteBooking(booking: Booking) = bookingRepository.delete(booking)
}
