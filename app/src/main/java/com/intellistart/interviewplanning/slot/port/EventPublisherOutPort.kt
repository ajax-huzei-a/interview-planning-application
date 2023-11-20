package com.intellistart.interviewplanning.slot.port

import com.intellistart.interviewplanning.output.pubsub.slot.SlotUpdatedEvent

interface EventPublisherOutPort {

    fun publishNatsSlotUpdatedEvent(slotUpdatedEvent: SlotUpdatedEvent)
}
