package com.intellistart.interviewplanning.slot.infrastructure.adapters.nats.slot

import com.google.protobuf.Parser
import com.intellistart.interviewplanning.NatsSubject
import com.intellistart.interviewplanning.request.slot.get_all.proto.GetAllSlotsRequest
import com.intellistart.interviewplanning.request.slot.get_all.proto.GetAllSlotsResponse
import com.intellistart.interviewplanning.slot.port.SlotOperationsInPort
import com.intellistart.interviewplanning.slot.domain.model.Slot
import com.intellistart.interviewplanning.slot.infrastructure.adapters.nats.NatsController
import com.intellistart.interviewplanning.slot.infrastructure.dto.toProto
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class GetSlotsNatsController(
    private val slotService: SlotOperationsInPort,
    override val connection: Connection,
) : NatsController<GetAllSlotsRequest, GetAllSlotsResponse> {

    override val subject: String = NatsSubject.Slot.GET_ALL

    override val parser: Parser<GetAllSlotsRequest> = GetAllSlotsRequest.parser()

    override fun handle(request: GetAllSlotsRequest): Mono<GetAllSlotsResponse> =
        slotService.getAllSlotsByEmail(request.email)
            .collectList()
            .map { buildSuccessResponse(it) }
            .onErrorResume { Mono.just(buildFailureResponse(it)) }

    private fun buildSuccessResponse(slots: List<Slot>): GetAllSlotsResponse =
        GetAllSlotsResponse.newBuilder().apply {
            successBuilder.slotsBuilder.addAllSlotProto(slots.map { it.toProto() })
        }.build()

    private fun buildFailureResponse(exception: Throwable): GetAllSlotsResponse =
        GetAllSlotsResponse.newBuilder().apply {
            failureBuilder.message = exception.message
        }.build()
}
