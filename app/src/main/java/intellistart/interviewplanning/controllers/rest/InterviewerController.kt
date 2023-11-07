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
    ): Mono<SlotDto> {
        val jwtUserDetails = authentication.principal as JwtUserDetails
        return getInterviewerSlotFromDto(slotDto)
            .flatMap { interviewerSlot ->
                slotValidator.validateCreating(interviewerSlot, jwtUserDetails.email)
                    .then(slotService.create(interviewerSlot, jwtUserDetails.email))
            }
            .map { createdInterviewerSlot ->
                createdInterviewerSlot.toDto()
            }
    }

    @PostMapping("/interviewer/slot/update/{slotId}")
    fun updateInterviewerSlot(
        @RequestBody slotDto: SlotDto,
        @PathVariable("slotId") slotId: String,
        authentication: Authentication
    ): Mono<SlotDto> {
        val jwtUserDetails = authentication.principal as JwtUserDetails

        return getInterviewerSlotFromDto(slotDto)
            .map { interviewerSlot ->
                interviewerSlot.copy(id = ObjectId(slotId))
            }
            .flatMap { updatedSlot ->
                slotValidator.validateUpdating(updatedSlot, jwtUserDetails.email)
                    .then(slotService.update(updatedSlot, jwtUserDetails.email))
            }
            .map { updatedInterviewerSlot ->
                updatedInterviewerSlot.toDto()
            }
    }

    @GetMapping("/interviewer/slots")
    fun getAllInterviewerSlots(
        authentication: Authentication
    ): Flux<SlotDto> {
        val jwtUserDetails = authentication.principal as JwtUserDetails

        return slotService.getAllSlotsByEmail(jwtUserDetails.email)
            .map { it.toDto() }
            .defaultIfEmpty(SlotDto())
    }

    private fun getInterviewerSlotFromDto(slotDto: SlotDto): Mono<Slot> =
        periodService.obtainPeriod(slotDto.from, slotDto.to, slotDto.date)
            .map { period ->
                Slot(
                    id = ObjectId(),
                    period = period,
                    bookings = emptyList()
                )
            }
}
