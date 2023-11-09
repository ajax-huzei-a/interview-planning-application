package intellistart.interviewplanning.controllers.rest

import intellistart.interviewplanning.controllers.dto.SlotDto
import intellistart.interviewplanning.controllers.dto.toDto
import intellistart.interviewplanning.model.period.PeriodService
import intellistart.interviewplanning.model.slot.Slot
import intellistart.interviewplanning.model.slot.SlotService
import intellistart.interviewplanning.model.slot.validation.SlotValidator
import intellistart.interviewplanning.security.JwtUserDetails
import org.bson.types.ObjectId
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmptyDeferred
import reactor.kotlin.core.publisher.toMono

@RestController
@CrossOrigin
class InterviewerController(
    private val slotService: SlotService,
    private val slotValidator: SlotValidator,
    private val periodService: PeriodService
) {

    @PostMapping("/interviewer/slot/create")
    fun createInterviewerSlot(
        @RequestBody slotDto: SlotDto,
        authentication: Authentication
    ): Mono<SlotDto> = Mono.defer {
        val jwtUserDetails = authentication.principal as JwtUserDetails
        val interviewerSlot = getInterviewerSlotFromDto(slotDto)
        slotValidator.validateCreating(interviewerSlot, jwtUserDetails.email)
            .flatMap { slotService.create(interviewerSlot, jwtUserDetails.email) }
            .map {
                it.toDto()
            }
    }

    @PostMapping("/interviewer/slot/update/{slotId}")
    fun updateInterviewerSlot(
        @RequestBody slotDto: SlotDto,
        @PathVariable("slotId") slotId: String,
        authentication: Authentication
    ): Mono<SlotDto> = Mono.defer {
        val jwtUserDetails = authentication.principal as JwtUserDetails
        val interviewerSlot = getInterviewerSlotFromDto(slotDto).copy(id = ObjectId(slotId))
        slotValidator.validateUpdating(interviewerSlot, jwtUserDetails.email)
            .flatMap { slotService.update(interviewerSlot, jwtUserDetails.email) }
            .map {
                it.toDto()
            }
    }

    @GetMapping("/interviewer/slots")
    fun getAllInterviewerSlots(
        authentication: Authentication
    ): Flux<SlotDto> {
        val jwtUserDetails = authentication.principal as JwtUserDetails

        return slotService.getAllSlotsByEmail(jwtUserDetails.email)
            .map { it.toDto() }
            .switchIfEmptyDeferred { SlotDto().toMono() }
    }

    private fun getInterviewerSlotFromDto(
        slotDto: SlotDto
    ): Slot {
        return Slot(
            id = ObjectId(),
            period = periodService
                .obtainPeriod(slotDto.from, slotDto.to, slotDto.date),
            bookings = listOf()
        )
    }
}
