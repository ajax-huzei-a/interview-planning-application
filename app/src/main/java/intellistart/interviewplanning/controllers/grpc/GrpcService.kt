package intellistart.interviewplanning.controllers.grpc

import intellistart.interviewplanning.controllers.dto.toProto
import intellistart.interviewplanning.controllers.nats.slot.event.NatsEventSubscriber
import intellistart.interviewplanning.grpc.slot_service.ReactorSlotServiceGrpc
import intellistart.interviewplanning.model.slot.Slot
import intellistart.interviewplanning.model.slot.SlotService
import intellistart.interviewplanning.output.pubsub.slot.SlotUpdatedEvent
import intellistart.interviewplanning.request.slot.stream_by_slot_id.StreamBySlotIdRequest
import intellistart.interviewplanning.request.slot.stream_by_slot_id.StreamBySlotIdResponse
import net.devh.boot.grpc.server.service.GrpcService
import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@GrpcService
class GrpcService(
    private val slotService: SlotService,
    private val natsEventSubscriber: NatsEventSubscriber
) : ReactorSlotServiceGrpc.SlotServiceImplBase() {

    override fun streamBySlotId(request: Mono<StreamBySlotIdRequest>): Flux<StreamBySlotIdResponse> {
        return request.flatMapMany { streamSlotByIdRequest ->
            slotService.getById(ObjectId(streamSlotByIdRequest.id))
                .flatMapMany { initStateSlot ->
                    natsEventSubscriber.subscribe(streamSlotByIdRequest.id)
                        .map { slotUpdatedEvent -> buildSuccessResponse(slotUpdatedEvent) }
                        .startWith(buildSuccessResponse(initStateSlot))
                }
                .onErrorResume { Mono.just(buildFailureResponse(it)) }
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
