package intellistart.interviewplanning.model.booking.validation

import intellistart.interviewplanning.exceptions.SlotException
import intellistart.interviewplanning.exceptions.BookingException
import intellistart.interviewplanning.exceptions.BookingLimitException
import intellistart.interviewplanning.exceptions.BookingException.BookingExceptionProfile
import intellistart.interviewplanning.exceptions.BookingLimitException.BookingLimitExceptionProfile
import intellistart.interviewplanning.exceptions.SlotException.SlotExceptionProfile
import intellistart.interviewplanning.model.booking.Booking
import intellistart.interviewplanning.model.bookinglimit.BookingLimitService
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlotService
import intellistart.interviewplanning.model.period.Period
import intellistart.interviewplanning.model.period.PeriodService
import intellistart.interviewplanning.model.period.TimeService
import intellistart.interviewplanning.model.week.WeekService
import org.springframework.stereotype.Component
import java.time.LocalDate

/**
 * Business logic Booking validator.
 */
@Component
class BookingValidator(
    private val periodService: PeriodService,
    private val timeService: TimeService,
    private val weekService: WeekService,
    private val bookingLimitService: BookingLimitService,
    private val interviewerSlotService: InterviewerSlotService
) {

    /**
     * Alias for [validateUpdating].
     */
    fun validateCreating(newBooking: Booking) {
        validateUpdating(newBooking, newBooking)
    }

    /**
     * Perform business logic validation for updating Booking.
     *
     * @param oldBooking Booking with old parameters
     * @param newBooking Booking with new parameters
     *
     * @throws SlotException          if duration of new Period is invalid
     * @throws BookingLimitException  if interviewer's booking limit is exceeded
     * @throws BookingException       if periods of InterviewSlot, CandidateSlot do not
     *                                intersect with new Period or new Period is overlapping
     *                                with existing Periods of InterviewerSlot and
     *                                CandidateSlot
     */
    fun validateUpdating(oldBooking: Booking, newBooking: Booking) {

        checkIfBookingPeriodIsNotGreaterThanNinetyMinutes(newBooking.period)

        checkIfSubjectAndDescriptionLengthIsWithinLimits(newBooking.subject, newBooking.description)

        checkIfTheBookingOverlapsWithSlotsOfInterviewerAndCandidate(newBooking)

        checkIfTheBookingOverlapsWithOtherBookingsOfTheInterviewerAndCandidate(newBooking, oldBooking.id)

        checkIfBookingLimitIsExceeded(newBooking)
    }

    private fun checkIfBookingPeriodIsNotGreaterThanNinetyMinutes(period: Period) {
        if (timeService.calculateDurationMinutes(period.from, period.to) < BOOKING_PERIOD_DURATION_MINUTES) {
            throw SlotException(SlotExceptionProfile.INVALID_BOUNDARIES)
        }
    }

    private fun checkIfSubjectAndDescriptionLengthIsWithinLimits(subject: String, description: String) {
        if (subject.length > SUBJECT_MAX_SIZE) {
            throw BookingException(BookingExceptionProfile.INVALID_SUBJECT)
        }
        if (description.length > DESCRIPTION_MAX_SIZE) {
            throw BookingException(BookingExceptionProfile.INVALID_DESCRIPTION)
        }
    }

    private fun checkIfTheBookingOverlapsWithSlotsOfInterviewerAndCandidate(booking: Booking) {
        val dateOfInterviewer: LocalDate = weekService.convertToLocalDate(
            booking.interviewerSlot.week.id, booking.interviewerSlot.dayOfWeek
        )
        val dateOfCandidate: LocalDate = booking.candidateSlot.date

        val periodOfInterviewer: Period = booking.interviewerSlot.period
        val periodOfCandidate: Period = booking.interviewerSlot.period

        val bookingPeriod: Period = booking.period

        if (
            !dateOfInterviewer.isEqual(dateOfCandidate) ||
            !periodService.isFirstInsideSecond(bookingPeriod, periodOfInterviewer) ||
            !periodService.isFirstInsideSecond(bookingPeriod, periodOfCandidate)
            ) {
            throw BookingException(BookingExceptionProfile.SLOTS_NOT_INTERSECTING)
        }
    }

    private fun checkIfTheBookingOverlapsWithOtherBookingsOfTheInterviewerAndCandidate(
        booking: Booking,
        updatedBookingId: Long
    ) {
        validatePeriodNotOverlappingWithOtherBookingPeriods(
            updatedBookingId, booking.period, booking.interviewerSlot.bookings
        )

        validatePeriodNotOverlappingWithOtherBookingPeriods(
            updatedBookingId, booking.period, booking.candidateSlot.bookings
        )
    }

    private fun validatePeriodNotOverlappingWithOtherBookingPeriods(
        updatingBookingId: Long,
        period: Period,
        bookings: Collection<Booking>
    ) {
        bookings.forEach { booking ->
            if (booking.id != updatingBookingId && periodService.areOverlapping(booking.period, period)) {
                throw BookingException(BookingExceptionProfile.SLOTS_NOT_INTERSECTING)
            }
        }
    }

    private fun checkIfBookingLimitIsExceeded(
        booking: Booking
    ) {
        val interviewerSlotsNewInterviewer = interviewerSlotService
            .getInterviewerSlotsByUserAndWeek(
                booking.interviewerSlot.user,
                booking.interviewerSlot.week
            )

        val bookingsNumber: Int = interviewerSlotsNewInterviewer.sumOf { it.bookings.size }

        val bookingLimit = bookingLimitService
            .getBookingLimitByInterviewer(booking.interviewerSlot.user, booking.interviewerSlot.week).bookingLimit

        if (bookingsNumber >= bookingLimit) {
            throw BookingLimitException(BookingLimitExceptionProfile.BOOKING_LIMIT_IS_EXCEEDED)
        }
    }

    companion object {
        const val BOOKING_PERIOD_DURATION_MINUTES = 90
        const val DESCRIPTION_MAX_SIZE = 4000
        const val SUBJECT_MAX_SIZE = 255
    }
}
