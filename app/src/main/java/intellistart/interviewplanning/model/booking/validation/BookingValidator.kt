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

@Component
class BookingValidator(
    private val periodService: PeriodService,
    private val timeService: TimeService,
    private val slotService: SlotService
) {

    fun validateCreating(newBooking: Booking) {
        validateUpdating(newBooking)
    }

    fun validateUpdating(newBooking: Booking) {
        checkBookingPeriodForNinetyMinutes(newBooking.period)

        checkSubjectAndDescriptionLength(newBooking.subject, newBooking.description)

        checkBookingForOverlapsWithSlots(newBooking)

        checkBookingForOverlapsWithOtherBookings(newBooking)
    }

    private fun checkBookingPeriodForNinetyMinutes(period: Period) {
        if (timeService.calculateDurationMinutes(period.from, period.to) < BOOKING_PERIOD_DURATION_MINUTES) {
            throw SlotException(SlotExceptionProfile.INVALID_BOUNDARIES)
        }
    }

    private fun checkSubjectAndDescriptionLength(subject: String, description: String) {
        if (subject.length > SUBJECT_MAX_SIZE) {
            throw BookingException(BookingExceptionProfile.INVALID_SUBJECT)
        }
        if (description.length > DESCRIPTION_MAX_SIZE) {
            throw BookingException(BookingExceptionProfile.INVALID_DESCRIPTION)
        }
    }

    private fun checkBookingForOverlapsWithSlots(booking: Booking) {
        val periodOfInterviewer: Period = slotService.getById(booking.interviewerSlotId).period
        val periodOfCandidate: Period = slotService.getById(booking.candidateSlotId).period

        val bookingPeriod: Period = booking.period

        if (
            !periodOfInterviewer.date.isEqual(periodOfCandidate.date) ||
            !periodService.isFirstInsideSecond(bookingPeriod, periodOfInterviewer) ||
            !periodService.isFirstInsideSecond(bookingPeriod, periodOfCandidate)
        ) {
            throw BookingException(BookingExceptionProfile.SLOTS_NOT_INTERSECTING)
        }
    }

    private fun checkBookingForOverlapsWithOtherBookings(
        booking: Booking
    ) {
        validatePeriodNotOverlappingWithOtherBookingPeriods(
            booking.id,
            booking.period,
            slotService.getById(booking.interviewerSlotId).bookings
        )

        validatePeriodNotOverlappingWithOtherBookingPeriods(
            booking.id,
            booking.period,
            slotService.getById(booking.candidateSlotId).bookings
        )
    }

    private fun validatePeriodNotOverlappingWithOtherBookingPeriods(
        updatingBookingId: ObjectId,
        period: Period,
        bookings: Collection<Booking>
    ) {
        bookings.forEach { booking ->
            if (booking.id != updatingBookingId && periodService.areOverlapping(booking.period, period)) {
                throw BookingException(BookingExceptionProfile.SLOTS_NOT_INTERSECTING)
            }
        }
    }

    companion object {
        const val BOOKING_PERIOD_DURATION_MINUTES = 90
        const val DESCRIPTION_MAX_SIZE = 4000
        const val SUBJECT_MAX_SIZE = 255
    }
}
