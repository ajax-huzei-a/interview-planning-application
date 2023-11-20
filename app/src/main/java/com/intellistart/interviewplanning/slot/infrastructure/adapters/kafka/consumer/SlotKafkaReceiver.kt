package com.intellistart.interviewplanning.slot.infrastructure.adapters.kafka.consumer

import com.intellistart.interviewplanning.output.pubsub.slot.SlotUpdatedEvent
import com.intellistart.interviewplanning.slot.port.EventPublisherOutPort
import org.springframework.stereotype.Component
import reactor.core.scheduler.Schedulers
import reactor.kafka.receiver.KafkaReceiver
import javax.annotation.PostConstruct

@Component
class SlotKafkaReceiver(
    private val kafkaConsumer: KafkaReceiver<String, SlotUpdatedEvent>,
    private val natsEventPublisher: EventPublisherOutPort
) {

    @PostConstruct
    fun initialize() {
        kafkaConsumer.receiveAutoAck()
            .flatMap { fluxRecord -> fluxRecord.map { natsEventPublisher.publishNatsSlotUpdatedEvent(it.value()) } }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }
}
