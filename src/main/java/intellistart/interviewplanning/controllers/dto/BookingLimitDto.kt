package intellistart.interviewplanning.controllers.dto

import intellistart.interviewplanning.model.bookinglimit.BookingLimit


/**
 * DTO for BookingLimit.
 */
data class BookingLimitDto(
    var userId: Long = 0,
    var weekNum: Long = 0,
    var bookingLimit: Int = 0
)

fun BookingLimit.toDTO():BookingLimitDto = BookingLimitDto(
    userId = user.id,
    weekNum = week.id,
    bookingLimit = bookingLimit
)
