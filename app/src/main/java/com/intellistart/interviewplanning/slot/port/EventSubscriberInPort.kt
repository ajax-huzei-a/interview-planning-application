package com.intellistart.interviewplanning.slot.port

import com.intellistart.interviewplanning.output.pubsub.slot.SlotUpdatedEvent
import reactor.core.publisher.Flux

interface EventSubscriberInPort {

    fun subscribeOnNatsSlotUpdatedEvent(slotId: String): Flux<SlotUpdatedEvent>
}
