package intellistart.interviewplanning.controllers.nats.slot

import com.google.protobuf.Parser
import intellistart.interviewplanning.NatsSubject.Slot.CREATE
import intellistart.interviewplanning.controllers.dto.toProto
import intellistart.interviewplanning.controllers.nats.NatsController
import intellistart.interviewplanning.exceptions.SlotException
import intellistart.interviewplanning.exceptions.UserException
import intellistart.interviewplanning.model.period.PeriodService
import intellistart.interviewplanning.model.slot.Slot
import intellistart.interviewplanning.model.slot.SlotService
import intellistart.interviewplanning.model.slot.validation.SlotValidator
import intellistart.interviewplanning.request.slot.create.proto.CreateSlotRequest
import intellistart.interviewplanning.request.slot.create.proto.CreateSlotResponse
import io.nats.client.Connection
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

@Component
class CreateSlotNatsController(
    override val connection: Connection,
    private val slotService: SlotService,
    private val slotValidator: SlotValidator,
    private val periodService: PeriodService
) : NatsController<CreateSlotRequest, CreateSlotResponse> {

    override val subject: String = CREATE

    override val parser: Parser<CreateSlotRequest> = CreateSlotRequest.parser()

    override fun handle(request: CreateSlotRequest): CreateSlotResponse = runCatching {
        val slot = getSlotFromProto(request)
        slotValidator.validateCreating(slot, request.email)
        buildSuccessResponse(slotService.create(slot, request.email))
    }.getOrElse {
        when (it) {
            is SlotException -> buildSlotFailureResponse(it)
            is UserException -> buildUserFailureResponse(it)
            else -> buildUnsupportedFailureResponse(it)
        }
    }

    private fun buildSuccessResponse(slot: Slot): CreateSlotResponse =
        CreateSlotResponse.newBuilder().apply {
            successBuilder.slotProto = slot.toProto()
        }.build()

    private fun buildSlotFailureResponse(exception: SlotException): CreateSlotResponse =
        CreateSlotResponse.newBuilder().apply {
            failureBuilder.message = exception.message
            when (exception.name) {
                "slot_is_booked" -> failureBuilder.slotIsBookedBuilder
                "invalid_boundaries" -> failureBuilder.invalidBoundariesBuilder
                "slot_not_found" -> failureBuilder.slotNotFoundBuilder
                "slot_is_overlapping" -> failureBuilder.slotIsOverlappingBuilder
                "slot_is_in_the_past" -> failureBuilder.slotIsInThePastBuilder
            }
        }.build()

    private fun buildUserFailureResponse(exception: UserException): CreateSlotResponse =
        CreateSlotResponse.newBuilder().apply {
            failureBuilder.message = exception.message
            when (exception.name) {
                "user_not_found" -> failureBuilder.slotNotFoundBuilder
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
            id = if (request.slotProto.hasId()) { ObjectId(request.slotProto.id) } else ObjectId(),
            period = periodService
                .obtainPeriod(request.slotProto.from, request.slotProto.to, request.slotProto.date),
            bookings = listOf()
        )
    }
}