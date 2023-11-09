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
import reactor.core.publisher.Mono

@Component
class UpdateSlotNatsController(
    private val slotService: SlotService,
    private val slotValidator: SlotValidator,
    private val periodService: PeriodService,
    override val connection: Connection
) : NatsController<UpdateSlotRequest, UpdateSlotResponse> {

    override val subject: String = NatsSubject.Slot.UPDATE

    override val parser: Parser<UpdateSlotRequest> = UpdateSlotRequest.parser()

    override fun handle(request: UpdateSlotRequest): Mono<UpdateSlotResponse> =
        Mono.defer {
            val slot = getSlotFromProto(request).copy(id = ObjectId(request.slotId))
            slotValidator.validateUpdating(slot, request.email)
                .then(slotService.update(slot, request.email))
                .map { buildSuccessResponse(it) }
        }.onErrorResume { Mono.just(buildFailureResponse(it)) }

    private fun buildFailureResponse(exc: Throwable): UpdateSlotResponse =
        when (exc) {
            is SlotException -> buildSlotFailureResponse(exc)
            is UserException -> buildUserFailureResponse(exc)
            else -> buildUnsupportedFailureResponse(exc)
        }

    private fun buildSuccessResponse(slot: Slot): UpdateSlotResponse =
        UpdateSlotResponse.newBuilder().apply {
            successBuilder.slot = slot.toProto()
        }.build()

    private fun buildSlotFailureResponse(exception: SlotException): UpdateSlotResponse =
        UpdateSlotResponse.newBuilder().apply {
            failureBuilder.message = exception.message
            when (exception.name) {
                SLOT_IS_BOOKED -> failureBuilder.slotIsBookedBuilder
                INVALID_BOUNDARIES -> failureBuilder.invalidBoundariesBuilder
                SLOT_NOT_FOUND -> failureBuilder.slotNotFoundBuilder
                SLOT_IS_OVERLAPPING -> failureBuilder.slotIsOverlappingBuilder
                SLOT_IS_IN_THE_PAST -> failureBuilder.slotIsInThePastBuilder
            }
        }.build()

    private fun buildUserFailureResponse(exception: UserException): UpdateSlotResponse =
        UpdateSlotResponse.newBuilder().apply {
            failureBuilder.message = exception.message
            when (exception.name) {
                USER_NOT_FOUND -> failureBuilder.slotNotFoundBuilder
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
