package com.intellistart.interviewplanning.slot.infrastructure.adapters.grpc

import com.intellistart.interviewplanning.output.pubsub.slot.SlotUpdatedEvent
import com.intellistart.interviewplanning.request.slot.stream_by_slot_id.StreamBySlotIdRequest
import com.intellistart.interviewplanning.request.slot.stream_by_slot_id.StreamBySlotIdResponse
import com.intellistart.interviewplanning.slot.application.port.NatsEventSubscriberInPort
import com.intellistart.interviewplanning.slot.application.port.SlotOperationsInPort
import com.intellistart.interviewplanning.slot.domain.model.Slot
import com.intellistart.interviewplanning.slot.infrastructure.dto.toProto
import intellistart.interviewplanning.grpc.slot_service.ReactorSlotServiceGrpc
import net.devh.boot.grpc.server.service.GrpcService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@GrpcService
class SlotGrpcService(
    private val slotService: SlotOperationsInPort,
    private val natsEventSubscriber: NatsEventSubscriberInPort
) : ReactorSlotServiceGrpc.SlotServiceImplBase() {

    override fun streamBySlotId(request: Mono<StreamBySlotIdRequest>): Flux<StreamBySlotIdResponse> {
        return request.flatMapMany { streamRequest ->
            natsEventSubscriber.subscribe(streamRequest.id)
                .map { slotUpdatedEvent -> buildSuccessResponse(slotUpdatedEvent) }
                .startWith(
                    slotService.getById(streamRequest.id).map { buildSuccessResponse(it) }
                )
                .onErrorResume { buildFailureResponse(it).toMono() }
        }
    }

    private fun buildSuccessResponse(slotUpdatedEvent: SlotUpdatedEvent): StreamBySlotIdResponse {
        return StreamBySlotIdResponse.newBuilder().apply {
            successBuilder.slot = slotUpdatedEvent.slot
        }.build()
    }

    private fun buildFailureResponse(exc: Throwable): StreamBySlotIdResponse {
        return StreamBySlotIdResponse.newBuilder().apply {
            failureBuilder.message = exc.message
        }.build()
    }

    private fun buildSuccessResponse(slot: Slot): StreamBySlotIdResponse =
        StreamBySlotIdResponse.newBuilder().apply {
            successBuilder.slot = slot.toProto()
        }.build()
}
