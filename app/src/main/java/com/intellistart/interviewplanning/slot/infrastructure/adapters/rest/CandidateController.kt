package com.intellistart.interviewplanning.slot.infrastructure.adapters.rest

import com.intellistart.interviewplanning.appltication.port.PeriodOperationsInPort
import com.intellistart.interviewplanning.security.JwtUserDetails
import com.intellistart.interviewplanning.slot.application.port.SlotOperationsInPort
import com.intellistart.interviewplanning.slot.application.port.SlotValidatorInPort
import com.intellistart.interviewplanning.slot.domain.model.Slot
import com.intellistart.interviewplanning.slot.infrastructure.dto.SlotDto
import com.intellistart.interviewplanning.slot.infrastructure.dto.toDto
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
class CandidateController(
    private val slotService: SlotOperationsInPort,
    private val slotValidator: SlotValidatorInPort,
    private val periodService: PeriodOperationsInPort,
) {

    @PostMapping("/candidate/slot/create")
    fun createCandidateSlot(
        @RequestBody request: SlotDto,
        authentication: Authentication,
    ): Mono<SlotDto> = Mono.defer {
        val jwtUserDetails = authentication.principal as JwtUserDetails
        val candidateSlot = getCandidateSlotFromDto(request)
        slotValidator.validateCreating(candidateSlot, jwtUserDetails.email)
            .flatMap { slotService.create(candidateSlot, jwtUserDetails.email) }
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
        val candidateSlot = getCandidateSlotFromDto(request).copy(id = id)
        slotValidator.validateUpdating(candidateSlot, jwtUserDetails.email)
            .flatMap { slotService.update(candidateSlot, jwtUserDetails.email) }
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
            .switchIfEmptyDeferred { SlotDto().toMono() }
    }

    private fun getCandidateSlotFromDto(
        candidateSlotDto: SlotDto
    ): Slot {
        return Slot(
            id = ObjectId().toHexString(),
            period = periodService
                .obtainPeriod(candidateSlotDto.from, candidateSlotDto.to, candidateSlotDto.date),
            bookings = listOf()
        )
    }
}
