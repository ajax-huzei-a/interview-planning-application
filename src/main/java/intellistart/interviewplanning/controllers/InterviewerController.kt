package intellistart.interviewplanning.controllers

import intellistart.interviewplanning.controllers.dto.InterviewerSlotDtoRequest
import intellistart.interviewplanning.controllers.dto.toDtoResponse
import intellistart.interviewplanning.controllers.dto.InterviewerSlotDtoResponse
import intellistart.interviewplanning.controllers.dto.BookingLimitDto
import intellistart.interviewplanning.controllers.dto.toDto
import intellistart.interviewplanning.controllers.dto.InterviewerSlotsDto
import intellistart.interviewplanning.controllers.dto.toDtoList
import intellistart.interviewplanning.exceptions.BookingLimitException
import intellistart.interviewplanning.exceptions.SlotException
import intellistart.interviewplanning.exceptions.UserException
import intellistart.interviewplanning.model.bookinglimit.BookingLimitService
import intellistart.interviewplanning.model.dayofweek.DayOfWeek
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlot
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlotDtoValidator
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlotService
import intellistart.interviewplanning.model.period.PeriodService
import intellistart.interviewplanning.model.user.UserService
import intellistart.interviewplanning.model.week.WeekService
import intellistart.interviewplanning.security.JwtUserDetails
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.GetMapping

/**
 * Controller for processing requests from users with Interview role.
 */
