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
class CandidateController(
    private val slotService: SlotService,
    private val slotValidator: SlotValidator,
    private val periodService: PeriodService,
) {

    @PostMapping("/candidate/slot/create")
    fun createCandidateSlot(
        @RequestBody request: SlotDto,
        authentication: Authentication,
    ): Mono<SlotDto> {
        val jwtUserDetails = authentication.principal as JwtUserDetails
        return getCandidateSlotFromDto(request)
            .flatMap { candidateSlot ->
                slotValidator.validateCreating(candidateSlot, jwtUserDetails.email)
                    .then(slotService.create(candidateSlot, jwtUserDetails.email))
            }
            .map { createdCandidateSlot ->
                createdCandidateSlot.toDto()
            }
    }

    @PostMapping("/candidate/slot/update/{slotId}")
    fun updateCandidateSlot(
        @RequestBody request: SlotDto,
        @PathVariable("slotId") id: String,
        authentication: Authentication,
    ): Mono<SlotDto> {
        val jwtUserDetails = authentication.principal as JwtUserDetails

        return getCandidateSlotFromDto(request)
            .map { candidateSlot ->
                candidateSlot.copy(id = ObjectId(id))
            }
            .flatMap { updatedSlot ->
                slotValidator.validateUpdating(updatedSlot, jwtUserDetails.email)
                    .then(slotService.update(updatedSlot, jwtUserDetails.email))
            }
            .map { updatedCandidateSlot ->
                updatedCandidateSlot.toDto()
            }
    }

    @GetMapping("/candidate/slots")
    fun getAllSlotsOfCandidate(
        authentication: Authentication,
    ): Flux<SlotDto> {
        val jwtUserDetails = authentication.principal as JwtUserDetails

        return slotService.getAllSlotsByEmail(jwtUserDetails.email)
            .map { it.toDto() }
            .defaultIfEmpty(SlotDto())
    }

    private fun getCandidateSlotFromDto(
        slotDto: SlotDto,
    ): Mono<Slot> = periodService.obtainPeriod(slotDto.from, slotDto.to, slotDto.date)
        .map { period ->
            Slot(
                id = ObjectId(),
                period = period,
                bookings = emptyList()
            )
        }
}
