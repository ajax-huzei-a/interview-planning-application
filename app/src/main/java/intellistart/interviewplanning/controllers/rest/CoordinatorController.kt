package intellistart.interviewplanning.controllers.rest

import intellistart.interviewplanning.controllers.dto.BookingDto
import intellistart.interviewplanning.controllers.dto.EmailDto
import intellistart.interviewplanning.controllers.dto.toDto
import intellistart.interviewplanning.model.booking.Booking
import intellistart.interviewplanning.model.booking.BookingService
import intellistart.interviewplanning.model.booking.validation.BookingValidator
import intellistart.interviewplanning.model.period.PeriodService
import intellistart.interviewplanning.model.user.Role
import intellistart.interviewplanning.model.user.User
import intellistart.interviewplanning.model.user.UserService
import intellistart.interviewplanning.security.JwtUserDetails
import org.bson.types.ObjectId
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

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
    fun grantInterviewerByEmail(@RequestBody request: EmailDto): Mono<User> =
        userService.grantRoleByEmail(request.email, Role.INTERVIEWER)

    @PostMapping("/users/grant/coordinator")
    fun grantCoordinatorByEmail(@RequestBody request: EmailDto): Mono<User> =
        userService.grantRoleByEmail(request.email, Role.COORDINATOR)

    @GetMapping("/users/interviewers")
    fun getAllInterviewers(): Flux<User> =
        userService.getUsersByRole(Role.INTERVIEWER)

    @GetMapping("/users/coordinators")
    fun getAllCoordinators(): Flux<User> =
        userService.getUsersByRole(Role.COORDINATOR)

    @DeleteMapping("/users/delete/interviewer/{id}")
    fun deleteInterviewerById(@PathVariable("id") id: String): Mono<User> =
        userService.deleteInterviewer(ObjectId(id))

    @DeleteMapping("/users/delete/coordinator/{id}")
    fun deleteCoordinatorById(
        @PathVariable("id") id: String,
        authentication: Authentication
    ): Mono<User> {
        val jwtUserDetails = authentication.principal as JwtUserDetails
        val currentEmailCoordinator = jwtUserDetails.email
        return userService.deleteCoordinator(ObjectId(id), currentEmailCoordinator)
    }

    @GetMapping("/dashboard")
    fun getDashboard(): Flux<User> = userService.getDashboard()

    @PostMapping("/booking/update/{id}")
    fun updateBooking(
        @RequestBody bookingDto: BookingDto,
        @PathVariable id: String
    ): Mono<BookingDto> {
        val newDataBookingMono = getFromDto(bookingDto)
            .map { it.copy(id = ObjectId(id)) }

        return newDataBookingMono
            .flatMap { newDataBooking ->
                bookingValidator.validateUpdating(newDataBooking)
                    .then(bookingService.update(newDataBooking))
                    .map { savedBooking -> savedBooking.toDto() }
            }
    }

    @PostMapping("/booking/create")
    fun createBooking(@RequestBody bookingDto: BookingDto): Mono<BookingDto> = getFromDto(bookingDto)
        .flatMap { booking ->
            bookingValidator.validateCreating(booking)
                .then(bookingService.create(booking))
                .map { savedBooking -> savedBooking.toDto() }
        }

    @DeleteMapping("/booking/delete/{id}")
    fun deleteBooking(@PathVariable("id") bookingId: String): Mono<BookingDto> {
        return bookingService.getById(ObjectId(bookingId))
            .flatMap { booking ->
                bookingService.delete(booking)
                    .map { it.toDto() }
            }
    }

    private fun getFromDto(bookingDto: BookingDto): Mono<Booking> =
        periodService.obtainPeriod(bookingDto.from, bookingDto.to, bookingDto.date)
            .map { period ->
                Booking(
                    id = ObjectId(),
                    subject = bookingDto.subject,
                    description = bookingDto.description,
                    interviewerSlotId = ObjectId(bookingDto.interviewerSlotId),
                    candidateSlotId = ObjectId(bookingDto.candidateSlotId),
                    period = period
                )
            }
}
