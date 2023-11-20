package com.intellistart.interviewplanning.slot.application.port

import com.intellistart.interviewplanning.output.pubsub.slot.SlotUpdatedEvent
import reactor.core.publisher.Flux

interface SlotUpdatesInPort {

    fun subscribe(slotId: String): Flux<SlotUpdatedEvent>
}