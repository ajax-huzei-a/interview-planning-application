package intellistart.interviewplanning.model.bookinglimit

import intellistart.interviewplanning.exceptions.BookingLimitException
import intellistart.interviewplanning.exceptions.UserException
import intellistart.interviewplanning.model.user.Role
import intellistart.interviewplanning.model.user.User
import intellistart.interviewplanning.model.week.Week
import intellistart.interviewplanning.model.week.WeekService
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrDefault

/**
 * Service for BookingLimit entity.
 */
@Service
class BookingLimitService(
    private val bookingLimitRepository: BookingLimitRepository,
    private val weekService: WeekService
) {

    /**
     * Return booking limit of a certain interviewer for a certain week,
     * or create an infinite booking limit.
     *
     * @param user - interviewer
     * @param week - certain week
     * @return BookingLimit
     * @throws UserException - not an interviewer id
     */
    fun getBookingLimitByInterviewer(user: User, week: Week): BookingLimit {
        if (user.role != Role.INTERVIEWER) {
            throw UserException(UserException.UserExceptionProfile.NOT_INTERVIEWER)
        }

        return bookingLimitRepository.findById(BookingLimitKey(user.id, week.id))
            .getOrDefault(createInfiniteBookingLimit(user, week))
    }

    private fun createInfiniteBookingLimit(user: User, week: Week): BookingLimit =
        BookingLimit(BookingLimitKey(user.id, week.id), INFINITE_BOOKING_LIMITS_NUMBER, user, week)

    /**
     * Create BookingLimit for the next week.
     *
     * @param user - interviewer
     * @param bookingLimit - booking limit
     * @return BookingLimit
     * @throws BookingLimitException - invalid bookingLimit exception
     * @throws UserException - not an interviewer id
     */
    fun createBookingLimit(user: User, bookingLimit: Int): BookingLimit {
        if (user.role != Role.INTERVIEWER) {
            throw UserException(UserException.UserExceptionProfile.NOT_INTERVIEWER)
        }

        if (bookingLimit <= 0) {
            throw BookingLimitException(BookingLimitException.BookingLimitExceptionProfile.INVALID_BOOKING_LIMIT)
        }

        val nextWeek = weekService.getNextWeek()

        val newBookingLimit = BookingLimit(BookingLimitKey(user.id, nextWeek.id), bookingLimit, user, nextWeek)

        return bookingLimitRepository.save(newBookingLimit)
    }

    /**
     * Get booking limit for the next week.
     *
     * @param user - interviewer
     * @return BookingLimit
     * @throws UserException - not an interviewer id
     */
    fun getBookingLimitForNextWeek(user: User): BookingLimit =
        getBookingLimitByInterviewer(user, weekService.getNextWeek())

    /**
     * Get booking limit for the current week.
     *
     * @param user - interviewer
     * @return BookingLimit
     * @throws UserException - not an interviewer id
     */
    fun getBookingLimitForCurrentWeek(user: User): BookingLimit =
        getBookingLimitByInterviewer(user, weekService.getCurrentWeek())

    companion object {
        private const val INFINITE_BOOKING_LIMITS_NUMBER = 1000
    }
}
