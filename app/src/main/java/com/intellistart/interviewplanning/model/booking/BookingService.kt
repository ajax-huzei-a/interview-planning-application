package com.intellistart.interviewplanning.model.booking

import com.intellistart.interviewplanning.exceptions.BookingException
import com.intellistart.interviewplanning.exceptions.BookingException.BookingExceptionProfile
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

@Service
class BookingService(
    private val bookingRepository: BookingRepository
) {

    fun getById(id: ObjectId): Mono<Booking> = bookingRepository.findById(id)
        .switchIfEmpty {
            BookingException(BookingExceptionProfile.BOOKING_NOT_FOUND)
                .toMono()
        }

    fun create(booking: Booking): Mono<Booking> = bookingRepository.save(booking)

    fun update(booking: Booking): Mono<Booking> =
        getById(booking.id)
            .switchIfEmpty {
                BookingException(BookingExceptionProfile.BOOKING_NOT_FOUND)
                    .toMono()
            }
            .flatMap { _ ->
                bookingRepository.update(booking)
            }

    fun delete(booking: Booking): Mono<Booking> = bookingRepository.delete(booking)
}