@RestController
@CrossOrigin
class InterviewerController(
    private val interviewerSlotService: InterviewerSlotService,
    private val interviewerSlotDtoValidator: InterviewerSlotDtoValidator,
    private val bookingLimitService: BookingLimitService,
    private val weekService: WeekService,
    private val userService: UserService,
    private val periodService: PeriodService
) {
    /**
     * Post Request for creating slot.
     *
     * @param interviewerSlotDto - DTO from request
     * @param interviewerId      - user Id from request
     * @return interviewerSlotDto - and/or HTTP status
     *
     * @throws SlotException when:
     *
     *  * cannot edit this week
     *  * invalid boundaries of time period
     *  * when slot is not found by slotId
     *  * slot is overlapping
     *
     *
     * @throws UserException invalid user (interviewer) exception
     */
    @PostMapping("/interviewers/{interviewerId}/slots")
    fun createInterviewerSlot(
        @RequestBody interviewerSlotDto: InterviewerSlotDtoRequest,
        @PathVariable("interviewerId") interviewerId: Long,
        authentication: Authentication
    ): ResponseEntity<InterviewerSlotDtoResponse> {
        val interviewerSlot = getInterviewerSlotsFromDto(interviewerSlotDto, authentication)
        interviewerSlotDtoValidator
            .validateCreating(interviewerSlot, authentication, interviewerId)
        val createdInterviewerSlot = interviewerSlotService.create(interviewerSlot)
        return ResponseEntity(createdInterviewerSlot.toDtoResponse(), HttpStatus.OK)
    }

    /**
     * Post Request for updating slot.
     *
     * @param interviewerSlotDtoRequest - DTO from request
     * @param interviewerId      - user Id from request
     * @param slotId             - slot Id from request
     *
     * @return interviewerSlotDto - and/or HTTP status
     *
     * @throws UserException when:
     *
     *  * cannot edit this week
     *  * invalid boundaries of time period
     *  * when slot is not found by slotId
     *
     *
     * @throws SlotException - when slot has at least one booking or slot overlaps
     */
    @PostMapping("/interviewers/{interviewerId}/slots/{slotId}")
    fun updateInterviewerSlot(
        @RequestBody interviewerSlotDtoRequest: InterviewerSlotDtoRequest,
        @PathVariable("interviewerId") interviewerId: Long,
        @PathVariable("slotId") slotId: Long,
        authentication: Authentication
    ): ResponseEntity<InterviewerSlotDtoResponse> {
        val interviewerSlot = getInterviewerSlotsFromDto(interviewerSlotDtoRequest, authentication)
        interviewerSlotDtoValidator
            .validateUpdating(
                interviewerSlot,
                authentication, interviewerId, slotId
            )
        val updatedInterviewerSlot = interviewerSlotService.update(interviewerSlot)
        return ResponseEntity(updatedInterviewerSlot.toDtoResponse(), HttpStatus.OK)
    }

    /**
     * Post Request for creating booking limit.
     *
     * @param bookingLimitDto - DTO for BookingLimit
     * @param interviewerId   - user id from request
     * @return BookingLimitDto and HTTP status
     * @throws UserException - invalid user (interviewer) exception or not interviewer id
     * @throws BookingLimitException - invalid bookingLimit exception
     */
    @PostMapping("/interviewers/{interviewerId}/booking-limits")
    fun createBookingLimit(
        @RequestBody bookingLimitDto: BookingLimitDto,
        @PathVariable("interviewerId") interviewerId: Long
    ): ResponseEntity<BookingLimitDto> {
        val user = userService.getUserById(interviewerId)
        val bookingLimit = bookingLimitService.createBookingLimit(
            user,
            bookingLimitDto.bookingLimit
        )
        return ResponseEntity.ok(bookingLimit.toDto())
    }

    /**
     * Request for getting booking limit for current week.
     *
     * @param interviewerId - user Id from request
     * @return BookingLimitDto and HTTP status
     * @throws UserException - invalid user (interviewer) exception or ot interviewer id
     */
    @GetMapping("/interviewers/{interviewerId}/booking-limits/current-week")
    fun getBookingLimitForCurrentWeek(
        @PathVariable("interviewerId") interviewerId: Long
    ): ResponseEntity<BookingLimitDto> {
        val user = userService.getUserById(interviewerId)
        val bookingLimit = bookingLimitService.getBookingLimitForCurrentWeek(user)
        return ResponseEntity.ok(bookingLimit.toDto())
    }

    /**
     * Request for getting booking limit for next week.
     *
     * @param interviewerId user Id from request
     * @return BookingLimitDto and HTTP status
     * @throws UserException - invalid user (interviewer) exception or ot interviewer id
     */
    @GetMapping("/interviewers/{interviewerId}/booking-limits/next-week")
    fun getBookingLimitForNextWeek(
        @PathVariable("interviewerId") interviewerId: Long
    ): ResponseEntity<BookingLimitDto> {
        val user = userService.getUserById(interviewerId)
        val bookingLimit = bookingLimitService.getBookingLimitForNextWeek(user)
        return ResponseEntity.ok(bookingLimit.toDto())
    }

    /**
     * Request for getting Interviewer Slots of current user for current week.
     *
     * @param authentication - user
     * @return [List] of [InterviewerSlot]
     */
    @GetMapping("/interviewers/current/slots")
    fun getInterviewerSlotsForCurrentWeek(
        authentication: Authentication
    ): ResponseEntity<InterviewerSlotsDto> {
        val jwtUserDetails = authentication.principal as JwtUserDetails
        val email = jwtUserDetails.email
        val currentWeekId = weekService.getCurrentWeek().id
        val slots = interviewerSlotService.getSlotsByWeek(email, currentWeekId)
        return ResponseEntity.ok(slots.toDtoList())
    }

    /**
     * Request for getting Interviewer Slots of current user for next week.
     *
     * @param authentication - user
     * @return [List] of [InterviewerSlot]
     */
    @GetMapping("/interviewers/next/slots")
    fun getInterviewerSlotsForNextWeek(
        authentication: Authentication
    ): ResponseEntity<InterviewerSlotsDto> {
        val jwtUserDetails = authentication.principal as JwtUserDetails
        val email = jwtUserDetails.email
        val nextWeekId = weekService.getNextWeek().id
        val slots = interviewerSlotService.getSlotsByWeek(email, nextWeekId)
        return ResponseEntity.ok(slots.toDtoList())
    }

    private fun getInterviewerSlotsFromDto(
        interviewerSlotDto: InterviewerSlotDtoRequest,
        authentication: Authentication,
    ): InterviewerSlot {
        val jwtUserDetails = authentication.principal as JwtUserDetails
        val interviewerSlot = InterviewerSlot()
        interviewerSlot.week = weekService.getWeekByWeekNum(interviewerSlotDto.week)
        interviewerSlot.dayOfWeek = DayOfWeek.valueOf(interviewerSlotDto.dayOfWeek)
        interviewerSlot.period = periodService.obtainPeriod(interviewerSlotDto.from, interviewerSlotDto.to)
        interviewerSlot.user = userService.getUserByEmail(jwtUserDetails.email)
        return interviewerSlot
    }
}
