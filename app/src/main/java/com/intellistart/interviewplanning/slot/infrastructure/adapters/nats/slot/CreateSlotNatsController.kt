package com.intellistart.interviewplanning.slot.infrastructure.adapters.nats.slot

import com.google.protobuf.Parser
import com.intellistart.interviewplanning.NatsSubject.Slot.CREATE
import com.intellistart.interviewplanning.appltication.port.PeriodOperationsInPort
import com.intellistart.interviewplanning.exceptions.UserException
import com.intellistart.interviewplanning.request.slot.create.proto.CreateSlotRequest
import com.intellistart.interviewplanning.request.slot.create.proto.CreateSlotResponse
import com.intellistart.interviewplanning.slot.port.SlotOperationsInPort
import com.intellistart.interviewplanning.slot.port.SlotValidatorInPort
import com.intellistart.interviewplanning.slot.domain.exception.SlotException
import com.intellistart.interviewplanning.slot.domain.model.Slot
import com.intellistart.interviewplanning.slot.infrastructure.adapters.nats.NatsController
import com.intellistart.interviewplanning.slot.infrastructure.dto.toProto
import io.nats.client.Connection
import org.bson.types.ObjectId
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class CreateSlotNatsController(
    private val slotService: SlotOperationsInPort,
    private val slotValidator: SlotValidatorInPort,
    private val periodService: PeriodOperationsInPort,
    override val connection: Connection,
) : NatsController<CreateSlotRequest, CreateSlotResponse> {

    override val subject: String = CREATE

    override val parser: Parser<CreateSlotRequest> = CreateSlotRequest.parser()

    override fun handle(request: CreateSlotRequest): Mono<CreateSlotResponse> =
        Mono.fromSupplier { getSlotFromProto(request) }
            .flatMap { slotValidator.validateCreating(it, request.email).thenReturn(it) }
            .flatMap { slotService.create(it, request.email) }
            .map { buildSuccessResponse(it) }
            .onErrorResume { buildFailureResponse(it).toMono() }

    private fun buildFailureResponse(exc: Throwable): CreateSlotResponse =
        when (exc) {
            is SlotException -> buildSlotFailureResponse(exc)
            is UserException -> buildUserFailureResponse(exc)
            else -> buildUnsupportedFailureResponse(exc)
        }

    private fun buildSuccessResponse(slot: Slot): CreateSlotResponse =
        CreateSlotResponse.newBuilder().apply {
            successBuilder.slot = slot.toProto()
        }.build()

    private fun buildSlotFailureResponse(exception: SlotException): CreateSlotResponse =
        CreateSlotResponse.newBuilder().apply {
            failureBuilder.message = exception.message
            when (exception.name) {
                SLOT_IS_BOOKED -> failureBuilder.slotIsBookedBuilder
                INVALID_BOUNDARIES -> failureBuilder.invalidBoundariesBuilder
                SLOT_NOT_FOUND -> failureBuilder.slotNotFoundBuilder
                SLOT_IS_OVERLAPPING -> failureBuilder.slotIsOverlappingBuilder
                SLOT_IS_IN_THE_PAST -> failureBuilder.slotIsInThePastBuilder
            }
        }.build()

    private fun buildUserFailureResponse(exception: UserException): CreateSlotResponse =
        CreateSlotResponse.newBuilder().apply {
            failureBuilder.message = exception.message
            when (exception.name) {
                USER_NOT_FOUND -> failureBuilder.slotNotFoundBuilder
            }
        }.build()

    private fun buildUnsupportedFailureResponse(exception: Throwable): CreateSlotResponse =
        CreateSlotResponse.newBuilder().apply {
            failureBuilder.message = exception.message
        }.build()

    private fun getSlotFromProto(
        request: CreateSlotRequest
    ): Slot {
        return Slot(
            id = if (request.slot.hasId()) { request.slot.id } else ObjectId().toHexString(),
            period = periodService
                .obtainPeriod(request.slot.from, request.slot.to, request.slot.date),
            bookings = listOf()
        )
    }

    companion object {
        const val SLOT_IS_BOOKED = "slot_is_booked"
        const val INVALID_BOUNDARIES = "invalid_boundaries"
        const val SLOT_NOT_FOUND = "slot_not_found"
        const val SLOT_IS_OVERLAPPING = "slot_is_overlapping"
        const val SLOT_IS_IN_THE_PAST = "slot_is_in_the_past"
        const val USER_NOT_FOUND = "user_not_found"
    }
}