package intellistart.interviewplanning.controllers.nats.slot.event

import intellistart.interviewplanning.NatsSubject
import intellistart.interviewplanning.output.pubsub.slot.SlotUpdatedEvent
import io.nats.client.Connection
import org.springframework.stereotype.Component

@Component
class NatsEventPublisher(private val connection: Connection) {

    fun publish(slotUpdatedEvent: SlotUpdatedEvent) {
        val updateEventSubject = NatsSubject.createSlotEventNatsSubject(slotUpdatedEvent.slotId, "UPDATE")
        connection.publish(updateEventSubject, slotUpdatedEvent.toByteArray())
    }
}
