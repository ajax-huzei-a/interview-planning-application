package com.intellistart.interviewplanning.slot.application.port

import com.intellistart.interviewplanning.output.pubsub.slot.SlotUpdatedEvent

interface SlotUpdatesOutPort {

    fun publishSlotUpdate(slotUpdatedEvent: SlotUpdatedEvent)
}
