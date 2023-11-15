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
import reactor.kotlin.core.publisher.toMono

@GrpcService
class GrpcService(
    private val slotService: SlotService,
    private val natsEventSubscriber: NatsEventSubscriber
) : ReactorSlotServiceGrpc.SlotServiceImplBase() {

    override fun streamBySlotId(request: Mono<StreamBySlotIdRequest>): Flux<StreamBySlotIdResponse> {
        return request.flatMapMany { streamRequest ->
            natsEventSubscriber.subscribe(streamRequest.id)
                .map { slotUpdatedEvent -> buildSuccessResponse(slotUpdatedEvent) }
                .startWith(
                    slotService.getById(ObjectId(streamRequest.id)).map { buildSuccessResponse(it) }
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
