package intellistart.interviewplanning.controllers

import intellistart.interviewplanning.controllers.dto.DashboardMapDto
import intellistart.interviewplanning.controllers.dto.BookingDto
import intellistart.interviewplanning.controllers.dto.toDto
import intellistart.interviewplanning.controllers.dto.EmailDto
import intellistart.interviewplanning.controllers.dto.UsersDto
import intellistart.interviewplanning.controllers.dto.toUsersDto
import intellistart.interviewplanning.exceptions.BookingException
import intellistart.interviewplanning.exceptions.BookingLimitException
import intellistart.interviewplanning.exceptions.SlotException
import intellistart.interviewplanning.exceptions.UserException
import intellistart.interviewplanning.model.booking.Booking
import intellistart.interviewplanning.model.booking.BookingService
import intellistart.interviewplanning.model.booking.validation.BookingValidator
import intellistart.interviewplanning.model.candidateslot.CandidateSlotService
import intellistart.interviewplanning.model.dayofweek.DayOfWeek
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlotService
import intellistart.interviewplanning.model.period.PeriodService
import intellistart.interviewplanning.model.user.Role
import intellistart.interviewplanning.model.user.User
import intellistart.interviewplanning.model.user.UserService
import intellistart.interviewplanning.model.week.WeekService
import intellistart.interviewplanning.security.JwtUserDetails
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable

/**
 * Controller for processing requests from users with Coordinator role.
 */
