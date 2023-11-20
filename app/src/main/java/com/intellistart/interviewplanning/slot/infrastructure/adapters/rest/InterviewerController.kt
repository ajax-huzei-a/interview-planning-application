package com.intellistart.interviewplanning.slot.infrastructure.adapters.rest

import com.intellistart.interviewplanning.port.PeriodOperationsInPort
import com.intellistart.interviewplanning.security.JwtUserDetails
import com.intellistart.interviewplanning.slot.domain.model.Slot
import com.intellistart.interviewplanning.slot.infrastructure.dto.SlotDto
import com.intellistart.interviewplanning.slot.infrastructure.dto.toDto
import com.intellistart.interviewplanning.slot.port.SlotOperationsInPort
import com.intellistart.interviewplanning.slot.port.SlotValidatorInPort
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
    private val slotService: SlotOperationsInPort,
    private val slotValidator: SlotValidatorInPort,
    private val periodService: PeriodOperationsInPort
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
        val interviewerSlot = getInterviewerSlotFromDto(slotDto).copy(id = slotId)
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
            id = ObjectId().toHexString(),
            period = periodService
                .obtainPeriod(slotDto.from, slotDto.to, slotDto.date),
            bookings = listOf()
        )
    }
}
