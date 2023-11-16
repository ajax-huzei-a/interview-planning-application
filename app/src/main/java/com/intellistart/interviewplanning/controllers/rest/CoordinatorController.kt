package com.intellistart.interviewplanning.controllers.rest

import com.intellistart.interviewplanning.appltication.service.PeriodOperations
import com.intellistart.interviewplanning.controllers.dto.BookingDto
import com.intellistart.interviewplanning.controllers.dto.EmailDto
import com.intellistart.interviewplanning.controllers.dto.toDto
import com.intellistart.interviewplanning.model.booking.Booking
import com.intellistart.interviewplanning.model.booking.BookingService
import com.intellistart.interviewplanning.model.booking.validation.BookingValidator
import com.intellistart.interviewplanning.model.user.Role
import com.intellistart.interviewplanning.model.user.User
import com.intellistart.interviewplanning.model.user.UserService
import com.intellistart.interviewplanning.security.JwtUserDetails
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
    private val periodService: PeriodOperations,
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
    ): Mono<BookingDto> = Mono.fromSupplier {
        getFromDto(bookingDto).copy(id = ObjectId(id))
    }
        .flatMap { booking ->
            bookingValidator.validateUpdating(booking).thenReturn(booking)
        }
        .flatMap { booking -> bookingService.update(booking) }
        .map { it.toDto() }

    @PostMapping("/booking/create")
    fun createBooking(@RequestBody bookingDto: BookingDto): Mono<BookingDto> =
        Mono.fromSupplier {
            getFromDto(bookingDto)
        }
            .flatMap { booking ->
                bookingValidator.validateCreating(booking).thenReturn(booking)
            }
            .flatMap { booking -> bookingService.create(booking) }
            .map { it.toDto() }

    @DeleteMapping("/booking/delete/{id}")
    fun deleteBooking(@PathVariable("id") bookingId: String): Mono<BookingDto> {
        return bookingService.getById(ObjectId(bookingId))
            .flatMap { booking ->
                bookingService.delete(booking)
                    .map { it.toDto() }
            }
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
