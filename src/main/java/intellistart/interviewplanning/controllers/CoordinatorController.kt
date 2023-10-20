package intellistart.interviewplanning.controllers

import intellistart.interviewplanning.controllers.dto.BookingDto
import intellistart.interviewplanning.controllers.dto.EmailDto
import intellistart.interviewplanning.controllers.dto.UsersDto
import intellistart.interviewplanning.controllers.dto.toDto
import intellistart.interviewplanning.controllers.dto.toUsersDto
import intellistart.interviewplanning.model.booking.Booking
import intellistart.interviewplanning.model.booking.BookingService
import intellistart.interviewplanning.model.booking.validation.BookingValidator
import intellistart.interviewplanning.model.period.PeriodService
import intellistart.interviewplanning.model.user.Role
import intellistart.interviewplanning.model.user.User
import intellistart.interviewplanning.model.user.UserService
import intellistart.interviewplanning.security.JwtUserDetails
import org.bson.types.ObjectId
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin
@Suppress("LongParameterList", "TooManyFunctions")
class CoordinatorController(
    private val bookingService: BookingService,
    private val periodService: PeriodService,
    private val bookingValidator: BookingValidator,
    private val userService: UserService
) {

    @PostMapping("/users/grant/interviewer")
    fun grantInterviewerByEmail(@RequestBody request: EmailDto): ResponseEntity<User> =
        ResponseEntity.ok(userService.grantRoleByEmail(request.email, Role.INTERVIEWER))

    @PostMapping("/users/grant/coordinator")
    fun grantCoordinatorByEmail(@RequestBody request: EmailDto): ResponseEntity<User> =
        ResponseEntity.ok(userService.grantRoleByEmail(request.email, Role.COORDINATOR))

    @GetMapping("/users/interviewers")
    fun getAllInterviewers(): ResponseEntity<UsersDto> =
        ResponseEntity.ok(userService.getUsersByRole(Role.INTERVIEWER).toUsersDto())

    @GetMapping("/users/coordinators")
    fun getAllCoordinators(): ResponseEntity<UsersDto> =
        ResponseEntity.ok(userService.getUsersByRole(Role.COORDINATOR).toUsersDto())

    @DeleteMapping("/users/delete/interviewer/{id}")
    fun deleteInterviewerById(@PathVariable("id") id: String): ResponseEntity<User> =
        ResponseEntity.ok(userService.deleteInterviewer(ObjectId(id)))

    @DeleteMapping("/users/delete/coordinator/{id}")
    fun deleteCoordinatorById(
        @PathVariable("id") id: String,
        authentication: Authentication
    ): ResponseEntity<User> {
        val jwtUserDetails = authentication.principal as JwtUserDetails
        val currentEmailCoordinator = jwtUserDetails.email
        return ResponseEntity.ok(userService.deleteCoordinator(ObjectId(id), currentEmailCoordinator))
    }

    @GetMapping("/dashboard")
    fun getDashboard(): ResponseEntity<*> = ResponseEntity.ok(userService.getDashboard())

    @PostMapping("/booking/update/{id}")
    fun updateBooking(
        @RequestBody bookingDto: BookingDto,
        @PathVariable id: String
    ): ResponseEntity<BookingDto> {
        val newDataBooking = getFromDto(bookingDto).copy(id = ObjectId(id))
        bookingValidator.validateUpdating(newDataBooking)
        val savedBooking = bookingService.update(newDataBooking)
        return ResponseEntity.ok(savedBooking.toDto())
    }

    @PostMapping("/booking/create")
    fun createBooking(@RequestBody bookingDto: BookingDto): ResponseEntity<BookingDto> {
        val newBooking = getFromDto(bookingDto)
        bookingValidator.validateCreating(newBooking)
        val savedBooking = bookingService.create(newBooking)
        return ResponseEntity.ok(savedBooking.toDto())
    }

    @DeleteMapping("/booking/delete/{id}")
    fun deleteBooking(@PathVariable("id") bookingId: String): ResponseEntity<BookingDto> {
        val bookingToDelete = bookingService.getById(ObjectId(bookingId))
        bookingService.delete(bookingToDelete)
        return ResponseEntity.ok(bookingToDelete.toDto())
    }

    private fun getFromDto(bookingDto: BookingDto): Booking {
        return Booking(
            ObjectId(),
            bookingDto.subject,
            bookingDto.description,
            ObjectId(bookingDto.interviewerSlotId),
            ObjectId(bookingDto.candidateSlotId),
            periodService
                .obtainPeriod(bookingDto.from, bookingDto.to, bookingDto.date)
        )
    }
}