@RestController
@CrossOrigin
@Suppress("Many fields and functions in the class")
class CoordinatorController(
    private val bookingService: BookingService,
    private val bookingValidator: BookingValidator,
    private val interviewerSlotService: InterviewerSlotService,
    private val candidateSlotService: CandidateSlotService,
    private val periodService: PeriodService,
    private val userService: UserService,
    private val weekService: WeekService,
) {

    /**
     * POST request to grant an INTERVIEWER role by email.
     *
     * @param request - Request body of POST mapping.
     * @return ResponseEntity - Response of the granted User.
     * @throws UserException - when user already has a role.
     */
    @PostMapping("/users/interviewers")
    fun grantInterviewerByEmail(@RequestBody request: EmailDto): ResponseEntity<User> =
        ResponseEntity.ok(userService.grantRoleByEmail(request.email, Role.INTERVIEWER))

    /**
     * POST request to grant a COORDINATOR role by email.
     *
     * @param request - Request body of POST mapping.
     * @return ResponseEntity - Response of the granted User.
     * @throws UserException - - when the user already has a role.
     */
    @PostMapping("/users/coordinators")
    fun grantCoordinatorByEmail(@RequestBody request: EmailDto): ResponseEntity<User> =
        ResponseEntity.ok(userService.grantRoleByEmail(request.email, Role.COORDINATOR))

    /**
     * GET request to get a list of users with the interviewer role.
     *
     * @return ResponseEntity - Response of the list of users with the interviewer role.
     */
    @GetMapping("/users/interviewers")
    fun getAllInterviewers(): ResponseEntity<UsersDto> =
        ResponseEntity.ok(userService.obtainUsersByRole(Role.INTERVIEWER).toUsersDto())

    /**
     * GET request to get a list of users with the coordinator role.
     *
     * @return ResponseEntity - Response of the list of users with the coordinator role.
     */
    @GetMapping("/users/coordinators")
    fun getAllCoordinators(): ResponseEntity<UsersDto> =
        ResponseEntity.ok(userService.obtainUsersByRole(Role.COORDINATOR).toUsersDto())


    /**
     * DELETE request for deleting an interviewer.
     *
     * @param id - the interviewer's id to delete.
     * @return ResponseEntity - the deleted user.
     * @throws UserException - when the user is not found by the given id or does not have an interviewer role.
     */
    @DeleteMapping("/users/interviewers/{id}")
    fun deleteInterviewerById(@PathVariable("id") id: Long): ResponseEntity<User> =
        ResponseEntity.ok(userService.deleteInterviewer(id))

    /**
     * DELETE request for deleting a coordinator.
     *
     * @param id - the coordinator's id to delete.
     * @return ResponseEntity - the deleted user.
     * @throws UserException - when the user is not found by the given id or the coordinator removes himself.
     */
    @DeleteMapping("/users/coordinators/{id}")
    fun deleteCoordinatorById(
        @PathVariable("id") id: Long,
        authentication: Authentication
    ): ResponseEntity<User> {
        val jwtUserDetails = authentication.principal as JwtUserDetails
        val currentEmailCoordinator = jwtUserDetails.email
        return ResponseEntity.ok(userService.deleteCoordinator(id, currentEmailCoordinator))
    }

    /**
     * Returns [DashboardMapDto] object with week num and map of
     * LocalDate with DashboardDto which contains all candidate, interviewer
     * slots and bookings for the certain date.
     *
     * @param weekId number of the week to get all slots from
     * @return all candidate, interviewer slots and bookings for a certain week
     */
    @GetMapping("/weeks/{weekNum}/dashboard")
    fun getDashboard(@PathVariable("weekNum") weekId: Long): ResponseEntity<DashboardMapDto> {
        val week = weekService.getWeekByWeekNum(weekId)
        val dashboard = DashboardMapDto(weekId, weekService)

        val interviewerSlots = interviewerSlotService.getSlotsByWeek(week)
        dashboard.addInterviewerSlots(interviewerSlots)

        for (dayOfWeek in DayOfWeek.entries) {
            val date = weekService.convertToLocalDate(weekId, dayOfWeek)
            val candidateSlots = candidateSlotService.getCandidateSlotsByDate(date)
            dashboard.addCandidateSlots(candidateSlots)
        }

        return ResponseEntity.ok(dashboard)
    }

    /**
     * POST request method for updating a booking by id.
     *
     * @param bookingDto request DTO
     * @return ResponseEntity - Response of the saved updated object converted to a DTO.
     * @throws SlotException if Period boundaries are Invalid or Candidate/Interviewer Slot is not found
     * @throws BookingLimitException if the interviewer's booking limit is exceeded
     * @throws BookingException if CandidateSlot, InterviewerSlot do not intersect with Period
     */
    @PostMapping("bookings/{id}")
    fun updateBooking(
        @RequestBody bookingDto: BookingDto,
        @PathVariable id: Long
    ): ResponseEntity<BookingDto> {
        val updatingBooking = bookingService.getById(id)
        val newDataBooking = getFromDto(bookingDto)

        bookingValidator.validateUpdating(updatingBooking, newDataBooking)
        updatingBooking.populateFields(newDataBooking)

        val savedBooking = bookingService.save(updatingBooking)
        return ResponseEntity.ok(savedBooking.toDto())
    }

    /**
     * POST request method for adding a booking.
     *
     * @param bookingDto request DTO
     * @return ResponseEntity - Response of the saved created object converted to a DTO.
     * @throws SlotException if Period boundaries are Invalid or Candidate/Interviewer Slot is not found
     * @throws BookingLimitException if the interviewer's booking limit is exceeded
     * @throws BookingException if CandidateSlot, InterviewerSlot do not intersect with Period
     */
    @PostMapping("bookings")
    fun createBooking(@RequestBody bookingDto: BookingDto): ResponseEntity<BookingDto> {
        val newBooking = getFromDto(bookingDto)

        bookingValidator.validateCreating(newBooking)
        val savedBooking = bookingService.save(newBooking)

        return ResponseEntity.ok(savedBooking.toDto())
    }


    /**
     * DELETE request for deleting a booking by id.
     *
     * @param bookingId - id of the booking to delete
     * @return DTO of deleted booking
     * @throws BookingException - throw if the booking by the given id wasn't found
     */
    @DeleteMapping("/bookings/{id}")
    fun deleteBooking(@PathVariable("id") bookingId: Long): ResponseEntity<BookingDto> {
        val bookingToDelete = bookingService.getById(bookingId)
        bookingService.deleteBooking(bookingToDelete)

        return ResponseEntity.ok(bookingToDelete.toDto())
    }

    private fun getFromDto(bookingDto: BookingDto): Booking {
        return Booking().apply {
            subject = bookingDto.subject
            description = bookingDto.description
            interviewerSlot = interviewerSlotService.getById(bookingDto.interviewerSlotId)
            candidateSlot = candidateSlotService.getById(bookingDto.candidateSlotId)
            period = periodService
                .obtainPeriod(bookingDto.from, bookingDto.to)
        }
    }

    private fun Booking.populateFields(newDataBooking: Booking) {
        subject = newDataBooking.subject
        description = newDataBooking.description
        interviewerSlot = newDataBooking.interviewerSlot
        candidateSlot = newDataBooking.candidateSlot
        period = newDataBooking.period
    }
}
