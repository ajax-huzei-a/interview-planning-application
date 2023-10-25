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
            id = if (request.slot.hasId()) { ObjectId(request.slot.id) } else ObjectId(),
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
