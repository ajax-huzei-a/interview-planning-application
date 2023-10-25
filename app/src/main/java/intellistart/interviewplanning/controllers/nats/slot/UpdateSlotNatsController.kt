package intellistart.interviewplanning.controllers.nats.slot

import com.google.protobuf.Parser
import intellistart.interviewplanning.NatsSubject
import intellistart.interviewplanning.controllers.dto.toProto
import intellistart.interviewplanning.controllers.nats.NatsController
import intellistart.interviewplanning.exceptions.SlotException
import intellistart.interviewplanning.exceptions.UserException
import intellistart.interviewplanning.model.period.PeriodService
import intellistart.interviewplanning.model.slot.Slot
import intellistart.interviewplanning.model.slot.SlotService
import intellistart.interviewplanning.model.slot.validation.SlotValidator
import intellistart.interviewplanning.request.slot.update.proto.UpdateSlotRequest
import intellistart.interviewplanning.request.slot.update.proto.UpdateSlotResponse
import io.nats.client.Connection
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

@Component
class UpdateSlotNatsController(
    override val connection: Connection,
    private val slotService: SlotService,
    private val slotValidator: SlotValidator,
    private val periodService: PeriodService
) : NatsController<UpdateSlotRequest, UpdateSlotResponse> {

    override val subject: String = NatsSubject.Slot.UPDATE

    override val parser: Parser<UpdateSlotRequest> = UpdateSlotRequest.parser()

    override fun handle(request: UpdateSlotRequest): UpdateSlotResponse = runCatching {
        val slot = getSlotFromProto(request).copy(id = ObjectId(request.slotId))
        slotValidator.validateUpdating(slot, request.email)
        buildSuccessResponse(slotService.update(slot, request.email))
    }.getOrElse {
        when (it) {
            is SlotException -> buildSlotFailureResponse(it)
            is UserException -> buildUserFailureResponse(it)
            else -> buildUnsupportedFailureResponse(it)
        }
    }

    private fun buildSuccessResponse(slot: Slot): UpdateSlotResponse =
        UpdateSlotResponse.newBuilder().apply {
            successBuilder.setSlotProto(slot.toProto())
        }.build()

    private fun buildSlotFailureResponse(exception: SlotException): UpdateSlotResponse =
        UpdateSlotResponse.newBuilder().apply {
            failureBuilder.message = exception.message
            when (exception.name) {
                "slot_is_booked" -> failureBuilder.slotIsBookedBuilder
                "invalid_boundaries" -> failureBuilder.invalidBoundariesBuilder
                "slot_not_found" -> failureBuilder.slotNotFoundBuilder
                "slot_is_overlapping" -> failureBuilder.slotIsOverlappingBuilder
                "slot_is_in_the_past" -> failureBuilder.slotIsInThePastBuilder
            }
        }.build()

    private fun buildUserFailureResponse(exception: UserException): UpdateSlotResponse =
        UpdateSlotResponse.newBuilder().apply {
            failureBuilder.message = exception.message
            when (exception.name) {
                "user_not_found" -> failureBuilder.slotNotFoundBuilder
            }
        }.build()

    private fun buildUnsupportedFailureResponse(exception: Throwable): UpdateSlotResponse =
        UpdateSlotResponse.newBuilder().apply {
            failureBuilder.message = exception.message
        }.build()

    private fun getSlotFromProto(
        request: UpdateSlotRequest
    ): Slot {
        return Slot(
            id = ObjectId(),
            period = periodService
                .obtainPeriod(request.slotProto.from, request.slotProto.to, request.slotProto.date),
            bookings = listOf()
        )
    }
}
