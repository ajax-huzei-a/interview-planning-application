package com.intellistart.interviewplanning.slot.infrastructure.adapters.nats.event

import com.intellistart.interviewplanning.NatsSubject
import com.intellistart.interviewplanning.output.pubsub.slot.SlotUpdatedEvent
import com.intellistart.interviewplanning.slot.port.EventSubscriberInPort
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks

@Component
class EventSubscriber(private val connection: Connection) : EventSubscriberInPort {

    private val sink = Sinks.many().multicast().onBackpressureBuffer<SlotUpdatedEvent>()

    override fun subscribeOnNatsSlotUpdatedEvent(slotId: String): Flux<SlotUpdatedEvent> {
        connection.createDispatcher { message ->
            runCatching {
                SlotUpdatedEvent.parseFrom(message.data)
            }
                .onSuccess(sink::tryEmitNext)
                .onFailure(sink::tryEmitError)
        }.subscribe(NatsSubject.createSlotEventNatsSubject(slotId, "UPDATE"))
        return sink.asFlux()
    }
}
