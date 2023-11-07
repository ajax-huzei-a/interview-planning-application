package intellistart.interviewplanning.model.booking

import intellistart.interviewplanning.exceptions.BookingException
import intellistart.interviewplanning.exceptions.BookingException.BookingExceptionProfile
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class BookingService(
    private val bookingRepository: BookingRepository
) {

    fun getById(id: ObjectId): Mono<Booking> = bookingRepository.findById(id)
        .switchIfEmpty(Mono.error(BookingException(BookingExceptionProfile.BOOKING_NOT_FOUND)))

    fun create(booking: Booking): Mono<Booking> = bookingRepository.save(booking)

    fun update(booking: Booking): Mono<Booking> =
        getById(booking.id)
            .switchIfEmpty(Mono.error(BookingException(BookingExceptionProfile.BOOKING_NOT_FOUND)))
            .flatMap { _ ->
                bookingRepository.update(booking)
            }

    fun delete(booking: Booking): Mono<Booking> = bookingRepository.delete(booking)
}
