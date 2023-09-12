package intellistart.interviewplanning.controllers

import intellistart.interviewplanning.controllers.dto.CandidateSlotDto
import intellistart.interviewplanning.controllers.dto.CandidateSlotsDto
import intellistart.interviewplanning.controllers.dto.toDto
import intellistart.interviewplanning.controllers.dto.toDtoList
import intellistart.interviewplanning.exceptions.SlotException
import intellistart.interviewplanning.model.candidateslot.CandidateSlot
import intellistart.interviewplanning.model.candidateslot.CandidateSlotService
import intellistart.interviewplanning.model.candidateslot.validation.CandidateSlotValidator
import intellistart.interviewplanning.security.JwtUserDetails
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody

/**
 * Controller for processing requests from Candidate.
 */
@RestController
@CrossOrigin
class CandidateController(
    private val candidateSlotService: CandidateSlotService,
    private val candidateSlotValidator: CandidateSlotValidator
) {
    /**
     * POST request for adding a new CandidateSlot.
     * First we do the conversion, then we pass it to the validation,
     * and then we send it to the service for saving.
     *
     * @param request - Request body of POST mapping.
     *
     * @return ResponseEntity - Response of the saved object converted to a DTO.
     *
     * @throws SlotException - when parameters are incorrect or slot is overlapping.
     */
    @PostMapping("/candidates/current/slots")
    fun createCandidateSlot(
        @RequestBody request: CandidateSlotDto,
        authentication: Authentication
    ): ResponseEntity<CandidateSlotDto> {
        var candidateSlot = getCandidateSlotFromDto(request, authentication)
        candidateSlotValidator.validateCreating(candidateSlot)
        candidateSlot = candidateSlotService.create(candidateSlot)
        return ResponseEntity.ok(candidateSlot.toDto())
    }

    /**
     * POST request for editing the CandidateSlot.
     * First we do the conversion, then we pass it to the validation,
     * and then we send it to the service for updating.
     *
     * @param request - Request body of POST mapping.
     * @param id - Parameter from the request mapping. This is the slot id for update.
     *
     * @return ResponseEntity - Response of the updated object converted to a DTO.
     *
     * @throws SlotException - when parameters are incorrect or updated slot is booked
     * or slot is overlapping.
     */
    @PostMapping("/candidates/current/slots/{slotId}")
    fun updateCandidateSlot(
        @RequestBody request: CandidateSlotDto,
        @PathVariable("slotId") id: Long?, authentication: Authentication
    ): ResponseEntity<CandidateSlotDto> {
        var candidateSlot = getCandidateSlotFromDto(request, authentication)
        candidateSlot.id = id
        candidateSlotValidator.validateUpdating(candidateSlot)
        candidateSlot = candidateSlotService.update(candidateSlot)
        return ResponseEntity.ok(candidateSlot.toDto())
    }

    /**
     * GET request for getting all slots of current Candidate.
     *
     * @return ResponseEntity - Response of the list of slots converted to a DTO.
     */
    @GetMapping("/candidates/current/slots")
    fun getAllSlotsOfCandidate(authentication: Authentication): ResponseEntity<CandidateSlotsDto> {
        val jwtUserDetails = authentication.principal as JwtUserDetails
        val candidateSlots = candidateSlotService
            .getAllSlotsByEmail(jwtUserDetails.email)
        return ResponseEntity.ok(candidateSlots.toDtoList())
    }

    /**
     * Converts the candidate slot from the DTO.
     *
     * @param candidateSlotDto - DTO of Candidate slot.
     *
     * @return CandidateSlot object by given DTO.
     */
    private fun getCandidateSlotFromDto(
        candidateSlotDto: CandidateSlotDto,
        authentication: Authentication
    ): CandidateSlot {
        val jwtUserDetails = authentication.principal as JwtUserDetails
        return candidateSlotService.createCandidateSlot(
            candidateSlotDto.date,
            candidateSlotDto.from,
            candidateSlotDto.to,
            jwtUserDetails.email,
            jwtUserDetails.name
        )
    }
}
