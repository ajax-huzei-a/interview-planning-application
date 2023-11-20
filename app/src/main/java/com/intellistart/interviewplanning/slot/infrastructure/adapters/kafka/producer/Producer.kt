package com.intellistart.interviewplanning.slot.infrastructure.adapters.kafka.producer

import com.intellistart.interviewplanning.KafkaTopic
import com.intellistart.interviewplanning.output.pubsub.slot.SlotUpdatedEvent
import com.intellistart.interviewplanning.slot.port.ProducerOutPort
import com.intellistart.interviewplanning.slot.domain.model.Slot
import com.intellistart.interviewplanning.slot.infrastructure.dto.toProto
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toMono

@Component
class Producer(
    private val kafkaSender: KafkaSender<String, SlotUpdatedEvent>
) : ProducerOutPort {

    override fun produceSlotNotificationToKafka(slot: Slot, id: String) {
        val event = SlotUpdatedEvent.newBuilder().setSlotId(id).setSlot(slot.toProto()).build()
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
