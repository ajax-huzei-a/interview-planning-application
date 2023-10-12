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
import org.springframework.http.HttpStatus
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
class InterviewerController(
    private val slotService: SlotService,
    private val slotValidator: SlotValidator,
    private val periodService: PeriodService
) {

    @PostMapping("/interviewer/slot/create")
    fun createInterviewerSlot(
        @RequestBody slotDto: SlotDto,
        authentication: Authentication
    ): ResponseEntity<SlotDto> {
        val interviewerSlot = getInterviewerSlotFromDto(slotDto)
        slotValidator.validateCreating(interviewerSlot, authentication)
        val createdInterviewerSlot = slotService.create(interviewerSlot, authentication)
        return ResponseEntity(createdInterviewerSlot.toDto(), HttpStatus.OK)
    }

    @PostMapping("/interviewer/slot/update/{slotId}")
    fun updateInterviewerSlot(
        @RequestBody slotDto: SlotDto,
        @PathVariable("slotId") slotId: String,
        authentication: Authentication
    ): ResponseEntity<SlotDto> {
        val interviewerSlot = getInterviewerSlotFromDto(slotDto)
        interviewerSlot.id = ObjectId(slotId)
        slotValidator.validateUpdating(interviewerSlot, authentication)
        val updatedInterviewerSlot = slotService.update(interviewerSlot, authentication)
        return ResponseEntity(updatedInterviewerSlot.toDto(), HttpStatus.OK)
    }

    @GetMapping("/interviewer/slots")
    fun getInterviewerSlotsForNextWeek(
        authentication: Authentication
    ): ResponseEntity<SlotsDto> {
        val jwtUserDetails = authentication.principal as JwtUserDetails
        val slots = slotService.getAllSlotsByEmail(jwtUserDetails.email)
        return ResponseEntity.ok(slots.toDtoList())
    }

    private fun getInterviewerSlotFromDto(
        slotDto: SlotDto
    ): Slot {
        return Slot().apply {
            period = periodService
                .obtainPeriod(slotDto.from, slotDto.to, slotDto.date)
        }
    }
}
