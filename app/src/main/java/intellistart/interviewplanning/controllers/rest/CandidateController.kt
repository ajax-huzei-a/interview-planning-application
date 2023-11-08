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
    ): Mono<SlotDto> = Mono.defer {
        val jwtUserDetails = authentication.principal as JwtUserDetails
        val candidateSlot = getCandidateSlotFromDto(request)
        slotValidator.validateCreating(candidateSlot, jwtUserDetails.email)
            .then(slotService.create(candidateSlot, jwtUserDetails.email))
            .map {
                it.toDto()
            }
    }

    @PostMapping("/candidate/slot/update/{slotId}")
    fun updateCandidateSlot(
        @RequestBody request: SlotDto,
        @PathVariable("slotId") id: String,
        authentication: Authentication,
    ): Mono<SlotDto> = Mono.defer {
        val jwtUserDetails = authentication.principal as JwtUserDetails
        val candidateSlot = getCandidateSlotFromDto(request).copy(id = ObjectId(id))
        slotValidator.validateUpdating(candidateSlot, jwtUserDetails.email)
            .then(slotService.update(candidateSlot, jwtUserDetails.email))
            .map {
                it.toDto()
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
        candidateSlotDto: SlotDto
    ): Slot {
        return Slot(
            id = ObjectId(),
            period = periodService
                .obtainPeriod(candidateSlotDto.from, candidateSlotDto.to, candidateSlotDto.date),
            bookings = listOf()
        )
    }
}
