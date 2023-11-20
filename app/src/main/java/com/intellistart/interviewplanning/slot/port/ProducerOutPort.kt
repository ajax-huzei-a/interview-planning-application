package com.intellistart.interviewplanning.slot.port

import com.intellistart.interviewplanning.slot.domain.model.Slot

interface ProducerOutPort {

    fun produceSlotNotificationToKafka(slot: Slot, id: String)
}
