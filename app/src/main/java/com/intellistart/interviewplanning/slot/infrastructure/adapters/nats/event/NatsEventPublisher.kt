package com.intellistart.interviewplanning.slot.infrastructure.adapters.nats.event

import com.intellistart.interviewplanning.NatsSubject
import com.intellistart.interviewplanning.output.pubsub.slot.SlotUpdatedEvent
import com.intellistart.interviewplanning.slot.application.port.NatsEventPublisherOutPort
import io.nats.client.Connection
import org.springframework.stereotype.Component

@Component
class NatsEventPublisher(private val connection: Connection) : NatsEventPublisherOutPort {

    override fun publish(slotUpdatedEvent: SlotUpdatedEvent) {
        val updateEventSubject = NatsSubject.createSlotEventNatsSubject(slotUpdatedEvent.slotId, "UPDATE")
        connection.publish(updateEventSubject, slotUpdatedEvent.toByteArray())
    }
}
