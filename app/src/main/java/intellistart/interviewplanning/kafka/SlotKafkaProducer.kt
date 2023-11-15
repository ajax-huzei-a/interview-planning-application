package intellistart.interviewplanning.kafka

import intellistart.interviewplanning.KafkaTopic
import intellistart.interviewplanning.commonmodels.slot.Slot
import intellistart.interviewplanning.output.pubsub.slot.SlotUpdatedEvent
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toMono

@Component
class SlotKafkaProducer(
    private val kafkaSender: KafkaSender<String, SlotUpdatedEvent>
) {

    fun produceNotification(protoSlot: Slot, id: String) {
        val event = SlotUpdatedEvent.newBuilder().setSlotId(id).setSlot(protoSlot).build()
        kafkaSender.send(
            SenderRecord.create(
                ProducerRecord(
                    KafkaTopic.UPDATED_SLOT_EVENT,
                    event.slotId,
                    event
                ),
                null
            ).toMono()
        ).subscribe()
    }
}
