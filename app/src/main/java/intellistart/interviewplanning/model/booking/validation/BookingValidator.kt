package intellistart.interviewplanning.model.booking.validation

import intellistart.interviewplanning.exceptions.BookingException
import intellistart.interviewplanning.exceptions.BookingException.BookingExceptionProfile
import intellistart.interviewplanning.exceptions.SlotException
import intellistart.interviewplanning.exceptions.SlotException.SlotExceptionProfile
import intellistart.interviewplanning.model.booking.Booking
import intellistart.interviewplanning.model.period.Period
import intellistart.interviewplanning.model.period.PeriodService
import intellistart.interviewplanning.model.period.TimeService
import intellistart.interviewplanning.model.slot.SlotService
import org.bson.types.ObjectId
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class BookingValidator(
    private val periodService: PeriodService,
    private val timeService: TimeService,
    private val slotService: SlotService,
) {

    fun validateCreating(newBooking: Booking): Mono<Unit> = validateUpdating(newBooking)

    fun validateUpdating(newBooking: Booking): Mono<Unit> =
        checkBookingPeriodForNinetyMinutes(newBooking.period)
            .then(checkSubjectAndDescriptionLength(newBooking.subject, newBooking.description))
            .then(checkBookingForOverlapsWithSlots(newBooking))
            .then(checkBookingForOverlapsWithOtherBookings(newBooking))

    private fun checkBookingPeriodForNinetyMinutes(period: Period): Mono<Unit> =
        if (timeService.calculateDurationMinutes(period.from, period.to) < BOOKING_PERIOD_DURATION_MINUTES) {
            Mono.error(SlotException(SlotExceptionProfile.INVALID_BOUNDARIES))
        } else {
            Unit.toMono()
        }

    private fun checkSubjectAndDescriptionLength(subject: String, description: String): Mono<Unit> {
        if (subject.length > SUBJECT_MAX_SIZE) {
            return Mono.error(BookingException(BookingExceptionProfile.INVALID_SUBJECT))
        }
        return if (description.length > DESCRIPTION_MAX_SIZE) {
            Mono.error(BookingException(BookingExceptionProfile.INVALID_DESCRIPTION))
        } else {
            Unit.toMono()
        }
    }

    private fun checkBookingForOverlapsWithSlots(booking: Booking): Mono<Unit> = Mono.zip(
        slotService.getById(booking.interviewerSlotId),
        slotService.getById(booking.candidateSlotId)
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
    ): Mono<Unit> = Mono.zip(
        slotService.getById(booking.interviewerSlotId),
        slotService.getById(booking.candidateSlotId)
    ).flatMap { tuple ->
        val interviewerBookings: Collection<Booking> = tuple.t1.bookings
        val candidateBookings: Collection<Booking> = tuple.t2.bookings

        validatePeriodNotOverlappingWithOtherBookingPeriods(
            booking.id,
            booking.period,
            interviewerBookings
        ).then(
            validatePeriodNotOverlappingWithOtherBookingPeriods(
                booking.id,
                booking.period,
                candidateBookings
            )
        )
    }

    private fun validatePeriodNotOverlappingWithOtherBookingPeriods(
        updatingBookingId: ObjectId,
        period: Period,
        bookings: Collection<Booking>,
    ): Mono<Unit> {
        bookings.forEach { booking ->
            if (booking.id != updatingBookingId && periodService.areOverlapping(booking.period, period)) {
                return Mono.error(BookingException(BookingExceptionProfile.SLOTS_NOT_INTERSECTING))
            }
        }
        return Unit.toMono()
    }

    companion object {
        const val BOOKING_PERIOD_DURATION_MINUTES = 90
        const val DESCRIPTION_MAX_SIZE = 4000
        const val SUBJECT_MAX_SIZE = 255
    }
}
