package intellistart.interviewplanning.controllers.nats.slot.event

import intellistart.interviewplanning.NatsSubject
import intellistart.interviewplanning.output.pubsub.slot.SlotUpdatedEvent
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks

@Component
class NatsEventSubscriber(private val connection: Connection) {

    fun subscribe(slotId: String): Flux<SlotUpdatedEvent> {
        val sink = Sinks.many().unicast().onBackpressureBuffer<SlotUpdatedEvent>()
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
