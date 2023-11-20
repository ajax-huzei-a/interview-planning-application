package com.intellistart.interviewplanning.model.booking.validation

import com.intellistart.interviewplanning.appltication.port.PeriodOperationsInPort
import com.intellistart.interviewplanning.domain.model.Period
import com.intellistart.interviewplanning.exceptions.BookingException
import com.intellistart.interviewplanning.exceptions.BookingException.BookingExceptionProfile
import com.intellistart.interviewplanning.model.booking.Booking
import com.intellistart.interviewplanning.slot.port.SlotOperationsInPort
import org.bson.types.ObjectId
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class BookingValidator(
    private val periodService: PeriodOperationsInPort,
    private val slotService: SlotOperationsInPort
) {

    fun validateCreating(newBooking: Booking): Mono<Unit> = validateUpdating(newBooking)

    fun validateUpdating(newBooking: Booking): Mono<Unit> =
        Mono.`when`(
            Mono.fromCallable {
                checkSubjectAndDescriptionLength(newBooking.subject, newBooking.description)
            },
            checkBookingForOverlapsWithSlots(newBooking),
            checkBookingForOverlapsWithOtherBookings(newBooking),
        ).thenReturn(Unit)

    private fun checkSubjectAndDescriptionLength(subject: String, description: String) {
        if (subject.length > SUBJECT_MAX_SIZE) {
            throw BookingException(BookingExceptionProfile.INVALID_SUBJECT)
        }
        if (description.length > DESCRIPTION_MAX_SIZE) {
            throw BookingException(BookingExceptionProfile.INVALID_DESCRIPTION)
        }
    }

    private fun checkBookingForOverlapsWithSlots(booking: Booking): Mono<Unit> = Mono.zip(
        slotService.getById(booking.interviewerSlotId.toHexString()),
        slotService.getById(booking.candidateSlotId.toHexString())
    ).flatMap { tuple ->
        val periodOfInterviewer: Period = tuple.t1.period
        val periodOfCandidate: Period = tuple.t2.period
        val bookingPeriod: Period = booking.period

        if (
            !periodOfInterviewer.date.isEqual(periodOfCandidate.date) ||
            !periodService.isFirstInsideSecond(bookingPeriod, periodOfInterviewer) ||
            !periodService.isFirstInsideSecond(bookingPeriod, periodOfCandidate)
        ) {
            Mono.error(BookingException(BookingExceptionProfile.SLOTS_NOT_INTERSECTING))
        } else {
            Unit.toMono()
        }
    }

    private fun checkBookingForOverlapsWithOtherBookings(
        booking: Booking,
    ): Mono<Unit> = Mono.`when`(
        validateBookingIsApplicableToUser(booking.interviewerSlotId, booking),
        validateBookingIsApplicableToUser(booking.candidateSlotId, booking),
    ).thenReturn(Unit)

    private fun validateBookingIsApplicableToUser(userId: ObjectId, booking: Booking): Mono<Unit> =
        slotService.getById(userId.toHexString())
            .doOnNext { user ->
                validatePeriodNotOverlappingWithOtherBookingPeriods(
                    booking.id,
                    booking.period,
                    user.bookings
                )
            }
            .thenReturn(Unit)

    private fun validatePeriodNotOverlappingWithOtherBookingPeriods(
        updatingBookingId: ObjectId,
        period: Period,
        bookings: Collection<Booking>,
    ) {
        bookings.forEach { booking ->
            if (booking.id != updatingBookingId && periodService.areOverlapping(booking.period, period)) {
                throw BookingException(BookingExceptionProfile.SLOTS_NOT_INTERSECTING)
            }
        }
    }

    companion object {
        const val DESCRIPTION_MAX_SIZE = 4000
        const val SUBJECT_MAX_SIZE = 255
    }
}
