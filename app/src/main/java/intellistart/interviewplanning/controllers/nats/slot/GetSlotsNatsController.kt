package intellistart.interviewplanning.controllers.nats.slot

import com.google.protobuf.Parser
import intellistart.interviewplanning.NatsSubject
import intellistart.interviewplanning.controllers.dto.toProto
import intellistart.interviewplanning.controllers.nats.NatsController
import intellistart.interviewplanning.model.slot.Slot
import intellistart.interviewplanning.model.slot.SlotService
import intellistart.interviewplanning.request.slot.get_all.proto.GetAllSlotsRequest
import intellistart.interviewplanning.request.slot.get_all.proto.GetAllSlotsResponse
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class GetSlotsNatsController(
    private val slotService: SlotService,
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
