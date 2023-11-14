package intellistart.interviewplanning.kafka

import intellistart.interviewplanning.controllers.nats.slot.event.NatsEventPublisher
import intellistart.interviewplanning.output.pubsub.slot.SlotUpdatedEvent
import org.springframework.stereotype.Component
import reactor.core.scheduler.Schedulers
import reactor.kafka.receiver.KafkaReceiver
import javax.annotation.PostConstruct

@Component
class SlotKafkaReceiver(
    private val kafkaConsumer: KafkaReceiver<String, SlotUpdatedEvent>,
    private val natsEventPublisher: NatsEventPublisher
) {

    @PostConstruct
    fun initialize() {
        kafkaConsumer.receiveAutoAck()
            .flatMap { fluxRecord -> fluxRecord.map { natsEventPublisher.publish(it.value()) } }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }
}
