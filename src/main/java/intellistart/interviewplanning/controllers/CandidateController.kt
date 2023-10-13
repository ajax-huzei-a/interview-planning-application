package intellistart.interviewplanning.controllers

import intellistart.interviewplanning.controllers.dto.SlotDto
import intellistart.interviewplanning.controllers.dto.SlotsDto
import intellistart.interviewplanning.controllers.dto.toDto
import intellistart.interviewplanning.controllers.dto.toDtoList
import intellistart.interviewplanning.model.period.PeriodService
import intellistart.interviewplanning.model.slot.Slot
import intellistart.interviewplanning.model.slot.SlotService
import intellistart.interviewplanning.model.slot.validation.SlotValidator
import intellistart.interviewplanning.security.JwtUserDetails
import org.bson.types.ObjectId
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin
class CandidateController(
    private val slotService: SlotService,
    private val slotValidator: SlotValidator,
    private val periodService: PeriodService
) {

    @PostMapping("/candidate/slot/create")
    fun createCandidateSlot(
        @RequestBody request: SlotDto,
        authentication: Authentication
    ): ResponseEntity<SlotDto> {
        val candidateSlot = getCandidateSlotFromDto(request)
        slotValidator.validateCreating(candidateSlot, authentication)
        val createdCandidateSlot = slotService.create(candidateSlot, authentication)
        return ResponseEntity.ok(createdCandidateSlot.toDto())
    }

    @PostMapping("/candidate/slot/update/{slotId}")
    fun updateCandidateSlot(
        @RequestBody request: SlotDto,
        @PathVariable("slotId") id: String,
        authentication: Authentication
    ): ResponseEntity<SlotDto> {
        val candidateSlot = getCandidateSlotFromDto(request).copy(id = ObjectId(id))
        slotValidator.validateUpdating(candidateSlot, authentication)
        val updatedCandidateSlot = slotService.update(candidateSlot, authentication)
        return ResponseEntity.ok(updatedCandidateSlot.toDto())
    }

    @GetMapping("/candidate/slots")
    fun getAllSlotsOfCandidate(
        authentication: Authentication
    ): ResponseEntity<SlotsDto> {
        val jwtUserDetails = authentication.principal as JwtUserDetails
        val candidateSlots = slotService
            .getAllSlotsByEmail(jwtUserDetails.email)
        return ResponseEntity.ok(candidateSlots.toDtoList())
    }

    private fun getCandidateSlotFromDto(
        candidateSlotDto: SlotDto
    ): Slot {
        return Slot(
            period = periodService
                .obtainPeriod(candidateSlotDto.from, candidateSlotDto.to, candidateSlotDto.date)
        )
    }
}
